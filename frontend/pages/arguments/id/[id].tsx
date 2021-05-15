import { GetServerSideProps, NextPage } from 'next'
import { useRouter } from 'next/router'
import { useContext, useEffect, useMemo, useState } from 'react'
import { DragDropContext, Droppable, DroppableProvided, DropResult } from 'react-beautiful-dnd'
import AddItemToArgumentButton from 'components/arguments/AddItemToArgumentButton'
import ArgumentCardDisplay from 'components/arguments/ArgumentCardDisplay'
import ArgumentName from 'components/arguments/ArgumentName'
import DeleteArgumentButton from 'components/arguments/DeleteArgumentButton'
import { ResponseArgument, ResponseArgumentCard } from 'types/argument'
import redirectToLogin from 'lib/redirectToLogin'
import theme from 'lib/theme'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import userContext from 'lib/hooks/UserContext'
import DashboardPage from 'components/dash/DashboardPage'
import { Box, Flex, Text, useColorModeValue } from '@chakra-ui/react'
import ArgumentDeletedMessage from 'components/arguments/ArgumentDeletedMessage'
import { ResponseAnalytic } from 'types/analytic'
import ArgumentAnalyticDisplay from 'components/arguments/analytics/ArgumentAnalyticDisplay'

interface ResponseArgumentCardWithType extends ResponseArgumentCard {
  type: 'card'
}

interface AnalyticWithType extends ResponseAnalytic {
  type: 'analytic'
}

interface Props {
  id?: string
  fetchingErrorText?: string
  argument?: ResponseArgument
}

const ViewArgument: NextPage<Props> = ({ fetchingErrorText, argument: initialArgument, id }) => {
  const [argument, setArgument] = useState(initialArgument)
  const [name, setName] = useState(argument?.name)
  const [isRestored, setRestored] = useState(false)
  const router = useRouter()
  const { setErrorMessage } = useContext(errorMessageContext)
  const { jwt } = useContext(userContext)
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
  const bgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')
  const items = useMemo(() => {
    const arr: (ResponseArgumentCardWithType | AnalyticWithType)[] = []
    if (!argument) {
      return arr
    }
    argument.cards.forEach(c => {
      arr[c.position] = { ...c, type: 'card' }
    })
    argument.analytics.forEach(a => {
      arr[a.position] = { ...a, type: 'analytic' }
    })
    return arr
  }, [argument.cards, argument.analytics])

  useEffect(() => {
    if (fetchingErrorText) {
      setErrorMessage(fetchingErrorText)
    }
  }, [])

  const handleDragEnd = async (result: DropResult) => {
    if (!result.destination || result.destination.index === result.source.index) {
      return
    }

    const newItems = Array.from(items)

    newItems[result.source.index].position = result.destination.index
    if (result.source.index < result.destination.index) {
      for (let i = result.source.index + 1; i <= result.destination.index; i++) {
        newItems[i].position--
      }
    } else {
      for (let i = result.destination.index; i < result.source.index; i++) {
        newItems[i].position++
      }
    }

    // https://stackoverflow.com/a/7180095
    // Move element from old index to new index
    newItems.splice(result.destination.index, 0, newItems.splice(result.source.index, 1)[0])

    const newCards = []
    const newAnalytics = []
    newItems.forEach(i => {
      if (i.type === 'card') {
        newCards.push(i)
      } else {
        newAnalytics.push(i)
      }
    })

    setArgument({ ...argument, cards: newCards, analytics: newAnalytics })

    // todo throttle
    const response = await fetch(`/api/v1/arguments/id/${encodeURIComponent(argument.id)}/items`, {
      method: 'PATCH',
      headers: { Authorization: `Bearer ${jwt}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({
        oldIndex: result.source.index,
        newIndex: result.destination.index
      })
    })
    // todo show success alert if worked
    if (!response.ok) {
      setErrorMessage(`Unknown error occurred while reordering items. Status code: ${response.status}`)
    }
  }

  const handleRestore = () => {
    setRestored(true)
  }

  const handleDeleteAnalytic = (position: number) => {
    const newAnalytics = Array.from(argument.analytics)
    for (let i = 0; i < newAnalytics.length; i++) {
      if (newAnalytics[i].position === position) {
        newAnalytics.splice(i, 1)
      }
    }
    setArgument({ ...argument, analytics: newAnalytics })
  }

  return (
    <DashboardPage>
      {argument && (
        <Flex flexDirection='column' alignItems='center' w='100%' m='5vh 0'>
          <Box w={{ base: '90%', sm: '85%', md: '80%', lg: '70%', xl: '50%' }}>
            {argument.deleted && !isRestored && <ArgumentDeletedMessage id={argument.id} onRestore={handleRestore} />}
            <Flex justifyContent='space-between' alignItems='center'>
              <ArgumentName name={name} argumentId={argument.id} onNameChange={setName} />
              <DeleteArgumentButton
                argumentId={argument.id}
                argumentName={argument.name}
                onDelete={() => router.push('/arguments')}
              />
            </Flex>
            <div
              style={{
                width: '100%',
                margin: '2vh 0',
                height: 1,
                backgroundColor: theme.palette.lightGrey.main
              }}
            />
            <DragDropContext onDragEnd={handleDragEnd}>
              <Droppable droppableId='CARDS_LIST'>
                {(dropProvided: DroppableProvided) => (
                  <Box
                    bg={bgColor}
                    borderWidth='1px'
                    borderStyle='solid'
                    borderColor={borderColor}
                    borderRadius='5px'
                    p='3vh 3vw'
                    ref={dropProvided.innerRef}
                  >
                    {!items.length && (
                      <Text color='darkGray' textAlign='center'>
                        This argument doesn't have any items
                      </Text>
                    )}
                    {items &&
                      items.map((item, index) => {
                        if (item.type === 'card') {
                          return (
                            <ArgumentCardDisplay
                              card={item}
                              // eslint-disable-next-line react/no-array-index-key
                              key={`${item.id}@${index}`}
                              argumentId={id}
                              indexInArgument={index}
                              onRemove={() =>
                                setArgument({ ...argument, cards: argument.cards.filter((_element, i) => i !== index) })
                              }
                            />
                          )
                        }
                        // todo separate component for analytics with drag and drop, editing, and deleting
                        return (
                          <ArgumentAnalyticDisplay
                            // eslint-disable-next-line react/no-array-index-key
                            key={`${item.id}@${index}`}
                            analytic={item}
                            argId={argument.id}
                            onDelete={() => handleDeleteAnalytic(item.position)}
                          />
                        )
                      })}
                    {dropProvided.placeholder}
                  </Box>
                )}
              </Droppable>
            </DragDropContext>
            <div style={{ marginTop: 25 }}>
              <AddItemToArgumentButton argId={argument.id} />
            </div>
          </Box>
        </Flex>
      )}
    </DashboardPage>
  )
}

export default ViewArgument

export const getServerSideProps: GetServerSideProps<Props> = async ({ query, req, res }) => {
  let errorText: string | null = null
  let argument: ResponseArgument | null = null
  const id: string = typeof query.id === 'string' ? query.id : query?.id[0]

  if (!id) {
    return {
      props: {
        fetchingErrorText: 'Invalid argument id'
      }
    }
  }

  const { jwt } = req.cookies
  if (!jwt) {
    redirectToLogin(res, `/cards/id/${id}`)
    return {
      props: {}
    }
  }
  const dev = process.env.NODE_ENV !== 'production'
  const response = await fetch(
    (dev ? 'http://localhost' : 'https://cardtown.co') + `/api/v1/arguments/id/${encodeURIComponent(id)}`,
    {
      headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${jwt}` }
    }
  )
  if (response.ok) {
    argument = await response.json()
  } else if (response.status === 404 || response.status === 400) {
    errorText = 'Argument not found'
  } else if (response.status === 403) {
    errorText = "You don't have access to this argument"
  } else if (response.status === 401) {
    redirectToLogin(res, `/arguments/id/${encodeURIComponent(id)}`)
    return {
      props: {}
    }
  } else if (response.status === 500) {
    errorText = 'There was an unknown server error. Please try again later'
  } else if (response.status === 406) {
    errorText = 'The ID for this argument is invalid.'
  } else {
    errorText = `There was an unrecognized error. Status: ${response.status}`
  }
  return {
    props: {
      id,
      fetchingErrorText: errorText,
      argument,
      jwt
    }
  }
}

import { Typography } from '@material-ui/core'
import { GetServerSideProps, NextPage } from 'next'
import { useRouter } from 'next/router'
import { useContext, useEffect, useState } from 'react'
import { DragDropContext, Droppable, DroppableProvided, DropResult } from 'react-beautiful-dnd'
import AddCardToArgumentButton from 'components/arguments/AddCardToArgumentButton'
import ArgumentCardDisplay from 'components/arguments/ArgumentCardDisplay'
import ArgumentName from 'components/arguments/ArgumentName'
import DeleteArgumentButton from 'components/arguments/DeleteArgumentButton'
import DashboardNavbar from 'components/dash/DashboardNavbar'
import ResponseArgument from 'types/ResponseArgument'
import useWindowSize from 'lib/hooks/useWindowSize'
import redirectToLogin from 'lib/redirectToLogin'
import theme from 'lib/theme'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import userContext from 'lib/hooks/UserContext'

interface Props {
  id?: string
  fetchingErrorText?: string
  argument?: ResponseArgument
}

const ViewArgument: NextPage<Props> = ({ fetchingErrorText, argument: initialArgument, id }) => {
  const [argument, setArgument] = useState(initialArgument)
  const [name, setName] = useState(argument?.name)
  const { width } = useWindowSize(1920, 1080)
  const router = useRouter()
  const { setErrorMessage } = useContext(errorMessageContext)
  const { jwt } = useContext(userContext)

  useEffect(() => {
    if (fetchingErrorText) {
      setErrorMessage(fetchingErrorText)
    }
  }, [])

  const handleDragEnd = async (result: DropResult) => {
    if (!result.destination) {
      return
    }
    if (result.destination.index === result.source.index) {
      return
    }

    const newCards = Array.from(argument.cards)
    // https://stackoverflow.com/a/7180095
    // Move element from old index to new index
    newCards.splice(result.destination.index, 0, newCards.splice(result.source.index, 1)[0])
    setArgument({ ...argument, cards: newCards })

    // todo throttle
    const response = await fetch(`/api/v1/arguments/id/${encodeURIComponent(argument.id)}/cards`, {
      method: 'PATCH',
      headers: { Authorization: `Bearer ${jwt}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({
        oldIndex: result.source.index,
        newIndex: result.destination.index
      })
    })
    // todo show success alert if worked
    if (!response.ok) {
      setErrorMessage(`Unknown error occurred while reordering cards. Status code: ${response.status}`)
    }
  }

  return (
    <div
      style={{
        width: '100%',
        backgroundColor: theme.palette.lightBlue.main,
        minHeight: '100%',
        overflow: 'auto'
      }}
    >
      <DashboardNavbar windowWidth={width} pageName='Arguments' />
      {argument && (
        <div
          style={{
            width: '50%',
            margin: '10vh auto'
          }}
        >
          <Typography
            style={{
              color: theme.palette.darkGrey.main,
              textTransform: 'uppercase',
              fontSize: 11,
              marginTop: 19,
              letterSpacing: 0.5
            }}
          >
            Argument
          </Typography>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <ArgumentName name={name} argumentId={argument.id} onNameChange={setName} />
            <DeleteArgumentButton
              argumentId={argument.id}
              argumentName={argument.name}
              onDelete={() => router.push('/arguments/all')}
            />
          </div>
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
                <div
                  style={{
                    backgroundColor: theme.palette.secondary.main,
                    border: `1px solid ${theme.palette.lightGrey.main}`,
                    borderRadius: 5,
                    padding: '3vh 3vw'
                  }}
                  ref={dropProvided.innerRef}
                >
                  {!argument?.cards?.length && (
                    <Typography color='textSecondary' style={{ textAlign: 'center' }}>
                      This argument doesn't have any cards
                    </Typography>
                  )}
                  {argument?.cards &&
                    argument.cards.map((card, index) => (
                      <ArgumentCardDisplay
                        card={card}
                        windowWidth={width}
                        // eslint-disable-next-line react/no-array-index-key
                        key={`${card.id}@${index}`}
                        argumentId={id}
                        indexInArgument={index}
                        onRemove={() =>
                          setArgument({ ...argument, cards: argument.cards.filter((_element, i) => i !== index) })
                        }
                      />
                    ))}
                  {dropProvided.placeholder}
                </div>
              )}
            </Droppable>
          </DragDropContext>
          <div style={{ marginTop: 25 }}>
            <AddCardToArgumentButton argId={argument.id} windowWidth={width} />
          </div>
        </div>
      )}
    </div>
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

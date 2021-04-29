import { useContext, useEffect, useState } from 'react'
import { GetServerSideProps, NextPage } from 'next'
import useWindowSize from 'lib/hooks/useWindowSize'
import redirectToLogin from 'lib/redirectToLogin'
import { ResponseCard } from 'types/card'
import CardDisplay from 'components/cards/CardDisplay'
import { ArgumentWithCardModel } from 'types/argument'
import CardArgumentsDisplay from 'components/cards/CardArgumentsDisplay'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import DashboardPage from 'components/dash/DashboardPage'
import { Box, Flex, useColorModeValue } from '@chakra-ui/react'
import CardDeletedMessage from 'components/cards/CardDeletedMessage'

interface Props {
  id?: string
  fetchingErrorText?: string
  card?: ResponseCard
  relatedArguments?: ArgumentWithCardModel[]
}

// todo show arguments (with hyperlink and the index that the card appears) that contain the card
// using GET /api/v1/cards/[id]/arguments
const ViewCard: NextPage<Props> = ({ fetchingErrorText, card, relatedArguments }) => {
  const { setErrorMessage } = useContext(errorMessageContext)
  const bgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
  const [isRestored, setRestored] = useState(false)

  useEffect(() => {
    if (fetchingErrorText) {
      setErrorMessage(fetchingErrorText)
    }
  }, [])

  const handleRestore = () => {
    setRestored(true)
  }

  return (
    <DashboardPage>
      <Flex flexDirection='column' alignItems='center' w='100%' m='5vh 0'>
        <Box w={{ base: '90%', sm: '85%', md: '80%', lg: '70%', xl: '50%' }}>
          {card.deleted && !isRestored && <CardDeletedMessage id={card.id} onRestore={handleRestore} />}
          <Box
            bg='white'
            borderRadius='5px'
            p='3vh 3vw'
            bgColor={bgColor}
            borderWidth='1px'
            borderStyle='solid'
            borderColor={borderColor}
          >
            {card && <CardDisplay card={card} isRestored={isRestored} />}
          </Box>
          {relatedArguments && relatedArguments.length !== 0 && <CardArgumentsDisplay relatedArguments={relatedArguments} />}
        </Box>
      </Flex>
    </DashboardPage>
  )
}

export default ViewCard

export const getServerSideProps: GetServerSideProps<Props> = async ({ query, req, res }) => {
  let errorText: string | null = null
  let card: ResponseCard | null = null
  const id: string = typeof query.id === 'string' ? query.id : query?.id[0]

  if (!id) {
    return {
      props: {
        fetchingErrorText: 'Invalid card id'
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
  const domain = process.env.NODE_ENV !== 'production' ? 'http://localhost' : 'https://cardtown.co'
  // todo do promise.all()
  const cardResponse = await fetch(`${domain}/api/v1/cards/id/${id}`, {
    headers: { Authorization: `Bearer ${jwt}` }
  })
  if (cardResponse.ok) {
    card = await cardResponse.json()
  } else {
    if (cardResponse.status === 404 || cardResponse.status === 400) {
      errorText = 'This card could not be found'
    } else if (cardResponse.status === 403) {
      errorText = "You don't have access to this card!"
    } else if (cardResponse.status === 401) {
      redirectToLogin(res, `/cards/id/${id}`)
      return {
        props: {}
      }
    } else if (cardResponse.status === 500) {
      errorText = 'There was an unknown server error. Please try again'
    } else if (cardResponse.status === 406) {
      errorText = 'The ID for this card is invalid'
    } else {
      errorText = `There was an unknown error. ${cardResponse.status}: ${cardResponse.statusText}`
    }
    return {
      props: {
        id,
        fetchingErrorText: errorText,
        card
      }
    }
  }

  const argumentsResponse = await fetch(`${domain}/api/v1/cards/id/${id}/arguments`, {
    headers: { Authorization: `Bearer ${jwt}` }
  })
  let relatedArguments: ArgumentWithCardModel[] | null
  if (argumentsResponse.ok) {
    relatedArguments = (await argumentsResponse.json()) as ArgumentWithCardModel[]
  } else {
    errorText = `Error fetching related arguments. Status code: ${argumentsResponse.status}`
  }
  return {
    props: {
      id,
      fetchingErrorText: errorText,
      card,
      relatedArguments: relatedArguments || null
    }
  }
}

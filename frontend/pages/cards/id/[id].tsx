import { parse } from 'cookie'
import { useState } from 'react'
import { GetServerSideProps, NextPage } from 'next'
import DashboardSidebar from '../../../components/dash/DashboardSidebar'
import theme from '../../../components/utils/theme'
import useWindowSize from '../../../components/utils/hooks/useWindowSize'
import ErrorAlert from '../../../components/utils/ErrorAlert'
import redirectToLogin from '../../../components/utils/redirectToLogin'
import ResponseCard from '../../../components/types/ResponseCard'
import CardDisplay from '../../../components/cards/CardDisplay'
import ArgumentWithCardModel from '../../../components/types/ArgumentWithCardModel'
import CardArgumentsDisplay from '../../../components/cards/CardArgumentsDisplay'

interface Props {
  id?: string
  fetchingErrorText?: string
  card?: ResponseCard
  jwt?: string
  relatedArguments?: ArgumentWithCardModel[]
}

// todo show arguments (with hyperlink and the index that the card appears) that contain the card
// using GET /api/v1/cards/[id]/arguments
// todo don't url encode ids on the frontend anymore
const ViewCard: NextPage<Props> = ({
  fetchingErrorText, card, jwt, relatedArguments
}) => {
  const { width } = useWindowSize(1920, 1080)
  const [ errorText, setErrorText ] = useState('')

  return (
    <div
      style={{
        width: '100%', backgroundColor: theme.palette.lightBlue.main, minHeight: '100%', overflow: 'auto'
      }}
    >
      <DashboardSidebar windowWidth={width} pageName='Cards' />
      <div
        style={{
          width: '50%',
          margin: '10vh auto 5vh auto',
          backgroundColor: theme.palette.secondary.main,
          border: `1px solid ${theme.palette.lightGrey.main}`,
          borderRadius: 5,
          padding: '3vh 3vw'
        }}
      >
        {
          card && (
            <CardDisplay
              onError={setErrorText}
              card={card}
              jwt={jwt}
              windowWidth={width}
            />
          )
        }
      </div>
      {
        relatedArguments && (
          <CardArgumentsDisplay relatedArguments={relatedArguments} />
        )
      }
      {
        (fetchingErrorText || errorText) && <ErrorAlert disableClose text={fetchingErrorText || errorText} />
      }
    </div>
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

  let jwt: string | null = null
  if (req.headers?.cookie) {
    jwt = parse(req.headers?.cookie)?.jwt
  }
  if (!jwt) {
    redirectToLogin(res, `/cards/id/${id}`)
    return {
      props: {}
    }
  }
  const domain = process.env.NODE_ENV !== 'production' ? 'http://localhost' : 'https://cardtown.co'
  // todo do promise.all()
  const cardResponse = await fetch(`${domain}/api/v1/cards/id/${encodeURIComponent(id)}`, {
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
      redirectToLogin(res, `/cards/id/${encodeURIComponent(id)}`)
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
        card,
        jwt
      }
    }
  }

  const argumentsResponse = await fetch(`${domain}/api/v1/cards/id/${id}/arguments`, {
    headers: { Authorization: `Bearer ${jwt}` }
  })
  let relatedArguments: ArgumentWithCardModel[] | null
  if (argumentsResponse.ok) {
    relatedArguments = await argumentsResponse.json() as ArgumentWithCardModel[]
    console.log(relatedArguments)
  } else {
    errorText = `Error fetching related arguments. Status code: ${argumentsResponse.status}`
  }
  return {
    props: {
      id,
      fetchingErrorText: errorText,
      card,
      jwt,
      relatedArguments: relatedArguments || null
    }
  }
}

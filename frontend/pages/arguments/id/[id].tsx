import { parse } from 'cookie'
import { GetServerSideProps, NextPage } from 'next'
import { useState } from 'react'
import CardDisplay from '../../../components/cards/CardDisplay'
import DashboardSidebar from '../../../components/dash/DashboardSidebar'
import ResponseArgument from '../../../components/types/ResponseArgument'
import ErrorAlert from '../../../components/utils/ErrorAlert'
import useWindowSize from '../../../components/utils/hooks/useWindowSize'
import redirectToLogin from '../../../components/utils/redirectToLogin'
import theme from '../../../components/utils/theme'

interface Props {
  id?: string
  fetchingErrorText?: string
  argument?: ResponseArgument
  jwt?: string
}

const ViewArgument: NextPage<Props> = ({ fetchingErrorText, argument, jwt }) => {
  const [ errorText, setErrorText ] = useState('')
  const { width } = useWindowSize(1920, 1080)

  return (
    <div style={{
      width: '100%', backgroundColor: theme.palette.lightBlue.main, minHeight: '100%', overflow: 'auto'
    }}
    >
      <DashboardSidebar windowWidth={width} pageName='Arguments' />
      <div
        style={{
          width: '50%',
          margin: '10vh auto',
          backgroundColor: theme.palette.secondary.main,
          border: `1px solid ${theme.palette.lightGrey.main}`,
          borderRadius: 5,
          padding: '3vh 3vw'
        }}
      >
        {
          argument?.cards && argument.cards.map(card => (
            <CardDisplay
              card={card}
              jwt={jwt}
              onError={setErrorText}
              windowWidth={width}
              key={card.id}
            />
          ))
        }
      </div>
      {
        (fetchingErrorText || errorText) && <ErrorAlert disableClose text={fetchingErrorText || errorText} />
      }
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
  const dev = process.env.NODE_ENV !== 'production'
  const response = await fetch((dev ? 'http://localhost' : 'https://cardtown.co') + `/api/v1/arguments/id/${encodeURIComponent(id)}`, {
    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${jwt}` }
  })
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

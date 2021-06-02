import { FC, useContext, useEffect } from 'react'
import { Flex } from '@chakra-ui/react'
import { GetServerSideProps } from 'next'
import redirectToLogin from 'lib/redirectToLogin'
import DashboardSidebar from 'components/dash/DashboardSidebar'
import { TeamHeader } from 'types/team'
import DashActionFeed from 'components/dash/DashActionFeed'
import { ResponseAction } from 'types/action'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import DashboardPage from 'components/dash/DashboardPage'

interface Props {
  team?: TeamHeader
  fetchErrorText?: string
  actions?: ResponseAction[]
}

// todo improve responsiveness of sidebar etc
const Dash: FC<Props> = ({ team, fetchErrorText, actions }) => {
  const { setErrorMessage } = useContext(errorMessageContext)

  useEffect(() => {
    if (fetchErrorText) {
      setErrorMessage(fetchErrorText)
    }
  }, [])

  return (
    <DashboardPage>
      <Flex>
        <DashboardSidebar team={team} />
        <DashActionFeed actions={actions} />
      </Flex>
    </DashboardPage>
  )
}

export default Dash

export const getServerSideProps: GetServerSideProps<Props> = async ({ req, res }) => {
  const { jwt } = req.cookies
  if (!jwt) {
    redirectToLogin(res, '/dash')
    return {
      props: {}
    }
  }
  const domain = process.env.NODE_ENV !== 'production' ? 'http://localhost' : 'https://cardtown.co'
  const teamResponse = await fetch(`${domain}/api/v1/teams`, {
    headers: { Authorization: `Bearer ${jwt}` }
  })
  let teamHeader: TeamHeader | null
  if (teamResponse.ok && teamResponse.status !== 204) {
    // 204 means that user is not in a team
    teamHeader = (await teamResponse.json()) as TeamHeader
  } else if (!teamResponse.ok) {
    let errorText: string | null = null
    if (teamResponse.status === 500) {
      errorText = 'A server error occurred while getting data. Please try again'
    } else {
      errorText = `An error occurred white getting data. Status code: ${teamResponse.status}`
    }
    return {
      props: {
        fetchErrorText: errorText
      }
    }
  }

  const actionResponse = await fetch(`${domain}/api/v1/actions/recent`, {
    headers: { Authorization: `Bearer ${jwt}` }
  })
  let actions: ResponseAction[] | null
  if (actionResponse.ok) {
    actions = (await actionResponse.json()) as ResponseAction[]
  } else {
    let errorText: string | null = null
    if (actionResponse.status === 500) {
      errorText = 'A server error occurred while getting recent events. Please try again'
    } else {
      errorText = `An error occurred while getting recent events. Please try again. Status code: ${actionResponse.status}`
    }
    return {
      props: {
        fetchErrorText: errorText,
        team: teamHeader ?? null
      }
    }
  }
  return {
    props: {
      team: teamHeader ?? null,
      actions
    }
  }
}

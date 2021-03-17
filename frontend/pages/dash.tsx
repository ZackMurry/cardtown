import { FC } from 'react'
import DashboardNavbar from 'components/dash/DashboardNavbar'
import useWindowSize from 'lib/hooks/useWindowSize'
import theme from 'lib/theme'
import { GetServerSideProps } from 'next'
import redirectToLogin from 'lib/redirectToLogin'
import DashboardSidebar from 'components/dash/DashboardSidebar'
import TeamHeader from 'types/TeamHeader'
import ErrorAlert from 'components/utils/ErrorAlert'

interface Props {
  jwt?: string
  team?: TeamHeader
  fetchErrorText?: string
}

// todo wrap dash pages in a DashboardPage component instead of rewriting layout
const Dash: FC<Props> = ({ jwt, team, fetchErrorText }) => {
  const { width } = useWindowSize(1920, 1080)
  return (
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main }}>
      <DashboardNavbar pageName='Dashboard' windowWidth={width} jwt={jwt} />
      <DashboardSidebar team={team} />
      {
        fetchErrorText && <ErrorAlert text={fetchErrorText} disableClose />
      }
    </div>
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
  const response = await fetch(`${domain}/api/v1/teams`, {
    headers: { Authorization: `Bearer ${jwt}` }
  })
  if (response.status === 200) {
    const teamHeader = (await response.json()) as TeamHeader
    return {
      props: {
        jwt,
        team: teamHeader as TeamHeader
      }
    }
  }
  if (response.status === 204) {
    return {
      props: {
        jwt
      }
    }
  }
  let errorText: string | null = null
  if (response.status === 500) {
    errorText = 'A server error occurred during your request. Please try again'
  } else {
    errorText = `An error occurred during your request. Status code: ${response.status}`
  }
  return {
    props: {
      fetchErrorText: errorText,
      jwt
    }
  }
}

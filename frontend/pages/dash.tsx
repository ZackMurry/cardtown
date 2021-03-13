import { FC } from 'react'
import DashboardNavbar from 'components/dash/DashboardNavbar'
import useWindowSize from 'lib/hooks/useWindowSize'
import theme from 'lib/theme'
import { GetServerSideProps } from 'next'
import redirectToLogin from 'lib/redirectToLogin'

interface Props {
  jwt?: string
}

// todo wrap dash pages in a DashboardPage component instead of rewriting layout
const Dash: FC<Props> = ({ jwt }) => {
  const { width } = useWindowSize(1920, 1080)
  return (
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main }}>
      <DashboardNavbar pageName='Dashboard' windowWidth={width} jwt={jwt} />

      <div style={{ marginLeft: width >= theme.breakpoints.values.lg ? '12.9vw' : 0, paddingLeft: 38, paddingRight: 38 }} />

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
  return {
    props: {
      jwt
    }
  }
}

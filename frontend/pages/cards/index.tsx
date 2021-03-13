import { GetServerSideProps, NextPage } from 'next'
import DashboardNavbar from '../../components/dash/DashboardNavbar'
import useWindowSize from '../../components/utils/hooks/useWindowSize'
import redirectToLogin from '../../components/utils/redirectToLogin'
import ErrorAlert from '../../components/utils/ErrorAlert'

interface Props {
  jwt?: string
  cardCount?: number
  fetchErrorText?: string
}

const Cards: NextPage<Props> = ({ jwt, cardCount, fetchErrorText }) => {
  const { width } = useWindowSize(1920, 1080)

  return (
    <div style={{ width: '100%' }}>
      <DashboardNavbar windowWidth={width} pageName='Cards' jwt={jwt} />
      {
        fetchErrorText && <ErrorAlert disableClose text={fetchErrorText} />
      }
    </div>
  )
}

export default Cards

export const getServerSideProps: GetServerSideProps<Props> = async ({ req, res }) => {
  const jwt = req.cookies.jwt
  if (!jwt) {
    redirectToLogin(res, '/cards')
    return {
      props: {}
    }
  }

  const domain = process.env.NODE_ENV !== 'production' ? 'http://localhost:8080' : 'https://cardtown.co'
  const response = await fetch(domain + '/api/v1/cards/count', {
    headers: { Authorization: `Bearer ${jwt}` }
  })
  let cardCount: number | null = null
  if (response.ok) {
    cardCount = (await response.json()).count as number
    // cardCount = (await response.json()).count
  } else if (response.status === 401 || response.status === 403) {
    redirectToLogin(res, '/cards')
    return {
      props: {}
    }
  } else {
    return {
      props: {
        jwt,
        fetchErrorText: `Error fetching card count. Response status: ${response.status}`
      }
    }
  }

  return {
    props: {
      jwt,
      cardCount
    }
  }
}

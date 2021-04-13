import { GetServerSideProps, NextPage } from 'next'
import DashboardNavbar from 'components/dash/DashboardNavbar'
import redirectToLogin from 'lib/redirectToLogin'
import ErrorAlert from 'components/utils/ErrorAlert'

interface Props {
  cardCount?: number
  fetchErrorText?: string
}

const Cards: NextPage<Props> = ({ cardCount, fetchErrorText }) => (
  <div style={{ width: '100%' }}>
    <DashboardNavbar pageName='Cards' />
    {fetchErrorText && <ErrorAlert disableClose text={fetchErrorText} />}
  </div>
)

export default Cards

export const getServerSideProps: GetServerSideProps<Props> = async ({ req, res }) => {
  const { jwt } = req.cookies
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
        fetchErrorText: `Error fetching card count. Response status: ${response.status}`
      }
    }
  }

  return {
    props: {
      cardCount
    }
  }
}

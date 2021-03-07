import { GetServerSideProps, NextPage } from 'next'
import { parse } from 'cookie'
import {
  Grid, GridItem, Heading, Text
} from '@chakra-ui/react'
import DashboardSidebar from '../../components/dash/DashboardSidebar'
import theme from '../../components/utils/theme'
import CardCount from '../../components/cards/CardCount'
import NewCard from '../../components/cards/NewCard'
import ImportCard from '../../components/cards/ImportCard'
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
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main }}>
      <DashboardSidebar windowWidth={width} pageName='Cards' />
      <div style={{ marginLeft: width >= theme.breakpoints.values.lg ? '12.9vw' : 0, paddingLeft: 38, paddingRight: 38 }}>
        <Text
          color='darkGray'
          textTransform='uppercase'
          fontSize={11}
          paddingTop={19}
          letterSpacing={0.5}
        >
          Overview
        </Text>
        <Heading as='h2' fontSize={24} fontWeight='bold'>
          Cards
        </Heading>
        <div
          style={{
            width: '100%', margin: '2vh 0', height: 1, backgroundColor: theme.palette.lightGrey.main
          }}
        />
        <Grid gap={4} templateColumns='repeat(6, 1fr)'>
          <GridItem
            borderRadius={10}
            backgroundColor='white'
            border={`1px solid ${theme.palette.lightGrey.main}`}
            minHeight='7.5vh'
            display='flex'
            padding={5}
            alignItems='center'
          >
            <CardCount count={cardCount} />
          </GridItem>

          <GridItem
            borderRadius={10}
            backgroundColor='white'
            border={`1px solid ${theme.palette.lightGrey.main}`}
            minHeight='7.5vh'
            display='flex'
            padding={5}
            alignItems='center'
          >
            <NewCard />
          </GridItem>

          <GridItem
            borderRadius={10}
            backgroundColor='white'
            border={`1px solid ${theme.palette.lightGrey.main}`}
            minHeight='7.5vh'
            display='flex'
            padding={5}
            alignItems='center'
          >
            <ImportCard />
          </GridItem>

        </Grid>
      </div>
      {
        fetchErrorText && <ErrorAlert disableClose text={fetchErrorText} />
      }
    </div>
  )
}

export default Cards

export const getServerSideProps: GetServerSideProps<Props> = async ({ req, res }) => {
  let jwt: string | null = null
  if (req.headers?.cookie) {
    jwt = parse(req.headers?.cookie)?.jwt
  }
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

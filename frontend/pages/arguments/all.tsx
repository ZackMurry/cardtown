import Link from 'next/link'
import { Grid, Typography } from '@material-ui/core'
import { parse } from 'cookie'
import { GetServerSideProps, NextPage } from 'next'
import { useRouter } from 'next/router'
import { useState } from 'react'
import DashboardSidebar from '../../components/dash/DashboardSidebar'
import ArgumentPreview from '../../components/types/ArgumentPreview'
import BlackText from '../../components/utils/BlackText'
import ErrorAlert from '../../components/utils/ErrorAlert'
import useWindowSize from '../../components/utils/hooks/useWindowSize'
import redirectToLogin from '../../components/utils/redirectToLogin'
import theme from '../../components/utils/theme'

interface Props {
  jwt?: string
  args?: ArgumentPreview[]
  fetchErrorText?: string
}

const AllArguments: NextPage<Props> = ({ jwt, args: initialArgs, fetchErrorText }) => {
  const [ args, setArgs ] = useState(initialArgs)
  const { width } = useWindowSize(1920, 1080)
  const router = useRouter()

  return (
    <div
      style={{
        width: '100%',
        backgroundColor: theme.palette.lightBlue.main,
        minHeight: '100%',
        overflow: 'auto'
      }}
    >
      <DashboardSidebar windowWidth={width} pageName='Arguments' />
      <div style={{ marginLeft: width >= theme.breakpoints.values.lg ? '12.9vw' : 0, paddingLeft: 38, paddingRight: 38 }}>

        <Typography
          style={{
            color: theme.palette.darkGrey.main,
            textTransform: 'uppercase',
            fontSize: 11,
            marginTop: 19,
            letterSpacing: 0.5
          }}
        >
          All
        </Typography>
        <BlackText style={{ fontSize: 24, fontWeight: 'bold' }}>
          All arguments
        </BlackText>
        <div
          style={{
            width: '100%', margin: '2vh 0', height: 1, backgroundColor: theme.palette.lightGrey.main
          }}
        />
        <div style={{ width: '100%', display: 'flex', justifyContent: 'flex-end' }}>
          {/* <SearchCards
            cards={initialCards}
            onResults={setCards}
            onClear={() => setCards(initialCards)}
            windowWidth={width}
          /> */}
        </div>
        {
          width >= theme.breakpoints.values.lg && (
            <Grid container>
              <Grid item lg={9} style={{ paddingLeft: 20 }}>
                <BlackText style={{ fontWeight: 500 }}>
                  Name
                </BlackText>
              </Grid>
              <Grid item lg={3} style={{ marginLeft: -10 }}>
                <BlackText style={{ fontWeight: 500 }}>
                  Number of cards
                </BlackText>
              </Grid>
            </Grid>
          )
        }

        {/* todo show information about the owner and make this expandable so that users can see the card tags and click on indivual cards */}
        {
          args.map(({ id, name, cardCount }) => (
            <Link href={`/arguments/id/${id}`} passHref key={id}>
              <a>
                <Grid
                  container
                  style={{
                    backgroundColor: theme.palette.secondary.main,
                    padding: 20,
                    border: `1px solid ${theme.palette.lightGrey.main}`,
                    borderRadius: 5,
                    margin: '15px 0',
                    cursor: 'pointer'
                  }}
                >
                  <Grid item xs={12} lg={9}>
                    {name}
                  </Grid>
                  <Grid item xs={12} lg={3}>
                    {`${cardCount} `}
                    cards
                  </Grid>
                </Grid>
              </a>
            </Link>
          ))
        }
      </div>
      {
        fetchErrorText && <ErrorAlert text={fetchErrorText} disableClose />
      }
    </div>

  )
}

export default AllArguments

export const getServerSideProps: GetServerSideProps<Props> = async ({ req, res }) => {
  let jwt: string | null
  if (req.headers?.cookie) {
    jwt = parse(req.headers?.cookie)?.jwt
  }
  if (!jwt) {
    redirectToLogin(res, '/arguments/all')
    return {
      props: {}
    }
  }

  const domain = process.env.NODE_ENV !== 'production' ? 'http://localhost' : 'https://cardtown.co'
  const response = await fetch(`${domain}/api/v1/arguments`, {
    headers: { Authorization: `Bearer ${jwt}` }
  })
  if (response.ok) {
    const args = await response.json() as ArgumentPreview[]
    return {
      props: {
        jwt,
        args
      }
    }
  }
  let fetchErrorText: string | null
  if (response.status === 400) {
    fetchErrorText = 'Error fetching arguments'
  } else if (response.status === 401 || response.status === 403) {
    redirectToLogin(res, '/arguments/all')
    return {
      props: {
        jwt
      }
    }
  } else if (response.status === 500) {
    fetchErrorText = 'A server error occured during your request. Please try again'
  } else {
    fetchErrorText = `An unknown error occured during your request. Status code: ${response.status}`
  }
  return {
    props: {
      jwt,
      fetchErrorText
    }
  }
}

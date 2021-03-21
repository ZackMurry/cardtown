import { GetServerSideProps, NextPage } from 'next'
import Link from 'next/link'
import { Grid, Typography } from '@material-ui/core'
import redirectToLogin from 'lib/redirectToLogin'
import useWindowSize from 'lib/hooks/useWindowSize'
import DashboardNavbar from 'components/dash/DashboardNavbar'
import theme from 'lib/theme'
import BlackText from 'components/utils/BlackText'
import NewArgument from 'components/arguments/NewArgument'
import { useContext, useEffect } from 'react'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'

interface Props {
  fetchErrorText?: string
  argCount?: number
}

const ArgumentsPage: NextPage<Props> = ({ fetchErrorText, argCount }) => {
  const { width } = useWindowSize(1920, 1080)
  const { setErrorMessage } = useContext(errorMessageContext)

  useEffect(() => {
    if (fetchErrorText) {
      setErrorMessage(fetchErrorText)
    }
  }, [])

  return (
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main }}>
      <DashboardNavbar windowWidth={width} pageName='Arguments' />
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
          Overview
        </Typography>
        <BlackText style={{ fontSize: 24, fontWeight: 'bold' }}>Arguments</BlackText>
        <div
          style={{
            width: '100%',
            margin: '2vh 0',
            height: 1,
            backgroundColor: theme.palette.lightGrey.main
          }}
        />
        <Grid container className='dash-grid' role='grid' style={{ width: '80%' }}>
          <Grid
            item
            xs={12}
            md={6}
            lg={3}
            style={{
              borderRadius: 10,
              backgroundColor: theme.palette.secondary.main,
              border: `1px solid ${theme.palette.lightGrey.main}`,
              minHeight: '7.5vh',
              padding: 20,
              display: 'flex',
              alignItems: 'center'
            }}
          >
            <Link href='/arguments/all'>
              <a
                href='/arguments/all'
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  width: '100%',
                  height: '100%'
                }}
              >
                <Typography variant='h5' style={{ fontSize: 22, color: theme.palette.blueBlack.main }}>
                  <span style={{ fontWeight: 500, paddingRight: 5 }}>{argCount}</span>
                  argument
                  {argCount !== 1 ? 's' : ''}
                </Typography>
                <div style={{ marginRight: 10 }}>
                  <div
                    style={{
                      borderRadius: 5,
                      border: `3px solid ${theme.palette.text.secondary}`,
                      width: '1.75vw',
                      minWidth: 20,
                      height: '2.5vh',
                      minHeight: 25
                    }}
                  />
                  <div
                    style={{
                      marginLeft: 10,
                      marginTop: -15,
                      borderRadius: 5,
                      border: `3px solid ${theme.palette.text.secondary}`,
                      width: '1.75vw',
                      minWidth: 20,
                      height: '2.5vh',
                      minHeight: 25
                    }}
                  />
                </div>
              </a>
            </Link>
          </Grid>
          <Grid
            item
            xs={12}
            md={6}
            lg={3}
            style={{
              borderRadius: 10,
              backgroundColor: theme.palette.secondary.main,
              border: `1px solid ${theme.palette.lightGrey.main}`,
              minHeight: '7.5vh',
              display: 'flex',
              alignItems: 'center',
              padding: 20
            }}
          >
            <NewArgument />
          </Grid>
        </Grid>
      </div>
    </div>
  )
}

export default ArgumentsPage

export const getServerSideProps: GetServerSideProps<Props> = async ({ req, res }) => {
  const { jwt } = req.cookies
  if (!jwt) {
    redirectToLogin(res, '/arguments')
    return {
      props: {}
    }
  }
  let argCount: number | null = null
  const domain = process.env.NODE_ENV !== 'production' ? 'http://localhost' : 'https://cardtown.co'
  const response = await fetch(domain + '/api/v1/arguments/count', {
    headers: { Authorization: `Bearer ${jwt}` }
  })
  if (response.ok) {
    argCount = (await response.json())?.count
  } else {
    return {
      props: {
        fetchErrorText: `Error fetching arguments. Status code: ${response.status}`
      }
    }
  }
  return {
    props: {
      argCount
    }
  }
}

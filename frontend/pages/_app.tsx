import { FC, useEffect, useMemo } from 'react'
import Head from 'next/head'
import { ThemeProvider } from '@material-ui/core/styles'
import { ChakraProvider } from '@chakra-ui/react'
import theme from 'lib/theme'
import chakraTheme from 'lib/chakraTheme'
import 'styles/globals.css' //todo move some styles into .module.css files
import userContext from 'lib/hooks/UserContext'
import parseJwt from 'lib/parseJwt'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import useErrorMessage from 'lib/hooks/useErrorMessage'
import ErrorAlert from 'components/utils/ErrorAlert'
import { NextPage } from 'next'
import { parse } from 'cookie'
import Cookies from 'js-cookie'

interface Props {
  Component?: React.ComponentType
  pageProps?: any
  jwt?: string
}

// from https://github.com/mui-org/material-ui/blob/master/examples/nextjs/pages/_app.js
// adds mui theme
// todo add error page (404)
const App: NextPage<Props> = ({ Component, pageProps, jwt }) => {
  const userModel = useMemo(() => ({ ...parseJwt(jwt), jwt }), [])
  const errorMessage = useErrorMessage()

  useEffect(() => {
    // Remove the server-side injected CSS.
    const jssStyles = document.querySelector('#jss-server-side')
    if (jssStyles) {
      jssStyles.parentElement.removeChild(jssStyles)
    }
  }, [])

  return (
    <>
      <Head>
        <title>cardtown</title>
        <meta name='viewport' content='minimum-scale=1, initial-scale=1, width=device-width' />
      </Head>
      <ChakraProvider theme={chakraTheme}>
        <ThemeProvider theme={theme}>
          <userContext.Provider value={userModel}>
            <errorMessageContext.Provider value={errorMessage}>
              <Component {...pageProps} />
            </errorMessageContext.Provider>
          </userContext.Provider>
        </ThemeProvider>
      </ChakraProvider>
      {errorMessage?.errorMessage && (
        <ErrorAlert text={errorMessage.errorMessage} onClose={() => errorMessage.setErrorMessage(null)} />
      )}
    </>
  )
}

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
// For some reason, this only works when I access { ctx } from the params, which makes zero sense
// because the type for this fn says that ctx isn't an available child
App.getInitialProps = async ({ ctx }) => {
  const { req } = ctx
  const jwt = req ? parse(req.headers.cookie)?.jwt : Cookies.get('jwt')
  return {
    jwt
  }
}

export default App

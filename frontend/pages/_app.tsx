import { useEffect, useMemo } from 'react'
import Head from 'next/head'
import { ThemeProvider } from '@material-ui/core/styles'
import { ChakraProvider, localStorageManager } from '@chakra-ui/react'
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
import secureCookieStorageManager from 'lib/secureCookieStorageManager'

interface Props {
  Component?: React.ComponentType
  pageProps?: any
  jwt?: string
  id?: string
  cookies: any
}

// from https://github.com/mui-org/material-ui/blob/master/examples/nextjs/pages/_app.js
// todo add error page (404)
// todo store userContext info in a cookie instead of in the jwt
const App: NextPage<Props> = ({ Component, pageProps, jwt, cookies, id }) => {
  const userModel = useMemo(() => ({ ...parseJwt(jwt), jwt, id }), [jwt])
  const errorMessage = useErrorMessage()
  const colorModeManager = typeof cookies === 'string' ? secureCookieStorageManager(cookies) : localStorageManager

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
      <ChakraProvider theme={chakraTheme} colorModeManager={colorModeManager}>
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
  if (req) {
    const parsed = parse(req.headers?.cookie ?? '')
    return {
      jwt: parsed?.jwt,
      id: parsed?.id,
      cookies: req.headers?.cookie ?? ''
    }
  }
  return {
    jwt: Cookies.get('jwt') ?? undefined,
    id: Cookies.get('id') ?? undefined,
    cookies: ''
  }
}

export default App

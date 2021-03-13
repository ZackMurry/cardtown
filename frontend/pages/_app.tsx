import { FC, useEffect } from 'react'
import Head from 'next/head'
import { ThemeProvider } from '@material-ui/core/styles'
import CssBaseline from '@material-ui/core/CssBaseline'
import { ChakraProvider } from '@chakra-ui/react'
import theme from 'lib/theme'
import chakraTheme from 'lib/chakraTheme'
import 'styles/globals.css' //todo move some styles into .module.css files

interface Props {
  Component: React.ComponentType
  pageProps: any
}

// from https://github.com/mui-org/material-ui/blob/master/examples/nextjs/pages/_app.js
// adds mui theme
// todo add error page (404)
const App: FC<Props> = ({ Component, pageProps }) => {
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
            {/* CssBaseline kickstart an elegant, consistent, and simple baseline to build upon. */}
            {/* <CssBaseline /> */}
            <Component {...pageProps} />
        </ThemeProvider>
      </ChakraProvider>
    </>
  )
}

export default App

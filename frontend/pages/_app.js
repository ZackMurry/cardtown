import { useEffect } from 'react'
import PropTypes from 'prop-types'
import Head from 'next/head'
import { ThemeProvider } from '@material-ui/core/styles'
import CssBaseline from '@material-ui/core/CssBaseline'
import theme from '../components/utils/theme'
import '../styles/globals.css' //todo move some styles into .module.css files

//from https://github.com/mui-org/material-ui/blob/master/examples/nextjs/pages/_app.js
//adds mui theme
//todo add error page (404)
//change 'display papers' to have a minWidth of 500 so that they become bigger on smaller screens
export default function App(props) {
  const { Component, pageProps } = props

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
    <ThemeProvider theme={theme}>
        {/* CssBaseline kickstart an elegant, consistent, and simple baseline to build upon. */}
        <CssBaseline />
        <Component {...pageProps} />
    </ThemeProvider>
    </>
  )
}

App.propTypes = {
  Component: PropTypes.elementType.isRequired,
  // eslint-disable-next-line react/forbid-prop-types
  pageProps: PropTypes.object.isRequired
}

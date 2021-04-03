import { extendTheme } from '@chakra-ui/react'

const chakraTheme = extendTheme({
  colors: {
    lightBlue: '#869ab8',
    black: '#2e3032',
    darkGray: '#6e84a3',
    darkBlue: '#5b8ae9',
    lightGray: '#e3ebf6',
    cardtownBlue: '#3377ff',
    darkGrayHover: '#8ba3c4',
    offWhite: '#f6f8fa',
    offWhiteAccent: 'f9f9f9',
    grayBorder: '#eaecef',
    offBlack: '#12161c',
    offBlackAccent: '#161b22',
    darkElevated: '#1b2129',
    darkGrayBorder: '#21252B'
  },
  fonts: {
    heading: 'Ubuntu, Roboto',
    body: 'Ubuntu, Roboto'
  },
  styles: {
    global: {
      'html, body': {
        backgroundColor: '#f6f8fa'
      }
    }
  },
  config: {
    initialColorMode: 'light',
    useSystemColorMode: false
  }
})

export default chakraTheme

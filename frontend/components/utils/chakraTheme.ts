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
    offWhite: '#f6f8fa'
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
  }
})

export default chakraTheme

import { createMuiTheme } from '@material-ui/core/styles'
import { red } from '@material-ui/core/colors'

// Create a theme instance.
const theme = createMuiTheme({
  palette: {
    primary: {
      main: '#3377FF'
    },
    secondary: {
      main: '#FEFEFE'
    },
    error: {
      main: red.A400
    },
    background: {
      default: '#FEFEFE'
    },
    action: {
      disabled: '#2d323e'
    },
    text: {
      primary: '#2E3032',
      secondary: '#869AB8'
    }
  }
})

const colors = {
  lightBlue: {
    main: '#f9fbfd'
  },
  darkGrey: {
    main: '#6e84a3'
  },
  lightGrey: {
    main: '#e3ebf6',
    translucent: 'rgba(227, 235, 246, 0.5)'
  },
  darkBlue: {
    main: '#5B8AE9'
  },
  blueBlack: {
    main: '#12263F'
  },
  black: {
    main: '#2E3032'
  }
}

export default {
  ...theme,
  palette: {
    ...theme.palette,
    ...colors
  }
}

import { createMuiTheme } from '@material-ui/core/styles'
import { red } from '@material-ui/core/colors'

// Create a theme instance.
const theme = createMuiTheme({
  palette: {
    primary: {
      main: '#FEFEFE'
    },
    secondary: {
      main: '#3377FF'
    },
    black: {
      main: '#2E3032'
    },
    error: {
      main: red.A400
    },
    background: {
      default: '#FEFEFE'
    },
    action: {
      disabled: '#2d323e'
    }
  }
})

export default theme

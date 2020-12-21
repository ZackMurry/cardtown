import { Typography } from '@material-ui/core'
import theme from './theme'

/**
 * a simple wrapper for MuiTypography that makes it have theme.palette.black.main as its color.
 * it's not actually black, but off-black.
 */
export default function BlackText({ style, ...props }) {
  return <Typography {...props} style={{ ...style, color: theme.palette.black.main }} />
}

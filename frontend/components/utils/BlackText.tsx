import { Typography } from '@material-ui/core'
import { CSSProperties, FC } from 'react'
import theme from '../../lib/theme'

interface Props {
  style?: CSSProperties
  variant?: 'inherit' | 'h1' | 'h2' | 'h3' | 'h4' | 'h5' | 'h6'
  | 'subtitle1' | 'subtitle2' | 'body1' | 'body2' | 'caption' | 'button' | 'overline' | 'srOnly'

}

/**
 * a simple wrapper for MuiTypography that makes it have theme.palette.black.main as its color.
 * it's not actually black, but off-black.
 */
const BlackText: FC<Props> = ({ variant, style, children }) => (
  <Typography variant={variant ?? undefined} style={{ ...style, color: theme.palette.black.main }}>
    {children}
  </Typography>
)

export default BlackText

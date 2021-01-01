import { Typography } from '@material-ui/core'
import theme from '../../utils/theme'

export default function LandingPageMobileNav() {
  return (
    <div
      style={{
        height: '20vh',
        width: '100%',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center'
      }}
    >
      <Typography
        variant='h5'
        style={{
          textTransform: 'uppercase',
          color: theme.palette.darkGrey.main,
          fontSize: 18,
          textAlign: 'center'
        }}
      >
        Cardtown
      </Typography>
    </div>
  )
}

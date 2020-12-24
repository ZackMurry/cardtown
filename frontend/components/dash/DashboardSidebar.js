import { Typography } from '@material-ui/core'
import BlackText from '../utils/BlackText'
import theme from '../utils/theme'

export default function DashboardSidebar({ pageName }) {
  return (
    <div
      style={{
        position: 'fixed',
        height: '100vh',
        width: '12.9vw',
        left: 0,
        top: 0,
        backgroundColor: theme.palette.secondary.main,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        borderRight: `1px solid ${theme.palette.lightGrey.main}`
      }}
    >
      <BlackText variant='h3' style={{ fontSize: 28, fontWeight: 500, padding: '3vh 0', width: '50%' }}>
        card
        <span style={{ color: theme.palette.primary.main }}>
          town
        </span>
      </BlackText>
      <div style={{ width: '50%' }}>
        <Typography
          style={{
            color: pageName === 'Dashboard' ? theme.palette.darkBlue.main : theme.palette.darkGrey.main,
            fontSize: 16,
            margin: '12.5px 0'
          }}
        >
          Dashboard
        </Typography>
        <Typography
          style={{
            color: pageName === 'Cards' ? theme.palette.darkBlue.main : theme.palette.darkGrey.main,
            fontSize: 16,
            margin: '12.5px 0'
          }}
        >
          Cards
        </Typography>
        <Typography
          style={{
            color: pageName === 'Arguments' ? theme.palette.darkBlue.main : theme.palette.darkGrey.main,
            fontSize: 16,
            margin: '12.5px 0'
          }}
        >
          Arguments
        </Typography>
        <Typography
          style={{
            color: pageName === 'Speeches' ? theme.palette.darkBlue.main : theme.palette.darkGrey.main,
            fontSize: 16,
            margin: '12.5px 0'
          }}
        >
          Speeches
        </Typography>
        <Typography
          style={{
            color: pageName === 'Rounds' ? theme.palette.darkBlue.main : theme.palette.darkGrey.main,
            fontSize: 16,
            margin: '12.5px 0'
          }}
        >
          Rounds
        </Typography>
      </div>
    </div>
  )
}

import { Typography } from '@material-ui/core'
import Link from 'next/link'
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
      role='navigation'
    >
      <BlackText variant='h3' style={{ fontSize: 28, fontWeight: 500, padding: '3vh 0', width: '50%' }}>
        card
        <span style={{ color: theme.palette.primary.main }}>
          town
        </span>
      </BlackText>
      <div style={{ width: '50%' }}>
        <Link href='/dash' passHref>
          <a>
            <Typography
              style={{
                color: pageName === 'Dashboard' ? theme.palette.darkBlue.main : theme.palette.darkGrey.main,
                fontSize: 16,
                margin: '12.5px 0'
              }}
            >
              Dashboard
            </Typography>
          </a>
        </Link>
        
        <Link href='/cards' passHref>
          <a>
            <Typography
              style={{
                color: pageName === 'Cards' ? theme.palette.darkBlue.main : theme.palette.darkGrey.main,
                fontSize: 16,
                margin: '12.5px 0'
              }}
            >
              Cards
            </Typography>
          </a>
        </Link>
        
        <Link href='/arguments' passHref>
          <a>
            <Typography
              style={{
                color: pageName === 'Arguments' ? theme.palette.darkBlue.main : theme.palette.darkGrey.main,
                fontSize: 16,
                margin: '12.5px 0'
              }}
            >
              Arguments
            </Typography>
          </a>
        </Link>
        
        <Link href='/speeches' passHref>
          <a>
            <Typography
              style={{
                color: pageName === 'Speeches' ? theme.palette.darkBlue.main : theme.palette.darkGrey.main,
                fontSize: 16,
                margin: '12.5px 0'
              }}
            >
              Speeches
            </Typography>
          </a>
        </Link>
        
        <Link href='/rounds'>
          <a>
            <Typography
              style={{
                color: pageName === 'Rounds' ? theme.palette.darkBlue.main : theme.palette.darkGrey.main,
                fontSize: 16,
                margin: '12.5px 0'
              }}
            >
              Rounds
            </Typography>
          </a>
        </Link>
      </div>
    </div>
  )
}

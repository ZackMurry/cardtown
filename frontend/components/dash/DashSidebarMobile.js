import { Collapse, IconButton, Typography } from '@material-ui/core'
import MenuIcon from '@material-ui/icons/Menu'
import Link from 'next/link'
import { useState } from 'react'
import BlackText from '../utils/BlackText'
import theme from '../utils/theme'

export default function DashSidebarMobile({ pageName }) {
  const [ isExpanded, setExpanded ] = useState(false)

  return (
    <div>
      <div
        style={{
          height: '8vh',
          padding: '12.5px 25px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          borderBottom: `1px solid ${theme.palette.lightGrey.main}`,
          backgroundColor: theme.palette.secondary.main
        }}
      >
        <IconButton onClick={() => setExpanded(!isExpanded)}>
          <MenuIcon style={{ width: 35, height: 35 }} />
        </IconButton>
        <div style={{ marginRight: 10 }}>
          <BlackText variant='h3' style={{ fontSize: 22, fontWeight: 500, padding: '3vh 0', width: '50%' }}>
            card
            <span style={{ color: theme.palette.primary.main }}>
              town
            </span>
          </BlackText>
        </div>
      </div>
      <Collapse
        in={isExpanded}
        style={{
          backgroundColor: theme.palette.secondary.main,
          borderBottom: `1px solid ${theme.palette.lightGrey.main}`
        }}
      >
        <div style={{ width: '80%', margin: '0 auto' }}>
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
      </Collapse>
    </div>
  )
}

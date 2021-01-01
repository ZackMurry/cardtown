import Link from 'next/link'
import { Button, Typography } from '@material-ui/core'
import theme from '../utils/theme'
import BlackText from '../utils/BlackText'

export default function LandingPageNavbar() {
  return (
    <>
      {/* for padding purposes */}
      <div style={{ height: '10vh' }} />
      <div
        style={{
          position: 'absolute',
          top: 0,
          left: 0,
          backgroundColor: theme.palette.secondary.main,
          display: 'flex',
          justifyContent: 'space-between',
          padding: '31px 15vw',
          width: '100%',
          alignItems: 'center',
          height: '10vh'
        }}
      >
        <BlackText variant='h3' style={{ fontSize: 28, fontWeight: 500, paddingBottom: 5 }}>
          card
          <span style={{ color: theme.palette.primary.main }}>
            town
          </span>
        </BlackText>
        <div
          style={{
            display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: 349
          }}
        >
          <Link href='/features'>
            <a href='/features'>
              <BlackText variant='h4' style={{ fontSize: 20, fontWeight: 300 }}>
                Features
              </BlackText>
            </a>
          </Link>

          <Link href='/about'>
            <a href='/about'>
              <BlackText variant='h4' style={{ fontSize: 20, fontWeight: 300 }}>
                About
              </BlackText>
            </a>
          </Link>

          <Button
            variant='outlined'
            color='primary'
            style={{ textTransform: 'none', border: '2px solid' }}
          >
            <Link href='/login'>
              <a href='/login'>
                <Typography variant='h4' style={{ fontSize: 20, padding: '5px 5px' }}>
                  Login
                </Typography>
              </a>
            </Link>
          </Button>
        </div>
      </div>
    </>
  )
}

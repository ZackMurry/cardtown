import Link from 'next/link'
import { useState } from 'react'
import theme from '../utils/theme'
import BlackText from '../utils/BlackText'
import { Typography } from '@material-ui/core'

export default function LandingPageNavbar() {

  const [ hoveringOverLogin, setHoveringOverLogin ] = useState(false)

  return (
    // navbar
    // todo might not want to be position: fixed (just absolute)
    <>
      {/* for padding purposes */}
      <div style={{ height: '10vh' }} />
      <div
        style={{
          position: 'fixed',
          top: 0,
          left: 0,
          backgroundColor: theme.palette.primary.main,
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
          <span style={{ color: theme.palette.secondary.main }}>
            town
          </span>
        </BlackText>
        <div
          style={{
            display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: 349
          }}
        >
          <Link href='/about'>
            <a href='/about'>
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
          
          {/* todo make this look better (probly use an MUI outlined button) */}
          <Link href='/login'>
            <a href='/login'>
              <div
                style={{
                  border: `2px solid ${theme.palette.secondary.main}`,
                  borderRadius: 10,
                  padding: '7.5px 15px',
                  color: hoveringOverLogin ? 'white' : theme.palette.secondary.main,
                  backgroundColor: hoveringOverLogin ? theme.palette.secondary.main : undefined,
                  transition: 'background-color color 0.2s ease-in-out',
                  cursor: 'pointer'
                }}
                onMouseEnter={() => setHoveringOverLogin(true)}
                onMouseLeave={() => setHoveringOverLogin(false)}
              >
                <Typography variant='h4' style={{ fontSize: 20 }}>
                  Login
                </Typography>
              </div>
            </a>
          </Link>
        </div>
      </div>
    </>
  )
}

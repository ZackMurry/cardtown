import { Button, Typography } from '@material-ui/core'
import { useState } from 'react'
import theme from '../utils/theme'

export default function LandingPageJoinBeta() {
  const [ email, setEmail ] = useState('')
  const [ invalid, setInvalid ] = useState(false)

  const handleSubmit = e => {
    e.preventDefault()
    setInvalid(false)
    console.log(email)
  }

  // todo validation message
  const handleInvalid = e => {
    e.preventDefault()
    setInvalid(true)
  }

  return (
    <>
      <form onSubmit={handleSubmit} onInvalid={handleInvalid} style={{ display: 'flex' }}>
        <input
          type='email'
          placeholder='Email address'
          style={{
            outline: 'none',
            padding: 15,
            fontSize: 16,
            border: '2px solid #CBCEDA',
            borderRadius: '10px 0 0 10px',
            color: theme.palette.black.main
          }}
          value={email}
          onChange={e => setEmail(e.target.value)}
        />
        <Button
          type='submit'
          variant='contained'
          color='secondary'
          style={{
            borderRadius: '0 10px 10px 0',
            boxShadow: 'none',
            textTransform: 'none'
          }}
        >
          <Typography variant='h5' style={{ fontWeight: 500, fontSize: 18 }}>
            Join the beta
          </Typography>
        </Button>
      </form>
      {/* todo maybe make invalid look a bit better */}
      {
        invalid && (
          <Typography
            style={{
              color: theme.palette.error.main, textAlign: 'center', fontWeight: 500, margin: 10
            }}
          >
            This isn't a valid email address.
          </Typography>
        )
      }
    </>
  )
}

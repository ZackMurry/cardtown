import {
  Button,
  IconButton, InputAdornment, TextField, Typography
} from '@material-ui/core'
import VisibilityOffIcon from '@material-ui/icons/VisibilityOff'
import VisibilityIcon from '@material-ui/icons/Visibility'
import { useState } from 'react'
import Link from 'next/link'
import { useRouter } from 'next/router'
import Cookie from 'js-cookie'
import BlackText from '../components/utils/BlackText'
import ToggleIcon from '../components/utils/ToggleIcon'
import ErrorAlert from '../components/utils/ErrorAlert'
import theme from '../components/utils/theme'

export default function Login({ redirect }) {
  const [ email, setEmail ] = useState('')
  const [ password, setPassword ] = useState('')

  const [ showPassword, setShowPassword ] = useState(false)
  const [ errorText, setErrorText ] = useState('')

  const router = useRouter()

  const handleSubmit = async e => {
    e.preventDefault()

    const response = await fetch('/api/v1/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email,
        password
      })
    })

    // 400: no account found
    // 400: bad password
    if (response.status < 400) {
      const json = await response.json()
      Cookie.set('jwt', json.jwt)
      router.push(redirect || '/dash')
    } else if (response.status === 400) {
      // todo resetting passwords
      setErrorText('Incorrect email and/or password.')
    } else if (response.status === 404) {
      setErrorText('There was an error communicating with the server. Please try again later.')
    } else {
      setErrorText('There was an unknown error. Status code: ' + response.status)
    }
  }

  return (
    <div
      style={{
        display: 'flex', justifyContent: 'center', alignItems: 'center', width: '100%', height: '90vh'
      }}
    >
      <div style={{
        width: '25%', minWidth: 400, display: 'flex', flexDirection: 'column', alignItems: 'center'
      }}
      >
        <BlackText variant='h3' style={{ fontSize: 42, textAlign: 'center' }}>
          Sign in
        </BlackText>
        <Typography color='textSecondary' style={{ fontSize: 18, marginTop: 5 }}>
          Simplify your debate experience
        </Typography>
        <form style={{ width: '62.5%', marginTop: 20 }} onSubmit={handleSubmit}>
          <TextField
            type='text'
            label='Email address'
            value={email}
            onChange={e => setEmail(e.target.value)}
            variant='outlined'
            style={{ width: '100%', marginBottom: 10 }}
          />
          <TextField
            type={showPassword ? 'text' : 'password'}
            label='Password'
            value={password}
            onChange={e => setPassword(e.target.value)}
            variant='outlined'
            style={{ width: '100%', margin: '10px 0' }}
            InputProps={{
              endAdornment: (
                <InputAdornment position='end'>
                  <IconButton onClick={() => setShowPassword(!showPassword)} style={{ padding: 0 }}>
                    <ToggleIcon on={showPassword} onIcon={<VisibilityIcon />} offIcon={<VisibilityOffIcon />} timeout={250} />
                  </IconButton>
                </InputAdornment>
              )
            }}
          />
          <Button
            type='submit'
            variant='contained'
            color='primary'
            style={{
              width: '100%', height: 50, marginTop: 15, textTransform: 'none'
            }}
          >
            <Typography variant='h5' style={{ fontWeight: 500, fontSize: 18 }}>
              Log in
            </Typography>
          </Button>
          {
            errorText && <ErrorAlert text={errorText} onClose={() => setErrorText('')} />
          }
        </form>
        <Typography color='textSecondary' variant='h6' style={{ fontSize: 14, marginTop: 20 }}>
          Don't have an account?
          <Link href='/signup'>
            <a href='/signup' style={{ marginLeft: 2, color: theme.palette.primary.main }}>
              Sign up
            </a>
          </Link>
          .
        </Typography>
      </div>
    </div>
  )
}

export async function getServerSideProps({ query }) {
  return {
    props: {
      redirect: query?.redirect || null
    }
  }
}

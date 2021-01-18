import {
  Button,
  IconButton, InputAdornment, TextField, Typography
} from '@material-ui/core'
import VisibilityOffIcon from '@material-ui/icons/VisibilityOff'
import VisibilityIcon from '@material-ui/icons/Visibility'
import { FormEvent, useState } from 'react'
import Link from 'next/link'
import { useRouter } from 'next/router'
import Cookie from 'js-cookie'
import { NextPage } from 'next'
import BlackText from '../components/utils/BlackText'
import ToggleIcon from '../components/utils/ToggleIcon'
import ErrorAlert from '../components/utils/ErrorAlert'
import theme from '../components/utils/theme'

interface Props {
  redirect?: string
  initialEmail?: string
}

const Signup: NextPage<Props> = ({ redirect, initialEmail }) => {
  const [ email, setEmail ] = useState(initialEmail)
  const [ first, setFirst ] = useState('')
  const [ last, setLast ] = useState('')
  const [ password, setPassword ] = useState('')

  const [ showPassword, setShowPassword ] = useState(false)
  const [ errorText, setErrorText ] = useState('')

  const router = useRouter()

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()

    if (!first) {
      setErrorText('Your first name cannot be empty.')
      return
    }

    if (first.length > 32) {
      setErrorText('Your first name cannot be more than 32 characters.')
      return
    }

    if (!last) {
      setErrorText('Your last name cannot be empty.')
      return
    }

    if (last.length > 32) {
      setErrorText('Your last name cannot be more than 32 characters.')
      return
    }

    if (password.length < 8) {
      setErrorText('Your password must be at least 8 characters long.')
      return
    }

    // form onInvalid should check this but ya never know
    if (!email || email.length > 320) {
      return
    }

    if (first === '__TEST__' && last === '__USER__') {
      // __TEST__ __USER__ is what the unit test users are called in the backend.
      // if one of the test fails, it's likely that test users will be left. if this is the case,
      // i can easily delete them using a SQL statement.
      setErrorText('This username is not allowed.')
      return
    }

    const response = await fetch('/api/v1/users', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email,
        firstName: first,
        lastName: last,
        password
      })
    })

    if (response.status < 400) {
      const json = await response.json()
      Cookie.set('jwt', json.jwt)
      router.push(redirect || '/dash')
    } else if (response.status === 412) {
      setErrorText('An account with this email already exists.')
    } else if (response.status === 500) {
      setErrorText('There was an error in the server. Please try again later.')
    } else if (response.status === 411) {
      // shouldn't happen because of fron
      setErrorText('One or more of the fields have an invalid length.')
    } else if (response.status === 404) {
      setErrorText('There was an error communicating with the server. Please try again later.')
    } else {
      setErrorText('There was an unknown error. Status code: ' + response.status)
    }
  }

  // since the only field w built-in validation is email, we know that email is invalid
  const handleInvalid = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setErrorText('Please enter a valid email address.')
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
          Sign up
        </BlackText>
        <Typography color='textSecondary' style={{ fontSize: 18, marginTop: 5 }}>
          Simplify your debate experience
        </Typography>
        <form style={{ width: '62.5%', marginTop: 20 }} onSubmit={handleSubmit} onInvalid={handleInvalid}>
          <TextField
            type='email'
            label='Email address'
            value={email}
            onChange={e => setEmail(e.target.value)}
            variant='outlined'
            style={{ width: '100%', marginBottom: 10 }}
            autoComplete='email'
          />
          <div style={{ display: 'flex', justifyContent: 'space-between', margin: '5px 0' }}>
            <TextField
              label='First'
              value={first}
              onChange={e => setFirst(e.target.value)}
              variant='outlined'
              style={{ width: '45%' }}
              autoComplete='given-name'
            />
            <TextField
              label='Last'
              value={last}
              onChange={e => setLast(e.target.value)}
              variant='outlined'
              style={{ width: '45%' }}
              autoComplete='family-name'
            />
          </div>
          <TextField
            type={showPassword ? 'text' : 'password'}
            label='Password'
            value={password}
            onChange={e => setPassword(e.target.value)}
            variant='outlined'
            style={{ width: '100%', margin: '10px 0' }}
            InputProps={{
              endAdornment: (
                <InputAdornment position='end' style={{ marginLeft: '-15%', marginTop: '-7.5%' }}>
                  <IconButton onClick={() => setShowPassword(!showPassword)} style={{ padding: 0 }}>
                    <ToggleIcon on={showPassword} onIcon={<VisibilityIcon />} offIcon={<VisibilityOffIcon />} timeout={250} />
                  </IconButton>
                </InputAdornment>
              )
            }}
            autoComplete='new-password'
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
              Create account
            </Typography>
          </Button>
          {
            errorText && <ErrorAlert text={errorText} onClose={() => setErrorText('')} />
          }
        </form>
        <Typography color='textSecondary' variant='h6' style={{ fontSize: 14, marginTop: 20 }}>
          Already have an account?
          <Link href='/login'>
            <a href='/login' style={{ marginLeft: 2, color: theme.palette.primary.main }}>
              Log in
            </a>
          </Link>
          .
        </Typography>
      </div>
    </div>
  )
}

export default Signup

export async function getServerSideProps({ query }) {
  return {
    props: {
      redirect: query?.redirect || null,
      initialEmail: query?.email || ''
    }
  }
}

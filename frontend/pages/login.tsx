import {
  Heading, Input, InputGroup, InputRightElement, Text, IconButton, Button
} from '@chakra-ui/react'
import { GetServerSideProps, NextPage } from 'next'
import { ViewIcon, ViewOffIcon } from '@chakra-ui/icons'
import { FormEvent, useState } from 'react'
import Link from 'next/link'
import { useRouter } from 'next/router'
import Cookie from 'js-cookie'
import ErrorAlert from 'lib/ErrorAlert'
import theme from 'lib/theme'

interface Props {
  redirect?: string
}

const Login: NextPage<Props> = ({ redirect }) => {
  const [ email, setEmail ] = useState('')
  const [ password, setPassword ] = useState('')

  const [ showPassword, setShowPassword ] = useState(false)
  const [ errorText, setErrorText ] = useState('')

  const router = useRouter()

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
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
      setErrorText('Invalid email and/or password.')
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
        <Heading as='h3' fontSize={42} textAlign='center'>
          Sign in
        </Heading>
        <Text color='lightBlue' fontSize={18} marginTop={5}>
          Simplify your debate experience
        </Text>
        <form style={{ width: '62.5%', marginTop: 20 }} onSubmit={handleSubmit}>
          <Input
            label='Email address'
            type='text'
            placeholder='Email address'
            value={email}
            onChange={e => setEmail(e.target.value)}
            focusBorderColor='blue.400'
            minHeight={50}
            size='md'
          />
          <InputGroup size='md'>
            <Input
              type={showPassword ? 'text' : 'password'}
              label='Password'
              placeholder='Password'
              value={password}
              onChange={e => setPassword(e.target.value)}
              margin='10px 0'
              autoComplete='current-password'
              minHeight={50}
              size='md'
            />
            <InputRightElement width='4.5rem' marginTop={15}>
              <IconButton
                aria-label='Show password'
                icon={
                  showPassword
                    ? <ViewIcon />
                    : <ViewOffIcon />
                }
                onClick={() => setShowPassword(!showPassword)}
                background='none'
              />
            </InputRightElement>
          </InputGroup>
          <Button
            type='submit'
            height={50}
            marginTop={15}
            colorScheme='blue'
            isFullWidth
          >
            Log in
          </Button>
          {
            errorText && <ErrorAlert text={errorText} onClose={() => setErrorText('')} />
          }
        </form>
        <Text color='lightBlue' fontSize={14} marginTop={5}>
          Don't have an account?
          <Link href='/signup'>
            <a href='/signup' style={{ marginLeft: 2, color: theme.palette.primary.main }}>
              Sign up
            </a>
          </Link>
          .
        </Text>
      </div>
    </div>
  )
}

export default Login

export const getServerSideProps: GetServerSideProps<Props> = async ({ query }) => {
  if (!query?.redirect) {
    return {
      props: {}
    }
  }
  return {
    props: {
      redirect: (typeof query.redirect === 'string'
        ? query?.redirect
        : query?.redirect[0]) || null
    }
  }
}

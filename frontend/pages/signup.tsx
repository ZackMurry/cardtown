import { FormEvent, useContext, useState } from 'react'
import Link from 'next/link'
import { useRouter } from 'next/router'
import {
  Heading,
  Text,
  Input,
  InputGroup,
  InputRightElement,
  IconButton,
  Button,
  Flex,
  useColorModeValue
} from '@chakra-ui/react'
import Cookie from 'js-cookie'
import { GetServerSideProps, NextPage } from 'next'
import { ViewIcon, ViewOffIcon } from '@chakra-ui/icons'
import theme from 'lib/theme'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'

interface Props {
  redirect?: string
  initialEmail?: string
}

const Signup: NextPage<Props> = ({ redirect, initialEmail }) => {
  const [email, setEmail] = useState(initialEmail)
  const [first, setFirst] = useState('')
  const [last, setLast] = useState('')
  const [password, setPassword] = useState('')

  const [showPassword, setShowPassword] = useState(false)
  const { setErrorMessage } = useContext(errorMessageContext)

  const bgColor = useColorModeValue('offWhite', 'offBlack')
  const router = useRouter()

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()

    if (!first) {
      setErrorMessage('Your first name cannot be empty.')
      return
    }

    if (first.length > 32) {
      setErrorMessage('Your first name cannot be more than 32 characters.')
      return
    }

    if (!last) {
      setErrorMessage('Your last name cannot be empty.')
      return
    }

    if (last.length > 32) {
      setErrorMessage('Your last name cannot be more than 32 characters.')
      return
    }

    if (password.length < 8) {
      setErrorMessage('Your password must be at least 8 characters long.')
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
      setErrorMessage('This username is not allowed.')
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
      setErrorMessage('An account with this email already exists.')
    } else if (response.status === 500) {
      setErrorMessage('There was an error in the server. Please try again later.')
    } else if (response.status === 411) {
      // shouldn't happen because of fron
      setErrorMessage('One or more of the fields have an invalid length.')
    } else if (response.status === 404) {
      setErrorMessage('There was an error communicating with the server. Please try again later.')
    } else {
      setErrorMessage('There was an unknown error. Status code: ' + response.status)
    }
  }

  // since the only field w built-in validation is email, we know that email is invalid
  const handleInvalid = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setErrorMessage('Please enter a valid email address.')
  }

  return (
    <Flex justifyContent='center' alignItems='center' w='100%' h='100vh' bg={bgColor}>
      <div
        style={{
          width: '25%',
          minWidth: 400,
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center'
        }}
      >
        <Heading as='h3' fontSize={42} textAlign='center'>
          Sign up
        </Heading>
        <Text color='lightBlue' fontSize={18} marginTop={2}>
          Simplify your debate experience
        </Text>
        <form style={{ width: '62.5%', marginTop: 20 }} onSubmit={handleSubmit} onInvalid={handleInvalid}>
          <Input
            type='email'
            label='Email address'
            placeholder='Email address'
            value={email}
            onChange={e => setEmail(e.target.value)}
            marginBottom={3}
            autoComplete='email'
            minHeight={50}
          />
          <div style={{ display: 'flex', justifyContent: 'space-between', margin: '5px 0' }}>
            <Input
              type='text'
              label='First'
              placeholder='First'
              value={first}
              onChange={e => setFirst(e.target.value)}
              width='45%'
              autoComplete='given-name'
              minHeight={50}
            />
            <Input
              type='text'
              label='Last'
              placeholder='Last'
              value={last}
              onChange={e => setLast(e.target.value)}
              width='45%'
              autoComplete='family-name'
              minHeight={50}
            />
          </div>
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
                icon={showPassword ? <ViewIcon /> : <ViewOffIcon />}
                onClick={() => setShowPassword(!showPassword)}
                background='none'
              />
            </InputRightElement>
          </InputGroup>
          <Button
            type='submit'
            colorScheme='blue'
            bgColor='cardtownBlue'
            color='white'
            height={50}
            isFullWidth
            marginTop={15}
          >
            Create account
          </Button>
        </form>
        <Text color='lightBlue' fontSize={14} marginTop={5}>
          Already have an account?
          <Link href='/login'>
            <a href='/login' style={{ marginLeft: 2, color: theme.palette.primary.main }}>
              Log in
            </a>
          </Link>
          .
        </Text>
      </div>
    </Flex>
  )
}

export default Signup

export const getServerSideProps: GetServerSideProps = async ({ query }) => ({
  props: {
    redirect: query?.redirect || null,
    initialEmail: query?.email || ''
  }
})

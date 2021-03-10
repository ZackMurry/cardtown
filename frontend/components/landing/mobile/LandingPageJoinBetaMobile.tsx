import {
  Button, Heading, Grid, GridItem, Text
} from '@chakra-ui/react'
import { FC, FormEvent, useState } from 'react'
import { useRouter } from 'next/router'
import theme from '../../utils/theme'

const LandingPageJoinBetaMobile: FC = () => {
  const [ email, setEmail ] = useState('')
  const [ invalid, setInvalid ] = useState(false)

  const router = useRouter()

  const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setInvalid(false)
    if (email === '') {
      setInvalid(true)
      return
    }
    router.push(`/signup?email=${email}`)
  }

  // todo validation message
  const handleInvalid = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setInvalid(true)
  }

  return (
    <>
      <form onSubmit={handleSubmit} onInvalid={handleInvalid} style={{ display: 'flex' }}>
        <Grid templateColumns='repeat(3, 1fr)' gap={0}>
          <GridItem colSpan={2}>
            <input
              type='email'
              placeholder='Email address'
              style={{
                outline: 'none',
                padding: 15,
                fontSize: 16,
                border: '2px solid #CBCEDA',
                borderRadius: '10px 0 0 10px',
                color: theme.palette.black.main,
                width: '100%'
              }}
              value={email}
              onChange={e => setEmail(e.target.value)}
              autoComplete='email'
              aria-label='Email'
            />
          </GridItem>
          <GridItem>
            <Button
              type='submit'
              colorScheme='blue'
              bg='cardtownBlue'
              borderRadius='0 10px 10px 0'
              isFullWidth
              height='100%'
            >
              <Heading as='h5' fontWeight='500px' fontSize='15px'>
                Join the beta
              </Heading>
            </Button>
          </GridItem>
        </Grid>
      </form>
      {/* todo maybe make invalid look a bit better */}
      {
        invalid && (
          <Text
            color='red.500'
            textAlign='center'
            fontWeight='400'
            margin={10}
            style={{
              color: theme.palette.error.main, textAlign: 'center', fontWeight: 500, margin: 10
            }}
          >
            This isn't a valid email address.
          </Text>
        )
      }
    </>
  )
}

export default LandingPageJoinBetaMobile

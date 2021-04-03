import { Button, Grid, Text, GridItem, useColorModeValue } from '@chakra-ui/react'
import { FC, useState, FormEvent } from 'react'
import { useRouter } from 'next/router'
import theme from 'lib/theme'
import chakraTheme from 'lib/chakraTheme'

const LandingPageJoinBeta: FC = () => {
  const [email, setEmail] = useState('')
  const [invalid, setInvalid] = useState(false)
  const bgColor = useColorModeValue(chakraTheme.colors.offWhiteAccent, chakraTheme.colors.offBlackAccent)
  const inputBorderColor = useColorModeValue(chakraTheme.colors.grayBorder, chakraTheme.colors.darkGrayBorder)

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
      <form onSubmit={handleSubmit} onInvalid={handleInvalid}>
        <Grid columns={2} templateColumns='repeat(3, 1fr)' gap={0}>
          <GridItem colSpan={2}>
            <input
              type='email'
              placeholder='Email address'
              style={{
                outline: 'none',
                padding: 10,
                fontSize: 14,
                border: `2px solid ${inputBorderColor}`,
                borderRadius: '10px 0 0 10px',
                width: '100%',
                height: '100%',
                backgroundColor: bgColor
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
              color='white'
              borderRadius='0 10px 10px 0'
              width='100%'
              height='100%'
            >
              Join the beta
            </Button>
          </GridItem>
        </Grid>
      </form>
      {/* todo maybe make invalid look a bit better */}
      {invalid && (
        <Text color='red.500' textAlign='center' fontWeight={500} margin='10px'>
          This isn't a valid email address.
        </Text>
      )}
    </>
  )
}

export default LandingPageJoinBeta

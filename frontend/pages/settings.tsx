import {
  Avatar,
  Box,
  Button,
  Divider,
  Flex,
  FormControl,
  FormErrorMessage,
  FormLabel,
  Heading,
  Input,
  SlideFade,
  Text,
  useColorModeValue
} from '@chakra-ui/react'
import { FC, FormEvent, useContext, useState } from 'react'
import DashboardPage from 'components/dash/DashboardPage'
import userContext from 'lib/hooks/UserContext'
import { useRouter } from 'next/router'
import Cookies from 'js-cookie'

const ChangeNameSection: FC = () => {
  const { firstName, lastName, jwt } = useContext(userContext)
  const [isLoading, setLoading] = useState(false)
  const [newFirstName, setNewFirstName] = useState(firstName)
  const [newLastName, setNewLastName] = useState(lastName)
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
  const [formErrorMsg, setFormErrorMsg] = useState(null)
  const router = useRouter()

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    if (newFirstName.length > 32) {
      setFormErrorMsg('Your first name cannot be longer than 32 characters')
      return
    }
    if (newLastName.length > 32) {
      setFormErrorMsg('Your last name cannot be longer than 32 characters')
      return
    }
    if (newFirstName === '__TEST__' && newLastName === '__USER__') {
      // __TEST__ __USER__ is what the unit test users are called in the backend.
      // if one of the test fails, it's likely that test users will be left. if this is the case,
      // i can easily delete them using a SQL statement.
      setFormErrorMsg('This username is not allowed.')
      return
    }
    setLoading(true)
    const response = await fetch('/api/v1/users/name', {
      method: 'PUT',
      headers: { Authorization: `Bearer ${jwt}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({
        first: newFirstName,
        last: newLastName
      })
    })
    setLoading(false)
    if (response.ok) {
      const { jwt: newJwt } = (await response.json()) as { jwt: string }
      Cookies.set('jwt', newJwt, {
        sameSite: 'Strict'
      })
      // Needs to refresh to reset user context
      router.reload()
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <FormControl
        id='name'
        isRequired
        isInvalid={formErrorMsg && (newFirstName !== firstName || newLastName !== lastName)}
        mt='15px'
      >
        <FormLabel>Name</FormLabel>
        <Flex justifyContent='space-between'>
          <Input
            type='text'
            placeholder='First'
            value={newFirstName}
            onChange={e => setNewFirstName(e.target.value)}
            borderColor={borderColor}
            _focus={{ borderColor: 'cardtownBlue' }}
            mr='5px'
          />
          <Input
            type='text'
            placeholder='Last'
            value={newLastName}
            onChange={e => setNewLastName(e.target.value)}
            borderColor={borderColor}
            _focus={{ borderColor: 'cardtownBlue' }}
            ml='5px'
          />
        </Flex>
        <FormErrorMessage>{formErrorMsg}</FormErrorMessage>
        <SlideFade in={newFirstName !== firstName || newLastName !== lastName}>
          <Flex justifyContent='flex-end' mt='10px'>
            <Button type='submit' colorScheme='blue' bg='cardtownBlue' size='sm' color='white' isLoading={isLoading}>
              Save
            </Button>
          </Flex>
        </SlideFade>
      </FormControl>
    </form>
  )
}

// todo more settings
const SettingsPage: FC = () => {
  const { firstName, lastName, email } = useContext(userContext)
  const bgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
  return (
    <DashboardPage>
      <Flex justifyContent='center' w='100%' mt='5vh'>
        <Box
          w='60vh'
          bg={bgColor}
          p='3% 5%'
          borderRadius='15px'
          borderWidth='1px'
          borderStyle='solid'
          borderColor={borderColor}
        >
          <Flex>
            <Avatar name={`${firstName} ${lastName}`} size='lg' />
            <Box pl='20px'>
              <Heading as='h6' fontSize='28px'>{`${firstName} ${lastName}`}</Heading>
              <Text>Your account settings</Text>
            </Box>
          </Flex>
          <Box mt='25px'>
            <Heading fontWeight='normal' fontSize='25px'>
              Public profile
            </Heading>
            <Divider borderColor={borderColor} />
            <ChangeNameSection />
          </Box>
        </Box>
      </Flex>
    </DashboardPage>
  )
}

export default SettingsPage

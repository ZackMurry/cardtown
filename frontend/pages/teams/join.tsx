import { Box, Flex, Heading, Text, useColorModeValue, Button } from '@chakra-ui/react'
import DashboardPage from 'components/dash/DashboardPage'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import userContext from 'lib/hooks/UserContext'
import redirectToLogin from 'lib/redirectToLogin'
import { GetServerSideProps } from 'next'
import { useRouter } from 'next/router'
import { FC, useContext, useEffect, useState } from 'react'
import { TeamPublicData } from 'types/team'

interface Props {
  secretKey?: string
  teamName?: string
  team?: TeamPublicData
  fetchErrorMsg?: string
}

// todo improve styling
// todo prompt user to leave team if already in a team
// todo show owner of the team
const JoinTeam: FC<Props> = ({ secretKey, team, fetchErrorMsg, teamName }) => {
  const { setErrorMessage } = useContext(errorMessageContext)
  const { jwt } = useContext(userContext)
  const bgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
  const [isLoading, setLoading] = useState(false)
  const router = useRouter()

  useEffect(() => {
    if (fetchErrorMsg) {
      setErrorMessage(fetchErrorMsg)
    }
  }, [])

  const handleJoin = async () => {
    setLoading(true)
    const response = await fetch('/api/v1/teams/join', {
      method: 'POST',
      headers: { Authorization: `Bearer ${jwt}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({
        teamId: team.id,
        teamSecretKey: secretKey
      })
    })
    setLoading(false)
    if (response.ok) {
      router.push('/dash')
    }
  }

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
          {team ? (
            <>
              <Heading as='h3' fontSize='28px' fontWeight='medium'>
                Join <span style={{ fontWeight: 'bold' }}>{teamName}</span>
              </Heading>
              <Text>{`${team.memberCount} member${team.memberCount !== 1 ? 's' : ''}`}</Text>
              <Button
                colorScheme='blue'
                bg='cardtownBlue'
                color='white'
                onClick={handleJoin}
                m='15px 0'
                isLoading={isLoading}
              >
                Join
              </Button>
              <Text fontSize='14px'>
                Note: you can only be in one team at a time. To join <span style={{ fontWeight: 'bold' }}>{teamName}</span>,
                you will need to leave your team.
              </Text>
            </>
          ) : (
            <Heading>Team not found</Heading>
          )}
        </Box>
      </Flex>
    </DashboardPage>
  )
}

export default JoinTeam

export const getServerSideProps: GetServerSideProps<Props> = async ({ req, res, query }) => {
  const { jwt } = req.cookies
  if (!jwt) {
    redirectToLogin(res, '/dash')
    return {
      props: {}
    }
  }
  const id = typeof query.id === 'string' ? query.id : query.id[0]
  const secretKey = typeof query.key === 'string' ? query.key : query.key[0]
  const teamName = typeof query.name === 'string' ? query.name : query.name[0]
  if (!id) {
    // todo improve
    res.statusCode = 404
    res.end()
  }
  const domain = process.env.NODE_ENV !== 'production' ? 'http://localhost' : 'https://cardtown.co'
  const response = await fetch(`${domain}/api/v1/teams/id/${id}`, { headers: { Authorization: `Bearer ${jwt}` } })
  let teamPublicData: TeamPublicData | null
  if (response.ok) {
    teamPublicData = (await response.json()) as TeamPublicData
  } else {
    if (response.status === 403) {
      redirectToLogin(res, '/dash')
      return {
        props: {}
      }
    }
    let fetchErrorMsg: string | undefined
    if (response.status === 500) {
      fetchErrorMsg = 'A server error occurred during your request. Please try again'
    } else if (response.status === 404) {
      fetchErrorMsg = 'No team found with the requested id'
    } else {
      fetchErrorMsg = `An unknown error occurred during your request. Status code: ${response.status}`
    }
    return {
      props: {
        fetchErrorMsg,
        id,
        secretKey,
        teamName
      }
    }
  }
  return {
    props: {
      id,
      team: teamPublicData,
      secretKey,
      teamName
    }
  }
}

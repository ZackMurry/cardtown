import {
  Box, Button, Heading, Text
} from '@chakra-ui/react'
import { FC } from 'react'
import Link from 'next/link'
import TeamHeader from 'types/TeamHeader'

interface Props {
  team: TeamHeader
}

// todo
const DashSidebarTeamInformation: FC<Props> = ({ team }) => (
  <Box p='10%'>
    <Heading as='h4' fontSize={16}>
      Team
    </Heading>
    <Box pl='15px' pt='15px'>
      <Text color='darkGray' fontSize={14} mt='3px'>
        {`${team.name} — ${team.memberCount} member${team.memberCount !== 1 ? 's' : ''}`}
      </Text>
      <Link href='/team' passHref>
        <a>
          <Text color='darkGray' fontSize={13} textDecor='underline' marginTop='5px'>
            View team settings
          </Text>
        </a>
      </Link>
      <Button colorScheme='blue' bg='cardtownBlue' size='xs' mt='10px'>
        Create invite link
      </Button>
    </Box>
  </Box>
)

export default DashSidebarTeamInformation

import { Box, Button, Heading, Text, useDisclosure } from '@chakra-ui/react'
import { FC } from 'react'
import DashCreateTeamModal from './DashCreateTeamModal'

const DashSidebarNoTeamSection: FC = () => {
  const { isOpen, onOpen, onClose } = useDisclosure()

  return (
    <>
      <Box p='10%'>
        <Heading as='h4' fontSize={16} fontWeight='medium'>
          Part of a team?
        </Heading>
        <Text fontSize={13} color='darkGray' pt='5px'>
          Cardtown was made for teams and makes collaboration easy. Create a team to improve the way your team works
          together.
        </Text>
        <Button variant='outline' size='sm' mt='10px' onClick={onOpen}>
          Create a team
        </Button>
        <Text fontSize={13} color='darkGray' pt='7px'>
          If your debate team has already created a team with Cardtown, you can ask them for an invite link.
        </Text>
      </Box>
      <DashCreateTeamModal isOpen={isOpen} onClose={onClose} />
    </>
  )
}

export default DashSidebarNoTeamSection

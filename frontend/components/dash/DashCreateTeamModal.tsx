import {
  Button,
  Flex,
  FormControl,
  FormErrorMessage,
  FormLabel,
  Input,
  InputGroup,
  InputRightAddon,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalHeader,
  ModalOverlay,
  FormHelperText,
  Text
} from '@chakra-ui/react'
import Cookies from 'js-cookie'
import createTeamInviteLink from 'lib/createTeamInviteLink'
import { useRouter } from 'next/router'
import { FC, FormEvent, useRef, useState } from 'react'
import { TeamLinkData } from 'types/team'

interface Props {
  isOpen: boolean
  onClose: () => void
}

const DashCreateTeamModal: FC<Props> = ({ isOpen, onClose }) => {
  const [stage, setStage] = useState<'creating' | 'loading' | 'created'>('creating')
  const [teamName, setTeamName] = useState('')
  const [formErrorMsg, setFormErrorMsg] = useState('')
  const [inviteUrl, setInviteUrl] = useState('')
  const linkRef = useRef<HTMLInputElement>(null)
  const router = useRouter()

  const handleTeamCreate = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setFormErrorMsg('')
    if (teamName.length > 128) {
      setFormErrorMsg("Your team's name cannot have more than 128 characters")
      return
    }
    setStage('loading')
    const response = await fetch('/api/v1/teams', {
      method: 'POST',
      headers: { Authorization: `Bearer ${Cookies.get('jwt')}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: teamName
      })
    })
    if (response.ok) {
      const data = (await response.json()) as TeamLinkData
      setInviteUrl(createTeamInviteLink(data))
      setStage('created')
      return
    }
    setStage('creating')
    if (response.status === 409) {
      setFormErrorMsg(
        "It looks like you're already in a team. To join another team, leave the one that you are currently in"
      )
    } else if (response.status >= 500) {
      setFormErrorMsg('A server error occurred during your request. Please try again')
    } else {
      setFormErrorMsg(`An unknown error occured. Status code ${response.status}`)
    }
  }

  const handleInvalid = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    setFormErrorMsg('Your team must have a name')
  }

  const copyLinkToClipboard = () => {
    linkRef.current.select()
    document.execCommand('copy')
  }

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <ModalOverlay />
      {stage === 'creating' || stage === 'loading' ? (
        <ModalContent>
          <ModalHeader>Create a team</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <form onSubmit={handleTeamCreate} onInvalid={handleInvalid}>
              <FormControl id='team-name' isRequired isInvalid={Boolean(formErrorMsg)}>
                <FormLabel>Team name</FormLabel>
                <Input type='text' value={teamName} onChange={e => setTeamName(e.target.value)} placeholder='Team name' />
                <FormHelperText ml='5px'>You can change this at any time</FormHelperText>
                <FormErrorMessage>{formErrorMsg}</FormErrorMessage>
              </FormControl>
              <Flex justifyContent='flex-end' mt='25px'>
                <Button type='submit' colorScheme='blue' bg='cardtownBlue' isLoading={stage === 'loading'}>
                  Create
                </Button>
              </Flex>
            </form>
          </ModalBody>
        </ModalContent>
      ) : (
        <ModalContent>
          <ModalHeader>Team created!</ModalHeader>
          <ModalCloseButton />
          <ModalBody>
            <Text>
              Below is your invite link. Send this to your teammates to share your cards, arguments, speeches, rounds, and
              more!
            </Text>
            <InputGroup size='sm' mt='15px'>
              <Input isReadOnly value={inviteUrl} ref={linkRef} />
              <InputRightAddon p='0'>
                <Button variant='outline' size='sm' border='none' isFullWidth onClick={copyLinkToClipboard}>
                  Copy
                </Button>
              </InputRightAddon>
            </InputGroup>
            <Text color='darkGray' fontSize={12} m='10px 3px'>
              You can view this link at any time by going to your team's settings page.
            </Text>
            <Flex justifyContent='flex-end'>
              <Button onClick={router.reload} colorScheme='blue' bg='cardtownBlue' m='10px 5px'>
                Awesome!
              </Button>
            </Flex>
          </ModalBody>
        </ModalContent>
      )}
    </Modal>
  )
}

export default DashCreateTeamModal

import { Box, Flex, Text, useColorModeValue } from '@chakra-ui/react'
import userContext from 'lib/hooks/UserContext'
import Link from 'next/link'
import { FC, useContext } from 'react'
import { ResponseAction } from 'types/action'

const DashActionCardItem: FC<{ action: ResponseAction }> = ({ action, children }) => {
  const bgColor = useColorModeValue('white', 'darkElevated')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
  return (
    <Box w='100%' mb='10px'>
      {children}
      <Link href={`/cards/id/${action.card.id}`}>
        <a>
          <Box
            bg={bgColor}
            borderWidth='1px'
            borderStyle='solid'
            borderColor={borderColor}
            borderRadius='5px'
            p='15px 20px'
            mt='10px'
            w='100%'
          >
            <Text fontWeight='medium' fontSize='14px'>
              {action.card.tag}
            </Text>
            <Text fontWeight='medium' fontSize='14px'>
              {action.card.cite}
            </Text>
            <Flex justifyContent='space-between' pt='3px'>
              <Text color='darkGray' fontSize='14px'>
                {`${action.card.bodyText.split(' ').length} words`}
              </Text>
              <Text color='darkGray' fontSize='14px'>
                {action.card.numRelatedArguments === 0
                  ? 'Not in any arguments'
                  : `In ${action.card.numRelatedArguments} argument`}
                {action.card.numRelatedArguments > 1 ? 's' : ''}
              </Text>
            </Flex>
          </Box>
        </a>
      </Link>
    </Box>
  )
}

const DashActionArgumentItem: FC<{ action: ResponseAction }> = ({ action, children }) => {
  const bgColor = useColorModeValue('white', 'darkElevated')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
  return (
    <Box w='100%' mb='10px'>
      {children}
      <Link href={`/arguments/id/${action.argument.id}`}>
        <a>
          <Box
            bg={bgColor}
            borderWidth='1px'
            borderStyle='solid'
            borderColor={borderColor}
            borderRadius='5px'
            p='15px 20px'
            mt='10px'
            w='100%'
          >
            <Text fontWeight='medium' fontSize='14px'>
              {action.argument.name}
            </Text>
            <Flex justifyContent='space-between' pt='3px'>
              <Text color='darkGray' fontSize='14px'>
                {`${action.argument.numCards} card${action.argument.numCards !== 1 ? 's' : ''}`}
              </Text>
              <Text color='darkGray' fontSize='14px'>
                Not in any speeches
                {/* hard-coded for now */}
              </Text>
            </Flex>
          </Box>
        </a>
      </Link>
    </Box>
  )
}

// todo once teams are better implemented on the frontend
const DashActionUserItem: FC<{ action: ResponseAction }> = ({ action, children }) => (
  <div>
    {children}
    User preview
  </div>
)

interface Props {
  action: ResponseAction
}

const DashActionItem: FC<Props> = ({ action }) => {
  const { firstName, lastName } = useContext(userContext)
  let subjectName: string
  if (action.subject.name.first === firstName && action.subject.name.last === lastName) {
    subjectName = 'You'
  } else {
    subjectName = `${action.subject.name.first} ${action.subject.name.last}`
  }
  if (action.actionType === 'CREATE_CARD') {
    return (
      <DashActionCardItem action={action}>
        <Text fontSize={14}>
          <b>{`${subjectName} `}</b>
          created a card
          <Link href={`/cards/id/${action.card.id}`} passHref>
            <a>
              <b>{` ${action.card.cite}`}</b>
            </a>
          </Link>
        </Text>
      </DashActionCardItem>
    )
  }
  if (action.actionType === 'DELETE_CARD') {
    // todo the linked card page needs to show that this card has been deleted instead of 404
    return (
      <DashActionCardItem action={action}>
        <Text fontSize={14}>
          <b>{`${subjectName} `}</b>
          deleted
          <Link href={`/cards/id/${action.card.id}`} passHref>
            <a>
              <b>{` ${action.card.cite}`}</b>
            </a>
          </Link>
        </Text>
      </DashActionCardItem>
    )
  }
  if (action.actionType === 'EDIT_CARD') {
    return (
      <DashActionCardItem action={action}>
        <Text fontSize={14}>
          <b>{`${subjectName} `}</b>
          edited
          <Link href={`/cards/id/${action.card.id}`} passHref>
            <a>
              <b>{` ${action.card.cite}`}</b>
            </a>
          </Link>
        </Text>
      </DashActionCardItem>
    )
  }
  if (action.actionType === 'ADD_CARD_TO_ARGUMENT') {
    return (
      <DashActionCardItem action={action}>
        <Text fontSize={14}>
          <b>{`${subjectName} `}</b>
          added
          <Link href={`/cards/id/${action.card.id}`} passHref>
            <a>
              <b>{` ${action.card.cite} `}</b>
            </a>
          </Link>
          to
          <Link href={`/arguments/id/${action.argument.id}`} passHref>
            <a>
              <b>{` ${action.argument.name}`}</b>
            </a>
          </Link>
        </Text>
      </DashActionCardItem>
    )
  }
  if (action.actionType === 'REMOVE_CARD_FROM_ARGUMENT') {
    return (
      <DashActionCardItem action={action}>
        <Text fontSize={14}>
          <b>{`${subjectName} `}</b>
          removed
          <Link href={`/cards/id/${action.card.id}`} passHref>
            <a>
              <b>{` ${action.card.cite} `}</b>
            </a>
          </Link>
          from
          <Link href={`/arguments/id/${action.argument.id}`} passHref>
            <a>
              <b>{` ${action.argument.name}`}</b>
            </a>
          </Link>
        </Text>
      </DashActionCardItem>
    )
  }
  if (action.actionType === 'CREATE_ARGUMENT') {
    return (
      <DashActionArgumentItem action={action}>
        <Text fontSize={14}>
          <b>{`${subjectName} `}</b>
          created an argument
          <Link href={`/arguments/id/${action.argument.id}`} passHref>
            <a>
              <b>{` ${action.argument.name}`}</b>
            </a>
          </Link>
        </Text>
      </DashActionArgumentItem>
    )
  }
  if (action.actionType === 'DELETE_ARGUMENT') {
    return (
      <DashActionArgumentItem action={action}>
        <Text fontSize={14}>
          <b>{`${subjectName}`}</b>
          deleted
          <Link href={`/arguments/id/${action.argument.id}`} passHref>
            <a>
              <b>{` ${action.argument.name}`}</b>
            </a>
          </Link>
        </Text>
      </DashActionArgumentItem>
    )
  }
  if (action.actionType === 'EDIT_ARGUMENT') {
    return (
      <DashActionArgumentItem action={action}>
        <Text fontSize={14}>
          <b>{`${subjectName}`}</b>
          edited
          <Link href={`/arguments/id/${action.argument.id}`} passHref>
            <a>
              <b>{` ${action.argument.name}`}</b>
            </a>
          </Link>
        </Text>
      </DashActionArgumentItem>
    )
  }
  // actionType is implicitly 'JOIN_TEAM'
  return (
    <DashActionUserItem action={action}>
      <Text fontSize={14}>
        <b>{`${subjectName} `}</b>
        joined your team
      </Text>
    </DashActionUserItem>
  )
}

export default DashActionItem

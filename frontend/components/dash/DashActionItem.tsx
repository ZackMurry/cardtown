import { Box, Flex, Text, useColorModeValue } from '@chakra-ui/react'
import chakraTheme from 'lib/chakraTheme'
import userContext from 'lib/hooks/UserContext'
import Link from 'next/link'
import { FC, useContext } from 'react'
import { format as formatTime } from 'timeago.js'
import { ResponseAction } from 'types/action'

// todo show subject pfp

const DashActionCardItem: FC<{ action: ResponseAction }> = ({ action, children }) => {
  const bgColor = useColorModeValue('white', 'darkElevated')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
  return (
    <Box w='100%' mb='25px'>
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
    <Box w='100%' mb='25px'>
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
  <Box w='100%' mb='25px'>
    {children}
  </Box>
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
  const timeAgo = (
    <span style={{ fontSize: 12, color: chakraTheme.colors.darkGray, marginLeft: 5, marginBottom: 5 }}>
      {formatTime(action.time)}
    </span>
  )
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
          {timeAgo}
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
          {timeAgo}
        </Text>
      </DashActionCardItem>
    )
  }
  if (action.actionType === 'DELETE_CARD') {
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
          {timeAgo}
        </Text>
      </DashActionCardItem>
    )
  }
  if (action.actionType === 'RESTORE_CARD') {
    return (
      <DashActionCardItem action={action}>
        <Text fontSize={14}>
          <b>{`${subjectName} `}</b>
          restored
          <Link href={`/cards/id/${action.card.id}`} passHref>
            <a>
              <b>{` ${action.card.cite}`}</b>
            </a>
          </Link>
          {timeAgo}
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
          {timeAgo}
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
          {timeAgo}
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
          {timeAgo}
        </Text>
      </DashActionArgumentItem>
    )
  }
  if (action.actionType === 'EDIT_ARGUMENT') {
    return (
      <DashActionArgumentItem action={action}>
        <Text fontSize={14}>
          <b>{`${subjectName} `}</b>
          edited
          <Link href={`/arguments/id/${action.argument.id}`} passHref>
            <a>
              <b>{` ${action.argument.name}`}</b>
            </a>
          </Link>
          {timeAgo}
        </Text>
      </DashActionArgumentItem>
    )
  }
  if (action.actionType === 'DELETE_ARGUMENT') {
    return (
      <DashActionArgumentItem action={action}>
        <Text fontSize={14}>
          <b>{`${subjectName} `}</b>
          deleted
          <Link href={`/arguments/id/${action.argument.id}`} passHref>
            <a>
              <b>{` ${action.argument.name}`}</b>
            </a>
          </Link>
          {timeAgo}
        </Text>
      </DashActionArgumentItem>
    )
  }
  if (action.actionType === 'RESTORE_ARGUMENT') {
    return (
      <DashActionArgumentItem action={action}>
        <Text fontSize={14}>
          <b>{`${subjectName} `}</b>
          restored
          <Link href={`/arguments/id/${action.argument.id}`} passHref>
            <a>
              <b>{` ${action.argument.name}`}</b>
            </a>
          </Link>
          {timeAgo}
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
        {timeAgo}
      </Text>
    </DashActionUserItem>
  )
}

export default DashActionItem

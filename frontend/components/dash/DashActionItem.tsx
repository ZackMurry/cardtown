import { Text } from '@chakra-ui/react'
import { FC } from 'react'
import { ResponseAction } from 'types/action'
import JwtBody from 'types/JwtBody'

const DashActionCardItem: FC<{ action: ResponseAction }> = ({ action, children }) => (
  <div>
    {children}
    Card preview
  </div>
)

const DashActionArgumentItem: FC<{ action: ResponseAction }> = ({ action, children }) => (
  <div>
    {children}
    Argument preview
  </div>
)

const DashActionUserItem: FC<{ action: ResponseAction }> = ({ action, children }) => (
  <div>
    {children}
    User preview
  </div>
)

interface Props {
  action: ResponseAction
  jwt: JwtBody
}

const DashActionItem: FC<Props> = ({ action, jwt }) => {
  let subjectName: string
  if (action.subject.name.first === jwt.firstName && action.subject.name.last === jwt.lastName) {
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
          <b>{` ${action.card.cite}`}</b>
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
          <b>{` ${action.card.cite}`}</b>
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
          <b>{` ${action.card.cite}`}</b>
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
          <b>{` ${action.card.cite} `}</b>
          to
          <b>{` ${action.argument.name}`}</b>
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
          <b>{` ${action.card.cite} `}</b>
          from
          <b>{` ${action.argument.name}`}</b>
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
          <b>{` ${action.argument.name}`}</b>
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
          <b>{` ${action.argument.name}`}</b>
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
          <b>{` ${action.argument.name}`}</b>
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

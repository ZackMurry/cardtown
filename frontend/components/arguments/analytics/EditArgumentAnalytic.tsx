import CloseIcon from '@material-ui/icons/Close'
import DoneIcon from '@material-ui/icons/Done'
import React, { FC, useContext, useState } from 'react'
import { useColorModeValue, IconButton, Tooltip, Textarea, Flex } from '@chakra-ui/react'
import useErrorMessage from 'lib/hooks/useErrorMessage'
import userContext from 'lib/hooks/UserContext'
import { ResponseAnalytic } from 'types/analytic'

interface Props {
  onCancel: () => void
  onDone: (newValue: ResponseAnalytic) => void
  analytic: ResponseAnalytic
  argId: string
}

const EditArgumentAnalytic: FC<Props> = ({ onCancel, onDone, analytic, argId }) => {
  const [body, setBody] = useState(analytic.body)
  const { setErrorMessage } = useErrorMessage()
  const { jwt } = useContext(userContext)
  const bgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')

  const handleDone = async (e: React.MouseEvent<HTMLInputElement, MouseEvent>) => {
    if (!jwt) {
      setErrorMessage('You need to be signed in to do this')
      onCancel()
    }

    if (!body) {
      setErrorMessage("The analytic's body cannot be blank")
    }
    if (body.length > 2048) {
      setErrorMessage("The analytic's body cannot be longer than 2048 characters")
      return
    }

    const response = await fetch(`/api/v1/arguments/id/${argId}/analytics/id/${analytic.id}`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${jwt}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({
        body
      })
    })

    if (response.ok) {
      onDone({ ...analytic, body })
    } else {
      setErrorMessage(`An unexpected error occured. Status code: ${response.status}`)
    }
  }

  return (
    <Flex justifyContent='space-between' alignItems='flex-start'>
      <Textarea
        value={body}
        onChange={e => setBody(e.target.value)}
        bg={bgColor}
        fontWeight='bold'
        fontSize='18px'
        w='100%'
        resize='vertical'
        rows={3}
        _focus={{ borderColor: 'cardtownBlue' }}
      />
      <Flex flexDir='column' justifyContent='space-between'>
        <Tooltip label='Done'>
          <IconButton aria-label='Done' onClick={handleDone} bg='none'>
            <DoneIcon />
          </IconButton>
        </Tooltip>
        <Tooltip label='Cancel'>
          <IconButton aria-label='Cancel' onClick={onCancel} bg='none'>
            <CloseIcon />
          </IconButton>
        </Tooltip>
      </Flex>
    </Flex>
  )
}

export default EditArgumentAnalytic

import { FC, FormEvent, useContext, useState } from 'react'
import EditIcon from '@material-ui/icons/Edit'
import DoneIcon from '@material-ui/icons/Done'
import CloseIcon from '@material-ui/icons/Close'
import userContext from 'lib/hooks/UserContext'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import { Text, Input, IconButton } from '@chakra-ui/react'

interface Props {
  name: string
  argumentId: string
  onNameChange: (newName: string) => void
}

const ArgumentName: FC<Props> = ({ name: initialName, argumentId, onNameChange }) => {
  const [editMode, setEditMode] = useState(false)
  const [name, setName] = useState(initialName)
  const { jwt } = useContext(userContext)
  const { setErrorMessage } = useContext(errorMessageContext)
  const [isLoading, setLoading] = useState(false)

  const handleCancel = () => {
    setName(initialName)
    setEditMode(false)
  }

  const handleDone = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault()
    if (name === initialName) {
      setEditMode(false)
      return
    }
    if (name.length > 128) {
      setErrorMessage('The name cannot be longer than 128 characters')
      return
    }
    if (name.length < 1) {
      setErrorMessage('The name must be at least one character')
      return
    }

    setLoading(true)
    const response = await fetch(`/api/v1/arguments/id/${encodeURIComponent(argumentId)}`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${jwt}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name
      })
    })
    setLoading(false)
    if (response.ok) {
      onNameChange(name)
      setEditMode(false)
    } else {
      setErrorMessage(`Error renaming argument. Status code: ${response.status}`)
    }
  }

  return (
    <>
      {editMode ? (
        <form onSubmit={handleDone} style={{ display: 'flex', justifyContent: 'space-between', width: '100%' }}>
          <Input
            type='text'
            value={name}
            onChange={e => setName(e.target.value)}
            w='100%'
            p='14px'
            _hover={{ borderColor: 'cardtownBlue' }}
            _focus={{ borderColor: 'cardtownBlue' }}
          />
          <div
            style={{
              display: 'flex',
              justifyContent: 'flex-end',
              alignItems: 'center',
              paddingLeft: '5%'
            }}
          >
            <IconButton aria-label='Cancel' onClick={handleCancel} bg='none'>
              <CloseIcon fontSize='small' />
            </IconButton>
            <IconButton aria-label='Done' type='submit' bg='none' isLoading={isLoading}>
              <DoneIcon fontSize='small' />
            </IconButton>
          </div>
        </form>
      ) : (
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <Text fontSize='24px' fontWeight='bold' overflowWrap='anywhere'>
            {name}
          </Text>
          <IconButton ml='10px' aria-label='Edit' onClick={() => setEditMode(true)} bg='none'>
            <EditIcon fontSize='small' />
          </IconButton>
        </div>
      )}
    </>
  )
}

export default ArgumentName

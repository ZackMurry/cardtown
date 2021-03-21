import { IconButton, TextField } from '@material-ui/core'
import { FC, FormEvent, useContext, useState } from 'react'
import EditIcon from '@material-ui/icons/Edit'
import DoneIcon from '@material-ui/icons/Done'
import CloseIcon from '@material-ui/icons/Close'
import BlackText from 'components/utils/BlackText'
import userContext from 'lib/hooks/UserContext'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'

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

    const response = await fetch(`/api/v1/arguments/id/${encodeURIComponent(argumentId)}`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${jwt}`, 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name
      })
    })
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
          <TextField
            value={name}
            onChange={e => setName(e.target.value)}
            variant='outlined'
            style={{ width: '100%' }}
            InputProps={{ inputProps: { style: { padding: 14 } } }}
          />
          <div
            style={{
              display: 'flex',
              justifyContent: 'flex-end',
              alignItems: 'center',
              paddingLeft: '5%'
            }}
          >
            <IconButton onClick={handleCancel} style={{ height: 48, width: 48 }}>
              <CloseIcon fontSize='small' />
            </IconButton>
            <IconButton type='submit' style={{ height: 48, width: 48 }}>
              <DoneIcon fontSize='small' />
            </IconButton>
          </div>
        </form>
      ) : (
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <BlackText style={{ fontSize: 24, fontWeight: 'bold', overflowWrap: 'anywhere' }}>{name}</BlackText>
          <IconButton onClick={() => setEditMode(true)} style={{ height: 48, width: 48 }}>
            <EditIcon fontSize='small' />
          </IconButton>
        </div>
      )}
    </>
  )
}

export default ArgumentName

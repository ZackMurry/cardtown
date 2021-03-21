import { IconButton } from '@material-ui/core'
import { FC, useContext, useState } from 'react'
import DeleteIcon from '@material-ui/icons/Delete'
import ConfirmationDialog from 'components/utils/ConfirmationDialog'
import userContext from 'lib/hooks/UserContext'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'

interface Props {
  argumentId: string
  argumentName: string
  onDelete: () => void
}

const DeleteArgumentButton: FC<Props> = ({ argumentId, argumentName, onDelete }) => {
  const [dialogOpen, setDialogOpen] = useState(false)

  const { jwt } = useContext(userContext)
  const { setErrorMessage } = useContext(errorMessageContext)

  const handleDelete = async () => {
    const response = await fetch(`/api/v1/arguments/id/${encodeURIComponent(argumentId)}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${jwt}` }
    })
    if (response.ok) {
      onDelete()
    } else {
      setErrorMessage(`Error deleting argument. Status code: ${response.status}`)
    }
  }

  return (
    <>
      <IconButton onClick={() => setDialogOpen(true)} style={{ width: 48, height: 48 }}>
        <DeleteIcon fontSize='small' />
      </IconButton>
      <ConfirmationDialog
        open={dialogOpen}
        onCancel={() => setDialogOpen(false)}
        onConfirm={handleDelete}
        title={`Delete ${argumentName}?`}
        body='Are you sure? This action cannot be reverted.'
      />
    </>
  )
}

export default DeleteArgumentButton

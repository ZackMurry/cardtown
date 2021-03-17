import { IconButton } from '@material-ui/core'
import { FC, useState } from 'react'
import DeleteIcon from '@material-ui/icons/Delete'
import ConfirmationDialog from 'components/utils/ConfirmationDialog'

interface Props {
  jwt: string
  argumentId: string
  argumentName: string
  onDelete: () => void
  onError: (msg: string) => void
}

const DeleteArgumentButton: FC<Props> = ({
  jwt, argumentId, argumentName, onDelete, onError
}) => {
  const [ dialogOpen, setDialogOpen ] = useState(false)

  const handleDelete = async () => {
    const response = await fetch(`/api/v1/arguments/id/${encodeURIComponent(argumentId)}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${jwt}` }
    })
    if (response.ok) {
      onDelete()
    } else {
      onError(`Error deleting argument. Status code: ${response.status}`)
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

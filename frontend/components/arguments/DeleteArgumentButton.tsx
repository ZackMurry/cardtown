import { FC, useContext, useState } from 'react'
import { IconButton } from '@chakra-ui/react'
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
  const [isLoading, setLoading] = useState(false)

  const { jwt } = useContext(userContext)
  const { setErrorMessage } = useContext(errorMessageContext)

  const handleDelete = async () => {
    setLoading(true)
    const response = await fetch(`/api/v1/arguments/id/${encodeURIComponent(argumentId)}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${jwt}` }
    })
    setLoading(false)
    if (response.ok) {
      onDelete()
    } else {
      setErrorMessage(`Error deleting argument. Status code: ${response.status}`)
    }
  }

  return (
    <>
      <IconButton aria-label='Delete' onClick={() => setDialogOpen(true)} bg='none' isLoading={isLoading}>
        <DeleteIcon fontSize='small' />
      </IconButton>
      <ConfirmationDialog
        open={dialogOpen}
        onCancel={() => setDialogOpen(false)}
        onConfirm={handleDelete}
        title={`Delete ${argumentName}?`}
        body='Are you sure? This action can be reverted.'
      />
    </>
  )
}

export default DeleteArgumentButton

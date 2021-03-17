import EditIcon from '@material-ui/icons/Edit'
import {
  Button, IconButton, MenuItem, Paper, Popover
} from '@material-ui/core'
import MoreVertIcon from '@material-ui/icons/MoreVert'
import { useRouter } from 'next/router'
import DeleteIcon from '@material-ui/icons/Delete'
import { FC, useState } from 'react'
import ErrorAlert from 'components/utils/ErrorAlert'
import styles from 'styles/ViewCard.module.css'

interface Props {
  id: string
  jwt: string
  onEdit: () => void
}

// todo: copy card
const CardOptionsButton: FC<Props> = ({ id, jwt, onEdit }) => {
  const [ anchorEl, setAnchorEl ] = useState<HTMLButtonElement | null>(null)
  const [ errorText, setErrorText ] = useState('')

  const router = useRouter()

  const handleClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(e.currentTarget)
  }

  const handleDelete = async () => {
    const response = await fetch(`/api/v1/cards/${encodeURIComponent(id)}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${jwt}` }
    })
    if (response.ok) {
      router.push('/cards')
    } else {
      setErrorText(`Error deleting card: ${response.status}`)
    }
  }

  return (
    <div>
      <IconButton onClick={handleClick}>
        <MoreVertIcon />
      </IconButton>
      <div>
        <Popover open={!!anchorEl} anchorEl={anchorEl} anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }} onClose={() => setAnchorEl(null)}>
          <Paper elevation={1} style={{ borderRadius: 7, padding: '5px 0' }}>
            <MenuItem style={{ padding: 0, minHeight: 36 }}>
              <Button
                className={styles['card-context-menu-button']}
                onClick={handleDelete}
                variant='contained'
                color='secondary'
                disableElevation
                disableFocusRipple
                startIcon={<DeleteIcon />}
              >
                Delete
              </Button>
            </MenuItem>
            <MenuItem style={{ padding: 0, minHeight: 36 }}>
              <Button
                className={styles['card-context-menu-button']}
                onClick={onEdit}
                variant='contained'
                color='secondary'
                disableFocusRipple
                startIcon={<EditIcon />}
                style={{ justifyContent: 'flex-start' }}
                disableElevation
                fullWidth
              >
                Edit
              </Button>
            </MenuItem>
          </Paper>
        </Popover>
      </div>
      {
        errorText && (
          <ErrorAlert text={errorText} onClose={() => setErrorText(errorText)} />
        )
      }
    </div>
  )
}

export default CardOptionsButton

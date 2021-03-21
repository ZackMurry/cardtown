import EditIcon from '@material-ui/icons/Edit'
import { Button, IconButton, MenuItem, Paper, Popover } from '@material-ui/core'
import MoreVertIcon from '@material-ui/icons/MoreVert'
import DeleteIcon from '@material-ui/icons/Delete'
import React, { FC, useContext, useState } from 'react'
import styles from 'styles/ViewCard.module.css'
import userContext from 'lib/hooks/UserContext'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'

interface Props {
  cardId: string
  argumentId: string
  indexInArgument: number
  onEdit: () => void
  onRemove: () => void
}

const ArgumentCardOptionsButton: FC<Props> = ({ cardId, argumentId, onEdit, indexInArgument, onRemove }) => {
  const [anchorEl, setAnchorEl] = useState(null)
  const { setErrorMessage } = useContext(errorMessageContext)
  const { jwt } = useContext(userContext)

  const handleClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(e.currentTarget)
  }

  const handleRemove = async () => {
    const response = await fetch(
      `/api/v1/arguments/id/${encodeURIComponent(argumentId)}/cards/${encodeURIComponent(cardId)}?index=${indexInArgument}`,
      {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${jwt}` }
      }
    )
    if (response.ok) {
      onRemove()
    } else {
      setErrorMessage(`Error deleting card: ${response.status}`)
    }
  }

  return (
    <div>
      <IconButton onClick={handleClick}>
        <MoreVertIcon />
      </IconButton>
      <div>
        <Popover
          open={!!anchorEl}
          anchorEl={anchorEl}
          anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
          onClose={() => setAnchorEl(null)}
        >
          <Paper elevation={1} style={{ borderRadius: 7, padding: '5px 0' }}>
            <MenuItem style={{ padding: 0, minHeight: 36 }}>
              <Button
                className={styles['card-context-menu-button']}
                onClick={handleRemove}
                variant='contained'
                color='secondary'
                disableElevation
                disableFocusRipple
                startIcon={<DeleteIcon />}
              >
                Remove
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
    </div>
  )
}

export default ArgumentCardOptionsButton

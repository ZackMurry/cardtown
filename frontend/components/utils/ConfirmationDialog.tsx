import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from '@material-ui/core'
import { FC } from 'react'

interface Props {
  open: boolean
  onCancel: () => void
  onConfirm: () => void
  body: string
  title: string
}

const ConfirmationDialog: FC<Props> = ({ open, body, title, onCancel, onConfirm }) => (
  <Dialog
    open={open}
    onClose={onCancel}
    aria-labelledby='confirm-dialog-title'
    aria-describedby='confirm-dialog-description'
  >
    <DialogTitle id='confirm-dialog-title'>{title}</DialogTitle>
    <DialogContent>
      <DialogContentText id='confirm-dialog-description'>{body}</DialogContentText>
    </DialogContent>
    <DialogActions>
      <Button autoFocus onClick={onCancel}>
        Cancel
      </Button>
      <Button onClick={onConfirm} color='primary' variant='contained'>
        Confirm
      </Button>
    </DialogActions>
  </Dialog>
)

export default ConfirmationDialog

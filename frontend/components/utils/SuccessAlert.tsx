import { Snackbar } from '@material-ui/core'
import MuiAlert from '@material-ui/lab/Alert'
import { FC } from 'react'

interface Props {
  text: string
  onClose: () => void
}

const SuccessAlert: FC<Props> = ({ text, onClose }) => (
  <Snackbar open={!!text} autoHideDuration={10000} onClose={onClose}>
    <MuiAlert onClose={onClose} severity='success' elevation={6} variant='filled'>
      {text}
    </MuiAlert>
  </Snackbar>
)

export default SuccessAlert

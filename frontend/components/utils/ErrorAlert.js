import { Snackbar } from '@material-ui/core'
import MuiAlert from '@material-ui/lab/Alert'

export default function ErrorAlert({ text, onClose }) {
  return (
    <Snackbar open={!!text} autoHideDuration={10000} onClose={() => onClose('')}>
      <MuiAlert onClose={() => onClose('')} severity='error' elevation={6} variant='filled'>
        {text}
      </MuiAlert>
    </Snackbar>
  )
}

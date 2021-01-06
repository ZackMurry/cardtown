import { Snackbar } from '@material-ui/core'
import MuiAlert from '@material-ui/lab/Alert'
import { useRouter } from 'next/router'

export default function ErrorAlert({ text, onClose, disableClose = false }) {
  return (
    <Snackbar autoHideDuration={disableClose ? undefined : 10000} open={!!text}>
      {
        disableClose
          ? (
            <MuiAlert action={null} severity='error' elevation={6} variant='filled'>
              {text}
            </MuiAlert>
          )
          : (
            <MuiAlert onClose={onClose} severity='error' elevation={6} variant='filled'>
              {text}
            </MuiAlert>
          )
      }
    </Snackbar>
  )
}

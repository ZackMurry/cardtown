import { Grow } from '@material-ui/core'

export default function ToggleIcon({
  on, onIcon, offIcon, timeout
}) {
  return (
    <>
      <Grow in={on} timeout={timeout} style={{ visibility: on ? undefined : 'none', position: on ? undefined : 'absolute' }}>
        {onIcon}
      </Grow>
      <Grow in={!on} timeout={timeout} style={{ visibility: on ? 'none' : undefined, position: on ? 'absolute' : undefined }}>
        {offIcon}
      </Grow>
    </>
  )
}

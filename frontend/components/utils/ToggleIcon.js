import { Grow } from '@material-ui/core'

export default function ToggleIcon({
  on, onIcon, offIcon, timeout, onMouseEnter, onMouseLeave
}) {
  return (
    <div onMouseEnter={onMouseEnter} onMouseLeave={onMouseLeave} style={{ marginLeft: '-80%', marginTop: '-80%' }}>
      <Grow in={on} timeout={timeout} style={{ position: 'absolute' }}>
        {onIcon}
      </Grow>
      <Grow in={!on} timeout={timeout} style={{ position: 'absolute' }}>
        {offIcon}
      </Grow>
    </div>
  )
}

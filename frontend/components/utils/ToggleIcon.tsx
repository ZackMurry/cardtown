import { Grow } from '@material-ui/core'
import React, { FC, ReactElement } from 'react'

interface Props {
  on: boolean
  onIcon: ReactElement<any, any>
  offIcon: ReactElement<any, any>
  timeout: number
  onMouseEnter?: (e: React.MouseEvent<HTMLDivElement>) => void
  onMouseLeave?: (e: React.MouseEvent<HTMLDivElement>) => void
}

const ToggleIcon: FC<Props> = ({ on, onIcon, offIcon, timeout, onMouseEnter, onMouseLeave }) => (
  <div onMouseEnter={onMouseEnter} onMouseLeave={onMouseLeave} style={{ marginLeft: '-80%', marginTop: '-80%' }}>
    <Grow in={on} timeout={timeout} style={{ position: 'absolute' }}>
      {onIcon}
    </Grow>
    <Grow in={!on} timeout={timeout} style={{ position: 'absolute' }}>
      {offIcon}
    </Grow>
  </div>
)

export default ToggleIcon

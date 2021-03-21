import { Box } from '@chakra-ui/react'
import parseJwt from 'lib/parseJwt'
import { FC, useMemo, useState } from 'react'
import { ResponseAction } from 'types/action'
import DashActionItem from './DashActionItem'

interface Props {
  actions: ResponseAction[]
  jwt: string
}

// todo extract conversion of jwt to parent since both this and the navbar do the same thing
// todo styling and infinite scrolling
const DashActionFeed: FC<Props> = ({ actions: initialActions, jwt: jwtStr }) => {
  const [actions, setActions] = useState(initialActions)
  const jwt = useMemo(() => parseJwt(jwtStr), [])
  return (
    <Box p='3%' w='50%'>
      {actions && actions.map(action => <DashActionItem action={action} key={action.time} jwt={jwt} />)}
    </Box>
  )
}

export default DashActionFeed

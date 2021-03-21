import { Box } from '@chakra-ui/react'
import { FC, useState } from 'react'
import { ResponseAction } from 'types/action'
import DashActionItem from './DashActionItem'

interface Props {
  actions: ResponseAction[]
}

// todo extract conversion of jwt to parent since both this and the navbar do the same thing
// todo styling and infinite scrolling
const DashActionFeed: FC<Props> = ({ actions: initialActions }) => {
  const [actions, setActions] = useState(initialActions)
  return (
    <Box p='3%' w='50%'>
      {actions && actions.map(action => <DashActionItem action={action} key={action.time} />)}
    </Box>
  )
}

export default DashActionFeed

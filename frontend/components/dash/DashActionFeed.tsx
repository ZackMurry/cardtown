import { Box, Button, Flex, Text, useBreakpointValue, useColorModeValue } from '@chakra-ui/react'
import userContext from 'lib/hooks/UserContext'
import { FC, useContext, useState } from 'react'
import { ResponseAction } from 'types/action'
import DashActionItem from './DashActionItem'

interface Props {
  actions: ResponseAction[]
}

const DashActionFeed: FC<Props> = ({ actions: initialActions }) => {
  const [actions, setActions] = useState<ResponseAction[]>(initialActions)
  const [numPagesLoaded, setNumPagesLoaded] = useState(1)
  const [moreActionsAvailable, setMoreActionsAvailable] = useState(() => initialActions?.length >= 10)
  const [isLoading, setLoading] = useState(false)
  const { jwt } = useContext(userContext)
  const buttonBgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')
  const buttonBorderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
  const buttonTextColor = useColorModeValue('darkText', 'darkGray')
  const width = useBreakpointValue({ sm: '100%', md: '50%' })

  const handleLoadMore = async () => {
    setLoading(true)
    const response = await fetch(`/api/v1/actions/recent?page=${numPagesLoaded}`, {
      headers: { Authorization: `Bearer ${jwt}` }
    })
    setLoading(false)
    if (response.ok) {
      const newActions = (await response.json()) as ResponseAction[]
      if (newActions.length < 10) {
        setMoreActionsAvailable(false)
      }
      setActions([...actions, ...newActions])
      setNumPagesLoaded(numPagesLoaded + 1)
    }
  }

  return (
    <Box p='3%' w={width}>
      {actions && actions.map(action => <DashActionItem action={action} key={action.time} />)}
      {moreActionsAvailable ? (
        <Flex justifyContent='center' mt='50px'>
          <Button
            variant='outline'
            bg={buttonBgColor}
            size='sm'
            onClick={handleLoadMore}
            isLoading={isLoading}
            borderColor={buttonBorderColor}
            color={buttonTextColor}
          >
            Load more
          </Button>
        </Flex>
      ) : (
        <Text textAlign='center' color='darkText' fontSize='14px'>
          No more actions to show
        </Text>
      )}
    </Box>
  )
}

export default DashActionFeed

import { Box, useColorModeValue } from '@chakra-ui/react'
import useWindowSize from 'lib/hooks/useWindowSize'
import { FC } from 'react'
import DashboardNavbar from './DashboardNavbar'

const DashboardPage: FC = ({ children }) => {
  const bgColor = useColorModeValue('offWhite', 'offBlack')
  const { width } = useWindowSize(1920, 1080)
  return (
    <Box bg={bgColor} minW='100%' minH='100vh' overflowY='hidden'>
      <DashboardNavbar windowWidth={width} />
      {children}
    </Box>
  )
}

export default DashboardPage

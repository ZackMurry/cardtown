import { Box, Divider, useBreakpointValue, useColorModeValue } from '@chakra-ui/react'
import useWindowSize from 'lib/hooks/useWindowSize'
import { FC, useEffect, useState } from 'react'
import { TeamHeader } from 'types/team'
import DashSidebarNoTeamSection from './DashSidebarNoTeamSection'
import DashSidebarTeamInformation from './DashSidebarTeamInformation'
import DashSidebarTopicList from './DashSidebarTopicList'

interface Props {
  // todo topics
  team?: TeamHeader
}

const DashboardSidebar: FC<Props> = ({ team }) => {
  const bgColor = useColorModeValue('offWhiteAccent', 'offBlackAccent')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
  const [scroll, setScroll] = useState(0)
  const { height } = useWindowSize(1920, 1080)

  useEffect(() => {
    const handleScroll = () => setScroll(window.pageYOffset)
    window.addEventListener('scroll', handleScroll)
    return () => window.removeEventListener('scroll', handleScroll)
  }, [])

  const isVisible = useBreakpointValue({ md: false, lg: true })
  if (!isVisible) {
    return <></>
  }

  return (
    <Box w='18%' h='100vh'>
      <Box
        position={scroll > height * 0.07 ? 'fixed' : 'absolute'}
        top={scroll > height * 0.07 ? '0vh' : '7vh'}
        height='100vh'
        width='18vw'
        bg={bgColor}
        borderRightStyle='solid'
        borderRightWidth='1px'
        borderRightColor={borderColor}
      >
        <DashSidebarTopicList />
        <Divider color='darkGray' width='75%' margin='0 auto' />
        {team ? <DashSidebarTeamInformation team={team} /> : <DashSidebarNoTeamSection />}
      </Box>
    </Box>
  )
}

export default DashboardSidebar

import { Box, Divider, useColorModeValue } from '@chakra-ui/react'
import { FC } from 'react'
import TeamHeader from 'types/TeamHeader'
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
  return (
    <Box width='18%' height='93vh'>
      <Box
        position='fixed'
        left='0px'
        top='7vh'
        height='93vh'
        width='18%'
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

import { Heading, Text } from '@chakra-ui/react'
import { FC } from 'react'
import chakraTheme from '../components/utils/chakraTheme'
import DashboardNavbar from '../components/dash/DashboardNavbar'
import useWindowSize from '../components/utils/hooks/useWindowSize'
import theme from '../components/utils/theme'

// todo wrap dash pages in a DashboardPage component instead of rewriting layout
const Dash: FC = () => {
  const { width } = useWindowSize(1920, 1080)
  return (
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main }}>
      <DashboardNavbar pageName='Dashboard' windowWidth={width} />

      <div style={{ marginLeft: width >= theme.breakpoints.values.lg ? '12.9vw' : 0, paddingLeft: 38, paddingRight: 38 }}>

      </div>

    </div>
  )
}

export default Dash

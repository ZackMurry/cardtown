import { Heading, Text } from '@chakra-ui/react'
import { FC } from 'react'
import chakraTheme from '../components/utils/chakraTheme'
import DashboardSidebar from '../components/dash/DashboardSidebar'
import useWindowSize from '../components/utils/hooks/useWindowSize'
import theme from '../components/utils/theme'

// todo wrap dash pages in a DashboardPage component instead of rewriting layout
const Dash: FC = () => {
  const { width } = useWindowSize(1920, 1080)
  return (
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main }}>
      <DashboardSidebar pageName='Dashboard' windowWidth={width} />

      <div style={{ marginLeft: width >= theme.breakpoints.values.lg ? '12.9vw' : 0, paddingLeft: 38, paddingRight: 38 }}>
        <Text
          color='darkGray'
          textTransform='uppercase'
          fontSize={11}
          marginTop={19}
          letterSpacing={0.5}
        >
          Overview
        </Text>
        <Heading as='h3' fontSize={24} fontWeight='bold'>
          Dashboard
        </Heading>
        <div
          style={{
            width: '100%', margin: '2vh 0', height: 1, backgroundColor: chakraTheme.colors.lightGray
          }}
        />
      </div>

    </div>
  )
}

export default Dash

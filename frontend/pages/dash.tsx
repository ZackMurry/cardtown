import { FC } from 'react'
import { Typography } from '@material-ui/core'
import DashboardSidebar from '../components/dash/DashboardSidebar'
import BlackText from '../components/utils/BlackText'
import useWindowSize from '../components/utils/hooks/useWindowSize'
import theme from '../components/utils/theme'

const Dash: FC = () => {
  const { width } = useWindowSize(1920, 1080)
  return (
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main }}>
      <DashboardSidebar pageName='Dashboard' windowWidth={width} />

      <div style={{ marginLeft: width >= theme.breakpoints.values.lg ? '12.9vw' : 0, paddingLeft: 38, paddingRight: 38 }}>
        <Typography
          style={{
            color: theme.palette.darkGrey.main,
            textTransform: 'uppercase',
            fontSize: 11,
            marginTop: 19,
            letterSpacing: 0.5
          }}
        >
          Overview
        </Typography>
        <BlackText style={{ fontSize: 24, fontWeight: 'bold' }}>
          Dashboard
        </BlackText>
        <div
          style={{
            width: '100%', margin: '2vh 0', height: 1, backgroundColor: theme.palette.lightGrey.main
          }}
        />
      </div>

    </div>
  )
}

export default Dash

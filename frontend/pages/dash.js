import { Typography } from '@material-ui/core'
import DashboardSidebar from '../components/dash/DashboardSidebar'
import BlackText from '../components/utils/BlackText'
import theme from '../components/utils/theme'

export default function Dash() {
  return (
    <div style={{ width: '100%', backgroundColor: theme.palette.lightBlue.main }}>
      <DashboardSidebar pageName='Dashboard' />

      <div style={{ marginLeft: '12.9vw', paddingLeft: 38, paddingRight: 38 }}>
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

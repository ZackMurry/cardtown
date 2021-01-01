import theme from '../utils/theme'
import DashSidebarMobile from './DashSidebarMobile'
import DashSidebarDesktop from './DashSidebarDesktop'

export default function DashboardSidebar({ pageName, windowWidth }) {
  return (
    windowWidth >= theme.breakpoints.values.lg
      ? <DashSidebarDesktop pageName={pageName} />
      : <DashSidebarMobile pageName={pageName} />
  )
}

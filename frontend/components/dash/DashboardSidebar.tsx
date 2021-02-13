import { FC } from 'react'
import theme from '../utils/theme'
import DashSidebarMobile from './DashSidebarMobile'
import DashSidebarDesktop from './DashSidebarDesktop'
import PageName from '../types/PageName'

interface Props {
  pageName: PageName
  windowWidth: number
}

const DashboardSidebar: FC<Props> = ({ pageName, windowWidth }) => (
  windowWidth >= theme.breakpoints.values.lg
    ? <DashSidebarDesktop pageName={pageName} />
    : <DashSidebarMobile pageName={pageName} />
)

export default DashboardSidebar

import { FC } from 'react'
import theme from '../utils/theme'
import DashNavbarMobile from './DashNavbarMobile'
import DashNavbarDesktop from './DashNavbarDesktop'
import PageName from '../types/PageName'

interface Props {
  pageName: PageName
  windowWidth: number
}

const DashboardNavbar: FC<Props> = ({ pageName, windowWidth }) => (
  windowWidth >= theme.breakpoints.values.lg
    ? <DashNavbarDesktop pageName={pageName} />
    : <DashNavbarMobile pageName={pageName} />
)

export default DashboardNavbar

import { FC } from 'react'
import theme from 'lib/theme'
import PageName from 'types/PageName'
import DashNavbarMobile from './DashNavbarMobile'
import DashNavbarDesktop from './DashNavbarDesktop'

interface Props {
  pageName?: PageName
  windowWidth: number
}

const DashboardNavbar: FC<Props> = ({ pageName, windowWidth }) =>
  windowWidth >= theme.breakpoints.values.lg ? (
    <DashNavbarDesktop pageName={pageName} />
  ) : (
    <DashNavbarMobile pageName={pageName} />
  )

export default DashboardNavbar

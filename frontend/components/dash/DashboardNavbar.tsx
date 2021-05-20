import { FC } from 'react'
import { useBreakpointValue } from '@chakra-ui/react'
import DashNavbarMobile from './DashNavbarMobile'
import DashNavbarDesktop from './DashNavbarDesktop'

const DashboardNavbar: FC = () => {
  const isDesktop = useBreakpointValue({ md: false, lg: true })
  if (isDesktop) {
    return <DashNavbarDesktop />
  }
  return <DashNavbarMobile />
}
export default DashboardNavbar

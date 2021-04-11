import { FC } from 'react'
import { useBreakpointValue } from '@chakra-ui/react'
import PageName from 'types/PageName'
import DashNavbarMobile from './DashNavbarMobile'
import DashNavbarDesktop from './DashNavbarDesktop'

interface Props {
  pageName?: PageName
}

const DashboardNavbar: FC<Props> = ({ pageName }) => {
  const isDesktop = useBreakpointValue({ md: false, lg: true })
  if (isDesktop) {
    return <DashNavbarDesktop pageName={pageName} />
  }
  return <DashNavbarMobile pageName={pageName} />
}
export default DashboardNavbar

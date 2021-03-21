import { FC, useMemo } from 'react'
import theme from 'lib/theme'
import PageName from 'types/PageName'
import parseJwt from 'lib/parseJwt'
import DashNavbarMobile from './DashNavbarMobile'
import DashNavbarDesktop from './DashNavbarDesktop'

interface Props {
  pageName: PageName
  windowWidth: number
  jwt: string
}

const DashboardNavbar: FC<Props> = ({ pageName, windowWidth, jwt: jwtStr }) => {
  const jwt = useMemo(() => parseJwt(jwtStr), [])
  return windowWidth >= theme.breakpoints.values.lg ? (
    <DashNavbarDesktop pageName={pageName} jwt={jwt} />
  ) : (
    <DashNavbarMobile pageName={pageName} jwt={jwt} />
  )
}

export default DashboardNavbar

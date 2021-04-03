import { useColorMode, IconButton } from '@chakra-ui/react'
import { MoonIcon, SunIcon } from '@chakra-ui/icons'
import { FC } from 'react'

const DashNavbarColorSwitch: FC = () => {
  const { colorMode, toggleColorMode } = useColorMode()

  return (
    <IconButton
      aria-label='Change color mode'
      onClick={toggleColorMode}
      fontSize='large'
      icon={colorMode !== 'light' ? <SunIcon color='darkGray' /> : <MoonIcon color='darkGray' />}
      bg='none'
    />
  )
}

export default DashNavbarColorSwitch

import { Menu, MenuButton, MenuItem, MenuList, Box, Divider, Text, Avatar, useColorModeValue } from '@chakra-ui/react'
import { FC, useContext } from 'react'
import Cookie from 'js-cookie'
import { useRouter } from 'next/router'
import userContext from 'lib/hooks/UserContext'

const DashNavbarDesktopAvatar: FC = () => {
  const { firstName, lastName } = useContext(userContext)
  const router = useRouter()
  const bgColor = useColorModeValue('white', 'darkElevated')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')

  const handleSignOut = () => {
    Cookie.remove('jwt')
    router.push('/login')
  }

  return (
    <Menu placement='top-end'>
      <MenuButton>
        <Avatar name={`${firstName} ${lastName}`} width='30px' height='30px' fontSize='small' />
      </MenuButton>
      <MenuList bg={bgColor} borderColor={borderColor}>
        <Box padding='0.4rem 0.8rem'>
          <Text fontSize={14} paddingBottom='0.4rem' fontWeight='light'>
            Signed in as
            <span style={{ fontWeight: 'normal' }}>{` ${firstName} ${lastName}`}</span>
          </Text>
          <Divider />
        </Box>
        <MenuItem fontSize='14px'>Your profile</MenuItem>
        <MenuItem fontSize='14px'>Your team</MenuItem>
        <Divider m='5px 0' />
        <MenuItem fontSize='14px'>Settings</MenuItem>
        <MenuItem fontSize='14px'>Help</MenuItem>
        <MenuItem onClick={handleSignOut} fontSize='14px'>
          Sign out
        </MenuItem>
      </MenuList>
    </Menu>
  )
}

export default DashNavbarDesktopAvatar

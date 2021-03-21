import { Menu, MenuButton, MenuItem, MenuList, Box, Divider, Text, Avatar } from '@chakra-ui/react'
import { FC } from 'react'
import Cookie from 'js-cookie'
import { useRouter } from 'next/router'
import JwtBody from 'types/JwtBody'

interface Props {
  jwt: JwtBody
}

const DashNavbarDesktopAvatar: FC<Props> = ({ jwt }) => {
  const router = useRouter()

  const handleSignOut = () => {
    Cookie.remove('jwt')
    router.push('/login')
  }

  return (
    <Menu placement='top-end'>
      <MenuButton>
        <Avatar name={`${jwt.firstName} ${jwt.lastName}`} width='30px' height='30px' fontSize='small' />
      </MenuButton>
      <MenuList>
        <Box padding='0.4rem 0.8rem'>
          <Text fontSize={14} paddingBottom='0.4rem' fontWeight='light'>
            Signed in as
            <span style={{ fontWeight: 'normal' }}>{` ${jwt.firstName} ${jwt.lastName}`}</span>
          </Text>
          <Divider />
        </Box>
        <MenuItem fontSize='14px'>Your profile</MenuItem>
        <MenuItem fontSize='14px'>Your team</MenuItem>
        <Divider margin='5px 0' />
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

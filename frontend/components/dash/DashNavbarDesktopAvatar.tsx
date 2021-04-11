import { Menu, MenuButton, MenuItem, MenuList, Box, Divider, Text, Avatar, useColorModeValue } from '@chakra-ui/react'
import { FC, useContext } from 'react'
import Cookie from 'js-cookie'
import { useRouter } from 'next/router'
import userContext from 'lib/hooks/UserContext'
import Link from 'next/link'
import ProfilePicture from 'components/users/ProfilePicture'

const DashNavbarDesktopAvatar: FC = () => {
  const { firstName, lastName, id } = useContext(userContext)
  const router = useRouter()
  const bgColor = useColorModeValue('white', 'darkElevated')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')

  const handleSignOut = () => {
    Cookie.remove('jwt')
    router.push('/login')
  }

  return (
    <Menu placement='top-end'>
      <MenuButton ml='5px'>
        <ProfilePicture id={id} firstName={firstName} lastName={lastName} />
      </MenuButton>
      <MenuList bg={bgColor} borderColor={borderColor}>
        <Box padding='0.4rem 0.8rem'>
          <Text fontSize={14} paddingBottom='0.4rem' fontWeight='light'>
            Signed in as
            <span style={{ fontWeight: 'normal' }}>{` ${firstName} ${lastName}`}</span>
          </Text>
          <Divider />
        </Box>
        <MenuItem fontSize='14px'>Your team</MenuItem>
        <Divider m='5px 0' />
        <MenuItem fontSize='14px'>
          <Link href='/settings' passHref>
            <a style={{ width: '100%' }}>Settings</a>
          </Link>
        </MenuItem>
        <MenuItem fontSize='14px'>Help</MenuItem>
        <MenuItem onClick={handleSignOut} fontSize='14px'>
          Sign out
        </MenuItem>
      </MenuList>
    </Menu>
  )
}

export default DashNavbarDesktopAvatar

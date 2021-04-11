import { IconButton, Menu, MenuButton, MenuItem, MenuList, useColorModeValue } from '@chakra-ui/react'
import { AddIcon } from '@chakra-ui/icons'
import { FC } from 'react'
import Link from 'next/link'

const DashNavbarNewButton: FC = () => {
  const bgColor = useColorModeValue('white', 'darkElevated')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')
  return (
    <Menu placement='top-end'>
      <MenuButton as={IconButton} aria-label='New' icon={<AddIcon color='darkGray' />} bg='transparent' />
      <MenuList bg={bgColor} borderColor={borderColor}>
        <MenuItem fontSize='14px'>
          <Link href='/cards/new' passHref>
            <a style={{ width: '100%' }}>New card</a>
          </Link>
        </MenuItem>
        <MenuItem fontSize='14px'>
          <Link href='/arguments/new' passHref>
            <a style={{ width: '100%' }}>New argument</a>
          </Link>
        </MenuItem>
      </MenuList>
    </Menu>
  )
}

export default DashNavbarNewButton

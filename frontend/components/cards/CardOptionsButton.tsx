import { IconButton, Menu, MenuButton, MenuList, MenuItem, useColorModeValue } from '@chakra-ui/react'
import MoreVertIcon from '@material-ui/icons/MoreVert'
import { useRouter } from 'next/router'
import { FC, useContext, useState } from 'react'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import userContext from 'lib/hooks/UserContext'

interface Props {
  id: string
  onEdit: () => void
}

// todo: clone card
const CardOptionsButton: FC<Props> = ({ id, onEdit }) => {
  const { setErrorMessage } = useContext(errorMessageContext)
  const { jwt } = useContext(userContext)
  const menuBgColor = useColorModeValue('white', 'darkElevated')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')

  const router = useRouter()

  const handleDelete = async () => {
    const response = await fetch(`/api/v1/cards/id/${encodeURIComponent(id)}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${jwt}` }
    })
    if (response.ok) {
      router.push('/cards')
    } else {
      setErrorMessage(`Error deleting card: ${response.status}`)
    }
  }

  return (
    <Menu>
      <MenuButton>
        <IconButton aria-label='Card settings' bg='none'>
          <MoreVertIcon />
        </IconButton>
      </MenuButton>
      <MenuList bg={menuBgColor} borderColor={borderColor}>
        <MenuItem onClick={handleDelete}>Delete</MenuItem>
        <MenuItem onClick={onEdit}>Edit</MenuItem>
      </MenuList>
    </Menu>
  )
}

export default CardOptionsButton

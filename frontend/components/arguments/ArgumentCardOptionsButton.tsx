import MoreVertIcon from '@material-ui/icons/MoreVert'
import { FC, useContext } from 'react'
import userContext from 'lib/hooks/UserContext'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import { Menu, MenuButton, MenuList, MenuItem, useColorModeValue, IconButton } from '@chakra-ui/react'

interface Props {
  cardId: string
  argumentId: string
  indexInArgument: number
  onEdit: () => void
  onRemove: () => void
}

const ArgumentCardOptionsButton: FC<Props> = ({ cardId, argumentId, onEdit, indexInArgument, onRemove }) => {
  const { setErrorMessage } = useContext(errorMessageContext)
  const { jwt } = useContext(userContext)
  const menuBgColor = useColorModeValue('white', 'darkElevated')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')

  const handleRemove = async () => {
    const response = await fetch(
      `/api/v1/arguments/id/${argumentId}/cards/id/${encodeURIComponent(cardId)}?index=${indexInArgument}`,
      {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${jwt}` }
      }
    )
    if (response.ok) {
      onRemove()
    } else {
      setErrorMessage(`Error removing card: ${response.status}`)
    }
  }

  return (
    <Menu>
      <MenuButton>
        <IconButton aria-label='Card options' bg='none'>
          <MoreVertIcon />
        </IconButton>
      </MenuButton>
      <MenuList bg={menuBgColor} borderColor={borderColor}>
        <MenuItem onClick={handleRemove}>Remove</MenuItem>
        <MenuItem onClick={onEdit}>Edit</MenuItem>
      </MenuList>
    </Menu>
  )
}

export default ArgumentCardOptionsButton

import { IconButton, Menu, MenuButton, MenuItem, MenuList, useColorModeValue } from '@chakra-ui/react'
import MoreVertIcon from '@material-ui/icons/MoreVert'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import userContext from 'lib/hooks/UserContext'
import { FC, useContext } from 'react'
import { ResponseAnalytic } from 'types/analytic'

interface Props {
  analytic: ResponseAnalytic
  argId: string
  onDelete: () => void
  onEdit: () => void
}

const ArgumentAnalyticOptionsButton: FC<Props> = ({ analytic, onDelete, onEdit, argId }) => {
  const { setErrorMessage } = useContext(errorMessageContext)
  const { jwt } = useContext(userContext)
  const menuBgColor = useColorModeValue('white', 'darkElevated')
  const borderColor = useColorModeValue('grayBorder', 'darkGrayBorder')

  const handleDelete = async () => {
    const response = await fetch(`/api/v1/arguments/id/${argId}/analytics/id/${analytic.id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${jwt}` }
    })
    if (response.ok) {
      onDelete()
    } else {
      setErrorMessage(`Error deleting analytic. Response status: ${response.status}`)
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
        <MenuItem onClick={handleDelete}>Delete</MenuItem>
        <MenuItem onClick={onEdit}>Edit</MenuItem>
      </MenuList>
    </Menu>
  )
}

export default ArgumentAnalyticOptionsButton

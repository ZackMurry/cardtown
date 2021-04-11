import { Avatar } from '@chakra-ui/react'
import { FC } from 'react'

interface Props {
  l?: string
  firstName: string
  lastName: string
  id: string
}

const ProfilePicture: FC<Props> = ({ l = '30px', firstName, lastName, id }) => (
  <Avatar w={l} h={l} name={`${firstName} ${lastName}`} src={`/api/v1/static/images/pfp/${id}.png`} fontSize='small' />
)

export default ProfilePicture

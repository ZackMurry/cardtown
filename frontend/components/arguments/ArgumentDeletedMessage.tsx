import { Alert, AlertIcon, Link as ChakraLink, Text } from '@chakra-ui/react'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import userContext from 'lib/hooks/UserContext'
import { useRouter } from 'next/router'
import { FC, useContext } from 'react'

interface Props {
  id: string
  onRestore: () => void
}

const ArgumentDeletedMessage: FC<Props> = ({ id, onRestore }) => {
  const { jwt } = useContext(userContext)
  const { setErrorMessage } = useContext(errorMessageContext)
  const router = useRouter()

  const handleRestore = async () => {
    const response = await fetch(`/api/v1/arguments/id/${id}/restore`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${jwt}` }
    })
    if (response.ok) {
      onRestore()
    } else {
      setErrorMessage(`An error occurred while restoring this argument. Response status: ${response.status}`)
    }
  }

  const handlePermanentlyDelete = async () => {
    const response = await fetch(`/api/v1/arguments/id/${id}/delete`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${jwt}` }
    })
    if (response.ok) {
      router.push('/arguments')
    } else {
      setErrorMessage(`An error occurred while permanently deleting this argument. Response status: ${response.status}`)
    }
  }

  return (
    <Alert status='error' mb='1.5vh' borderRadius='5px'>
      <AlertIcon />
      <Text>
        This argument has been deleted. If you'd like to use it, you'll have to{' '}
        <ChakraLink color='cardtownBlue' onClick={handleRestore}>
          restore it
        </ChakraLink>
        . If you wish to remove all data about this argument, you can{' '}
        <ChakraLink color='cardtownBlue' onClick={handlePermanentlyDelete}>
          permanently delete
        </ChakraLink>{' '}
        it (the associated cards will not be deleted).
      </Text>
    </Alert>
  )
}

export default ArgumentDeletedMessage

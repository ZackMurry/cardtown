import { Alert, AlertIcon, Link as ChakraLink } from '@chakra-ui/react'
import { errorMessageContext } from 'lib/hooks/ErrorMessageContext'
import userContext from 'lib/hooks/UserContext'
import { FC, useContext } from 'react'

interface Props {
  id: string
  onRestore: () => void
}

const ArgumentDeletedMessage: FC<Props> = ({ id, onRestore }) => {
  const { jwt } = useContext(userContext)
  const { setErrorMessage } = useContext(errorMessageContext)

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

  return (
    <Alert status='error' mb='1.5vh' borderRadius='5px'>
      <AlertIcon />
      This argument has been deleted. If you'd like to use it, you'll have to
      <ChakraLink ml='4px' color='cardtownBlue' onClick={handleRestore}>
        restore it
      </ChakraLink>
      .
    </Alert>
  )
}

export default ArgumentDeletedMessage

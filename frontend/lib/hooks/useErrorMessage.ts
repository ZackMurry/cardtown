import { useCallback, useState } from 'react'
import { ErrorMessageContext } from './ErrorMessageContext'

const useErrorMessage = (): ErrorMessageContext => {
  const [errorMessage, _setErrorMessage] = useState(null)

  const setErrorMessage = useCallback((msg: string): void => {
    _setErrorMessage(msg)
  }, [])

  return {
    errorMessage,
    setErrorMessage
  }
}

export default useErrorMessage

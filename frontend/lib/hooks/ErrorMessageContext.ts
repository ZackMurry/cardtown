import { createContext } from 'react'

export interface ErrorMessageContext {
  errorMessage: string
  setErrorMessage: (msg: string) => void
}

// eslint-disable-next-line @typescript-eslint/no-empty-function
export const errorMessageContext = createContext<ErrorMessageContext>({ errorMessage: null, setErrorMessage: () => {} })

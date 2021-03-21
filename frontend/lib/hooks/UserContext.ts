import { createContext } from 'react'
import { UserModel } from 'types/user'

const userContext = createContext<UserModel | null>(null)

export default userContext

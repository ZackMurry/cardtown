import ResponseUserDetails from './ResponseUserDetails'

export default interface CardHeader {
  id: string
  owner: ResponseUserDetails
  tag: string
  cite: string
  timeCreatedAt: number
  lastModified: number
}

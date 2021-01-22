import ResponseCard from './ResponseCard'
import ResponseUserDetails from './ResponseUserDetails'

export default interface ResponseArgument {
  id: string,
  owner: ResponseUserDetails,
  name: string,
  cards: ResponseCard[]
}

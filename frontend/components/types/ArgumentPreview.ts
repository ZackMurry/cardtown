import CardHeader from './CardHeader'
import ResponseUserDetails from './ResponseUserDetails'

export default interface ArgumentPreview {
  id: string
  name: string
  owner: ResponseUserDetails
  cards: CardHeader[]
}

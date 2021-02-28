import ResponseUserDetails from './ResponseUserDetails'

export default interface ArgumentWithCardModel {
  name: string
  id: string
  owner: ResponseUserDetails
  indexInArgument: number
}

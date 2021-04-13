import CardHeader from './card'
import ResponseUserDetails from './user'

export interface ArgumentPreview {
  id: string
  name: string
  owner: ResponseUserDetails
  cards: CardHeader[]
}

export interface ArgumentWithCardModel {
  name: string
  id: string
  owner: ResponseUserDetails
  indexInArgument: number
}

export interface ResponseArgument {
  id: string
  owner: ResponseUserDetails
  name: string
  cards: ResponseCard[]
}

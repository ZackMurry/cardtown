import { ResponseAnalytic } from './analytic'
import CardHeader, { ResponseCard } from './card'
import ResponseUserDetails from './user'

export interface ArgumentPreview {
  id: string
  name: string
  owner: ResponseUserDetails
  cards: CardHeader[]
  deleted: boolean
}

export interface ArgumentWithCardModel {
  name: string
  id: string
  owner: ResponseUserDetails
  indexInArgument: number
}

export interface ResponseArgumentCard extends ResponseCard {
  position: number
}

export interface ResponseArgument {
  id: string
  owner: ResponseUserDetails
  name: string
  cards: ResponseArgumentCard[]
  analytics: ResponseAnalytic[]
  deleted: boolean
}

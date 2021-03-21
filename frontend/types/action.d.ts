import ActionArgumentPreview from './ActionArgumentPreview'
import ActionCardPreview from './ActionCardPreview'
import ResponseUserDetails from './ResponseUserDetails'

export interface ActionCardPreview {
  id: string
  tag: string
  cite: string
  bodyText: string
  numRelatedArguments: number
}

export interface ActionArgumentPreview {
  id: string
  name: string
  numCards: number
}

export type ActionType =
  | 'CREATE_CARD'
  | 'DELETE_CARD'
  | 'EDIT_CARD'
  | 'CREATE_ARGUMENT'
  | 'DELETE_ARGUMENT'
  | 'EDIT_ARGUMENT'
  | 'ADD_CARD_TO_ARGUMENT'
  | 'REMOVE_CARD_FROM_ARGUMENT'
  | 'JOIN_TEAM'

export interface ResponseAction {
  actionType: ActionType
  subject: ResponseUserDetails
  time: number
  user?: ResponseUserDetails
  card?: ActionCardPreview
  argument?: ActionArgumentPreview
}

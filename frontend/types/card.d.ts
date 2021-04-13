import ResponseUserDetails from './user'

export interface CardHeader {
  id: string
  owner: ResponseUserDetails
  tag: string
  cite: string
  timeCreatedAt: number
  lastModified: number
}

export interface CardPreview {
  id: string
  owner: ResponseUserDetails
  tag: string
  cite: string
  bodyText: string
  timeCreatedAt: number
  lastModified: number
}

export interface ResponseCard {
  id: string
  owner: ResponseUserDetails
  tag: string
  cite: string
  citeInformation: string
  bodyHtml: string
  bodyDraft: string
  bodyText: string
  timeCreatedAt: number
  lastModified: number
}

import ResponseUserDetails from './ResponseUserDetails'

export default interface ResponseCard {
  id: string
  owner: ResponseUserDetails
  tag: string
  cite: string
  citeInformation: string
  bodyHtml: string
  bodyDraft: any
}

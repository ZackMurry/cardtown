import ResponseUserDetails from './ResponseUserDetails'

interface CardPreview {
  id: string,
  owner: ResponseUserDetails,
  tag: string,
  cite: string,
  bodyText: string
}

export default CardPreview

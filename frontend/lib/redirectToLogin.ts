import { ServerResponse } from 'http'

const redirectToLogin = (res: ServerResponse, redirectPath: string): void => {
  res.statusCode = 302
  res.setHeader('location', `/login?redirect=${encodeURIComponent(redirectPath)}`)
  res.end()
}

export default redirectToLogin

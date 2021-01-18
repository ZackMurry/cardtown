import { ServerResponse } from 'http'

export default function redirectToLogin(res: ServerResponse, redirectPath: string) {
  res.statusCode = 302
  res.setHeader('location', `/login?redirect=${encodeURIComponent(redirectPath)}`)
  res.end()
}

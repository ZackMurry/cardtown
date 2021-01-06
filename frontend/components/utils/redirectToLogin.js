export default function redirectToLogin(res, redirectPath) {
  res.statusCode = 302
  res.setHeader('location', `/login?redirect=${encodeURIComponent(redirectPath)}`)
  res.end()
}

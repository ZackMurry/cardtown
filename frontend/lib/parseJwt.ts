import jwtDecode from 'jwt-decode'
import Cookie from 'js-cookie'
import JwtBody from 'types/JwtBody'

interface RawJwt {
  ek: string
  sub: string
  f_name: string
  l_name: string
}

const parseJwt = (jwt: string = Cookie.get('jwt')): JwtBody | null => {
  if (!jwt) {
    return
  }
  const rawJwt = jwtDecode(jwt) as RawJwt
  const fixedJwt: JwtBody = {
    encryptionKey: rawJwt.ek,
    email: rawJwt.sub,
    firstName: rawJwt.f_name,
    lastName: rawJwt.l_name
  }
  return fixedJwt
}

export default parseJwt

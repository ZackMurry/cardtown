export interface UserModel {
  firstName: string
  lastName: string
  encryptionKey: string
  email: string
  jwt: string
  id: string
}

export interface FirstLastName {
  first: string
  last: string
}

export interface ResponseUserDetails {
  name: FirstLastName
  id: string
}

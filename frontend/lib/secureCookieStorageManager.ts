import { ColorMode, storageKey, StorageManager } from '@chakra-ui/react'

/**
 * Simple object to handle read-write to cookies
 */
const secureCookieStorageManager = (cookies = ''): StorageManager => ({
  get(init?) {
    const match = cookies.match(new RegExp(`(^| )${storageKey}=([^;]+)`))

    if (match) {
      return match[2] as ColorMode
    }
    return init
  },
  set(value) {
    document.cookie = `${storageKey}=${value}; max-age=31536000; SameSite=Secure; path=/`
  },
  type: 'cookie'
})

export default secureCookieStorageManager

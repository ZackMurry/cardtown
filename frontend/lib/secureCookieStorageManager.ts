import { ColorMode, storageKey, StorageManager } from '@chakra-ui/react'

/**
 * Simple object to handle read-write to cookies
 */
const secureCookieStorageManager = (cookies = ''): StorageManager => ({
  get(init?) {
    const match = cookies.match(new RegExp(`(^| )${storageKey}=([^;]+)`))

    if (match) {
      console.log('match: ', match)
      console.log(match[2])
      return match[2] as ColorMode
    }
    console.log('using fallback value ', init)
    return init
  },
  set(value) {
    document.cookie = `${storageKey}=${value}; max-age=31536000; SameSite=Secure; path=/`
    console.warn('setting color mode to: ', value)
  },
  type: 'cookie'
})

export default secureCookieStorageManager

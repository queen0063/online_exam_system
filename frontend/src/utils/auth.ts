import { TOKEN_KEY, USER_KEY, storage } from './storage'
import type { UserInfo } from '@/types'

export function getToken() {
  return storage.get<string>(TOKEN_KEY, '')
}

export function setToken(token: string) {
  storage.set(TOKEN_KEY, token)
}

export function clearToken() {
  storage.remove(TOKEN_KEY)
}

export function getCachedUser() {
  return storage.get<UserInfo>(USER_KEY)
}

export function setCachedUser(user: UserInfo) {
  storage.set(USER_KEY, user)
}

export function clearCachedUser() {
  storage.remove(USER_KEY)
}

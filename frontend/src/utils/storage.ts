export const TOKEN_KEY = 'online_exam_token'
export const USER_KEY = 'online_exam_user'
export const ANSWER_CACHE_KEY = 'online_exam_answer_cache'

export const storage = {
  get<T>(key: string, fallback?: T): T | null {
    const value = window.localStorage.getItem(key)
    if (!value) {
      return fallback ?? null
    }

    try {
      return JSON.parse(value) as T
    } catch {
      return (value as T) ?? fallback ?? null
    }
  },
  set(key: string, value: unknown) {
    window.localStorage.setItem(key, JSON.stringify(value))
  },
  remove(key: string) {
    window.localStorage.removeItem(key)
  }
}

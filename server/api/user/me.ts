import { getToken } from '#auth'
import type { UserMeData } from '~/types'

export default defineEventHandler(async (event) => {
  const token = await getToken({ event })

  const defaultHeaders: HeadersInit = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token?.access_token}`,
  }

  const result = await $fetch(`http://10.42.2.96:9909/api/users/me`, {
    headers: defaultHeaders
  })

  return result
})

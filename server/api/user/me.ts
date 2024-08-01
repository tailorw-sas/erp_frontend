import { getToken } from '#auth'
import type { UserMeData } from '~/types'

export default defineEventHandler(async (event) => {
  const token = await getToken({ event })

  const defaultHeaders: HeadersInit = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token?.access_token}`,
  }

  const result = await $fetch(`${process.env.VITE_APP_BASE_URL}/settings/api/manage-employee/me`, {
    headers: defaultHeaders
  })

  return result
})

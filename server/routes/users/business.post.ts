import { getToken } from '#auth'

export default defineEventHandler(async (event) => {
  const body = await readBody(event)
  const token = await getToken({ event })

  const defaultHeaders: HeadersInit = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token?.access_token}`,
  }

  const response: any = await $fetch(`${process.env.VITE_APP_BASE_URL}/identity/api/users/${(token as any).userId}/business`, {
    method: 'PATCH',
    body,
    headers: defaultHeaders
  })

  return response
})

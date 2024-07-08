import { getToken } from '#auth'

export default defineEventHandler(async (event): Promise<any> => {
  const microservice = getRouterParam(event, 'microservice')
  const controller = getRouterParam(event, 'controller')
  const id = getRouterParam(event, 'id')

  const token = await getToken({ event })

  const defaultHeaders: HeadersInit = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token?.access_token}`,
  }

  const response = await $fetch(`${process.env.VITE_APP_BASE_URL}/${microservice}/api/${controller}/${id}`, {
    method: 'GET',
    headers: defaultHeaders
  })

  return response
})

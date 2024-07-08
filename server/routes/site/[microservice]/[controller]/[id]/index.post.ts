import { getToken } from '#auth'

export default defineEventHandler(async (event): Promise<any> => {
  const body = await readBody(event)
  const microservice = getRouterParam(event, 'microservice')
  const controller = getRouterParam(event, 'controller')
  const id = getRouterParam(event, 'id')

  const token = await getToken({ event })

  const defaultHeaders: HeadersInit = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token?.access_token}`,
  }
  const url = `${process.env.VITE_APP_BASE_URL}/${microservice}/api/${controller}/${id}`
  const response = await $fetch(url, {
    method: 'POST',
    body,
    headers: defaultHeaders
  })
  return response
})

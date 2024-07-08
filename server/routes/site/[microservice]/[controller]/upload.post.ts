import { getToken } from '#auth'

export default defineEventHandler(async (event): Promise<any> => {
  const body = await readFormData(event)

  const microservice = getRouterParam(event, 'microservice')
  const controller = getRouterParam(event, 'controller')

  const token = await getToken({ event })

  const defaultHeaders: HeadersInit = {
    Authorization: `Bearer ${token?.access_token}`,
  }

  const response = await $fetch(`${process.env.VITE_APP_BASE_URL}/${microservice}/api/${controller}`, {
    method: 'POST',
    body,
    headers: defaultHeaders
  })
  return response
})

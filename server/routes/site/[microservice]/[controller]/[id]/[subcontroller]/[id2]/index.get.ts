import { getToken } from '#auth'

export default defineEventHandler(async (event): Promise<any> => {
  const microservice = getRouterParam(event, 'microservice')
  const controller = getRouterParam(event, 'controller')
  const subController = getRouterParam(event, 'subcontroller')
  const id = getRouterParam(event, 'id')
  const id2 = getRouterParam(event, 'id2')

  const token = await getToken({ event })

  const defaultHeaders: HeadersInit = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token?.access_token}`,
  }

  const response = await $fetch(`${process.env.VITE_APP_BASE_URL}/${microservice}/api/${controller}/${id}/${subController}/${id2}`, {
    method: 'GET',
    headers: defaultHeaders
  })

  return response
})

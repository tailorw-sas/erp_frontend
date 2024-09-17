import { getToken } from '#auth'

export default defineEventHandler(async (event): Promise<any> => {
  const body = await readBody(event)
  const microservice = getRouterParam(event, 'microservice')
  const controller = getRouterParam(event, 'controller')

  const token = await getToken({ event })

  const defaultHeaders: HeadersInit = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token?.access_token}`,
  }
  console.log(body)
  console.log('Si entro al reporte')

  const response = await $fetch(`${process.env.VITE_APP_BASE_URL}/${microservice}/api/${controller}/report`, {
    method: 'POST',
    body,
    headers: defaultHeaders
  })

  return response
})

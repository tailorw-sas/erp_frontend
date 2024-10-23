import { getToken } from '#auth'

export default defineEventHandler(async (event): Promise<any> => {
  let bodyFormData = null
  let bodyJson = null
  try {
    bodyJson = await readBody(event)
  }
  catch (error: any) {

  }
  try {
    bodyFormData = await readFormData(event)
  }
  catch (error: any) {

  }
  // const bodyJson = await readBody(event)
  const microservice = getRouterParam(event, 'microservice')
  // const controller = getRouterParam(event, 'controller')

  const token = await getToken({ event })

  const defaultHeaders: HeadersInit = {
    Authorization: `Bearer ${token?.access_token}`,
    contentType: bodyFormData ? 'multipart/form-data' : 'application/json',
  }

  if (bodyFormData) {
    const response = await $fetch(`${process.env.VITE_APP_BASE_URL}/${microservice}/api/share-file`, {
      method: 'POST',
      body: bodyFormData,
      headers: defaultHeaders
    })
    return response
  }
  else {
    const response = await $fetch(`${process.env.VITE_APP_BASE_URL}/${microservice}/api/share-file/search`, {
      method: 'POST',
      body: bodyJson,
      headers: defaultHeaders
    })
    return response
  }
})

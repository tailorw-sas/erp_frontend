import { getToken } from '#auth'

export default defineEventHandler(async (event): Promise<any> => {
  const body = await readBody(event)
  const microservice = getRouterParam(event, 'microservice')
  const controller = getRouterParam(event, 'controller')

  const token = await getToken({ event })

  const defaultHeaders: HeadersInit = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token?.access_token}`,
    // 'Accept': 'application/pdf',
  }
  try {
    const response = await $fetch(`${process.env.VITE_APP_BASE_URL}/${microservice}/api/${controller}/generate/template`, {
      method: 'POST',
      body,
      headers: defaultHeaders
    })
    return response
  }
  catch (error) {
    console.log(error)
  }
  // const response = await $fetch(`${process.env.VITE_APP_BASE_URL}/${microservice}/api/${controller}/generate/template`, {
  //   method: 'POST',
  //   body,
  //   headers: defaultHeaders
  // })
  // return response
})

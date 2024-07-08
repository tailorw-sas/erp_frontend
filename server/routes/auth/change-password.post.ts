import { helpers } from '@/utils/helpers'

export default defineEventHandler(async (event) => {
  const body = await readBody(event)

  // const aux = helpers()
  // const { recaptcha: { secretKey } } = useRuntimeConfig(event)
  // const isHuman = await aux.verifyRecaptchaToken(body.tokenCaptcha, secretKey)
  // if (!isHuman) {
  //   throw createError({
  //     statusCode: 403,
  //     statusMessage: 'Error verificando captcha',
  //   })
  // }

  const defaultHeaders: HeadersInit = {
    'Content-Type': 'application/json',
  }

  const payload = body.payload
  const response: any = await $fetch(`${process.env.VITE_APP_BASE_URL}/identity/api/auth/change-password`, {
    method: 'POST',
    payload,
    headers: defaultHeaders
  })

  return response
})

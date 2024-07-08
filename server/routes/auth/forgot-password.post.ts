import { helpers } from '@/utils/helpers'

export default defineEventHandler(async (event) => {
  const body = await readBody(event)

  // const appRuntimeConfig = useRuntimeConfig()
  // const { recaptcha: { secretKey } } = appRuntimeConfig
  // const aux = helpers()
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

  const response: any = await $fetch(`${process.env.VITE_APP_BASE_URL}/identity/api/auth/forgot-password?email=${body.email}`, {
    method: 'POST',
    headers: defaultHeaders
  })

  return response
})

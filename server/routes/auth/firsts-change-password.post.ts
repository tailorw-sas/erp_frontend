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

  const response: any = await $fetch(`${process.env.VITE_APP_BASE_URL}/identity/api/auth/firsts-change-password`, {
    method: 'POST',
    body,
    headers: defaultHeaders
  })

  return response
})

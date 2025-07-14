import { useRouter } from 'vue-router'

export default defineNuxtPlugin((nuxtApp) => {
  const config = useRuntimeConfig()
  const router = useRouter()

  const api = $fetch.create({
    baseURL: config.app.baseURL,
    timeout: 600_000,
    onRequest({ request, options }) {
      if (process.env.NODE_ENV !== 'production') {
        // eslint-disable-next-line no-console
        console.log('Requesting:', request, options)
      }
    },
    onResponseError({ response }) {
      if (response.status === 401) {
        router.push('/auth/login')
      }
    }
  })

  nuxtApp.provide('api', api)
})

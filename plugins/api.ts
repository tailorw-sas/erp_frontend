import { signOut } from 'next-auth/react'

export default defineNuxtPlugin({
  setup() {
    const config = useRuntimeConfig()
    const api = $fetch.create({
      baseURL: config.app.baseURL,
      onResponseError({ response }) {
        if (response.status === 401) {
          signOut({ callbackUrl: '/auth/login' })
        }
      }
    })

    return {
      provide: {
        api
      }
    }
  }
})

// plugins/auth-interceptor.client.ts
export default defineNuxtPlugin(() => {
  const { signOut, status } = useAuth()

  // Interceptor global para todas las peticiones $fetch
  const originalFetch = globalThis.$fetch

  globalThis.$fetch = originalFetch.create({
    onResponseError({ response }) {
      // Interceptar 401 de cualquier API (excepto las de auth que ya las maneja NextAuth)
      if (response.status === 401 && !response.url?.includes('/api/auth/')) {
        console.warn('API returned 401, session expired. Redirecting to login...')

        // Verificar que estemos autenticados antes de hacer signOut
        if (status.value === 'authenticated') {
          signOut({
            callbackUrl: '/auth/login',
            redirect: true
          }).catch((error) => {
            console.error('Error during signOut:', error)
            // Fallback: redirección manual
            navigateTo('/auth/login')
          })
        }
        else {
          // Si ya no estamos autenticados, solo redirigir
          navigateTo('/auth/login')
        }
      }
    }
  })

  // También interceptar errores específicos en server-side rendering
  if (process.client) {
    // Escuchar cambios en el estado de autenticación
    watch(() => status.value, (newStatus) => {
      if (newStatus === 'unauthenticated') {
        console.info('Auth status changed to unauthenticated, redirecting...')
        navigateTo('/auth/login')
      }
    })
  }
})

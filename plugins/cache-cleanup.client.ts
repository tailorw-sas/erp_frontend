// plugins/cache-cleanup.client.ts
export default defineNuxtPlugin(() => {
  const { status, data } = useAuth()

  // Función centralizada para limpiar cache de usuario
  const clearUserCache = async (userId?: string) => {
    try {
      if (!userId) {
        // Intentar obtener userId de la sesión actual
        userId = (data.value as any)?.user?.userId
      }

      if (userId) {
        console.info('Clearing user cache for:', userId)

        // Limpiar IndexedDB
        const { clearUserData } = await import('~/utils/indexedDbClient')
        await clearUserData(userId)

        // Limpiar cache en memoria
        const { useDynamicCacheStore } = await import('~/stores/useDynamicCacheStore')
        const dynamicCache = useDynamicCacheStore()
        dynamicCache.delete(`userData_${userId}`)

        console.info('User cache cleared successfully')
      }
    }
    catch (error) {
      console.error('Error clearing user cache:', error)
    }
  }

  // ✅ NUEVO: Interceptar TODOS los calls a $fetch hacia signOut
  if (process.client) {
    // Override global $fetch para interceptar calls al endpoint de signOut
    const originalFetch = globalThis.$fetch

    globalThis.$fetch = originalFetch.create({
      async onRequest({ request }) {
        // Detectar si es una petición de signOut
        const url = request.toString()
        if (url.includes('/api/auth/signout') || url.includes('/signout')) {
          console.info('SignOut detected, clearing user cache...')
          await clearUserCache()
        }
      }
    })
  }

  // Escuchar cambios en el estado de autenticación
  watch(() => status.value, async (newStatus, oldStatus) => {
    // Cuando cambia de authenticated a unauthenticated
    if (oldStatus === 'authenticated' && newStatus === 'unauthenticated') {
      console.warn('Auth status changed to unauthenticated, clearing cache')
      await clearUserCache()
    }
  })

  // Limpiar cache cuando la pestaña se cierra/refresca sin autenticación
  if (process.client) {
    window.addEventListener('beforeunload', async () => {
      if (status.value !== 'authenticated') {
        try {
          // Limpiar datos expirados en background
          const { clearUserData } = await import('~/utils/indexedDbClient')
          await clearUserData() // Sin userId = limpiar todos los datos expirados
        }
        catch (error) {
          console.error('Error cleaning up on beforeunload:', error)
        }
      }
    })

    // Limpiar cache cuando la pestaña vuelve a estar activa si no hay sesión
    document.addEventListener('visibilitychange', async () => {
      if (!document.hidden && status.value === 'unauthenticated') {
        await clearUserCache()
      }
    })
  }

  // Exponer función para uso manual
  return {
    provide: {
      clearUserCache
    }
  }
})

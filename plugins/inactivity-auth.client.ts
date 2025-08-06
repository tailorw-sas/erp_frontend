// plugins/inactivity-auth.client.ts - Fixed with function hoisting

export default defineNuxtPlugin(() => {
  const { signOut, status, data } = useAuth()

  // Configuration
  const INACTIVITY_TIMEOUT = 15 * 60 * 1000 // 15 minutes in ms
  const WARNING_TIME = 2 * 60 * 1000 // Warn 2 minutes before

  let inactivityTimer: NodeJS.Timeout | null = null
  let warningTimer: NodeJS.Timeout | null = null
  let lastActivity = Date.now()
  let warningShown = false

  // Events that count as user activity
  const activityEvents = [
    'mousedown',
    'mousemove',
    'keypress',
    'scroll',
    'touchstart',
    'click',
    'keydown'
  ]

  // ✅ SOLUTION: Use function declarations (hoisted) instead of const/arrow functions
  async function logoutDueToInactivity() {
    if (status.value === 'authenticated') {
      console.warn('Logging out due to inactivity')

      // ✅ FIXED: Limpiar cache de usuario específicamente
      try {
        const { data: sessionData } = useAuth()
        const userId = (sessionData.value as any)?.user?.userId

        if (userId) {
          // Limpiar IndexedDB
          const { clearUserData } = await import('~/utils/indexedDbClient')
          await clearUserData(userId)

          // Limpiar cache en memoria
          const { useDynamicCacheStore } = await import('~/stores/useDynamicCacheStore')
          const dynamicCache = useDynamicCacheStore()
          dynamicCache.delete(`userData_${userId}`)

          console.info('User cache cleared on inactivity logout')
        }
      }
      catch (error) {
        console.error('Error clearing user cache on logout:', error)
      }

      try {
        await signOut({
          callbackUrl: '/auth/login?reason=inactivity',
          redirect: true
        })
      }
      catch (error) {
        console.error('Error during inactivity logout:', error)
        navigateTo('/auth/login?reason=inactivity')
      }
    }
  }

  function checkTokenExpiration() {
    if (status.value === 'authenticated' && data.value) {
      const session = data.value
      const tokenExpires = session.accessTokenExpires

      if (tokenExpires && Date.now() > tokenExpires) {
        console.warn('Token expired, logging out')
        logoutDueToInactivity()
      }
    }
  }

  function showInactivityWarning() {
    if (status.value === 'authenticated' && !warningShown) {
      warningShown = true

      // Handle toast without assuming types exist
      try {
        const nuxtApp = useNuxtApp()
        // Check if toast plugin exists and is properly typed
        if ('$toast' in nuxtApp && nuxtApp.$toast) {
          const toast = nuxtApp.$toast as any
          if (toast && typeof toast.add === 'function') {
            toast.add({
              severity: 'warn',
              summary: 'Session about to expire',
              detail: 'Your session will expire in 2 minutes due to inactivity. Click to continue.',
              life: WARNING_TIME,
              closable: true
            })
            return // Exit early if toast worked
          }
        }
      }
      catch (error) {
        console.warn('Toast not available:', error)
      }

      // Fallback: just extend session automatically
      console.warn('SESSION WARNING: Your session will expire in 2 minutes due to inactivity')
      updateLastActivity()
    }
  }

  function resetInactivityTimer() {
    // Clear existing timers
    if (inactivityTimer) {
      clearTimeout(inactivityTimer)
    }
    if (warningTimer) {
      clearTimeout(warningTimer)
    }

    // Only set timers if authenticated
    if (status.value === 'authenticated') {
      // Check if token already expired first
      checkTokenExpiration()

      // Timer to show warning
      warningTimer = setTimeout(showInactivityWarning, INACTIVITY_TIMEOUT - WARNING_TIME)

      // Timer for automatic logout
      inactivityTimer = setTimeout(logoutDueToInactivity, INACTIVITY_TIMEOUT)
    }
  }

  function updateLastActivity() {
    lastActivity = Date.now()
    warningShown = false
    resetInactivityTimer() // Now this works because of hoisting!
  }

  // Interceptor for 401 errors
  globalThis.$fetch = globalThis.$fetch.create({
    onResponseError({ response }) {
      if (response.status === 401 && !response.url?.includes('/api/auth/')) {
        console.warn('API returned 401, session expired')
        signOut({
          callbackUrl: '/auth/login?reason=expired',
          redirect: true
        })
      }
    }
  })

  function startInactivityDetection() {
    if (process.client && status.value === 'authenticated') {
      // Add activity listeners
      activityEvents.forEach((event) => {
        document.addEventListener(event, updateLastActivity, { passive: true })
      })

      // Start timer
      resetInactivityTimer()
    }
  }

  function stopInactivityDetection() {
    if (process.client) {
      // Remove listeners
      activityEvents.forEach((event) => {
        document.removeEventListener(event, updateLastActivity)
      })

      // Clear timers
      if (inactivityTimer) {
        clearTimeout(inactivityTimer)
        inactivityTimer = null
      }
      if (warningTimer) {
        clearTimeout(warningTimer)
        warningTimer = null
      }
    }
  }

  if (process.client) {
    // Listen to auth status changes
    watch(() => status.value, (newStatus) => {
      if (newStatus === 'authenticated') {
        startInactivityDetection()
      }
      else {
        stopInactivityDetection()
      }
    }, { immediate: true })

    // Detect when tab becomes active again
    document.addEventListener('visibilitychange', () => {
      if (!document.hidden && status.value === 'authenticated') {
        // Check if too much time passed while inactive
        const timeInactive = Date.now() - lastActivity
        if (timeInactive >= INACTIVITY_TIMEOUT) {
          logoutDueToInactivity()
        }
        else {
          updateLastActivity()
        }
      }
    })

    // Cleanup on unmount
    onBeforeUnmount(() => {
      stopInactivityDetection()
    })
  }

  return {
    provide: {
      resetInactivity: updateLastActivity,
      getLastActivity: () => lastActivity
    }
  }
})

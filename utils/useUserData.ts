// composables/useUserData.ts
import { clearUserData, getUserData, isUserDataExpired, storeUserData } from '~/utils/indexedDbClient'
import { useDynamicCacheStore } from '~/stores/useDynamicCacheStore'

export function useUserData() {
  const { data: sessionData, status } = useAuth()
  const dynamicCache = useDynamicCacheStore()
  const config = useRuntimeConfig()
  const USE_INDEXEDDB_CACHE = config.public?.useIndexedDb ?? true

  // Reactive user data - Tipado explícito
  const userData = ref<any>(null)
  const userPermissions = ref<string[]>([])
  const userRoles = ref<string[]>([])
  const userModules = ref<string[]>([])
  const userAccesses = ref<string[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  // ✅ FIXED: Function declarations para evitar hoisting issues
  function getCurrentUserId(): string | null {
    return (sessionData.value as any)?.user?.userId || null
  }

  async function fetchUserDataFromAPI(): Promise<any> {
    try {
      const response = await $fetch('/api/user/me')
      return response
    }
    catch (error: any) {
      console.error('Error fetching user data from API:', error)
      throw error
    }
  }

  function setUserDataFromCache(data: any) {
    userData.value = data
    userPermissions.value = data.permissions || []
    userRoles.value = data.roles || []
    userModules.value = data.modules || []
    userAccesses.value = data.accesses || []
  }

  function clearLocalData() {
    userData.value = null
    userPermissions.value = []
    userRoles.value = []
    userModules.value = []
    userAccesses.value = []
    error.value = null
  }

  async function loadUserData(forceRefresh = false): Promise<void> {
    const userId = getCurrentUserId()
    if (!userId || status.value !== 'authenticated') {
      clearLocalData()
      return
    }

    isLoading.value = true
    error.value = null

    try {
      // Check memory cache first
      const cacheKey = `userData_${userId}`
      const fromMemory = dynamicCache.get(cacheKey)

      if (fromMemory && !forceRefresh) {
        setUserDataFromCache(fromMemory) // ✅ FIXED: Ya no necesita unwrap
        isLoading.value = false
        console.info('User data loaded from memory cache')
        return
      }

      // Check IndexedDB cache
      if (USE_INDEXEDDB_CACHE && !forceRefresh) {
        const shouldRefresh = await isUserDataExpired(userId)

        if (!shouldRefresh) {
          const cachedData = await getUserData(userId)
          if (cachedData) {
            setUserDataFromCache(cachedData)
            dynamicCache.set(cacheKey, cachedData) // Store in memory too - ✅ FIXED: Directo, sin array
            console.info('User data loaded from IndexedDB cache')
            isLoading.value = false
            return
          }
        }
      }

      // Fetch from API
      console.info('Fetching user data from API...')
      const apiResponse = await fetchUserDataFromAPI()
      const apiData = apiResponse.data

      // Process and store data
      const processedData = {
        userId,
        userName: apiData.userName,
        email: apiData.email,
        name: apiData.name,
        lastName: apiData.lastName,
        image: apiData.image,
        isAdmin: apiData.isAdmin,
        permissions: apiData.permissions || [],
        roles: apiData.roles || [],
        modules: apiData.modules || [],
        accesses: apiData.accesses || []
      }

      // Store in IndexedDB
      if (USE_INDEXEDDB_CACHE) {
        await storeUserData(userId, processedData)
      }

      // Store in memory cache - ✅ FIXED: Directo, sin envolver en array
      dynamicCache.set(cacheKey, processedData)

      // Update reactive data
      setUserDataFromCache(processedData)

      console.info('User data fetched and cached successfully')
    }
    catch (err: any) {
      error.value = err.message || 'Failed to load user data'
      console.error('Error loading user data:', err)

      // Try fallback to cache
      try {
        const cachedData = await getUserData(userId)
        if (cachedData) {
          setUserDataFromCache(cachedData)
          console.warn('Using cached data as fallback')
        }
      }
      catch (cacheError) {
        console.error('Cache fallback also failed:', cacheError)
      }
    }
    finally {
      isLoading.value = false
    }
  }

  // Permission checking functions
  function hasPermission(permission: string): boolean {
    return userPermissions.value.includes(permission)
  }

  function hasRole(role: string): boolean {
    return userRoles.value.includes(role)
  }

  function hasModuleAccess(module: string): boolean {
    return userModules.value.includes(module)
  }

  function hasAccess(access: string): boolean {
    return userAccesses.value.includes(access)
  }

  // Check if user is admin
  const isAdmin = computed(() => {
    return userData.value?.isAdmin || false
  })

  // Clear user data on logout
  async function clearData(): Promise<void> {
    const userId = getCurrentUserId()

    if (userId) {
      // Clear from IndexedDB
      if (USE_INDEXEDDB_CACHE) {
        await clearUserData(userId)
      }

      // Clear specific key from memory cache
      const cacheKey = `userData_${userId}`
      dynamicCache.delete(cacheKey)

      console.info('User data cleared from all caches')
    }

    clearLocalData()
  }

  // ✅ NUEVO: Función para usar la limpieza centralizada
  const clearUserCacheCompletely = async (): Promise<void> => {
    try {
      const { $clearUserCache } = useNuxtApp()
      if ($clearUserCache) {
        await $clearUserCache()
      }
      else {
        // Fallback manual
        await clearData()
      }
    }
    catch (error) {
      console.error('Error using centralized cache cleanup:', error)
      await clearData() // Fallback
    }
  }

  // Refresh user data
  async function refreshUserData(): Promise<void> {
    await loadUserData(true)
  }

  // Watch for auth status changes
  watch(() => status.value, async (newStatus) => {
    if (newStatus === 'authenticated') {
      await loadUserData()
    }
    else if (newStatus === 'unauthenticated') {
      // El plugin cache-cleanup.client.ts manejará la limpieza automáticamente
      clearLocalData() // Solo limpiar estado local reactive
    }
  }, { immediate: true })

  // Auto-load on mount if authenticated
  onMounted(async () => {
    if (status.value === 'authenticated') {
      await loadUserData()
    }
  })

  return {
    // Reactive data
    userData: readonly(userData),
    userPermissions: readonly(userPermissions),
    userRoles: readonly(userRoles),
    userModules: readonly(userModules),
    userAccesses: readonly(userAccesses),
    isLoading: readonly(isLoading),
    error: readonly(error),

    // Computed
    isAdmin,

    // Methods
    loadUserData,
    refreshUserData,
    clearData,
    clearUserCacheCompletely, // ✅ NUEVO: Función de limpieza completa
    hasPermission,
    hasRole,
    hasModuleAccess,
    hasAccess,

    // Utils
    getCurrentUserId
  }
}

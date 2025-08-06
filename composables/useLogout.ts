// composables/useLogout.ts
export function useLogout() {
  const { signOut } = useAuth()
  const { clearUserCacheCompletely } = useUserData()

  const isLoggingOut = ref(false)

  const logout = async (options?: { callbackUrl?: string, reason?: string }) => {
    try {
      isLoggingOut.value = true

      // 1. Limpiar cache de usuario primero
      console.info('Clearing user cache before logout...')
      await clearUserCacheCompletely()

      // 2. Ejecutar signOut
      console.info('Executing signOut...')
      await signOut({
        callbackUrl: options?.callbackUrl || '/auth/login',
        ...options
      })

      console.info('Logout completed successfully')
    }
    catch (error) {
      console.error('Error during logout process:', error)
      throw error
    }
    finally {
      isLoggingOut.value = false
    }
  }

  // Logout con confirmación (para tu uso específico)
  const logoutWithConfirmation = async (
    dialogRef: Ref<boolean>,
    loadingRef: Ref<boolean>,
    options?: { callbackUrl?: string, reason?: string }
  ) => {
    try {
      loadingRef.value = true
      await logout(options)
    }
    catch (error) {
      console.error('Error in logout with confirmation:', error)
      // Aquí podrías agregar tu toast de error
    }
    finally {
      dialogRef.value = false
      loadingRef.value = false
    }
  }

  return {
    logout,
    logoutWithConfirmation,
    isLoggingOut: readonly(isLoggingOut)
  }
}

// composables/useErrorBoundary.ts
import { readonly, ref } from 'vue'
import { useToast } from 'primevue/usetoast'
import Logger from '~/utils/Logger'

export interface ErrorBoundaryError {
  id: string
  context: string
  error: Error
  timestamp: string
  userAgent: string
  canRetry: boolean
  retryCount: number
  maxRetries: number
}

export interface ErrorBoundaryOptions {
  maxRetries?: number
  showToast?: boolean
  logToConsole?: boolean
  autoRetryDelay?: number
}

export function useErrorBoundary(options: ErrorBoundaryOptions = {}) {
  const {
    maxRetries = 3,
    showToast = true,
    logToConsole = true,
    autoRetryDelay = 2000
  } = options

  const toast = useToast()
  const errors = ref<Map<string, ErrorBoundaryError>>(new Map())
  const isRetrying = ref<Set<string>>(new Set())

  // Error classification for better UX
  const getErrorSeverity = (error: Error): 'error' | 'warn' | 'info' => {
    const message = error.message.toLowerCase()

    if (message.includes('network') || message.includes('timeout')) { return 'warn' }
    if (message.includes('permission') || message.includes('auth')) { return 'error' }
    if (message.includes('validation')) { return 'info' }

    return 'error'
  }

  const getErrorTitle = (context: string, severity: string): string => {
    const titles = {
      generation: {
        error: 'Report Generation Failed',
        warn: 'Generation Interrupted',
        info: 'Generation Issue'
      },
      submission: {
        error: 'Submission Failed',
        warn: 'Submission Delayed',
        info: 'Submission Notice'
      },
      download: {
        error: 'Download Failed',
        warn: 'Download Issue',
        info: 'Download Notice'
      },
      form: {
        error: 'Form Error',
        warn: 'Form Warning',
        info: 'Form Validation'
      },
      default: {
        error: 'Operation Failed',
        warn: 'Operation Warning',
        info: 'Operation Notice'
      }
    }

    return titles[context as keyof typeof titles]?.[severity as keyof typeof titles.default]
      || titles.default[severity as keyof typeof titles.default]
  }

  const getErrorMessage = (error: Error): string => {
    // User-friendly error messages
    const message = error.message

    if (message.includes('Failed to fetch')) {
      return 'Network connection problem. Please check your internet connection and try again.'
    }

    if (message.includes('timeout')) {
      return 'The operation took too long to complete. This might be due to server load.'
    }

    if (message.includes('rate limit')) {
      return 'Too many requests. Please wait a moment before trying again.'
    }

    if (message.includes('unauthorized') || message.includes('permission')) {
      return 'You don\'t have permission to perform this action. Please contact support.'
    }

    if (message.includes('validation')) {
      return 'Please check your input and try again.'
    }

    // Return original message if no specific case matches
    return message || 'An unexpected error occurred'
  }

  const canRetryError = (error: Error): boolean => {
    const message = error.message.toLowerCase()

    // Don't retry these errors
    const nonRetryableErrors = [
      'unauthorized',
      'forbidden',
      'permission',
      'validation',
      'not found',
      'bad request'
    ]

    return !nonRetryableErrors.some(nonRetryable => message.includes(nonRetryable))
  }

  const captureError = (context: string, error: Error, metadata: Record<string, any> = {}) => {
    const errorId = `${context}-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`
    const severity = getErrorSeverity(error)
    const canRetry = canRetryError(error)

    const errorBoundaryError: ErrorBoundaryError = {
      id: errorId,
      context,
      error,
      timestamp: new Date().toISOString(),
      userAgent: navigator.userAgent,
      canRetry,
      retryCount: 0,
      maxRetries
    }

    errors.value.set(errorId, errorBoundaryError)

    // Enhanced logging with structured data
    if (logToConsole) {
      Logger.error(`[ERROR BOUNDARY][${context.toUpperCase()}]`, {
        errorId,
        message: error.message,
        stack: error.stack,
        severity,
        canRetry,
        metadata,
        timestamp: errorBoundaryError.timestamp,
        userAgent: errorBoundaryError.userAgent,
        url: window.location.href,
        userId: metadata.userId || 'anonymous'
      })
    }

    // Show user-friendly toast
    if (showToast) {
      toast.add({
        severity,
        summary: getErrorTitle(context, severity),
        detail: getErrorMessage(error),
        life: severity === 'error' ? 8000 : 5000,
        group: 'error-boundary'
      })
    }

    return errorId
  }

  const retryOperation = async (
    errorId: string,
    operation: () => Promise<any>,
    onSuccess?: (result: any) => void,
    onFailure?: (error: Error) => void
  ): Promise<boolean> => {
    const errorData = errors.value.get(errorId)

    if (!errorData || !errorData.canRetry || errorData.retryCount >= errorData.maxRetries) {
      return false
    }

    if (isRetrying.value.has(errorId)) {
      return false // Already retrying
    }

    isRetrying.value.add(errorId)
    errorData.retryCount++

    try {
      // Show retry toast
      if (showToast) {
        toast.add({
          severity: 'info',
          summary: 'Retrying Operation',
          detail: `Attempt ${errorData.retryCount} of ${errorData.maxRetries}`,
          life: 3000,
          group: 'retry'
        })
      }

      // Add delay for better UX
      if (autoRetryDelay > 0) {
        await new Promise(resolve => setTimeout(resolve, autoRetryDelay))
      }

      const result = await operation()

      // Success - clear error
      clearError(errorId)

      if (showToast) {
        toast.add({
          severity: 'success',
          summary: 'Operation Successful',
          detail: 'The operation completed successfully after retry',
          life: 4000,
          group: 'success'
        })
      }

      onSuccess?.(result)
      return true
    }
    catch (retryError: any) {
      Logger.warn(`[ERROR BOUNDARY][RETRY] Attempt ${errorData.retryCount} failed:`, retryError)

      // If max retries reached, update error state
      if (errorData.retryCount >= errorData.maxRetries) {
        errorData.canRetry = false

        if (showToast) {
          toast.add({
            severity: 'error',
            summary: 'Max Retries Reached',
            detail: 'The operation failed after multiple attempts. Please try again later.',
            life: 8000,
            group: 'error-boundary'
          })
        }
      }

      onFailure?.(retryError)
      return false
    }
    finally {
      isRetrying.value.delete(errorId)
    }
  }

  const clearError = (errorId: string) => {
    errors.value.delete(errorId)
    isRetrying.value.delete(errorId)
  }

  const clearAllErrors = () => {
    errors.value.clear()
    isRetrying.value.clear()
  }

  const getErrorsByContext = (context: string): ErrorBoundaryError[] => {
    return Array.from(errors.value.values()).filter(error => error.context === context)
  }

  const hasErrors = (context?: string): boolean => {
    if (!context) { return errors.value.size > 0 }
    return getErrorsByContext(context).length > 0
  }

  const getLatestError = (context: string): ErrorBoundaryError | null => {
    const contextErrors = getErrorsByContext(context)
    if (contextErrors.length === 0) { return null }

    return contextErrors.reduce((latest, current) =>
      new Date(current.timestamp) > new Date(latest.timestamp) ? current : latest
    )
  }

  // Auto-cleanup old errors (older than 5 minutes)
  const cleanupOldErrors = () => {
    const fiveMinutesAgo = Date.now() - 5 * 60 * 1000

    for (const [errorId, errorData] of errors.value.entries()) {
      if (new Date(errorData.timestamp).getTime() < fiveMinutesAgo) {
        errors.value.delete(errorId)
      }
    }
  }

  if (process.client) {
    setInterval(cleanupOldErrors, 60000) // Every minute
  }
  // Cleanup interval
  // setInterval(cleanupOldErrors, 60000) // Every minute

  return {
    // State
    errors: readonly(errors),
    isRetrying: readonly(isRetrying),

    // Methods
    captureError,
    retryOperation,
    clearError,
    clearAllErrors,
    getErrorsByContext,
    hasErrors,
    getLatestError,

    // Utilities
    getErrorSeverity,
    getErrorTitle,
    getErrorMessage,
    canRetryError
  }
}

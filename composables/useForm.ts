// composables/useForm.ts
import {
  type ComputedRef,
  type Ref,
  computed,
  nextTick,
  reactive,
  ref,
  toRaw,
  watch
} from 'vue'
import type {
  FormActions,
  FormConfig,
  FormField,
  FormState,
  UseFormOptions,
  ValidationError,
  ValidationResult
} from '../types/form'

// Default configuration
const defaultConfig: Required<FormConfig> = {
  size: 'medium',
  variant: 'default',
  density: 'comfortable',
  validateOnChange: true,
  validateOnBlur: true,
  showRequiredIndicator: true,
  requiredIndicator: '*',
  className: [],
  style: {}
}

export function useForm<T extends Record<string, unknown> = Record<string, unknown>>(
  options: UseFormOptions<T>
): {
    state: ComputedRef<FormState<T>>
    actions: FormActions<T>
    config: ComputedRef<Required<FormConfig>>
    fields: ComputedRef<Map<string, FormField>>
    isValid: ComputedRef<boolean>
    isDirty: ComputedRef<boolean>
    isTouched: ComputedRef<boolean>
    isSubmitting: Ref<boolean>
    submitCount: Ref<number>
  } {
  // Internal state using Record types instead of generic T for better type safety
  const values = reactive<Record<string, unknown>>({})
  const errors = reactive<Record<string, ValidationError[]>>({})
  const touched = reactive<Record<string, boolean>>({})
  const dirty = reactive<Record<string, boolean>>({})
  const isSubmitting = ref(false)
  const isSubmitted = ref(false)
  const submitCount = ref(0)
  const isValidating = ref(false)

  // Configuration
  const config = computed(() => ({
    ...defaultConfig,
    ...options.config
  }))

  // Fields map for easy lookup
  const fields = computed(() => {
    const fieldMap = new Map<string, FormField>()
    options.fields.forEach((field) => {
      fieldMap.set(field.name, field)
    })
    return fieldMap
  })

  // Type-safe helpers for accessing reactive objects
  const setValue = (name: string, value: unknown): void => {
    ;(values as Record<string, unknown>)[name] = value
  }

  const getValue = (name: string): unknown => {
    return (values as Record<string, unknown>)[name]
  }

  const setErrors = (name: string, fieldErrors: ValidationError[]): void => {
    ;(errors as Record<string, ValidationError[]>)[name] = fieldErrors
  }

  const getErrors = (name: string): ValidationError[] => {
    return (errors as Record<string, ValidationError[]>)[name] || []
  }

  const setTouched = (name: string, isTouched: boolean): void => {
    ;(touched as Record<string, boolean>)[name] = isTouched
  }

  const getTouched = (name: string): boolean => {
    return (touched as Record<string, boolean>)[name] || false
  }

  const setDirty = (name: string, isDirtyValue: boolean): void => {
    ;(dirty as Record<string, boolean>)[name] = isDirtyValue
  }

  const getDirty = (name: string): boolean => {
    return (dirty as Record<string, boolean>)[name] || false
  }

  // Initialize values
  const initializeValues = (): void => {
    options.fields.forEach((field) => {
      const initialValue = options.initialValues?.[field.name as keyof T] ?? field.defaultValue
      setValue(field.name, initialValue)
      setErrors(field.name, [])
      setTouched(field.name, false)
      setDirty(field.name, false)
    })
  }

  // Validation utilities
  const validateSingleField = async (name: string): Promise<ValidationResult> => {
    const field = fields.value.get(name)
    if (!field?.validation) {
      return { success: true, errors: [] }
    }

    const value = getValue(name)

    try {
      const result = field.validation.safeParse(value)

      // Update errors for this field
      if (result.success) {
        setErrors(name, [])
      }
      else {
        setErrors(name, result.errors)
      }

      return result
    }
    catch (error) {
      // Fallback error handling if validation throws
      const validationError: ValidationError = {
        code: 'validation_error',
        message: error instanceof Error ? error.message : 'Validation failed',
        path: name,
        value
      }

      setErrors(name, [validationError])

      return {
        success: false,
        errors: [validationError]
      }
    }
  }

  const validateAllFields = async (): Promise<ValidationResult> => {
    isValidating.value = true
    const fieldResults: ValidationResult[] = []

    try {
      // Clear all errors first
      options.fields.forEach((field) => {
        setErrors(field.name, [])
      })

      // Validate all fields
      for (const field of options.fields) {
        const result = await validateSingleField(field.name)
        fieldResults.push(result)
      }

      // Run form-level validation if provided
      if (options.onValidate) {
        try {
          const formResult = await options.onValidate(toRaw(values) as T)
          fieldResults.push(formResult)

          if (!formResult.success) {
            // Add form-level errors to appropriate fields
            formResult.errors.forEach((error) => {
              if (error.path && error.path in values) {
                const currentErrors = getErrors(error.path)
                setErrors(error.path, [...currentErrors, error])
              }
            })
          }
        }
        catch (validationError) {
          // Handle form validation errors
          const fallbackError: ValidationError = {
            code: 'form_validation_error',
            message: validationError instanceof Error ? validationError.message : 'Form validation failed',
            path: '',
            value: values
          }

          fieldResults.push({
            success: false,
            errors: [fallbackError]
          })
        }
      }

      const allErrors = fieldResults.flatMap(r => r.errors)
      const success = allErrors.length === 0

      return {
        success,
        errors: allErrors
      }
    }
    finally {
      isValidating.value = false
    }
  }

  // Helper function to get field default value
  const getFieldDefaultValue = (fieldName: string): unknown => {
    const field = fields.value.get(fieldName)
    return field?.defaultValue ?? undefined
  }

  // Helper function to get field initial value
  const getFieldInitialValue = (fieldName: string): unknown => {
    const initialValue = options.initialValues?.[fieldName as keyof T]
    const defaultValue = getFieldDefaultValue(fieldName)
    return initialValue ?? defaultValue
  }

  // Computed state
  const state = computed<FormState<T>>(() => {
    // Create proper typed copies
    const valuesTyped = {} as T
    const errorsTyped = {} as Record<keyof T, ValidationError[]>
    const touchedTyped = {} as Record<keyof T, boolean>
    const dirtyTyped = {} as Record<keyof T, boolean>

    // Copy values with proper typing
    options.fields.forEach((field) => {
      const key = field.name as keyof T
      valuesTyped[key] = getValue(field.name) as T[keyof T]
      errorsTyped[key] = getErrors(field.name)
      touchedTyped[key] = getTouched(field.name)
      dirtyTyped[key] = getDirty(field.name)
    })

    return {
      values: valuesTyped,
      errors: errorsTyped,
      touched: touchedTyped,
      dirty: dirtyTyped,
      meta: {
        valid: Object.values(errors).every(fieldErrors => Array.isArray(fieldErrors) && fieldErrors.length === 0),
        pending: isValidating.value,
        submitted: isSubmitted.value,
        submitting: isSubmitting.value,
        dirty: Object.values(dirty).some(Boolean),
        touched: Object.values(touched).some(Boolean)
      }
    }
  })

  const isValid = computed(() => state.value.meta.valid)
  const isDirty = computed(() => state.value.meta.dirty)
  const isTouched = computed(() => state.value.meta.touched)

  // Actions
  const actions: FormActions<T> = {
    setFieldValue: (name: keyof T, value: T[keyof T]) => {
      const fieldName = name as string
      const previousValue = getValue(fieldName)
      setValue(fieldName, value)

      // Only mark as dirty if value actually changed
      if (previousValue !== value) {
        setDirty(fieldName, true)
      }

      // Auto-validate on change if enabled
      if (config.value.validateOnChange) {
        nextTick(() => {
          validateSingleField(fieldName)
        })
      }
    },

    setFieldError: (name: keyof T, fieldErrors: ValidationError[]) => {
      const fieldName = name as string
      setErrors(fieldName, [...fieldErrors])
    },

    setFieldTouched: (name: keyof T, isTouchedValue: boolean = true) => {
      const fieldName = name as string
      setTouched(fieldName, isTouchedValue)

      // Auto-validate on blur if enabled and field is touched
      if (isTouchedValue && config.value.validateOnBlur) {
        nextTick(() => {
          validateSingleField(fieldName)
        })
      }
    },

    clearField: (name: keyof T) => {
      const fieldName = name as string
      const defaultValue = getFieldDefaultValue(fieldName)
      setValue(fieldName, defaultValue)
      setErrors(fieldName, [])
      setTouched(fieldName, false)
      setDirty(fieldName, false)
    },

    resetField: (name: keyof T) => {
      const fieldName = name as string
      const resetValue = getFieldInitialValue(fieldName)
      setValue(fieldName, resetValue)
      setErrors(fieldName, [])
      setTouched(fieldName, false)
      setDirty(fieldName, false)
    },

    validateField: (name: keyof T) => validateSingleField(name as string),

    validateForm: () => validateAllFields(),

    reset: () => {
      // Reset all values to initial values
      options.fields.forEach((field) => {
        const resetValue = getFieldInitialValue(field.name)
        setValue(field.name, resetValue)
        setErrors(field.name, [])
        setTouched(field.name, false)
        setDirty(field.name, false)
      })

      isSubmitted.value = false
      submitCount.value = 0
    },

    submit: async () => {
      if (isSubmitting.value) { return }

      isSubmitting.value = true
      submitCount.value++

      try {
        // Mark all fields as touched
        options.fields.forEach((field) => {
          setTouched(field.name, true)
        })

        // Validate form
        const validationResult = await validateAllFields()

        if (validationResult.success && options.onSubmit) {
          await options.onSubmit(toRaw(values) as T, actions)
          isSubmitted.value = true

          // Reset dirty state after successful submission
          options.fields.forEach((field) => {
            setDirty(field.name, false)
          })
        }
      }
      catch (error) {
        if (process.env.NODE_ENV === 'development') {
          console.error('Form submission error:', error)
        }
        throw error
      }
      finally {
        isSubmitting.value = false
      }
    }
  }

  // Helper function to evaluate conditional logic
  const evaluateConditional = (
    conditionFieldValue: unknown,
    conditionValue: unknown,
    operator: string = 'eq'
  ): boolean => {
    switch (operator) {
      case 'eq':
        return conditionFieldValue === conditionValue
      case 'neq':
        return conditionFieldValue !== conditionValue
      case 'in':
        return Array.isArray(conditionValue) && conditionValue.includes(conditionFieldValue)
      case 'nin':
        return Array.isArray(conditionValue) && !conditionValue.includes(conditionFieldValue)
      case 'gt':
        return typeof conditionFieldValue === 'number'
          && typeof conditionValue === 'number'
          && conditionFieldValue > conditionValue
      case 'lt':
        return typeof conditionFieldValue === 'number'
          && typeof conditionValue === 'number'
          && conditionFieldValue < conditionValue
      case 'gte':
        return typeof conditionFieldValue === 'number'
          && typeof conditionValue === 'number'
          && conditionFieldValue >= conditionValue
      case 'lte':
        return typeof conditionFieldValue === 'number'
          && typeof conditionValue === 'number'
          && conditionFieldValue <= conditionValue
      default:
        return true
    }
  }

  // Watchers for field dependencies
  watch(
    () => options.fields,
    (newFields) => {
      // Handle dynamic field changes
      newFields.forEach((field) => {
        if (!(field.name in values)) {
          const initialValue = getFieldInitialValue(field.name)
          setValue(field.name, initialValue)
          setErrors(field.name, [])
          setTouched(field.name, false)
          setDirty(field.name, false)
        }
      })
    },
    { immediate: true, deep: true }
  )

  // Watch for conditional field dependencies
  watch(values, () => {
    options.fields.forEach((field) => {
      if (field.conditional) {
        const { field: conditionField, value: conditionValue, operator = 'eq' } = field.conditional
        const conditionFieldValue = getValue(conditionField)

        const shouldShow = evaluateConditional(conditionFieldValue, conditionValue, operator)

        // If field should be hidden and has a value, clear it
        if (!shouldShow && field.name in values) {
          const defaultValue = getFieldDefaultValue(field.name)

          // Only clear if the field currently has a value different from default
          if (getValue(field.name) !== defaultValue) {
            setValue(field.name, defaultValue)
            setErrors(field.name, [])
            setTouched(field.name, false)
            setDirty(field.name, false)
          }
        }
      }
    })
  }, { deep: true })

  // Initialize values on mount
  initializeValues()

  return {
    state,
    actions,
    config,
    fields,
    isValid,
    isDirty,
    isTouched,
    isSubmitting,
    submitCount
  }
}

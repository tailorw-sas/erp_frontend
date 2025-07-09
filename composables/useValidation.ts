// composables/useValidation.ts
import { type ComputedRef, computed } from 'vue'
import type { ValidationError, ValidationResult, ValidationSchema } from '../types/form'

// Validation utilities
export const ValidationCodes = {
  REQUIRED: 'REQUIRED',
  EMAIL: 'EMAIL',
  MIN_LENGTH: 'MIN_LENGTH',
  MAX_LENGTH: 'MAX_LENGTH',
  MIN_VALUE: 'MIN_VALUE',
  MAX_VALUE: 'MAX_VALUE',
  PATTERN: 'PATTERN',
  CUSTOM: 'CUSTOM'
} as const

export type ValidationCode = typeof ValidationCodes[keyof typeof ValidationCodes]

export class FormValidationError extends Error implements ValidationError {
  constructor(
    public readonly code: ValidationCode,
    public readonly message: string,
    public readonly path: string,
    public readonly value?: unknown
  ) {
    super(message)
    this.name = 'FormValidationError'
  }
}

// Base validator class
export abstract class BaseValidator<T = unknown> implements ValidationSchema<T> {
  protected errors: ValidationError[] = []
  protected warnings: ValidationError[] = []

  abstract validate(value: T): boolean

  safeParse(value: T): ValidationResult {
    this.errors = []
    this.warnings = []

    try {
      const success = this.validate(value)
      return {
        success,
        errors: [...this.errors],
        warnings: [...this.warnings]
      }
    }
    catch (error) {
      if (error instanceof FormValidationError) {
        this.errors.push(error)
      }
      else {
        this.errors.push(new FormValidationError(
          ValidationCodes.CUSTOM,
          error instanceof Error ? error.message : 'Unknown validation error',
          '',
          value
        ))
      }

      return {
        success: false,
        errors: [...this.errors],
        warnings: [...this.warnings]
      }
    }
  }

  parse(value: T): T {
    const result = this.safeParse(value)
    if (!result.success) {
      throw new Error(`Validation failed: ${result.errors.map(e => e.message).join(', ')}`)
    }
    return value
  }

  protected addError(code: ValidationCode, message: string, path: string = '', value?: unknown): void {
    this.errors.push(new FormValidationError(code, message, path, value))
  }

  protected addWarning(code: ValidationCode, message: string, path: string = '', value?: unknown): void {
    this.warnings.push(new FormValidationError(code, message, path, value))
  }
}

// String validators
export class StringValidator extends BaseValidator<string> {
  private _required = false
  private _minLength?: number
  private _maxLength?: number
  private _pattern?: RegExp
  private _email = false

  required(message?: string) {
    this._required = true
    return this
  }

  minLength(length: number, message?: string) {
    this._minLength = length
    return this
  }

  maxLength(length: number, message?: string) {
    this._maxLength = length
    return this
  }

  pattern(regex: RegExp, message?: string) {
    this._pattern = regex
    return this
  }

  email(message?: string) {
    this._email = true
    return this
  }

  validate(value: string): boolean {
    // Handle null/undefined
    if (value == null || value === '') {
      if (this._required) {
        this.addError(ValidationCodes.REQUIRED, 'This field is required')
        return false
      }
      return true
    }

    // Type check
    if (typeof value !== 'string') {
      this.addError(ValidationCodes.CUSTOM, 'Value must be a string')
      return false
    }

    // Min length
    if (this._minLength !== undefined && value.length < this._minLength) {
      this.addError(
        ValidationCodes.MIN_LENGTH,
        `Must be at least ${this._minLength} characters long`
      )
      return false
    }

    // Max length
    if (this._maxLength !== undefined && value.length > this._maxLength) {
      this.addError(
        ValidationCodes.MAX_LENGTH,
        `Must be no more than ${this._maxLength} characters long`
      )
      return false
    }

    // Email validation
    if (this._email) {
      const emailRegex = /^[^\s@]+@[^\s@][^\s.@]*\.[^\s@]+$/
      if (!emailRegex.test(value)) {
        this.addError(ValidationCodes.EMAIL, 'Please enter a valid email address')
        return false
      }
    }

    // Pattern validation
    if (this._pattern && !this._pattern.test(value)) {
      this.addError(ValidationCodes.PATTERN, 'Value does not match the required pattern')
      return false
    }

    return true
  }
}

// Number validators
export class NumberValidator extends BaseValidator<number> {
  private _required = false
  private _min?: number
  private _max?: number
  private _integer = false
  private _positive = false

  required(message?: string) {
    this._required = true
    return this
  }

  min(value: number, message?: string) {
    this._min = value
    return this
  }

  max(value: number, message?: string) {
    this._max = value
    return this
  }

  integer(message?: string) {
    this._integer = true
    return this
  }

  positive(message?: string) {
    this._positive = true
    return this
  }

  validate(value: number): boolean {
    // Handle null/undefined
    if (value == null) {
      if (this._required) {
        this.addError(ValidationCodes.REQUIRED, 'This field is required')
        return false
      }
      return true
    }

    // Type check
    if (typeof value !== 'number' || isNaN(value)) {
      this.addError(ValidationCodes.CUSTOM, 'Value must be a valid number')
      return false
    }

    // Min value
    if (this._min !== undefined && value < this._min) {
      this.addError(ValidationCodes.MIN_VALUE, `Must be at least ${this._min}`)
      return false
    }

    // Max value
    if (this._max !== undefined && value > this._max) {
      this.addError(ValidationCodes.MAX_VALUE, `Must be no more than ${this._max}`)
      return false
    }

    // Integer check
    if (this._integer && !Number.isInteger(value)) {
      this.addError(ValidationCodes.CUSTOM, 'Value must be an integer')
      return false
    }

    // Positive check
    if (this._positive && value <= 0) {
      this.addError(ValidationCodes.CUSTOM, 'Value must be positive')
      return false
    }

    return true
  }
}

// Array validators
export class ArrayValidator<T> extends BaseValidator<T[]> {
  private _required = false
  private _minLength?: number
  private _maxLength?: number
  private _itemValidator?: BaseValidator<T>

  required(message?: string) {
    this._required = true
    return this
  }

  minLength(length: number, message?: string) {
    this._minLength = length
    return this
  }

  maxLength(length: number, message?: string) {
    this._maxLength = length
    return this
  }

  items(validator: BaseValidator<T>) {
    this._itemValidator = validator
    return this
  }

  validate(value: T[]): boolean {
    // Handle null/undefined
    if (value == null) {
      if (this._required) {
        this.addError(ValidationCodes.REQUIRED, 'This field is required')
        return false
      }
      return true
    }

    // Type check
    if (!Array.isArray(value)) {
      this.addError(ValidationCodes.CUSTOM, 'Value must be an array')
      return false
    }

    // Min length
    if (this._minLength !== undefined && value.length < this._minLength) {
      this.addError(
        ValidationCodes.MIN_LENGTH,
        `Must select at least ${this._minLength} item${this._minLength === 1 ? '' : 's'}`
      )
      return false
    }

    // Max length
    if (this._maxLength !== undefined && value.length > this._maxLength) {
      this.addError(
        ValidationCodes.MAX_LENGTH,
        `Must select no more than ${this._maxLength} item${this._maxLength === 1 ? '' : 's'}`
      )
      return false
    }

    // Validate items
    if (this._itemValidator) {
      let isValid = true
      value.forEach((item, index) => {
        const result = this._itemValidator!.safeParse(item)
        if (!result.success) {
          result.errors.forEach((error) => {
            this.addError(
              error.code as ValidationCode,
              `Item ${index + 1}: ${error.message}`,
              `[${index}]`,
              item
            )
          })
          isValid = false
        }
      })
      return isValid
    }

    return true
  }
}

// Custom validator
export class CustomValidator<T> extends BaseValidator<T> {
  constructor(
    private validatorFn: (value: T) => boolean | string | ValidationError[]
  ) {
    super()
  }

  validate(value: T): boolean {
    const result = this.validatorFn(value)

    if (typeof result === 'boolean') {
      return result
    }

    if (typeof result === 'string') {
      this.addError(ValidationCodes.CUSTOM, result)
      return false
    }

    if (Array.isArray(result)) {
      this.errors.push(...result)
      return false
    }

    return true
  }
}

// Schema builder
export const v = {
  string: () => new StringValidator(),
  number: () => new NumberValidator(),
  array: <T>() => new ArrayValidator<T>(),
  custom: <T>(fn: (value: T) => boolean | string | ValidationError[]) => new CustomValidator(fn),

  // Utility validators
  optional: <T>(validator: BaseValidator<T>) => {
    return new CustomValidator<T | null | undefined>((value) => {
      if (value == null) { return true }
      return validator.safeParse(value as T).success
    })
  },

  union: <T>(...validators: BaseValidator<T>[]) => {
    return new CustomValidator<T>((value) => {
      return validators.some(validator => validator.safeParse(value).success)
    })
  }
}

// Composable for field validation
export function useFieldValidation<T>(
  value: ComputedRef<T>,
  validator?: ValidationSchema<T>
) {
  const result = computed(() => {
    if (!validator) {
      return { success: true, errors: [], warnings: [] }
    }
    return validator.safeParse(value.value)
  })

  const isValid = computed(() => result.value.success)
  const errors = computed(() => result.value.errors)
  const warnings = computed(() => result.value.warnings || [])
  const hasErrors = computed(() => errors.value.length > 0)
  const hasWarnings = computed(() => warnings.value.length > 0)

  return {
    result,
    isValid,
    errors,
    warnings,
    hasErrors,
    hasWarnings
  }
}

// Async validation support
export class AsyncValidator<T> extends BaseValidator<T> {
  constructor(
    private asyncValidatorFn: (value: T) => Promise<boolean | string | ValidationError[]>
  ) {
    super()
  }

  validate(value: T): boolean {
    // This will be called synchronously, but we need async validation
    // For now, return true and handle async validation separately
    return true
  }

  async validateAsync(value: T): Promise<ValidationResult> {
    this.errors = []
    this.warnings = []

    try {
      const result = await this.asyncValidatorFn(value)

      if (typeof result === 'boolean') {
        return { success: result, errors: this.errors, warnings: this.warnings }
      }

      if (typeof result === 'string') {
        this.addError(ValidationCodes.CUSTOM, result)
        return { success: false, errors: this.errors, warnings: this.warnings }
      }

      if (Array.isArray(result)) {
        this.errors.push(...result)
        return { success: false, errors: this.errors, warnings: this.warnings }
      }

      return { success: true, errors: this.errors, warnings: this.warnings }
    }
    catch (error) {
      this.addError(
        ValidationCodes.CUSTOM,
        error instanceof Error ? error.message : 'Async validation failed'
      )
      return { success: false, errors: this.errors, warnings: this.warnings }
    }
  }
}

export const va = {
  async: <T>(fn: (value: T) => Promise<boolean | string | ValidationError[]>) =>
    new AsyncValidator(fn)
}

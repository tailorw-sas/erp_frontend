// types/form.ts
export type FieldType =
  | 'text'
  | 'email'
  | 'password'
  | 'textarea'
  | 'number'
  | 'currency'
  | 'percentage'
  | 'date'
  | 'datetime'
  | 'time'
  | 'select'
  | 'multiselect'
  | 'localselect'
  | 'autocomplete'
  | 'checkbox'
  | 'radio'
  | 'toggle'
  | 'file'
  | 'image'

export type ValidationRule =
  | 'required'
  | 'email'
  | 'min'
  | 'max'
  | 'minLength'
  | 'maxLength'
  | 'pattern'
  | 'custom'

export type FormSize = 'small' | 'medium' | 'large'
export type FormVariant = 'default' | 'outlined' | 'filled' | 'underlined'
export type FormDensity = 'comfortable' | 'compact' | 'spacious'

export interface ValidationError {
  readonly code: string
  readonly message: string
  readonly path: string
  readonly value?: unknown
}

export interface ValidationResult {
  readonly success: boolean
  readonly errors: ValidationError[]
  readonly warnings?: ValidationError[]
}

export interface ValidationSchema<T = unknown> {
  safeParse: (value: T) => ValidationResult
  parse?: (value: T) => T
}

export interface FieldUIConfig {
  readonly size?: FormSize
  readonly variant?: FormVariant
  readonly density?: FormDensity
  readonly placeholder?: string
  readonly helpText?: string
  readonly prefix?: string
  readonly suffix?: string
  readonly icon?: string
  readonly clearable?: boolean
  readonly loading?: boolean
  readonly readonly?: boolean
  readonly className?: string | string[]
  readonly style?: Record<string, string | number>
}

export interface SelectOption<T = unknown> {
  readonly label: string
  readonly value: T
  readonly disabled?: boolean
  readonly group?: string
  readonly description?: string
  readonly icon?: string
  readonly metadata?: Record<string, unknown>
}

export interface AsyncDataSource<T = unknown> {
  readonly endpoint?: string
  readonly queryParam?: string
  readonly searchDelay?: number
  readonly minSearchLength?: number
  readonly pageSize?: number
  readonly transform?: (data: unknown) => SelectOption<T>[]
}

export interface FormField<T = unknown> {
  readonly name: string
  readonly type: FieldType
  readonly label?: string
  readonly validation?: ValidationSchema<T>
  readonly ui?: FieldUIConfig
  readonly defaultValue?: T
  readonly dependsOn?: string[]
  readonly conditional?: {
    readonly field: string
    readonly value: unknown
    readonly operator?: 'eq' | 'neq' | 'in' | 'nin' | 'gt' | 'lt' | 'gte' | 'lte'
  }

  // Specific to select/multiselect fields
  readonly options?: SelectOption<T>[]
  readonly asyncDataSource?: AsyncDataSource<T>
  readonly multiple?: boolean
  readonly searchable?: boolean
  readonly creatable?: boolean

  // Specific to number fields
  readonly min?: number
  readonly max?: number
  readonly step?: number
  readonly precision?: number

  // Specific to text fields
  readonly minLength?: number
  readonly maxLength?: number
  readonly pattern?: RegExp
  readonly mask?: string

  // Specific to file fields
  readonly accept?: string
  readonly maxFileSize?: number
  readonly maxFiles?: number
}

export interface LocalSelectOption {
  name: string
  value: any
  id?: string | number
  description?: string
  label?: string
  text?: string
}

export type ExtendedFormField = FormField & {
  // UI properties
  placeholder?: string
  showClear?: boolean
  filterable?: boolean
  required?: boolean
  helpText?: string

  // Validation properties
  minLength?: number
  maxLength?: number
  min?: number
  max?: number
  pattern?: string | RegExp

  // Selection field properties
  multiple?: boolean
  maxSelectedLabels?: number
  selectionLimit?: number

  // Display properties
  hidden?: boolean
  readonly?: boolean
  class?: string
  style?: Record<string, any>

  // Event handlers
  onChange?: (value: any) => void
  onBlur?: () => void
  onFocus?: () => void

  // Allow any additional properties to be flexible
  [key: string]: any
}

export function isExtendedFormField(field: any): field is ExtendedFormField {
  return field && (
    'placeholder' in field
    || 'helpText' in field
    || 'required' in field
    || 'showClear' in field
  )
}

export interface FormConfig {
  readonly size?: FormSize
  readonly variant?: FormVariant
  readonly density?: FormDensity
  readonly validateOnChange?: boolean
  readonly validateOnBlur?: boolean
  readonly showRequiredIndicator?: boolean
  readonly requiredIndicator?: string
  readonly className?: string | string[]
  readonly style?: Record<string, string | number>
}

export interface FormState<T extends Record<string, unknown> = Record<string, unknown>> {
  readonly values: T
  readonly errors: Record<keyof T, ValidationError[]>
  readonly touched: Record<keyof T, boolean>
  readonly dirty: Record<keyof T, boolean>
  readonly meta: {
    readonly valid: boolean
    readonly pending: boolean
    readonly submitted: boolean
    readonly submitting: boolean
    readonly dirty: boolean
    readonly touched: boolean
  }
}

export interface FormActions<T extends Record<string, unknown> = Record<string, unknown>> {
  setFieldValue: (name: keyof T, value: T[keyof T]) => void
  setFieldError: (name: keyof T, errors: ValidationError[]) => void
  setFieldTouched: (name: keyof T, touched?: boolean) => void
  clearField: (name: keyof T) => void
  resetField: (name: keyof T) => void
  validateField: (name: keyof T) => Promise<ValidationResult>
  validateForm: () => Promise<ValidationResult>
  reset: () => void
  submit: () => Promise<void>
}

export interface UseFormOptions<T extends Record<string, unknown> = Record<string, unknown>> {
  readonly initialValues?: Partial<T>
  readonly fields: FormField[]
  readonly config?: FormConfig
  readonly onSubmit?: (values: T, actions: FormActions<T>) => Promise<void> | void
  readonly onValidate?: (values: T) => ValidationResult | Promise<ValidationResult>
}

export interface FormFieldProps<T = unknown> {
  readonly field: FormField<T>
  readonly value: T
  readonly error?: ValidationError[]
  readonly touched?: boolean
  readonly dirty?: boolean
  readonly disabled?: boolean
  readonly loading?: boolean
  readonly config?: FormConfig
  readonly onUpdate: (value: T) => void
  readonly onBlur?: () => void
  readonly onFocus?: () => void
}

export interface FormContext<T extends Record<string, unknown> = Record<string, unknown>> {
  readonly state: FormState<T>
  readonly actions: FormActions<T>
  readonly config: FormConfig
  readonly fields: Map<string, FormField>
}

// Events
export interface FormFieldEvent<T = unknown> {
  readonly field: string
  readonly value: T
  readonly previousValue: T
  readonly timestamp: number
}

export interface FormSubmitEvent<T extends Record<string, unknown> = Record<string, unknown>> {
  readonly values: T
  readonly isValid: boolean
  readonly errors: Record<keyof T, ValidationError[]>
  readonly timestamp: number
}

export interface FormValidationEvent {
  readonly field?: string
  readonly isValid: boolean
  readonly errors: ValidationError[]
  readonly timestamp: number
}

// Utility types
export type FormFieldComponent<T = unknown> = Component<FormFieldProps<T>>
export type FormFieldRegistry = Map<FieldType, FormFieldComponent>

export interface FormPlugin {
  readonly name: string
  readonly version: string
  readonly install: (registry: FormFieldRegistry) => void
}

// Theme tokens
export interface FormTheme {
  readonly spacing: {
    readonly xs: string
    readonly sm: string
    readonly md: string
    readonly lg: string
    readonly xl: string
  }
  readonly colors: {
    readonly primary: string
    readonly secondary: string
    readonly success: string
    readonly warning: string
    readonly error: string
    readonly info: string
    readonly surface: string
    readonly background: string
    readonly text: string
    readonly textSecondary: string
    readonly border: string
    readonly borderFocus: string
  }
  readonly typography: {
    readonly fontFamily: string
    readonly fontSize: {
      readonly xs: string
      readonly sm: string
      readonly md: string
      readonly lg: string
      readonly xl: string
    }
    readonly fontWeight: {
      readonly normal: string
      readonly medium: string
      readonly semibold: string
      readonly bold: string
    }
    readonly lineHeight: {
      readonly tight: string
      readonly normal: string
      readonly relaxed: string
    }
  }
  readonly borderRadius: {
    readonly none: string
    readonly sm: string
    readonly md: string
    readonly lg: string
    readonly full: string
  }
  readonly shadows: {
    readonly sm: string
    readonly md: string
    readonly lg: string
    readonly focus: string
  }
  readonly transitions: {
    readonly fast: string
    readonly normal: string
    readonly slow: string
  }
}

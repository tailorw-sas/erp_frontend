import type { z } from 'zod'

export declare type FieldDefinitionType = FieldDefinition
export declare type ValidationErrorType = ValidationError

interface FieldDefinition {
  field: string
  dataType: string | null
  header?: string | Function
  footer?: string | null
  style?: any
  class?: any
  tabIndex?: number
  headerStyle?: any | null
  headerClass?: any | null
  footerStyle?: any | null
  footerClass?: any | null
  errorClass?: any | null
  resetValidation?: boolean
  containerFieldClass?: any | null
  showClearButton?: boolean | null
  maxConstraints?: number | null
  minFractionDigits?: number | null
  maxFractionDigits?: number | null
  hidden?: boolean
  disabled?: boolean
  toUppercase?: boolean
  validation?: z.ZodTypeAny
  backendValidation?: Array<ValidationErrorType>
  helpText?: string
  helpTextClass?: string
  objApi?: {
    moduleApi: string
    uriApi: string
    keyValue?: string
  }
  onUpdate?: (value: any) => void
}

interface ValidationError {
  [key: string]: string[]
}

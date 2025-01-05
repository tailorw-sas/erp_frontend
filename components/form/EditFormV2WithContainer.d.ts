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
  headerStyle?: any | null
  headerClass?: any | null
  footerStyle?: any | null
  footerClass?: any | null
  errorClass?: any | null
  resetValidation?: boolean
  minFractionDigits?: number
  maxFractionDigits?: number
  containerFieldClass?: any | null
  showClearButton?: boolean | null
  maxConstraints?: number | null
  hidden?: boolean
  disabled?: boolean
  toUppercase?: boolean
  validation?: z.ZodTypeAny
  backendValidation?: Array<ValidationErrorType>
  onUpdate?: (value: any) => void
}

interface Container {
  containerClass: string
  childs: FieldDefinition[]
}

interface ValidationError {
  [key: string]: string[]
}

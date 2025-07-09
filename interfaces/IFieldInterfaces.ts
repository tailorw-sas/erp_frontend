// Enhanced FieldDefinition interface with all necessary properties
export interface FieldDefinition {
  // Core field properties
  field: string
  dataType: 'text' | 'code' | 'textarea' | 'password' | 'number' | 'date' | 'check' | 'file' | 'fileupload' | 'image' | 'multiselect' | 'select' | 'dropdown'

  // Display properties
  header?: string | (() => string)
  headerStyle?: Record<string, any>
  headerClass?: string
  placeholder?: string
  helpText?: string
  helpTextClass?: string
  footer?: string
  footerStyle?: Record<string, any>
  footerClass?: string

  // Styling and layout
  class?: string
  style?: Record<string, any>
  containerFieldClass?: string
  errorClass?: string

  // Behavior properties
  disabled?: boolean
  hidden?: boolean
  tabIndex?: number
  toUppercase?: boolean
  showClearButton?: boolean

  // Select/MultiSelect specific properties
  options?: Array<{ label: string, value: any, [key: string]: any }>
  optionLabel?: string
  optionValue?: string
  multiple?: boolean // This was missing!
  filterable?: boolean
  showClear?: boolean
  maxSelectedLabels?: number
  selectionLimit?: number

  // Number field properties
  minFractionDigits?: number
  maxFractionDigits?: number

  // Textarea properties
  rows?: number

  // Validation
  validation?: {
    safeParse: (value: any) => {
      success: boolean
      error?: {
        issues: Array<{ message: string }>
      }
    }
  }
}

// Type alias for compatibility
export type FieldDefinitionType = FieldDefinition

// Interface for file metadata
export interface IFileMetadata {
  name: string
  size: number
  type: string
  objectURL?: string
  [key: string]: any
}

// Interface for form errors
export interface FormErrors {
  [key: string]: string[]
}

// Interface for field values
export interface FieldValues {
  [key: string]: any
}

// Interface for emit data
export interface EmitData {
  fieldKey: string
  value: any
}

// Props interface for the form component
export interface FormComponentProps {
  fields: Array<FieldDefinition>
  item: Record<string, any>
  loadingSave?: boolean
  loadingDelete?: boolean
  showActions?: boolean
  showActionsInline?: boolean
  forceSave?: boolean
  formClass?: string
  containerClass?: string
  unRestrictedContentHeight?: boolean
  backendValidation?: FormErrors
  hideDeleteButton?: boolean
  showCancel?: boolean
  errorList?: FormErrors
  autosave?: boolean
  autosaveDelay?: number
  validateOnChange?: boolean
  showRequiredIndicator?: boolean
  fileUploadMaxSize?: number
  acceptedImageTypes?: string
  acceptedFileTypes?: string
  fieldHeight?: string
  compactMode?: boolean
}

// Emits interface for the form component
export interface FormComponentEmits {
  'update:field': [data: EmitData]
  'reactiveUpdateField': [values: FieldValues]
  'clearField': [fieldKey: string]
  'submit': [data: FieldValues & { event: Event }]
  'cancel': []
  'delete': [event: Event]
  'forceSave': []
  'update:errorsList': [errors: FormErrors]
  'autosave': [values: FieldValues]
  'fieldValidated': [fieldKey: string, isValid: boolean]
}

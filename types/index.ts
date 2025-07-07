// index.ts - Main export file for the Enhanced Form System

// ===============================
// CORE TYPES
// ===============================
export type {
  FieldType,
  ValidationRule,
  FormSize,
  FormVariant,
  FormDensity,
  ValidationError,
  ValidationResult,
  ValidationSchema,
  FieldUIConfig,
  SelectOption,
  AsyncDataSource,
  FormField,
  FormConfig,
  FormState,
  FormActions,
  UseFormOptions,
  FormFieldProps,
  FormContext,
  FormFieldEvent,
  FormSubmitEvent,
  FormValidationEvent,
  FormFieldComponent,
  FormFieldRegistry,
  FormPlugin,
  FormTheme
} from './form'

// ===============================
// VALIDATION SYSTEM
// ===============================
export {
  ValidationCodes,
  FormValidationError,
  BaseValidator,
  StringValidator,
  NumberValidator,
  ArrayValidator,
  CustomValidator,
  AsyncValidator,
  v,
  va,
  useFieldValidation
} from '../composables/useValidation'

// ===============================
// FORM COMPOSABLE
// ===============================
export {
  useForm
} from '../composables/useForm'

// ===============================
// FIELD COMPONENTS
// ===============================
export { default as BaseField } from '../components/fields/BaseField.vue'
export { default as TextField } from '../components/fields/TextFieldV2.vue'
export { default as NumberField } from '../components/fields/NumberField.vue'
export { default as SelectField } from '../components/fields/SelectField.vue'
export { default as DateField } from '../components/fields/DateField.vue'
export { default as CheckboxField } from '../components/fields/CheckboxField.vue'

// Lazy-loaded components (for better performance)
export const PasswordField = () => import('../components/fields/PasswordFieldV2.vue')
// export const AutocompleteField = () => import('../components/fields/AutocompleteField.vue')
// export const RadioField = () => import('../components/fields/RadioField.vue')
// export const ToggleField = () => import('../components/fields/ToggleField.vue')
// export const FileField = () => import('./components/fields/FileField.vue')
// export const ImageField = () => import('./components/fields/ImageField.vue')

// ===============================
// MAIN FORM COMPONENT
// ===============================
export { default as EnhancedForm } from '../components/EnhancedForm.vue'

// ===============================
// FIELD REGISTRY SYSTEM
// ===============================
export {
  fieldComponents,
  FieldRegistry,
  FieldFactory,
  FieldPluginManager,
  globalFieldRegistry,
  globalFieldFactory,
  globalPluginManager,
  fieldUtils,
  fieldPresets,
  devUtils,
  createFieldPlugin
} from '../components/fields/registry'

export type {
  IFieldRegistry,
  FormFieldComponent as IFormFieldComponent,
  FieldType as IFieldType,
  FieldPlugin as IFieldPlugin
} from '../components/fields/registry'

// ===============================
// UTILITY FUNCTIONS
// ===============================

/**
 * Create a simple text field configuration
 */
export function createTextField(
  name: string,
  label?: string,
  config?: Partial<FormField>
): FormField {
  return {
    name,
    type: 'text',
    label: label || name,
    ...config
  }
}

/**
 * Create a simple email field configuration
 */
export function createEmailField(
  name: string,
  label?: string,
  config?: Partial<FormField>
): FormField {
  return {
    name,
    type: 'email',
    label: label || name,
    validation: v.string().email().required(),
    ...config
  }
}

/**
 * Create a simple number field configuration
 */
export function createNumberField(
  name: string,
  label?: string,
  min?: number,
  max?: number,
  config?: Partial<FormField>
): FormField {
  let validation = v.number()
  if (min !== undefined) { validation = validation.min(min) }
  if (max !== undefined) { validation = validation.max(max) }

  return {
    name,
    type: 'number',
    label: label || name,
    min,
    max,
    validation: validation.required(),
    ...config
  }
}

/**
 * Create a simple select field configuration
 */
export function createSelectField(
  name: string,
  options: SelectOption[],
  label?: string,
  config?: Partial<FormField>
): FormField {
  return {
    name,
    type: 'select',
    label: label || name,
    options,
    validation: v.custom((value) => {
      if (!value) { return 'Please select an option' }
      return true
    }),
    ...config
  }
}

/**
 * Create a simple checkbox field configuration
 */
export function createCheckboxField(
  name: string,
  label?: string,
  config?: Partial<FormField>
): FormField {
  return {
    name,
    type: 'checkbox',
    label: label || name,
    defaultValue: false,
    ...config
  }
}

/**
 * Create a simple date field configuration
 */
export function createDateField(
  name: string,
  label?: string,
  config?: Partial<FormField>
): FormField {
  return {
    name,
    type: 'date',
    label: label || name,
    validation: v.custom((value) => {
      if (!value) { return 'Please select a date' }
      return true
    }),
    ...config
  }
}

/**
 * Create a form field group for related fields
 */
export function createFieldGroup(
  fields: FormField[],
  groupConfig?: {
    title?: string
    description?: string
    collapsible?: boolean
    defaultCollapsed?: boolean
  }
): FormField[] {
  // Add group metadata to fields
  return fields.map(field => ({
    ...field,
    group: groupConfig
  }))
}

/**
 * Create conditional field logic
 */
export function createConditionalField(
  field: FormField,
  condition: {
    field: string
    value: any
    operator?: 'eq' | 'neq' | 'in' | 'nin' | 'gt' | 'lt' | 'gte' | 'lte'
  }
): FormField {
  return {
    ...field,
    conditional: condition
  }
}

/**
 * Validate a form configuration
 */
export function validateFormConfig(fields: FormField[]): {
  isValid: boolean
  errors: string[]
  warnings: string[]
} {
  const errors: string[] = []
  const warnings: string[] = []
  const fieldNames = new Set<string>()

  fields.forEach((field, index) => {
    // Check for duplicate field names
    if (fieldNames.has(field.name)) {
      errors.push(`Duplicate field name "${field.name}" at index ${index}`)
    }
    fieldNames.add(field.name)

    // Check if field type is registered
    if (!fieldUtils.isFieldAvailable(field.type)) {
      errors.push(`Field type "${field.type}" is not available for field "${field.name}"`)
    }

    // Check conditional dependencies
    if (field.conditional && !fieldNames.has(field.conditional.field)) {
      warnings.push(`Conditional field "${field.conditional.field}" for "${field.name}" is not defined yet`)
    }

    // Check for missing required properties
    if (!field.name) {
      errors.push(`Field at index ${index} is missing required "name" property`)
    }

    if (!field.type) {
      errors.push(`Field "${field.name}" is missing required "type" property`)
    }
  })

  return {
    isValid: errors.length === 0,
    errors,
    warnings
  }
}

/**
 * Transform legacy field configuration to new format
 */
export function transformLegacyFields(legacyFields: any[]): FormField[] {
  return legacyFields.map((legacyField) => {
    // Transform old field format to new format
    const field: FormField = {
      name: legacyField.field || legacyField.name,
      type: mapLegacyType(legacyField.dataType || legacyField.type),
      label: typeof legacyField.header === 'function'
        ? legacyField.header()
        : legacyField.header,
      defaultValue: getDefaultValueForType(legacyField.dataType || legacyField.type),
      ui: {
        placeholder: legacyField.placeholder,
        helpText: legacyField.helpText,
        clearable: legacyField.showClearButton,
        className: legacyField.class,
        style: legacyField.style,
        readonly: legacyField.disabled
      }
    }

    // Add field-specific properties
    if (legacyField.options) {
      field.options = legacyField.options
    }

    if (legacyField.multiple !== undefined) {
      field.multiple = legacyField.multiple
    }

    if (legacyField.rows) {
      (field as any).rows = legacyField.rows
    }

    return field
  })
}

/**
 * Map legacy field types to new field types
 */
function mapLegacyType(legacyType: string): FieldType {
  const typeMap: Record<string, FieldType> = {
    'text': 'text',
    'code': 'text',
    'textarea': 'textarea',
    'password': 'password',
    'number': 'number',
    'date': 'date',
    'check': 'checkbox',
    'file': 'file',
    'fileupload': 'file',
    'image': 'image',
    'multiselect': 'multiselect',
    'select': 'select',
    'dropdown': 'select',
    'local-select': 'select'
  }

  return typeMap[legacyType] || 'text'
}

/**
 * Get default value for field type
 */
function getDefaultValueForType(type: string): any {
  const defaultValues: Record<string, any> = {
    text: '',
    email: '',
    password: '',
    textarea: '',
    number: null,
    currency: null,
    percentage: null,
    date: null,
    datetime: null,
    time: null,
    checkbox: false,
    toggle: false,
    select: null,
    multiselect: [],
    autocomplete: null,
    radio: null,
    file: null,
    image: null
  }

  return defaultValues[type] ?? null
}

// ===============================
// THEME SYSTEM
// ===============================

/**
 * Default form theme
 */
export const defaultTheme: FormTheme = {
  spacing: {
    xs: '0.25rem',
    sm: '0.5rem',
    md: '0.75rem',
    lg: '1rem',
    xl: '1.5rem'
  },
  colors: {
    primary: '#3b82f6',
    secondary: '#6b7280',
    success: '#10b981',
    warning: '#f59e0b',
    error: '#ef4444',
    info: '#3b82f6',
    surface: '#ffffff',
    background: '#f9fafb',
    text: '#111827',
    textSecondary: '#6b7280',
    border: '#d1d5db',
    borderFocus: '#3b82f6'
  },
  typography: {
    fontFamily: 'system-ui, -apple-system, sans-serif',
    fontSize: {
      xs: '0.75rem',
      sm: '0.875rem',
      md: '1rem',
      lg: '1.125rem',
      xl: '1.25rem'
    },
    fontWeight: {
      normal: '400',
      medium: '500',
      semibold: '600',
      bold: '700'
    },
    lineHeight: {
      tight: '1.25',
      normal: '1.5',
      relaxed: '1.75'
    }
  },
  borderRadius: {
    none: '0',
    sm: '0.25rem',
    md: '0.5rem',
    lg: '0.75rem',
    full: '9999px'
  },
  shadows: {
    sm: '0 1px 2px 0 rgb(0 0 0 / 0.05)',
    md: '0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1)',
    lg: '0 10px 15px -3px rgb(0 0 0 / 0.1), 0 4px 6px -4px rgb(0 0 0 / 0.1)',
    focus: '0 0 0 2px rgb(59 130 246 / 0.5)'
  },
  transitions: {
    fast: 'all 0.15s ease',
    normal: 'all 0.2s ease',
    slow: 'all 0.3s ease'
  }
}

/**
 * Apply theme to CSS custom properties
 */
export function applyTheme(theme: Partial<FormTheme> = {}): void {
  const mergedTheme = { ...defaultTheme, ...theme }
  const root = document.documentElement

  // Apply spacing
  Object.entries(mergedTheme.spacing).forEach(([key, value]) => {
    root.style.setProperty(`--form-spacing-${key}`, value)
  })

  // Apply colors
  Object.entries(mergedTheme.colors).forEach(([key, value]) => {
    root.style.setProperty(`--form-color-${key}`, value)
  })

  // Apply typography
  root.style.setProperty('--form-font-family', mergedTheme.typography.fontFamily)
  Object.entries(mergedTheme.typography.fontSize).forEach(([key, value]) => {
    root.style.setProperty(`--form-font-size-${key}`, value)
  })
  Object.entries(mergedTheme.typography.fontWeight).forEach(([key, value]) => {
    root.style.setProperty(`--form-font-weight-${key}`, value)
  })
  Object.entries(mergedTheme.typography.lineHeight).forEach(([key, value]) => {
    root.style.setProperty(`--form-line-height-${key}`, value)
  })

  // Apply border radius
  Object.entries(mergedTheme.borderRadius).forEach(([key, value]) => {
    root.style.setProperty(`--form-border-radius-${key}`, value)
  })

  // Apply shadows
  Object.entries(mergedTheme.shadows).forEach(([key, value]) => {
    root.style.setProperty(`--form-shadow-${key}`, value)
  })

  // Apply transitions
  Object.entries(mergedTheme.transitions).forEach(([key, value]) => {
    root.style.setProperty(`--form-transition-${key}`, value)
  })
}

// ===============================
// PLUGIN SYSTEM
// ===============================

/**
 * Install form system plugins
 */
export function installFormPlugin(plugin: FormPlugin): void {
  globalPluginManager.install(plugin)
}

/**
 * Create a custom field plugin
 */
export function createCustomFieldPlugin(
  name: string,
  version: string,
  fieldDefinitions: Record<string, any>
): FormPlugin {
  return createFieldPlugin(name, version, fieldDefinitions)
}

// ===============================
// DEVELOPMENT UTILITIES
// ===============================

/**
 * Debug form configuration
 */
export function debugForm(fields: FormField[], config?: FormConfig): void {
  console.group('🔧 Enhanced Form Debug')

  console.log('📋 Form Configuration:', config)
  console.log('📝 Fields:', fields)
  console.log('✅ Field Validation:', validateFormConfig(fields))

  console.group('🎯 Field Registry')
  devUtils.debugRegistry()
  console.groupEnd()

  console.group('⚡ Performance Metrics')
  const uniqueTypes = [...new Set(fields.map(f => f.type))]
  Promise.all(
    uniqueTypes.map(async (type) => {
      const time = await devUtils.measureFieldPerformance(type)
      console.log(`${type}: ${time.toFixed(2)}ms`)
    })
  )
  console.groupEnd()

  console.groupEnd()
}

/**
 * Generate form documentation
 */
export function generateFormDocs(fields: FormField[]): string {
  let docs = '# Form Documentation\n\n'

  docs += '## Fields\n\n'
  fields.forEach((field) => {
    const metadata = fieldUtils.getFieldMetadata(field.type)
    docs += `### ${field.label || field.name}\n\n`
    docs += `- **Type**: ${field.type}\n`
    docs += `- **Name**: ${field.name}\n`
    docs += `- **Required**: ${field.validation ? 'Yes' : 'No'}\n`
    docs += `- **Description**: ${metadata.description}\n`
    if (field.ui?.helpText) {
      docs += `- **Help**: ${field.ui.helpText}\n`
    }
    if (field.defaultValue !== undefined) {
      docs += `- **Default**: ${JSON.stringify(field.defaultValue)}\n`
    }
    docs += '\n'
  })

  return docs
}

/**
 * Export form configuration as JSON
 */
export function exportFormConfig(fields: FormField[], config?: FormConfig): string {
  return JSON.stringify({
    version: '1.0.0',
    config: config || {},
    fields: fields.map(field => ({
      ...field,
      // Remove function references for serialization
      validation: field.validation ? '[Validation Function]' : undefined
    }))
  }, null, 2)
}

/**
 * Import form configuration from JSON
 */
export function importFormConfig(jsonString: string): {
  fields: FormField[]
  config?: FormConfig
} {
  try {
    const parsed = JSON.parse(jsonString)
    return {
      fields: parsed.fields || [],
      config: parsed.config
    }
  }
  catch (error) {
    throw new Error(`Failed to parse form configuration: ${error}`)
  }
}

// ===============================
// ACCESSIBILITY UTILITIES
// ===============================

/**
 * Validate form accessibility
 */
export function validateA11y(fields: FormField[]): {
  errors: string[]
  warnings: string[]
  suggestions: string[]
} {
  const errors: string[] = []
  const warnings: string[] = []
  const suggestions: string[] = []

  fields.forEach((field) => {
    // Check for missing labels
    if (!field.label) {
      errors.push(`Field "${field.name}" is missing a label`)
    }

    // Check for proper help text
    if (!field.ui?.helpText && field.validation) {
      warnings.push(`Field "${field.name}" has validation but no help text`)
    }

    // Check for color-only error indication
    if (field.validation && !field.ui?.helpText) {
      suggestions.push(`Consider adding help text to "${field.name}" to improve accessibility`)
    }
  })

  return { errors, warnings, suggestions }
}

/**
 * Generate ARIA attributes for a field
 */
export function generateAriaAttributes(field: FormField, hasError: boolean = false): Record<string, string> {
  const attrs: Record<string, string> = {}

  if (field.label) {
    attrs['aria-label'] = field.label
  }

  if (hasError) {
    attrs['aria-invalid'] = 'true'
    attrs['aria-describedby'] = `${field.name}-error`
  }
  else if (field.ui?.helpText) {
    attrs['aria-describedby'] = `${field.name}-help`
  }

  if (field.validation) {
    attrs['aria-required'] = 'true'
  }

  return attrs
}

// ===============================
// PERFORMANCE UTILITIES
// ===============================

/**
 * Optimize field rendering order
 */
export function optimizeFieldOrder(fields: FormField[]): FormField[] {
  // Sort fields by complexity (simple fields first)
  const complexity: Record<FieldType, number> = {
    text: 1,
    email: 1,
    password: 2,
    number: 2,
    currency: 3,
    percentage: 3,
    checkbox: 1,
    toggle: 2,
    radio: 2,
    textarea: 3,
    select: 4,
    multiselect: 5,
    autocomplete: 6,
    date: 4,
    datetime: 5,
    time: 4,
    file: 6,
    image: 7
  }

  return [...fields].sort((a, b) => {
    const aComplexity = complexity[a.type] || 10
    const bComplexity = complexity[b.type] || 10
    return aComplexity - bComplexity
  })
}

/**
 * Batch field validation for better performance
 */
export async function batchValidateFields(
  fields: FormField[],
  values: Record<string, any>
): Promise<Record<string, ValidationResult>> {
  const results: Record<string, ValidationResult> = {}

  // Process fields in batches of 5 to avoid blocking the UI
  const batchSize = 5
  for (let i = 0; i < fields.length; i += batchSize) {
    const batch = fields.slice(i, i + batchSize)

    await Promise.all(
      batch.map(async (field) => {
        if (field.validation) {
          const value = values[field.name]
          results[field.name] = field.validation.safeParse(value)
        }
        else {
          results[field.name] = { success: true, errors: [] }
        }
      })
    )

    // Allow other tasks to run
    await new Promise(resolve => setTimeout(resolve, 0))
  }

  return results
}

// ===============================
// TESTING UTILITIES
// ===============================

/**
 * Create mock form data for testing
 */
export function createMockFormData(fields: FormField[]): Record<string, any> {
  const data: Record<string, any> = {}

  fields.forEach((field) => {
    switch (field.type) {
      case 'text':
      case 'email':
      case 'password':
      case 'textarea':
        data[field.name] = 'Test value'
        break
      case 'number':
      case 'currency':
      case 'percentage':
        data[field.name] = 42
        break
      case 'checkbox':
      case 'toggle':
        data[field.name] = true
        break
      case 'date':
      case 'datetime':
      case 'time':
        data[field.name] = new Date()
        break
      case 'select':
      case 'autocomplete':
        data[field.name] = field.options?.[0]?.value || 'option1'
        break
      case 'multiselect':
        data[field.name] = field.options?.slice(0, 2).map(o => o.value) || ['option1', 'option2']
        break
      case 'radio':
        data[field.name] = field.options?.[0]?.value || 'option1'
        break
      default:
        data[field.name] = field.defaultValue
    }
  })

  return data
}

/**
 * Generate test cases for form validation
 */
export function generateValidationTests(fields: FormField[]): Array<{
  name: string
  data: Record<string, any>
  expectedValid: boolean
  description: string
}> {
  const tests: Array<{
    name: string
    data: Record<string, any>
    expectedValid: boolean
    description: string
  }> = []

  // Valid data test
  tests.push({
    name: 'Valid form data',
    data: createMockFormData(fields),
    expectedValid: true,
    description: 'All fields filled with valid data'
  })

  // Empty data test
  tests.push({
    name: 'Empty form data',
    data: {},
    expectedValid: false,
    description: 'All fields empty'
  })

  // Field-specific tests
  fields.forEach((field) => {
    if (field.validation) {
      const mockData = createMockFormData(fields)

      // Test with invalid data
      switch (field.type) {
        case 'email':
          tests.push({
            name: `Invalid ${field.name}`,
            data: { ...mockData, [field.name]: 'invalid-email' },
            expectedValid: false,
            description: `${field.name} with invalid email format`
          })
          break
        case 'number':
          if (field.min !== undefined) {
            tests.push({
              name: `${field.name} below minimum`,
              data: { ...mockData, [field.name]: field.min - 1 },
              expectedValid: false,
              description: `${field.name} below minimum value`
            })
          }
          break
      }
    }
  })

  return tests
}

// ===============================
// DEFAULT EXPORT
// ===============================

/**
 * Enhanced Form System
 * A comprehensive, modular, and accessible form system for Vue.js applications
 */
export default {
  // Core components
  EnhancedForm,
  BaseField,
  TextField,
  NumberField,
  SelectField,
  DateField,
  CheckboxField,

  // Composables
  useForm,
  useFieldValidation,

  // Validation
  v,
  va,
  ValidationCodes,

  // Registry system
  globalFieldRegistry,
  globalFieldFactory,
  fieldUtils,
  fieldPresets,

  // Utilities
  createTextField,
  createEmailField,
  createNumberField,
  createSelectField,
  createCheckboxField,
  createDateField,
  createFieldGroup,
  createConditionalField,
  validateFormConfig,
  transformLegacyFields,

  // Theme system
  defaultTheme,
  applyTheme,

  // Plugin system
  installFormPlugin,
  createCustomFieldPlugin,

  // Development
  debugForm,
  generateFormDocs,
  exportFormConfig,
  importFormConfig,

  // Accessibility
  validateA11y,
  generateAriaAttributes,

  // Performance
  optimizeFieldOrder,
  batchValidateFields,

  // Testing
  createMockFormData,
  generateValidationTests
}

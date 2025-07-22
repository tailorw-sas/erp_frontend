// components/fields/registry.ts - VERSIÓN CORREGIDA
import { type Component, defineAsyncComponent } from 'vue'
import type { FieldType, FormFieldComponent, FormFieldRegistry } from '../../types/form'

// Lazy-loaded field components for better performance
export const fieldComponents = {
  // Text-based fields
  text: defineAsyncComponent(() => import('./TextFieldV2.vue')),
  email: defineAsyncComponent(() => import('./TextFieldV2.vue')),
  // password: defineAsyncComponent(() => import('./PasswordField.vue')), // Not implemented yet
  textarea: defineAsyncComponent(() => import('./TextFieldV2.vue')),

  // Number fields
  // number: defineAsyncComponent(() => import('./NumberField.vue')), // Not implemented yet
  // currency: defineAsyncComponent(() => import('./NumberField.vue')), // Not implemented yet
  // percentage: defineAsyncComponent(() => import('./NumberField.vue')), // Not implemented yet

  // Date/Time fields
  date: defineAsyncComponent(() => import('./DateField.vue')),
  datetime: defineAsyncComponent(() => import('./DateField.vue')),
  time: defineAsyncComponent(() => import('./DateField.vue')),

  // ✅ FIXED: Selection fields ahora mapean a UnifiedSelectField
  select: defineAsyncComponent(() => import('./SelectField.vue')),
  multiselect: defineAsyncComponent(() => import('./SelectField.vue')),
  localselect: defineAsyncComponent(() => import('./LocalSelectField.vue')),

  // Boolean fields
  // checkbox: defineAsyncComponent(() => import('./CheckboxField.vue')) // Not implemented yet

  // Note: The following components are not implemented yet:
  // password, number, currency, percentage, checkbox, autocomplete, radio, toggle, file, image
  // They can be added when these components are created
} as const

// Field registry class for managing field components
export class FieldRegistry implements FormFieldRegistry {
  private components = new Map<FieldType, FormFieldComponent>()

  constructor() {
    this.registerDefaultComponents()
  }

  /**
   * Register default field components
   */
  private registerDefaultComponents(): void {
    Object.entries(fieldComponents).forEach(([type, component]) => {
      this.register(type as FieldType, component as FormFieldComponent)
    })
  }

  /**
   * Register a field component for a specific type
   */
  register(type: FieldType, component: FormFieldComponent): void {
    this.components.set(type, component)
  }

  /**
   * Unregister a field component
   */
  unregister(type: FieldType): boolean {
    return this.components.delete(type)
  }

  /**
   * Get a field component by type
   */
  get(type: FieldType): FormFieldComponent | undefined {
    return this.components.get(type)
  }

  /**
   * Check if a field type is registered
   */
  has(type: FieldType): boolean {
    return this.components.has(type)
  }

  /**
   * Get all registered field types
   */
  getRegisteredTypes(): FieldType[] {
    return Array.from(this.components.keys())
  }

  /**
   * Clear all registered components
   */
  clear(): void {
    this.components.clear()
  }

  /**
   * Get the size of the registry
   */
  get size(): number {
    return this.components.size
  }

  // Map interface compatibility
  set(type: FieldType, component: FormFieldComponent): this {
    this.register(type, component)
    return this
  }

  delete(type: FieldType): boolean {
    return this.unregister(type)
  }

  forEach(callback: (component: FormFieldComponent, type: FieldType, registry: Map<FieldType, FormFieldComponent>) => void): void {
    this.components.forEach(callback)
  }

  keys(): MapIterator<FieldType> {
    return this.components.keys()
  }

  values(): MapIterator<FormFieldComponent> {
    return this.components.values()
  }

  entries(): MapIterator<[FieldType, FormFieldComponent]> {
    return this.components.entries()
  }

  [Symbol.iterator](): MapIterator<[FieldType, FormFieldComponent]> {
    return this.components[Symbol.iterator]()
  }

  get [Symbol.toStringTag](): string {
    return 'FieldRegistry'
  }
}

// Global registry instance
export const globalFieldRegistry = new FieldRegistry()

// Field factory for creating field instances
export class FieldFactory {
  constructor(private registry: FieldRegistry = globalFieldRegistry) {}

  /**
   * Create a field component instance
   */
  create(type: FieldType): FormFieldComponent | null {
    const component = this.registry.get(type)

    if (!component) {
      if (process.env.NODE_ENV === 'development') {
        Logger.warn(`Field component for type "${type}" not found in registry`)
        Logger.log('Available types:', this.registry.getRegisteredTypes())
        Logger.log('Registry size:', this.registry.size)
      }
      return null
    }

    return component
  }

  /**
   * Check if a field type can be created
   */
  canCreate(type: FieldType): boolean {
    return this.registry.has(type)
  }

  /**
   * Get all available field types that can be created
   */
  getAvailableTypes(): FieldType[] {
    return this.registry.getRegisteredTypes()
  }
}

// Global factory instance
export const globalFieldFactory = new FieldFactory()

// ✅ NUEVO: Debug utilities mejoradas
export const registryDebugUtils = {
  /**
   * Debug registry state
   */
  debugRegistry(): void {
    if (process.env.NODE_ENV === 'development') {
      Logger.group('🔧 [DEBUG] Field Registry State')
      Logger.log('Registered types:', globalFieldRegistry.getRegisteredTypes())
      Logger.log('Registry size:', globalFieldRegistry.size)

      // Test each registered component
      const types = globalFieldRegistry.getRegisteredTypes()
      types.forEach((type) => {
        const component = globalFieldFactory.create(type)
        Logger.log(`- ${type}:`, component ? '✅ Available' : '❌ Missing')
      })

      // Test specific ReportViewer types
      const reportViewerTypes = ['select', 'multiselect', 'localselect', 'date', 'text']
      Logger.log('\nReportViewer compatibility:')
      reportViewerTypes.forEach((type) => {
        const canCreate = globalFieldFactory.canCreate(type as FieldType)
        Logger.log(`- ${type}:`, canCreate ? '✅ Compatible' : '❌ Incompatible')
      })

      console.groupEnd()
    }
  },

  /**
   * Test component creation
   */
  testComponentCreation(type: FieldType): boolean {
    try {
      const component = globalFieldFactory.create(type)
      return !!component
    }
    catch (error) {
      console.error(`Error creating component "${type}":`, error)
      return false
    }
  },

  /**
   * Validate ReportViewer integration
   */
  validateReportViewerIntegration(): {
    isValid: boolean
    missingComponents: string[]
    availableComponents: string[]
  } {
    const requiredTypes = ['select', 'multiselect', 'localselect', 'date', 'text']
    const availableComponents: string[] = []
    const missingComponents: string[] = []

    requiredTypes.forEach((type) => {
      if (globalFieldFactory.canCreate(type as FieldType)) {
        availableComponents.push(type)
      }
      else {
        missingComponents.push(type)
      }
    })

    return {
      isValid: missingComponents.length === 0,
      missingComponents,
      availableComponents
    }
  }
}

// Utility functions for field management
export const fieldUtils = {
  /**
   * Register a custom field component globally
   */
  registerField(type: FieldType, component: FormFieldComponent): void {
    globalFieldRegistry.register(type, component)
  },

  /**
   * Unregister a field component globally
   */
  unregisterField(type: FieldType): boolean {
    return globalFieldRegistry.unregister(type)
  },

  /**
   * Check if a field type is available
   */
  isFieldAvailable(type: FieldType): boolean {
    return globalFieldRegistry.has(type)
  },

  /**
   * Get a field component by type
   */
  getFieldComponent(type: FieldType): FormFieldComponent | null {
    return globalFieldFactory.create(type)
  },

  /**
   * Get all available field types
   */
  getAvailableFieldTypes(): FieldType[] {
    return globalFieldFactory.getAvailableTypes()
  },

  /**
   * Register additional field components when they become available
   */
  registerAdditionalComponents(): void {
    // This function can be called to register components that are not yet implemented
    // Example usage:
    // fieldUtils.registerAdditionalComponents()

    const additionalComponents = {
      // These will be registered when the components are created
      // password: defineAsyncComponent(() => import('./PasswordField.vue')),
      // number: defineAsyncComponent(() => import('./NumberField.vue')),
      // currency: defineAsyncComponent(() => import('./NumberField.vue')),
      // percentage: defineAsyncComponent(() => import('./NumberField.vue')),
      // checkbox: defineAsyncComponent(() => import('./CheckboxField.vue')),
      // autocomplete: defineAsyncComponent(() => import('./AutocompleteField.vue')),
      // radio: defineAsyncComponent(() => import('./RadioField.vue')),
      // toggle: defineAsyncComponent(() => import('./ToggleField.vue')),
      // file: defineAsyncComponent(() => import('./FileField.vue')),
      // image: defineAsyncComponent(() => import('./ImageField.vue'))
    }

    Object.entries(additionalComponents).forEach(([type, component]) => {
      if (component) {
        globalFieldRegistry.register(type as FieldType, component as FormFieldComponent)
      }
    })
  },

  /**
   * Check if a field type will be available (including future components)
   */
  willBeAvailable(type: FieldType): boolean {
    const plannedTypes: FieldType[] = ['password', 'number', 'currency', 'percentage', 'checkbox', 'autocomplete', 'radio', 'toggle', 'file', 'image']
    return globalFieldRegistry.has(type) || plannedTypes.includes(type)
  },

  /**
   * Validate field type
   */
  validateFieldType(type: string): type is FieldType {
    const validTypes = globalFieldFactory.getAvailableTypes()
    return validTypes.includes(type as FieldType)
  },

  /**
   * Get field type categories for UI organization
   */
  getFieldCategories(): Record<string, FieldType[]> {
    return {
      text: ['text', 'email', 'password', 'textarea'],
      numeric: ['number', 'currency', 'percentage'],
      datetime: ['date', 'datetime', 'time'],
      selection: ['select', 'multiselect', 'localselect'],
      boolean: ['checkbox']
      // Note: The following categories will be available when components are implemented:
      // autocomplete: ['autocomplete'],
      // radio: ['radio'],
      // toggle: ['toggle'],
      // file: ['file', 'image']
    }
  },

  /**
   * Get field type metadata
   */
  getFieldMetadata(type: FieldType): {
    category: string
    label: string
    description: string
    icon: string
    supportedValidations: string[]
    available: boolean
  } {
    const _metadata = {
      // Available components
      text: {
        category: 'text',
        label: 'Text Input',
        description: 'Single line text input',
        icon: 'pi pi-pencil',
        supportedValidations: ['required', 'minLength', 'maxLength', 'pattern'],
        available: true
      },
      email: {
        category: 'text',
        label: 'Email Input',
        description: 'Email address input with validation',
        icon: 'pi pi-envelope',
        supportedValidations: ['required', 'email', 'minLength', 'maxLength'],
        available: true
      },
      textarea: {
        category: 'text',
        label: 'Textarea',
        description: 'Multi-line text input',
        icon: 'pi pi-align-left',
        supportedValidations: ['required', 'minLength', 'maxLength'],
        available: true
      },
      date: {
        category: 'datetime',
        label: 'Date Picker',
        description: 'Date selection input',
        icon: 'pi pi-calendar',
        supportedValidations: ['required'],
        available: true
      },
      datetime: {
        category: 'datetime',
        label: 'DateTime Picker',
        description: 'Date and time selection',
        icon: 'pi pi-clock',
        supportedValidations: ['required'],
        available: true
      },
      time: {
        category: 'datetime',
        label: 'Time Picker',
        description: 'Time selection input',
        icon: 'pi pi-clock',
        supportedValidations: ['required'],
        available: true
      },
      select: {
        category: 'selection',
        label: 'Dropdown Select',
        description: 'Single option selection with API support',
        icon: 'pi pi-chevron-down',
        supportedValidations: ['required'],
        available: true
      },
      multiselect: {
        category: 'selection',
        label: 'Multi Select',
        description: 'Multiple option selection with API support',
        icon: 'pi pi-list',
        supportedValidations: ['required'],
        available: true
      },
      localselect: {
        category: 'selection',
        label: 'Local Select',
        description: 'Single option selection with static data',
        icon: 'pi pi-list',
        supportedValidations: ['required'],
        available: true
      },

      // Not yet implemented components
      password: {
        category: 'text',
        label: 'Password Input',
        description: 'Secure password input',
        icon: 'pi pi-key',
        supportedValidations: ['required', 'minLength', 'maxLength', 'pattern'],
        available: false
      },
      number: {
        category: 'numeric',
        label: 'Number Input',
        description: 'Numeric input with controls',
        icon: 'pi pi-hashtag',
        supportedValidations: ['required', 'min', 'max'],
        available: false
      },
      currency: {
        category: 'numeric',
        label: 'Currency Input',
        description: 'Currency amount input',
        icon: 'pi pi-dollar',
        supportedValidations: ['required', 'min', 'max'],
        available: false
      },
      percentage: {
        category: 'numeric',
        label: 'Percentage Input',
        description: 'Percentage value input',
        icon: 'pi pi-percentage',
        supportedValidations: ['required', 'min', 'max'],
        available: false
      },
      checkbox: {
        category: 'boolean',
        label: 'Checkbox',
        description: 'Boolean checkbox input',
        icon: 'pi pi-check-square',
        supportedValidations: ['required'],
        available: false
      },
      autocomplete: {
        category: 'selection',
        label: 'Autocomplete',
        description: 'Searchable option selection',
        icon: 'pi pi-search',
        supportedValidations: ['required'],
        available: false
      },
      radio: {
        category: 'selection',
        label: 'Radio Buttons',
        description: 'Single option radio selection',
        icon: 'pi pi-circle',
        supportedValidations: ['required'],
        available: false
      },
      toggle: {
        category: 'boolean',
        label: 'Toggle Switch',
        description: 'Boolean toggle switch',
        icon: 'pi pi-toggle-on',
        supportedValidations: ['required'],
        available: false
      },
      file: {
        category: 'file',
        label: 'File Upload',
        description: 'File selection and upload',
        icon: 'pi pi-upload',
        supportedValidations: ['required'],
        available: false
      },
      image: {
        category: 'file',
        label: 'Image Upload',
        description: 'Image file selection and preview',
        icon: 'pi pi-image',
        supportedValidations: ['required'],
        available: false
      }
    }

    return _metadata[type] || {
      category: 'unknown',
      label: type,
      description: `${type} field`,
      icon: 'pi pi-question',
      supportedValidations: [],
      available: false
    }
  }
}

// Plugin system for extending field functionality
export interface IFieldPlugin {
  name: string
  version: string
  install: (registry: FieldRegistry) => void
}

export class FieldPluginManager {
  private plugins = new Map<string, IFieldPlugin>()

  constructor(private registry: FieldRegistry = globalFieldRegistry) {}

  /**
   * Install a plugin
   */
  install(plugin: IFieldPlugin): void {
    if (this.plugins.has(plugin.name)) {
      if (process.env.NODE_ENV === 'development') {
        console.warn(`Plugin "${plugin.name}" is already installed`)
      }
      return
    }

    try {
      plugin.install(this.registry)
      this.plugins.set(plugin.name, plugin)
      if (process.env.NODE_ENV === 'development') {
        // eslint-disable-next-line no-console
        console.log(`Plugin "${plugin.name}" v${plugin.version} installed successfully`)
      }
    }
    catch (error) {
      if (process.env.NODE_ENV === 'development') {
        console.error(`Failed to install plugin "${plugin.name}":`, error)
      }
    }
  }

  /**
   * Uninstall a plugin
   */
  uninstall(name: string): boolean {
    const plugin = this.plugins.get(name)
    if (!plugin) {
      if (process.env.NODE_ENV === 'development') {
        console.warn(`Plugin "${name}" is not installed`)
      }
      return false
    }

    this.plugins.delete(name)
    if (process.env.NODE_ENV === 'development') {
      // eslint-disable-next-line no-console
      console.log(`Plugin "${name}" uninstalled`)
    }
    return true
  }

  /**
   * Check if a plugin is installed
   */
  isInstalled(name: string): boolean {
    return this.plugins.has(name)
  }

  /**
   * Get installed plugin info
   */
  getPlugin(name: string): IFieldPlugin | undefined {
    return this.plugins.get(name)
  }

  /**
   * Get all installed plugins
   */
  getAllPlugins(): IFieldPlugin[] {
    return Array.from(this.plugins.values())
  }

  /**
   * Get plugin names
   */
  getPluginNames(): string[] {
    return Array.from(this.plugins.keys())
  }
}

// Global plugin manager
export const globalPluginManager = new FieldPluginManager()

// Helper for creating custom field plugins
export function createFieldPlugin(
  name: string,
  version: string,
  fieldDefinitions: Record<string, FormFieldComponent>
): IFieldPlugin {
  return {
    name,
    version,
    install(registry: FieldRegistry) {
      Object.entries(fieldDefinitions).forEach(([type, component]) => {
        registry.register(type as FieldType, component)
      })
    }
  }
}

// Preset configurations for common field setups
export const fieldPresets = {
  /**
   * Basic form fields preset
   */
  basic: (): void => {
    // Already registered by default
  },

  /**
   * Advanced form fields preset with additional components
   */
  advanced: (): void => {
    // Note: These components are not yet implemented
    // They can be uncommented when the components are created
    /*
    const advancedFields = {
      richtext: defineAsyncComponent(() => import('./RichTextEditor.vue')),
      colorpicker: defineAsyncComponent(() => import('./ColorPickerField.vue')),
      slider: defineAsyncComponent(() => import('./SliderField.vue')),
      rating: defineAsyncComponent(() => import('./RatingField.vue')),
      autocomplete: defineAsyncComponent(() => import('./AutocompleteField.vue')),
      radio: defineAsyncComponent(() => import('./RadioField.vue')),
      toggle: defineAsyncComponent(() => import('./ToggleField.vue')),
      file: defineAsyncComponent(() => import('./FileField.vue')),
      image: defineAsyncComponent(() => import('./ImageField.vue'))
    }

    Object.entries(advancedFields).forEach(([type, component]) => {
      globalFieldRegistry.register(type as FieldType, component as FormFieldComponent)
    })
    */
    if (process.env.NODE_ENV === 'development') {
      console.warn('Advanced preset: Additional components not yet implemented')
    }
  },

  /**
   * Minimal form fields preset
   */
  minimal: (): void => {
    globalFieldRegistry.clear()
    const minimalFields = {
      text: fieldComponents.text,
      select: fieldComponents.select
    }

    Object.entries(minimalFields).forEach(([type, component]) => {
      globalFieldRegistry.register(type as FieldType, component as FormFieldComponent)
    })
  }
}

// Development utilities
export const devUtils = {
  /**
   * Debug registry state
   */
  debugRegistry(): void {
    registryDebugUtils.debugRegistry()
  },

  /**
   * Validate field configuration
   */
  validateFieldConfig(type: FieldType, config: Record<string, unknown>): {
    isValid: boolean
    errors: string[]
    warnings: string[]
  } {
    const errors: string[] = []
    const warnings: string[] = []

    // Check if field type is registered
    if (!globalFieldRegistry.has(type)) {
      errors.push(`Field type "${type}" is not registered`)
    }

    // Get field metadata and validate configuration
    const _metadata = fieldUtils.getFieldMetadata(type)

    // Validate required properties based on field type
    if (type === 'select' || type === 'multiselect' || type === 'radio' || type === 'localselect') {
      if (!config.options && !config.asyncDataSource && !config.objApi && !config.apiConfig) {
        if (type === 'localselect' && !config.options) {
          errors.push(`Field type "localselect" requires options array`)
        }
        else if (type !== 'localselect') {
          warnings.push(`Field type "${type}" should have options, apiConfig, or objApi`)
        }
      }
    }

    if (type === 'number' || type === 'currency' || type === 'percentage') {
      if (config.min !== undefined && config.max !== undefined
        && config.min !== null && config.max !== null
        && Number(config.min) > Number(config.max)) {
        errors.push(`Min value (${config.min}) cannot be greater than max value (${config.max})`)
      }
    }

    if (type === 'date' || type === 'datetime') {
      if (config.minDate && config.maxDate && config.minDate > config.maxDate) {
        errors.push('Min date cannot be greater than max date')
      }
    }

    return {
      isValid: errors.length === 0,
      errors,
      warnings
    }
  },

  /**
   * Performance metrics for field rendering
   */
  measureFieldPerformance(type: FieldType): Promise<number> {
    return new Promise((resolve) => {
      const start = performance.now()
      const component = globalFieldFactory.create(type)

      if (component) {
        // Simulate component instantiation time
        setTimeout(() => {
          const end = performance.now()
          resolve(end - start)
        }, 0)
      }
      else {
        resolve(-1) // Component not found
      }
    })
  },

  /**
   * Get field component bundle size estimation
   */
  async getFieldBundleSize(type: FieldType): Promise<number> {
    try {
      const component = globalFieldRegistry.get(type)
      if (!component) { return 0 }

      // This is a mock implementation
      // In a real scenario, you'd use bundler analysis tools
      const mockSizes: Record<FieldType, number> = {
        text: 15,
        email: 15,
        password: 25,
        textarea: 18,
        localselect: 20,
        number: 22,
        currency: 25,
        percentage: 23,
        date: 35,
        datetime: 38,
        time: 30,
        select: 45, // Increased for UnifiedSelectField
        multiselect: 45, // Increased for UnifiedSelectField
        autocomplete: 40,
        checkbox: 12,
        radio: 18,
        toggle: 15,
        file: 45,
        image: 50
      }

      return mockSizes[type] || 20
    }
    catch {
      return 0
    }
  },

  /**
   * Generate performance report
   */
  async generatePerformanceReport(): Promise<{
    totalFields: number
    averageLoadTime: number
    totalBundleSize: number
    fieldMetrics: Array<{
      type: FieldType
      loadTime: number
      bundleSize: number
    }>
  }> {
    const types = globalFieldRegistry.getRegisteredTypes()
    const fieldMetrics = []

    for (const type of types) {
      const loadTime = await devUtils.measureFieldPerformance(type)
      const bundleSize = await devUtils.getFieldBundleSize(type)

      fieldMetrics.push({
        type,
        loadTime,
        bundleSize
      })
    }

    const averageLoadTime = fieldMetrics.reduce((sum, metric) => sum + metric.loadTime, 0) / fieldMetrics.length
    const totalBundleSize = fieldMetrics.reduce((sum, metric) => sum + metric.bundleSize, 0)

    return {
      totalFields: types.length,
      averageLoadTime,
      totalBundleSize,
      fieldMetrics
    }
  }
}

// Field validation utilities
export const fieldValidationUtils = {
  /**
   * Get recommended validators for a field type
   */
  getRecommendedValidators(type: FieldType): string[] {
    const metadata = fieldUtils.getFieldMetadata(type)
    return metadata.supportedValidations
  },

  /**
   * Check if a validator is compatible with a field type
   */
  isValidatorCompatible(type: FieldType, validatorType: string): boolean {
    const recommended = fieldValidationUtils.getRecommendedValidators(type)
    return recommended.includes(validatorType)
  },

  /**
   * Generate validation suggestions
   */
  generateValidationSuggestions(type: FieldType): Array<{
    validator: string
    description: string
    example: string
  }> {
    const suggestions: Record<string, { description: string, example: string }> = {
      required: {
        description: 'Makes the field mandatory',
        example: 'v.string().required("This field is required")'
      },
      minLength: {
        description: 'Sets minimum character length',
        example: 'v.string().minLength(3, "Must be at least 3 characters")'
      },
      maxLength: {
        description: 'Sets maximum character length',
        example: 'v.string().maxLength(50, "Must be no more than 50 characters")'
      },
      email: {
        description: 'Validates email format',
        example: 'v.string().email("Please enter a valid email")'
      },
      pattern: {
        description: 'Validates against a regex pattern',
        example: 'v.string().pattern(/^[A-Z]/, "Must start with uppercase")'
      },
      min: {
        description: 'Sets minimum numeric value',
        example: 'v.number().min(0, "Must be positive")'
      },
      max: {
        description: 'Sets maximum numeric value',
        example: 'v.number().max(100, "Cannot exceed 100")'
      }
    }

    const recommended = fieldValidationUtils.getRecommendedValidators(type)
    return recommended.map(validator => ({
      validator,
      ...suggestions[validator]
    })).filter(Boolean)
  }
}

// Export types for external use
export type {
  FieldRegistry as IFieldRegistry,
  FormFieldComponent,
  FieldType,
  IFieldPlugin as FieldPlugin
}

// Default export
export default {
  registry: globalFieldRegistry,
  factory: globalFieldFactory,
  pluginManager: globalPluginManager,
  utils: fieldUtils,
  presets: fieldPresets,
  devUtils,
  validationUtils: fieldValidationUtils,
  createPlugin: createFieldPlugin,
  debugUtils: registryDebugUtils // ✅ NUEVO: Export debug utilities
}

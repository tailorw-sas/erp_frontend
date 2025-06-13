// utils/formFieldBuilder.ts - Utility functions for creating form field definitions

import type {
  CheckboxFieldDefinition,
  DateFieldDefinition,
  FieldDefinition,
  FileFieldDefinition,
  MultiSelectFieldDefinition,
  NumberFieldDefinition,
  PasswordFieldDefinition,
  SelectFieldDefinition,
  SelectOption,
  TextFieldDefinition,
  TextareaFieldDefinition,
  ValidationSchema
} from '../types/form'

/**
 * Form Field Builder - Utility class for creating field definitions
 */
export class FormFieldBuilder {
  /**
   * Create a text input field
   */
  static text(
    field: string,
    header?: string,
    options: Partial<TextFieldDefinition> = {}
  ): TextFieldDefinition {
    return {
      field,
      dataType: 'text',
      header: header || field,
      placeholder: `Enter ${header || field}`,
      class: 'col-12 md:col-6',
      ...options
    }
  }

  /**
   * Create a code input field (automatically uppercase)
   */
  static code(
    field: string,
    header?: string,
    options: Partial<TextFieldDefinition> = {}
  ): TextFieldDefinition {
    return {
      field,
      dataType: 'code',
      header: header || field,
      placeholder: `Enter ${header || field}`,
      class: 'col-12 md:col-6',
      toUppercase: true,
      ...options
    }
  }

  /**
   * Create a textarea field
   */
  static textarea(
    field: string,
    header?: string,
    options: Partial<TextareaFieldDefinition> = {}
  ): TextareaFieldDefinition {
    return {
      field,
      dataType: 'textarea',
      header: header || field,
      placeholder: `Enter ${header || field}`,
      class: 'col-12',
      rows: 4,
      ...options
    }
  }

  /**
   * Create a number input field
   */
  static number(
    field: string,
    header?: string,
    options: Partial<NumberFieldDefinition> = {}
  ): NumberFieldDefinition {
    return {
      field,
      dataType: 'number',
      header: header || field,
      placeholder: `Enter ${header || field}`,
      class: 'col-12 md:col-4',
      minFractionDigits: 0,
      maxFractionDigits: 2,
      ...options
    }
  }

  /**
   * Create a select/dropdown field
   */
  static select(
    field: string,
    header: string,
    options: SelectOption[],
    selectOptions: Partial<SelectFieldDefinition> = {}
  ): SelectFieldDefinition {
    return {
      field,
      dataType: 'select',
      header,
      placeholder: `Select ${header}`,
      class: 'col-12 md:col-6',
      options,
      optionLabel: 'label',
      optionValue: 'value',
      filterable: true,
      showClear: true,
      ...selectOptions
    }
  }

  /**
   * Create a multiselect field
   */
  static multiselect(
    field: string,
    header: string,
    options: SelectOption[],
    multiselectOptions: Partial<MultiSelectFieldDefinition> = {}
  ): MultiSelectFieldDefinition {
    return {
      field,
      dataType: 'multiselect',
      header,
      placeholder: `Select ${header}`,
      class: 'col-12 md:col-6',
      options,
      optionLabel: 'label',
      optionValue: 'value',
      multiple: true,
      filterable: true,
      showClear: true,
      maxSelectedLabels: 3,
      ...multiselectOptions
    }
  }

  /**
   * Create a date field
   */
  static date(
    field: string,
    header?: string,
    options: Partial<DateFieldDefinition> = {}
  ): DateFieldDefinition {
    return {
      field,
      dataType: 'date',
      header: header || field,
      placeholder: `Select ${header || field}`,
      class: 'col-12 md:col-4',
      dateFormat: 'yy-mm-dd',
      ...options
    }
  }

  /**
   * Create a file upload field
   */
  static file(
    field: string,
    header?: string,
    options: Partial<FileFieldDefinition> = {}
  ): FileFieldDefinition {
    return {
      field,
      dataType: 'file',
      header: header || field,
      class: 'col-12 md:col-6',
      accept: '*',
      maxFileSize: 1000000, // 1MB
      ...options
    }
  }

  /**
   * Create an image upload field
   */
  static image(
    field: string,
    header?: string,
    options: Partial<FileFieldDefinition> = {}
  ): FileFieldDefinition {
    return {
      field,
      dataType: 'image',
      header: header || field,
      class: 'col-12 md:col-6',
      accept: 'image/*',
      maxFileSize: 2000000, // 2MB
      ...options
    }
  }

  /**
   * Create a checkbox field
   */
  static checkbox(
    field: string,
    header?: string,
    options: Partial<CheckboxFieldDefinition> = {}
  ): CheckboxFieldDefinition {
    return {
      field,
      dataType: 'check',
      header: header || field,
      class: 'col-12',
      ...options
    }
  }

  /**
   * Create a password field
   */
  static password(
    field: string,
    header?: string,
    options: Partial<PasswordFieldDefinition> = {}
  ): PasswordFieldDefinition {
    return {
      field,
      dataType: 'password',
      header: header || field,
      placeholder: `Enter ${header || field}`,
      class: 'col-12 md:col-6',
      ...options
    }
  }

  /**
   * Create a required field (adds 'required' class)
   */
  static required<T extends FieldDefinition>(fieldDef: T): T {
    return {
      ...fieldDef,
      class: `${fieldDef.class || ''} required`.trim()
    }
  }

  /**
   * Create a full-width field (col-12)
   */
  static fullWidth<T extends FieldDefinition>(fieldDef: T): T {
    return {
      ...fieldDef,
      class: `${fieldDef.class?.replace(/col-\d+/g, '')} col-12`
    }
  }

  /**
   * Create a half-width field (col-6)
   */
  static halfWidth<T extends FieldDefinition>(fieldDef: T): T {
    return {
      ...fieldDef,
      class: `${fieldDef.class?.replace(/col-\d+/g, '')} col-12 md:col-6`
    }
  }

  /**
   * Add validation to a field
   */
  static withValidation<T extends FieldDefinition>(
    fieldDef: T,
    validation: ValidationSchema
  ): T {
    return {
      ...fieldDef,
      validation
    }
  }

  /**
   * Add help text to a field
   */
  static withHelp<T extends FieldDefinition>(
    fieldDef: T,
    helpText: string
  ): T {
    return {
      ...fieldDef,
      helpText
    }
  }

  /**
   * Make a field disabled
   */
  static disabled<T extends FieldDefinition>(fieldDef: T): T {
    return {
      ...fieldDef,
      disabled: true
    }
  }

  /**
   * Make a field hidden
   */
  static hidden<T extends FieldDefinition>(fieldDef: T): T {
    return {
      ...fieldDef,
      hidden: true
    }
  }
}

/**
 * Convenience functions for common field patterns
 */
export const createField = FormFieldBuilder

/**
 * Pre-defined common field configurations
 */
export const CommonFields = {
  /**
   * Standard name field
   */
  name: (validation?: ValidationSchema) => {
    const baseField = FormFieldBuilder.required(
      FormFieldBuilder.text('name', 'Full Name', {
        placeholder: 'Enter your full name'
      })
    )
    return validation ? FormFieldBuilder.withValidation(baseField, validation) : baseField
  },

  /**
   * Standard email field
   */
  email: (validation?: ValidationSchema) => {
    const baseField = FormFieldBuilder.required(
      FormFieldBuilder.text('email', 'Email Address', {
        placeholder: 'Enter your email address'
      })
    )
    return validation ? FormFieldBuilder.withValidation(baseField, validation) : baseField
  },

  /**
   * Standard phone field
   */
  phone: (validation?: ValidationSchema) => {
    const baseField = FormFieldBuilder.text('phone', 'Phone Number', {
      placeholder: 'Enter your phone number'
    })
    return validation ? FormFieldBuilder.withValidation(baseField, validation) : baseField
  },

  /**
   * Standard description field
   */
  description: (rows: number = 4) =>
    FormFieldBuilder.textarea('description', 'Description', {
      placeholder: 'Enter description...',
      rows,
      helpText: 'Provide a detailed description'
    }),

  /**
   * Standard status dropdown
   */
  status: (options: SelectOption[] = [
    { label: 'Active', value: 'active' },
    { label: 'Inactive', value: 'inactive' },
    { label: 'Pending', value: 'pending' }
  ]) =>
    FormFieldBuilder.select('status', 'Status', options),

  /**
   * Standard priority dropdown
   */
  priority: (options: SelectOption[] = [
    { label: 'Low', value: 'low' },
    { label: 'Medium', value: 'medium' },
    { label: 'High', value: 'high' },
    { label: 'Critical', value: 'critical' }
  ]) =>
    FormFieldBuilder.select('priority', 'Priority', options),

  /**
   * Standard date fields
   */
  createdAt: () =>
    FormFieldBuilder.date('createdAt', 'Created Date', {
      disabled: true,
      class: 'col-12 md:col-4'
    }),

  updatedAt: () =>
    FormFieldBuilder.date('updatedAt', 'Updated Date', {
      disabled: true,
      class: 'col-12 md:col-4'
    }),

  birthDate: () =>
    FormFieldBuilder.date('birthDate', 'Birth Date', {
      placeholder: 'Select your birth date',
      class: 'col-12 md:col-4'
    }),

  /**
   * Standard checkbox fields
   */
  isActive: () =>
    FormFieldBuilder.checkbox('isActive', 'Is Active'),

  agreeToTerms: () =>
    FormFieldBuilder.required(
      FormFieldBuilder.checkbox('agreeToTerms', 'I agree to the terms and conditions')
    ),

  /**
   * Standard file upload fields
   */
  avatar: () =>
    FormFieldBuilder.image('avatar', 'Profile Picture', {
      helpText: 'Upload a profile picture (JPG, PNG, max 2MB)'
    }),

  document: () =>
    FormFieldBuilder.file('document', 'Document', {
      accept: '.pdf,.doc,.docx',
      helpText: 'Upload a document (PDF, DOC, DOCX)'
    }),

  /**
   * Standard numeric fields
   */
  age: (validation?: ValidationSchema) => {
    const baseField = FormFieldBuilder.number('age', 'Age', {
      minFractionDigits: 0,
      maxFractionDigits: 0,
      min: 0,
      max: 120
    })
    return validation ? FormFieldBuilder.withValidation(baseField, validation) : baseField
  },

  salary: () =>
    FormFieldBuilder.number('salary', 'Salary', {
      minFractionDigits: 2,
      maxFractionDigits: 2,
      min: 0,
      helpText: 'Annual salary in USD'
    }),

  /**
   * Standard password field
   */
  password: (validation?: ValidationSchema) => {
    const baseField = FormFieldBuilder.required(
      FormFieldBuilder.password('password', 'Password', {
        placeholder: 'Enter a secure password'
      })
    )
    return validation ? FormFieldBuilder.withValidation(baseField, validation) : baseField
  },

  /**
   * Password confirmation field
   */
  confirmPassword: () =>
    FormFieldBuilder.required(
      FormFieldBuilder.password('confirmPassword', 'Confirm Password', {
        placeholder: 'Confirm your password'
      })
    )
}

/**
 * Utility functions for creating form layouts
 */
export const FormLayouts = {
  /**
   * Create a two-column layout
   */
  twoColumn: (fields: FieldDefinition[]): FieldDefinition[] =>
    fields.map(field => ({
      ...field,
      class: `${field.class?.replace(/col-\d+/g, '')} col-12 md:col-6`
    })),

  /**
   * Create a three-column layout
   */
  threeColumn: (fields: FieldDefinition[]): FieldDefinition[] =>
    fields.map(field => ({
      ...field,
      class: `${field.class?.replace(/col-\d+/g, '')} col-12 md:col-4`
    })),

  /**
   * Create a single column layout
   */
  singleColumn: (fields: FieldDefinition[]): FieldDefinition[] =>
    fields.map(field => ({
      ...field,
      class: `${field.class?.replace(/col-\d+/g, '')} col-12`
    })),

  /**
   * Create a responsive layout based on field types
   */
  responsive: (fields: FieldDefinition[]): FieldDefinition[] =>
    fields.map((field) => {
      switch (field.dataType) {
        case 'textarea':
          return { ...field, class: 'col-12' }
        case 'check':
          return { ...field, class: 'col-12' }
        case 'image':
        case 'file':
          return { ...field, class: 'col-12 md:col-6' }
        case 'date':
        case 'number':
          return { ...field, class: 'col-12 md:col-4' }
        default:
          return { ...field, class: 'col-12 md:col-6' }
      }
    })
}

/**
 * Form validation helpers
 */
export const ValidationHelpers = {
  /**
   * Create a required validation message
   */
  required: (fieldName: string) => `${fieldName} is required`,

  /**
   * Create a min length validation message
   */
  minLength: (fieldName: string, min: number) =>
    `${fieldName} must be at least ${min} characters`,

  /**
   * Create a max length validation message
   */
  maxLength: (fieldName: string, max: number) =>
    `${fieldName} must not exceed ${max} characters`,

  /**
   * Create an email validation message
   */
  email: () => 'Please enter a valid email address',

  /**
   * Create a numeric range validation message
   */
  range: (fieldName: string, min: number, max: number) =>
    `${fieldName} must be between ${min} and ${max}`,

  /**
   * Create a pattern validation message
   */
  pattern: (fieldName: string, description: string) =>
    `${fieldName} ${description}`
}

/**
 * Form section builder for organizing fields into groups
 */
export class FormSectionBuilder {
  private sections: { title: string, fields: FieldDefinition[], class?: string }[] = []

  /**
   * Add a section to the form
   */
  addSection(title: string, fields: FieldDefinition[], sectionClass?: string) {
    this.sections.push({ title, fields, class: sectionClass })
    return this
  }

  /**
   * Build the complete field list with section headers
   */
  build(): FieldDefinition[] {
    const allFields: FieldDefinition[] = []

    this.sections.forEach((section, index) => {
      // Add section header as a special field
      allFields.push({
        field: `section_header_${index}`,
        dataType: 'text',
        header: section.title,
        class: `col-12 section-header ${section.class || ''}`,
        disabled: true,
        hidden: false,
        headerClass: 'text-lg font-bold mt-4 mb-2 border-bottom-1 surface-border pb-2'
      })

      // Add section fields
      allFields.push(...section.fields)
    })

    return allFields
  }

  /**
   * Clear all sections
   */
  clear() {
    this.sections = []
    return this
  }
}

/**
 * Quick form builder for common form patterns
 */
export const QuickForms = {
  /**
   * User registration form
   */
  userRegistration: (validations?: Record<string, ValidationSchema>) => [
    CommonFields.name(validations?.name),
    CommonFields.email(validations?.email),
    CommonFields.password(validations?.password),
    CommonFields.confirmPassword(),
    CommonFields.phone(validations?.phone),
    CommonFields.birthDate(),
    CommonFields.agreeToTerms()
  ],

  /**
   * User profile form
   */
  userProfile: (validations?: Record<string, ValidationSchema>) => [
    CommonFields.name(validations?.name),
    CommonFields.email(validations?.email),
    CommonFields.phone(validations?.phone),
    CommonFields.birthDate(),
    CommonFields.avatar(),
    FormFieldBuilder.textarea('bio', 'Biography', {
      placeholder: 'Tell us about yourself...',
      rows: 4,
      class: 'col-12'
    })
  ],

  /**
   * Contact form
   */
  contact: (validations?: Record<string, ValidationSchema>) => [
    CommonFields.name(validations?.name),
    CommonFields.email(validations?.email),
    FormFieldBuilder.text('subject', 'Subject', {
      placeholder: 'Enter subject',
      class: 'col-12'
    }),
    FormFieldBuilder.textarea('message', 'Message', {
      placeholder: 'Enter your message...',
      rows: 6,
      class: 'col-12'
    })
  ],

  /**
   * Employee form
   */
  employee: (validations?: Record<string, ValidationSchema>) => [
    CommonFields.name(validations?.name),
    CommonFields.email(validations?.email),
    FormFieldBuilder.text('employeeId', 'Employee ID', {
      class: 'col-12 md:col-4'
    }),
    FormFieldBuilder.text('department', 'Department', {
      class: 'col-12 md:col-4'
    }),
    FormFieldBuilder.text('position', 'Position', {
      class: 'col-12 md:col-4'
    }),
    CommonFields.salary(),
    CommonFields.birthDate(),
    CommonFields.isActive()
  ]
}

/**
 * Default export for convenience
 */
export default FormFieldBuilder

<script setup lang="ts">
import { computed, ref } from 'vue'
import { z } from 'zod' // For validation
import EnhancedFormComponent from '../components/form/EnhancedFormComponent.vue'
import type { FieldDefinition, FormErrors } from '../types'

// Sample data for dropdowns
const statusOptions = [
  { label: 'Active', value: 'active' },
  { label: 'Inactive', value: 'inactive' },
  { label: 'Pending', value: 'pending' }
]

const categoryOptions = [
  { label: 'Technology', value: 'tech' },
  { label: 'Marketing', value: 'marketing' },
  { label: 'Sales', value: 'sales' },
  { label: 'Support', value: 'support' }
]

// Sample item data
const formItem = ref({
  id: 1,
  name: 'John Doe',
  email: 'john@example.com',
  age: 30,
  status: 'active',
  categories: ['tech', 'sales'],
  description: 'Sample description text',
  isActive: true,
  birthDate: new Date('1993-06-15'),
  salary: 50000,
  password: '',
  profileImage: null,
  document: null
})

// Loading states
const loadingSave = ref(false)
const loadingDelete = ref(false)
const backendErrors = ref<FormErrors>({})

// Validation schemas using Zod
const nameValidation = z.string()
  .min(2, 'Name must be at least 2 characters')
  .max(50, 'Name must not exceed 50 characters')

const emailValidation = z.string()
  .email('Please enter a valid email address')

const ageValidation = z.number()
  .min(18, 'Age must be at least 18')
  .max(100, 'Age must not exceed 100')

const salaryValidation = z.number()
  .min(0, 'Salary must be positive')
  .max(1000000, 'Salary seems unreasonably high')

const passwordValidation = z.string()
  .min(8, 'Password must be at least 8 characters')
  .regex(/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/, 'Password must contain uppercase, lowercase, and number')

// Form field definitions with proper typing
const formFields = computed<FieldDefinition[]>(() => [
  {
    field: 'name',
    dataType: 'text',
    header: 'Full Name',
    placeholder: 'Enter your full name',
    class: 'col-12 md:col-6 required',
    validation: nameValidation,
    helpText: 'Enter your first and last name',
    showClearButton: true
  },
  {
    field: 'email',
    dataType: 'text',
    header: 'Email Address',
    placeholder: 'Enter your email',
    class: 'col-12 md:col-6 required',
    validation: emailValidation,
    helpText: 'We will use this for important notifications'
  },
  {
    field: 'age',
    dataType: 'number',
    header: 'Age',
    placeholder: 'Enter your age',
    class: 'col-12 md:col-4 required',
    validation: ageValidation,
    minFractionDigits: 0,
    maxFractionDigits: 0
  },
  {
    field: 'salary',
    dataType: 'number',
    header: 'Annual Salary',
    placeholder: 'Enter salary amount',
    class: 'col-12 md:col-4',
    validation: salaryValidation,
    minFractionDigits: 2,
    maxFractionDigits: 2,
    helpText: 'Optional field'
  },
  {
    field: 'birthDate',
    dataType: 'date',
    header: 'Birth Date',
    placeholder: 'Select your birth date',
    class: 'col-12 md:col-4',
    helpText: 'Used for age verification'
  },
  {
    field: 'status',
    dataType: 'select',
    header: 'Status',
    placeholder: 'Select your status',
    class: 'col-12 md:col-6 required',
    options: statusOptions,
    optionLabel: 'label',
    optionValue: 'value',
    filterable: true,
    showClear: true
  },
  {
    field: 'categories',
    dataType: 'multiselect',
    header: 'Categories',
    placeholder: 'Select categories',
    class: 'col-12 md:col-6',
    options: categoryOptions,
    optionLabel: 'label',
    optionValue: 'value',
    multiple: true,
    filterable: true,
    showClear: true,
    maxSelectedLabels: 2,
    selectionLimit: 3,
    helpText: 'Select up to 3 categories'
  },
  {
    field: 'description',
    dataType: 'textarea',
    header: 'Description',
    placeholder: 'Enter a description...',
    class: 'col-12',
    rows: 4,
    helpText: 'Provide a brief description about yourself'
  },
  {
    field: 'password',
    dataType: 'password',
    header: 'Password',
    placeholder: 'Enter a secure password',
    class: 'col-12 md:col-6 required',
    validation: passwordValidation,
    helpText: 'Must contain uppercase, lowercase, and number'
  },
  {
    field: 'isActive',
    dataType: 'check',
    header: 'I agree to the terms and conditions',
    class: 'col-12 required'
  },
  {
    field: 'profileImage',
    dataType: 'image',
    header: 'Profile Image',
    class: 'col-12 md:col-6',
    helpText: 'Upload a profile picture (optional)'
  },
  {
    field: 'document',
    dataType: 'file',
    header: 'Document Upload',
    class: 'col-12 md:col-6',
    helpText: 'Upload any relevant documents'
  }
])

// Event handlers
function handleFieldUpdate(data: { fieldKey: string, value: any }) {
  console.log('Field updated:', data)
  // Update the item
  formItem.value = { ...formItem.value, [data.fieldKey]: data.value }
}

function handleReactiveUpdate(values: Record<string, any>) {
  console.log('Form values updated:', values)
  // You can sync with your store here
}

function handleClearField(fieldKey: string) {
  console.log('Field cleared:', fieldKey)
}

async function handleSubmit(data: Record<string, any> & { event: Event }) {
  console.log('Form submitted:', data)

  try {
    loadingSave.value = true

    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 2000))

    // Simulate backend validation errors (randomly)
    if (Math.random() > 0.7) {
      backendErrors.value = {
        email: ['This email is already taken'],
        name: ['Name contains inappropriate content']
      }
      return
    }

    // Clear any previous backend errors
    backendErrors.value = {}

    // Success handling
    console.log('Form saved successfully!')
    alert('Form saved successfully!')
  }
  catch (error) {
    console.error('Error saving form:', error)
    alert('Error saving form')
  }
  finally {
    loadingSave.value = false
  }
}

function handleCancel() {
  console.log('Form cancelled')
  // Reset form or navigate away
}

async function handleDelete(event: Event) {
  console.log('Delete requested:', event)

  if (!confirm('Are you sure you want to delete this item?')) {
    return
  }

  try {
    loadingDelete.value = true

    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1500))

    console.log('Item deleted successfully!')
    alert('Item deleted successfully!')
  }
  catch (error) {
    console.error('Error deleting item:', error)
    alert('Error deleting item')
  }
  finally {
    loadingDelete.value = false
  }
}

function handleAutosave(values: Record<string, any>) {
  console.log('Autosaving form:', values)
  // Implement autosave logic here
}

function handleFieldValidated(fieldKey: string, isValid: boolean) {
  console.log(`Field ${fieldKey} validation:`, isValid)
}

function handleErrorsUpdate(errors: FormErrors) {
  console.log('Form errors updated:', errors)
}

// Advanced usage examples
const compactMode = ref(false)
const showActionsInline = ref(false)
const autosaveEnabled = ref(false)

// Custom field height
const customFieldHeight = ref('2.5rem')
</script>

<template>
  <div class="form-demo-container p-4">
    <!-- Demo Controls -->
    <div class="demo-controls mb-4 p-3 surface-card border-round">
      <h3 class="mb-3">
        Form Demo Controls
      </h3>
      <div class="flex flex-wrap gap-3 align-items-center">
        <div class="flex align-items-center">
          <Checkbox
            v-model="compactMode"
            binary
            input-id="compact-mode"
          />
          <label for="compact-mode" class="ml-2">Compact Mode</label>
        </div>

        <div class="flex align-items-center">
          <Checkbox
            v-model="showActionsInline"
            binary
            input-id="inline-actions"
          />
          <label for="inline-actions" class="ml-2">Inline Actions</label>
        </div>

        <div class="flex align-items-center">
          <Checkbox
            v-model="autosaveEnabled"
            binary
            input-id="autosave"
          />
          <label for="autosave" class="ml-2">Enable Autosave</label>
        </div>

        <div class="flex align-items-center gap-2">
          <label for="field-height">Field Height:</label>
          <Dropdown
            v-model="customFieldHeight"
            :options="[
              { label: 'Small (2rem)', value: '2rem' },
              { label: 'Medium (2.5rem)', value: '2.5rem' },
              { label: 'Large (3rem)', value: '3rem' },
            ]"
            option-label="label"
            option-value="value"
            placeholder="Select height"
            class="w-10rem"
          />
        </div>
      </div>
    </div>

    <!-- Form Container -->
    <div class="form-container surface-card border-round shadow-2">
      <div class="p-4">
        <h2 class="mb-4 text-center">
          Enhanced Form Example
        </h2>

        <!-- Enhanced Form Component -->
        <EnhancedFormComponent
          :fields="formFields"
          :item="formItem"
          :loading-save="loadingSave"
          :loading-delete="loadingDelete"
          :backend-validation="backendErrors"
          :compact-mode="compactMode"
          :show-actions-inline="showActionsInline"
          :autosave="autosaveEnabled"
          :autosave-delay="3000"
          :field-height="customFieldHeight"
          :validate-on-change="true"
          :show-required-indicator="true"
          :show-cancel="true"
          :file-upload-max-size="2000000"
          accepted-image-types="image/*"
          accepted-file-types=".pdf,.doc,.docx"
          form-class="p-fluid formgrid grid"
          container-class="form-fields-container"
          @update:field="handleFieldUpdate"
          @reactive-update-field="handleReactiveUpdate"
          @clear-field="handleClearField"
          @submit="handleSubmit"
          @cancel="handleCancel"
          @delete="handleDelete"
          @autosave="handleAutosave"
          @field-validated="handleFieldValidated"
          @update:errors-list="handleErrorsUpdate"
        >
          <!-- Custom field slots example -->
          <template #field-name-custom="{ field, item, onUpdate, errors, isLoading }">
            <!-- You can completely customize individual fields here -->
            <div class="custom-name-field">
              <InputText
                v-if="!isLoading"
                :model-value="item[field.field]"
                placeholder="Custom name field"
                class="w-full"
                :class="{ 'p-invalid': errors }"
                @update:model-value="onUpdate(field.field, $event)"
              />
              <small v-if="errors" class="p-error">{{ errors.join(', ') }}</small>
            </div>
          </template>

          <!-- Custom header slot -->
          <template #header-email="{ field }">
            <div class="flex align-items-center gap-2">
              <i class="pi pi-envelope" />
              <strong>{{ field.header }}</strong>
              <Badge value="Required" severity="danger" size="small" />
            </div>
          </template>

          <!-- Custom footer slot -->
          <template #footer-description="{ field }">
            <div class="flex justify-content-between align-items-center mt-2">
              <small class="text-secondary">{{ field.footer || 'Optional field' }}</small>
              <small class="text-primary">Character count: {{ formItem.description?.length || 0 }}/500</small>
            </div>
          </template>

          <!-- Custom form buttons -->
          <template #form-footer="{ item, canSave, canDelete, isDirty }">
            <div class="flex flex-column gap-3">
              <!-- Status indicators -->
              <div class="flex justify-content-between align-items-center">
                <div class="flex gap-2">
                  <Badge
                    v-if="isDirty"
                    value="Unsaved Changes"
                    severity="warning"
                    size="small"
                  />
                  <Badge
                    v-if="!canSave"
                    value="Validation Errors"
                    severity="danger"
                    size="small"
                  />
                  <Badge
                    v-if="autosaveEnabled"
                    value="Autosave Enabled"
                    severity="info"
                    size="small"
                  />
                </div>

                <div class="text-sm text-secondary">
                  Last modified: {{ new Date().toLocaleString() }}
                </div>
              </div>

              <!-- Action buttons -->
              <div class="flex justify-content-between">
                <div class="flex gap-2">
                  <Button
                    label="Reset Form"
                    icon="pi pi-refresh"
                    severity="secondary"
                    outlined
                    size="small"
                    @click="formItem = { ...formItem, name: '', email: '', description: '' }"
                  />
                  <Button
                    label="Load Sample Data"
                    icon="pi pi-download"
                    severity="info"
                    outlined
                    size="small"
                    @click="formItem = {
                      ...formItem,
                      name: 'Jane Smith',
                      email: 'jane@example.com',
                      age: 28,
                      description: 'Sample loaded description',
                    }"
                  />
                </div>

                <div class="flex gap-2">
                  <Button
                    label="Cancel"
                    icon="pi pi-times"
                    severity="secondary"
                    @click="handleCancel"
                  />
                  <Button
                    v-if="canDelete"
                    label="Delete"
                    icon="pi pi-trash"
                    severity="danger"
                    outlined
                    :loading="loadingDelete"
                    @click="handleDelete($event)"
                  />
                  <Button
                    label="Save Draft"
                    icon="pi pi-save"
                    severity="success"
                    outlined
                    :disabled="!canSave"
                    :loading="loadingSave"
                    @click="handleSubmit({ ...item.fieldValues, event: $event })"
                  />
                  <Button
                    label="Save & Close"
                    icon="pi pi-check"
                    severity="success"
                    :disabled="!canSave"
                    :loading="loadingSave"
                    @click="handleSubmit({ ...item.fieldValues, event: $event })"
                  />
                </div>
              </div>
            </div>
          </template>
        </EnhancedFormComponent>
      </div>
    </div>

    <!-- Debug Information -->
    <div class="debug-info mt-4 p-3 surface-100 border-round">
      <h4 class="mb-3">
        Debug Information
      </h4>
      <div class="grid">
        <div class="col-12 md:col-6">
          <h5>Current Form Values:</h5>
          <pre class="text-sm">{{ JSON.stringify(formItem, null, 2) }}</pre>
        </div>
        <div class="col-12 md:col-6">
          <h5>Backend Errors:</h5>
          <pre class="text-sm">{{ JSON.stringify(backendErrors, null, 2) }}</pre>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.form-demo-container {
  max-width: 1200px;
  margin: 0 auto;
}

.demo-controls {
  background: var(--surface-50);
  border: 1px solid var(--surface-200);
}

.form-container {
  background: var(--surface-0);
}

.form-fields-container {
  min-height: 400px;
}

.custom-name-field {
  position: relative;
}

.debug-info {
  font-family: 'Courier New', monospace;
  font-size: 0.875rem;
  max-height: 300px;
  overflow-y: auto;
}

.debug-info pre {
  background: var(--surface-0);
  padding: 1rem;
  border-radius: 6px;
  border: 1px solid var(--surface-300);
  max-height: 200px;
  overflow-y: auto;
}

/* Custom styling for form fields */
:deep(.custom-name-field .p-inputtext) {
  border: 2px solid var(--primary-200);
  background: var(--primary-50);
}

:deep(.custom-name-field .p-inputtext:focus) {
  border-color: var(--primary-500);
  box-shadow: 0 0 0 2px var(--primary-100);
}

/* Responsive adjustments */
@media screen and (max-width: 768px) {
  .demo-controls .flex {
    flex-direction: column;
    align-items: stretch;
  }

  .demo-controls .flex > div {
    justify-content: center;
    padding: 0.5rem 0;
  }
}

/* Animation for form changes */
.form-container {
  transition: all 0.3s ease;
}

.compact-mode .form-container {
  transform: scale(0.95);
}

/* Status badges styling */
:deep(.p-badge) {
  font-size: 0.75rem;
  padding: 0.25rem 0.5rem;
}
</style>

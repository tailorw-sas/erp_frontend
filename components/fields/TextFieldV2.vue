<!-- components/fields/TextField.vue -->
<script setup lang="ts">
import { computed, nextTick, ref } from 'vue'
import InputText from 'primevue/inputtext'
import Textarea from 'primevue/textarea'
import type { FormField } from '../../types/form'

// Props interface - simplificada y compatible
interface TextFieldProps {
  field: FormField
  value?: string | number
  error?: string[]
  touched?: boolean
  dirty?: boolean
  disabled?: boolean
  loading?: boolean
  config?: any
}

const props = withDefaults(defineProps<TextFieldProps>(), {
  value: '',
  disabled: false,
  loading: false
})

// Emits
const emit = defineEmits<{
  'update:value': [value: string]
  'blur': []
  'focus': []
  'clear': []
}>()

// Template refs
const inputRef = ref<HTMLInputElement | null>(null)
const textareaRef = ref<HTMLTextAreaElement | null>(null)

// Computed properties
const inputValue = computed({
  get: () => (props.value || '').toString(),
  set: (newValue: string) => {
    emit('update:value', newValue)
  }
})

const hasError = computed(() => {
  return props.touched && props.error && props.error.length > 0
})

const isTextarea = computed(() =>
  props.field.type === 'textarea'
)

const inputClasses = computed(() => {
  const classes = []
  if (hasError.value) {
    classes.push('p-invalid')
  }
  return classes
})

const fieldClasses = computed(() => {
  const classes = ['text-field']

  if (hasError.value) { classes.push('text-field--error') }
  if (props.disabled) { classes.push('text-field--disabled') }
  if (props.loading) { classes.push('text-field--loading') }
  if ((props.field as any).class) { classes.push((props.field as any).class) }

  return classes.join(' ')
})

// Methods
function handleInput(event: Event) {
  const target = event.target as HTMLInputElement | HTMLTextAreaElement
  emit('update:value', target.value)
}

function handleBlur() {
  emit('blur')
}

function handleFocus() {
  emit('focus')
}

// Expose methods for parent components
async function focus() {
  await nextTick()
  if (isTextarea.value) {
    textareaRef.value?.focus()
  }
  else {
    inputRef.value?.focus()
  }
}

defineExpose({
  focus,
  inputRef,
  textareaRef
})
</script>

<template>
  <div :class="fieldClasses">
    <!-- Label -->
    <label
      v-if="(field as any).label"
      :for="field.name"
      class="text-field__label"
    >
      {{ (field as any).label }}
      <span v-if="(field as any).required" class="text-field__required">*</span>
    </label>

    <!-- Regular Input -->
    <InputText
      v-if="!isTextarea"
      :id="field.name"
      ref="inputRef"
      v-model="inputValue"
      :class="inputClasses"
      :disabled="disabled || loading"
      :placeholder="(field as any).placeholder"
      class="text-field__input"
      @input="handleInput"
      @blur="handleBlur"
      @focus="handleFocus"
    />

    <!-- Textarea -->
    <Textarea
      v-else
      :id="field.name"
      ref="textareaRef"
      v-model="inputValue"
      :class="inputClasses"
      :disabled="disabled || loading"
      :placeholder="(field as any).placeholder"
      class="text-field__input"
      :rows="3"
      :auto-resize="true"
      @input="handleInput"
      @blur="handleBlur"
      @focus="handleFocus"
    />

    <!-- Help Text -->
    <small
      v-if="(field as any).helpText && !hasError"
      class="text-field__help"
    >
      {{ (field as any).helpText }}
    </small>

    <!-- Error Messages -->
    <div v-if="hasError" class="text-field__errors">
      <small
        v-for="errorMsg in error"
        :key="errorMsg"
        class="text-field__error"
      >
        {{ errorMsg }}
      </small>
    </div>
  </div>
</template>

<style scoped>
.text-field {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  width: 100%;
}

.text-field__label {
  font-size: 0.875rem;
  font-weight: 600;
  color: #374151;
  margin-bottom: 0.25rem;
}

.text-field__required {
  color: #dc2626;
  margin-left: 0.125rem;
}

.text-field__input {
  width: 100%;
  height: 2.5rem;
  padding: 0 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.875rem;
  transition: all 0.2s ease-in-out;
}

.text-field__input:focus {
  outline: none;
  border-color: #6366f1;
  box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.1);
}

.text-field__input.p-invalid {
  border-color: #dc2626;
}

.text-field__help {
  color: #6b7280;
  font-size: 0.75rem;
  line-height: 1.3;
}

.text-field__errors {
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
}

.text-field__error {
  color: #dc2626;
  font-size: 0.75rem;
  line-height: 1.3;
}

.text-field--disabled {
  opacity: 0.6;
  pointer-events: none;
}

.text-field--loading .text-field__input {
  background-color: #f9fafb;
}

/* Textarea específico */
.text-field__input[rows] {
  height: auto;
  min-height: 4rem;
  resize: vertical;
  padding-top: 0.5rem;
  padding-bottom: 0.5rem;
}

/* Mobile responsive */
@media (max-width: 768px) {
  .text-field__input {
    height: 2.75rem;
    font-size: 16px; /* Prevent zoom on iOS */
  }
}
</style>

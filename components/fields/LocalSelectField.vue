<!-- LocalSelectField.vue - TEMPLATE ACTUALIZADO -->
<script setup lang="ts">
import { computed } from 'vue'
import Dropdown from 'primevue/dropdown'

// Props actualizado para incluir label
const props = defineProps<{
  field: {
    field: string
    dataType: string
    label?: string // ✅ NUEVO: Agregar label
    placeholder?: string // ✅ NUEVO: Agregar placeholder
    required?: boolean // ✅ NUEVO: Agregar required
    options?: any[]
    [key: string]: any
  }
  value?: any
  error?: any[]
  disabled?: boolean
}>()

const emit = defineEmits<{
  'update:value': [value: any]
}>()

// ✅ MEJORADO: Función normalizeOptions del ReportViewer
function normalizeOptions(options: any[]): Array<{ name: string, value: any }> {
  if (!options || !Array.isArray(options) || options.length === 0) {
    return []
  }

  // Handle backend localselect format: {id, name, slug, defaultValue}
  if (options.every(opt => opt && typeof opt === 'object' && 'name' in opt && 'id' in opt)) {
    return options.map(opt => ({
      name: opt.name,
      value: opt.id
    }))
  }

  // Si ya están normalizadas con 'name'
  if (options.every(opt => opt && typeof opt === 'object' && 'name' in opt && 'value' in opt)) {
    return options
  }

  // Si tienen 'label' en lugar de 'name'
  if (options.every(opt => opt && typeof opt === 'object' && 'label' in opt && 'value' in opt)) {
    return options.map(opt => ({ name: opt.label, value: opt.value }))
  }

  // Si son strings
  if (options.every(opt => typeof opt === 'string')) {
    return options.map(opt => ({ name: opt, value: opt }))
  }

  // Fallback
  return options.map((opt, index) => ({ name: String(opt), value: opt }))
}

// ✅ NUEVO: Computed properties para mejor UX
const fieldLabel = computed(() => {
  return props.field.label || props.field.field || 'Select Option'
})

const fieldPlaceholder = computed(() => {
  return props.field.placeholder || `Select ${fieldLabel.value}`
})

const isRequired = computed(() => {
  return props.field.required || false
})

const hasError = computed(() => {
  return props.error && props.error.length > 0
})

// Event handler
function handleChange(selectedValue: any) {
  emit('update:value', selectedValue)
}
</script>

<template>
  <!-- ✅ NUEVO: Estructura con label consistente -->
  <div v-if="field.dataType === 'localselect'" class="local-select-field w-full">
    <!-- ✅ LABEL CONSISTENTE -->
    <label
      v-if="fieldLabel"
      :for="`local-select-${field.field}`"
      class="field-label"
      :class="{ required: isRequired }"
    >
      {{ fieldLabel }}
    </label>

    <!-- ✅ DROPDOWN MEJORADO -->
    <Dropdown
      :id="`local-select-${field.field}`"
      :model-value="value"
      :options="normalizeOptions(field.options || [])"
      option-label="name"
      option-value="value"
      :placeholder="fieldPlaceholder"
      class="w-full"
      :class="{ 'p-invalid': hasError }"
      :filter="(field.options || []).length > 10"
      show-clear
      :disabled="disabled"
      :data-field="field.field"
      :data-field-label="fieldLabel"
      @update:model-value="handleChange"
    />

    <!-- ✅ NUEVO: Error messages -->
    <div v-if="hasError" class="field-error-messages">
      <small
        v-for="(errorItem, index) in error"
        :key="index"
        class="field-error-message"
      >
        <i class="pi pi-exclamation-triangle" />
        {{ errorItem.message || errorItem }}
      </small>
    </div>
  </div>
</template>

<style scoped>
/* ===============================
   LOCAL SELECT FIELD STYLES
   =============================== */

.local-select-field {
  --field-spacing: 0.75rem;
  --error-color: #ef4444;
  --gray-800: #1f2937;

  display: flex;
  flex-direction: column;
  width: 100%;
}

/* Label styling */
.field-label {
  font-weight: 600;
  color: var(--gray-800);
  margin-bottom: var(--field-spacing);
  font-size: 0.9rem;
  letter-spacing: 0.02em;
  line-height: 1.4;
  cursor: pointer;

  &.required::after {
    content: ' *';
    color: var(--error-color);
    font-weight: bold;
    margin-left: 0.125rem;
  }
}

/* Error messages */
.field-error-messages {
  margin-top: 0.25rem;
}

.field-error-message {
  display: flex;
  align-items: flex-start;
  gap: 0.25rem;
  color: var(--error-color);
  font-size: 0.75rem;
  line-height: 1.4;
  margin-bottom: 0.25rem;

  &:last-child {
    margin-bottom: 0;
  }

  .pi {
    flex-shrink: 0;
    margin-top: 0.125rem;
    font-size: 0.7rem;
  }
}

/* Responsive */
@media (max-width: 768px) {
  .field-label {
    font-size: 1rem;
    margin-bottom: 1rem;
  }
}
</style>

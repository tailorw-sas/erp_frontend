<script setup lang="ts">
import { type PropType, onMounted, onUpdated, ref, watch, watchEffect } from 'vue'
import type { IOptionField } from './interfaces/IFieldInterfaces'
import { validEmail } from '~/utils/valid_form_fields'

interface ObjErrorMessage {
  key: string
  message: string
}

interface ValidationMethods {
  [key: string]: (value: string) => string | undefined
}

const props = defineProps({
  properties: {
    type: Object as PropType<IOptionField>,
    required: true,
  },
  validationKeywords: {
    type: Array as () => string[],
    default: () => [],
  },
  submit: {
    type: Boolean,
    required: true,
    default: false
  },
  parentValue: {
    type: String,
    required: true
  },
})

const emits = defineEmits([
  'update:modelValue',
  'update:errorMessages',
  'invalidField'
])
const fieldModelValue = ref<any>(props.properties.value || null)
const fieldId = `text-field-${Math.random().toString(36).substring(7)}`

const listError = ref<ObjErrorMessage[]>([])

const methods: ValidationMethods = {
  // minLength,
  isEmail: validEmail,
}

function handleInput(event?: Event) {
  if (event) {
    const inputValue = (event.target as HTMLInputElement).value

    if (props.properties.maxLength && inputValue.length > props.properties.maxLength) {
      (event.target as HTMLInputElement).value = inputValue.slice(0, props.properties.maxLength)
    }
  }

  listError.value = []
  // listError.value = listError.value.filter(error => error.key !== 'required'); esta linea elimina solo el error que tiene que ver con la validacion especifica
  // Required
  if (props.properties.isRequired && !fieldModelValue.value) {
    listError.value = [...listError.value, { key: 'required', message: 'This is a required field' }]
  }

  for (const keyword of props.validationKeywords) {
    if (keyword in methods) {
      const error = methods[keyword](fieldModelValue.value)
      if (error) {
        listError.value = [...listError.value, { key: keyword, message: error }]
      }
    }
  }
  fieldModelValue.value = removeAccents(fieldModelValue.value)

  emits('update:modelValue', fieldModelValue.value)
  emits('update:errorMessages', [])
}

function removeAccents(value: string) {
  if (value) {
    const normalizeWord = value.normalize('NFD')

    // Eliminar los signos diacríticos utilizando una expresión regular
    const wordWithoutAccent = normalizeWord.replace(/[\u0300-\u036F]/g, '')

    return wordWithoutAccent
  }
}

function setMessageBackendError() {
  listError.value = listError.value.filter(error => error.key !== 'backend')
  for (const iterator of props.properties.errorMessage) {
    listError.value = [...listError.value, { key: 'backend', message: iterator }]
  }
}
watchEffect(() => {
  setMessageBackendError()
})
watch(() => listError.value, (newValue) => {
  if (newValue.length > 0) {
    emits('invalidField', true)
  }
  else {
    emits('invalidField', false)
  }
})
watch(() => props.submit, (newValue) => {
  if (newValue) {
    handleInput()
  }
})
watch(() => props.parentValue, (newValue) => {
  if (newValue) {
    fieldModelValue.value = props.parentValue
    handleInput()
  }
})

onMounted(() => {
  if (props.parentValue) {
    fieldModelValue.value = props.parentValue
  }
})
</script>

<template>
  <div>
    <i v-if="false" class="pi pi-envelope" />
    <div class="mb-1">
      <label :for="fieldId" class="text-900 font-semibold">
        {{ props.properties.label }}
        <span v-if="props.properties.isRequired && props.properties.showRequiredLabel" :class="{ 'required-label': props.properties.isRequired }">*</span>
      </label>
    </div>
    <div>
      <InputText
        :id="fieldId"
        v-model="fieldModelValue"
        :placeholder="props.properties.placeholder"
        :type="props.properties.type"
        :required="props.properties.isRequired"
        :class="{ 'p-invalid': listError.length !== 0 }"
        autocomplete="off"
        class="text-field-size h-3rem"
        :disabled="props.properties.disabled"
        size="large"
        @input="handleInput"
      />
    </div>
    <div v-if="props.properties.type === 'text'" class="flex justify-content-end py-0 my-0" style="font-size: 10px;">
      {{ fieldModelValue ? fieldModelValue.length : 0 }}/{{ props.properties.maxLength || 50 }}
    </div>
    <div :class="props.properties.showCounter ? 'margin-counter' : ''">
      <small :id="`${fieldId}-help`" class="p-error text-start">
        <transition-group name="error-fade">
          <span v-for="textError of listError" :key="textError.key" class="text-error block flex pl-2 mb-0">{{ textError.message }}</span>
        </transition-group>
      </small>
    </div>
  </div>
</template>

<style scoped>
.margin-counter {
  margin-top: -10px;
}

.text-field-size {
  width: 100%;
}
.error-fade-enter-active, .error-fade-leave-active {
  transition: opacity 0.5s;
}
.error-fade-enter, .error-fade-leave-to {
  opacity: 0;
}
.text-error {
  text-align: left;
}
.required-label {
  color: red;
}
</style>

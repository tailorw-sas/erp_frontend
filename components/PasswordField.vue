<script setup lang="ts">
import { type PropType, ref, watch } from 'vue'
import type { IOptionField, ObjErrorMessage, ValidationMethods } from './interfaces/IFieldInterfaces'
import { maxLength, minLength, validEmail, validLongitud } from '~/utils/valid_form_fields'

const props = defineProps({
  properties: {
    type: Object as PropType<IOptionField>,
    required: true
  },
  validationKeywords: {
    type: Array as () => string[],
    default: () => []
  },
  submit: {
    type: Boolean,
    required: true,
    default: false
  }
})

const emits = defineEmits([
  'update:modelValue',
  'update:errorMessages',
  'invalidField'
])
const fieldModelValue = ref<any>(props.properties.value || null)
const fieldId = `text-field-${Math.random().toString(36).substring(7)}`

const listError = ref<ObjErrorMessage[]>([])

const methods: ValidationMethods = ref({
  maxLength,
  minLength,
  isEmail: validEmail,
  longitude: validLongitud,
  latitude: validLongitud
})

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
    if (keyword in methods.value) {
      let error = ''
      if (keyword.includes('maxLength')) {
        // const cant = keyword.split(':')[1] para el caso de que quiera poner la cantidad de characters por el mismo keyword y no por la props
        const cant = props.properties.maxLength
        error = cant && methods.value[keyword](fieldModelValue.value, Number(cant))
      }
      else if (keyword.includes('minLength')) {
        const cant = props.properties.minLength
        error = cant && methods.value[keyword](fieldModelValue.value, Number(cant))
      }
      else {
        error = methods.value[keyword](fieldModelValue.value)
      }

      if (error) {
        listError.value = [...listError.value, { key: keyword, message: error }]
      }
    }
  }

  emits('update:modelValue', props.properties.type === 'number' ? Number(fieldModelValue.value) : fieldModelValue.value)
  emits('update:errorMessages', [])
}

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
</script>

<template>
  <div>
    <i v-if="false" class="pi pi-envelope" />
    <div class="mb-1" style="text-align: start">
      <label :for="fieldId" class="text-900 font-semibold">
        {{ props.properties.label }}
        <span
          v-if="props.properties.isRequired && props.properties.showRequiredLabel"
          :class="{ 'required-label': props.properties.isRequired }"
        >*</span>
      </label>
    </div>
    <Password
      :id="fieldId"
      v-model="fieldModelValue"
      :placeholder="props.properties.placeholder"
      :feedback="props.properties.showFeedBack"
      :class="{ 'p-invalid': listError.length !== 0 }"
      :toggle-mask="true"
      autocomplete="off"
      class="text-field-size h-3rem"
      @input="handleInput"
    />
    <small :id="`${fieldId}-help`" class="p-error text-start">
      <transition-group name="error-fade">
        <span
          v-for="textError of listError" :key="textError.key"
          class="text-error block flex pl-2 mb-0"
        >{{ textError.message }}</span>
      </transition-group>
    </small>
  </div>
</template>

<style scoped>
.text-field-size {
    width: 100%;
}

.error-fade-enter-active, .error-fade-leave-active {
    transition: opacity 0.5s;
}

.error-fade-enter, .error-fade-leave-to /* .error-fade-leave-active in <2.1.8 */
{
    opacity: 0;
}

.required-label {
    color: red;
}
</style>

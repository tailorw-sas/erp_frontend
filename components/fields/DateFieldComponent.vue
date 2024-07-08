<script setup lang="ts">
import { type PropType, onMounted, ref, watch, watchEffect } from 'vue'
import dayjs from 'dayjs'
import type { IOptionField } from './interfaces/IFieldInterfaces'
import { convertDates, convertOneDate, filterDatesByMonth } from '~/utils/valid_form_fields'

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
  localItems: {
    type: Array as () => any[],
    default: () => [],
    required: false
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
const listDates = ref<any[]>([])
const listError = ref<ObjErrorMessage[]>([])

const minDate = ref<Date>(dayjs('1850-01-01').toDate())
const maxDate = ref<Date>(dayjs('2500-01-01').toDate())

const methods: ValidationMethods = {
  // minLength,
}

function handleInput(event?: Event) {
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

  emits('update:modelValue', fieldModelValue.value ? dayjs(fieldModelValue.value).format('YYYY-MM-DD') : '')
  emits('update:errorMessages', [])
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

onMounted(() => {
  minDate.value = convertOneDate(props.properties.minDate) || minDate.value
  maxDate.value = convertOneDate(props.properties.maxDate) || maxDate.value

  // Darle el formato adecuado a la lista de FECHAS PERMITIDAS | FECHAS NO PERMITIDAS
  if (props.properties.allowedDates && props.properties.allowedDates?.length > 0) {
    listDates.value = filterDatesByMonth(props.properties.allowedDates)
  }
  if (props.properties.disabledDates && props.properties.disabledDates?.length > 0) {
    listDates.value = convertDates(props.properties.disabledDates)
  }
})
</script>

<template>
  <div>
    <!-- :minDate="props.properties.minDate"
      :maxDate="props.properties.maxDate"  -->
    <i v-if="false" class="pi pi-envelope" />
    <div class="mb-1">
      <label :for="fieldId" class="text-900 font-semibold">
        {{ props.properties.label }}
        <span v-if="props.properties.isRequired && props.properties.showRequiredLabel" :class="{ 'required-label': props.properties.isRequired }">*</span>
      </label>
    </div>
    <div>
      <Calendar
        :id="fieldId"
        v-model="fieldModelValue"
        :disabled-dates="listDates"
        :min-date="minDate"
        :max-date="maxDate"
        :class="{ 'p-invalid': listError.length !== 0 }"
        :manual-input="false"
        date-format="yy-mm-dd"
        class="text-field-size h-3rem"
        locale="es"
        @update:model-value="handleInput($event)"
      />
    </div>
    <div v-if="props.properties.showCounter && (props.properties.type === 'text' || props.properties.type === 'number')" class="flex justify-content-end py-0 my-0" style="font-size: 10px;">
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

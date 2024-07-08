<script setup lang="ts">
import { type PropType, onMounted, onUpdated, ref, watch, watchEffect } from 'vue'
import type { IOptionField, IStandardObject } from './interfaces/IFieldInterfaces'
import { validEmail } from '~/utils/valid_form_fields'
import { GenericService } from '@/services/generic-services'

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
    type: Array as () => IStandardObject[],
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
const listItems = ref<any[]>([])
const listError = ref<ObjErrorMessage[]>([])
const loading = ref(false)

const methods: ValidationMethods = {
  // minLength,
  isEmail: validEmail,
}

async function getList() {
  try {
    if (props.localItems.length === 0) {
      loading.value = true
      const payload
        = {
          filter: props.properties.filter,
          query: '',
          pageSize: 200,
          page: 0
        }

      listItems.value = []
      const response = await GenericService.search(props.properties.moduleApi, props.properties.uriApi, payload)
      const { data: dataList } = response
      for (const iterator of dataList) {
        listItems.value = [...listItems.value, { id: iterator.id, name: iterator.name }]
      }
    }
  }
  catch (error) {
    console.error('Error fetching Patient:', error)
  }
  finally {
    loading.value = false
  }
}

function handleInput() {
  listError.value = []

  if (props.properties.isRequired && (fieldModelValue.value === null || fieldModelValue.value === undefined || fieldModelValue.value === '')) {
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
  if (props.properties.returnObject) {
    emits('update:modelValue', typeof fieldModelValue.value === 'object' && fieldModelValue.value !== null ? fieldModelValue.value : fieldModelValue.value)
    emits('update:errorMessages', [])
  }
  else {
    emits('update:modelValue', typeof fieldModelValue.value === 'object' && fieldModelValue.value !== null ? fieldModelValue.value.id : fieldModelValue.value)
    emits('update:errorMessages', [])
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

watch(() => props.submit, (newValue) => {
  if (newValue) {
    handleInput()
  }
})

onMounted(() => {
  const valueTemp = fieldModelValue.value
  if (props.localItems.length > 0 && valueTemp) {
    fieldModelValue.value = props.localItems.find(item => item.id === valueTemp)
  }
})

getList()
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
      <Dropdown
        :id="fieldId"
        v-model="fieldModelValue"
        :options="listItems.length !== 0 ? listItems : localItems"
        option-label="name"
        :required="props.properties.isRequired"
        :placeholder="props.properties.placeholder"
        :class="{ 'p-invalid': listError.length !== 0 }"
        autocomplete="off"
        class="text-field-size h-3rem"
        filter
        :disabled="props.properties.disabled"
        size="large"
        @change="handleInput"
      >
        <template #value="slotProps">
          <div class="h-full flex align-item-center">
            <div v-if="slotProps.value" class="flex align-items-center">
              <div>{{ slotProps.value.name }}</div>
            </div>
            <div v-else class="flex align-items-center">
              {{ slotProps.placeholder }}
            </div>
          </div>
        </template>
        <template #option="slotProps">
          <div class="flex align-items-center">
            <div>{{ slotProps.option.name }}</div>
          </div>
        </template>
      </Dropdown>
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

<script setup lang="ts">
import { type PropType, onMounted, onUpdated, ref, watch, watchEffect } from 'vue'
import type { IFilter, IOptionField, IStandardObject } from './interfaces/IFieldInterfaces'
import { GenericService } from '@/services/generic-services'
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
const visible = ref(false)
const listboxValue = ref(null)
const searchListBox = ref('')

const methods: ValidationMethods = {
  // minLength,
  isEmail: validEmail,
}

async function getList(value: string = '') {
  try {
    if (props.localItems.length === 0) {
      loading.value = true
      let filterTemp: IFilter[] = []
      filterTemp = props.properties.filter
      for (const iterator of filterTemp) {
        iterator.value = value
      }
      const payload
        = {
          filter: filterTemp,
          query: '',
          pageSize: 200,
          page: 0
        }

      listItems.value = []
      const response = await GenericService.search(props.properties.moduleApi, props.properties.uriApi, payload)

      for (const iterator of response.data) {
        if (props.properties.uriApi === 'cie10' || props.properties.uriApi === 'procedure') {
          iterator.name = iterator.code ? `${iterator.code} - ${iterator.name}` : iterator.name
          listItems.value = [...listItems.value, iterator]
        }
        else {
          listItems.value = [...listItems.value, iterator]
        }
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
function isEmptyObj(obj) {
  return Object.keys(obj).length === 0
}

function handleInput() {
  listError.value = []

  if (props.properties.isRequired && fieldModelValue.value === null) {
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

function onSaveSearch() {
  if (listboxValue.value) {
    fieldModelValue.value = listboxValue.value
    searchListBox.value = ''
    visible.value = false
    handleInput()
  }
}

function onCancelSearch() {
  listboxValue.value = null
  listItems.value = []
  searchListBox.value = ''
  visible.value = false
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
  if (props.localItems.length > 0) {
    fieldModelValue.value = props.localItems.find(item => item.id === valueTemp)
  }
})

// getList()
</script>

<template>
  <div>
    <div class="mb-1">
      <label :for="fieldId" class="text-900 font-semibold">
        {{ props.properties.label }}
        <span v-if="props.properties.isRequired && props.properties.showRequiredLabel" :class="{ 'required-label': props.properties.isRequired }">*</span>
      </label>
    </div>
    <div class="flex">
      <Dropdown
        :id="fieldId"
        v-model="fieldModelValue"
        :options="listItems.length !== 0 ? listItems : localItems"
        option-label="name"
        :required="props.properties.isRequired"
        :placeholder="props.properties.placeholder"
        :class="{ 'p-invalid': listError.length !== 0 }"
        autocomplete="off"
        show-clear
        class="text-field-size h-3rem"
        filter
        :disabled="props.properties.disabled"
        size="large"
        empty-message="No hay elementos disponibles"
        empty-filter-message="No se encontraron resultados"
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
      <Button class="mx-1" icon="pi pi-search" label="" style="width: 64px;" @click="visible = !visible" />
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

    <div class="12">
      <Dialog
        v-model:visible="visible"
        modal
        header="Buscar"
        class="mx-3 sm:mx-0 sm:w-full md:w-4"
        content-class="border-round-bottom border-top-1 surface-border"
        @hide="visible = false"
      >
        <div class="grid p-2">
          <div class="col-12 field mb-1">
            <div class="mt-4 flex">
              <InputText v-model="searchListBox" placeholder="Escriba el texto a buscar" class="w-full" />
              <Button class="mx-1 w-2" label="Buscar" :loading="loading" @click="getList(searchListBox)" />
            </div>
          </div>

          <div class="col-12 field mb-0">
            <Listbox
              v-model="listboxValue"
              :options="listItems"
              option-label="name"
              empty-message="No hay ningún elemento en la lista."
              list-style="max-height:250px;height:250px"
            />
          </div>
        </div>
        <template #footer>
          <Button label="Cancelar" icon="pi pi-times" severity="secondary" text @click="onCancelSearch" />
          <Button label="Aceptar" icon="pi pi-check" text @click="onSaveSearch" />
        </template>
      </Dialog>
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

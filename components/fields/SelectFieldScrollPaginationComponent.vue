<script setup lang="ts">
import { type PropType, type Ref, onMounted, onUpdated, ref, watch, watchEffect } from 'vue'
import type { VirtualScrollerLazyEvent } from 'primevue/virtualscroller'
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
    type: Object as PropType<{
      value: string
      label: string
      type: string
      uriApi: string
      isRequired: boolean
      showRequiredLabel: boolean
      placeholder: string
      errorMessage: string[]
      haveError: boolean
    }>,
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
  }
})

const emits = defineEmits([
  'update:modelValue',
  'update:errorMessages'
])
const fieldModelValue = ref<any>(props.properties.value || null)
const fieldId = `text-field-${Math.random().toString(36).substring(7)}`
const listItems = ref<any[]>([])
const listItemsTemp = ref<any[]>([])
const listError = ref<ObjErrorMessage[]>([])
const loading = ref(false)
const page = ref(0)
const pageSize = ref(0)
const totalElement = ref(0)
const stepTemp = ref(0)

const methods: ValidationMethods = {
  // minLength,
  isEmail: validEmail,
}

async function getList() {
  try {
    loading.value = true
    // pagination.value.page = page
    const payload
        = {
          filter: [
            // {
            //     key: "parent.id",
            //     operator: "EQUALS",
            //     value: "6ae4cffd-bd82-4f89-9289-adb569e8c7bf",
            //     logicalOperation: "AND"
            // },
            // {
            //   key: "type",
            //   operator: "EQUALS",
            //   value: "PROVINCE",
            //   logicalOperation :"AND"
            // },
            // {
            //     key: "name",
            //     operator: "LIKE",
            //     value: "PI",
            //     logicalOperation: "AND"
            // }
          ],
          query: '',
          pageSize: 50,
          page: page.value
        }

    listItems.value = []
    const response = await GenericService.search(props.properties.uriApi, payload)
    totalElement.value = response.data.totalElements
    pageSize.value = response.data.totalElementsPage

    for (const iterator of response.data.data) {
      listItems.value = [...listItems.value, { id: iterator.id, name: iterator.name }]
    }
  }
  catch (error) {
    console.error('Error fetching Patient:', error)
  }
  finally {
    loading.value = false
  }
}

async function onLazyLoad(event: VirtualScrollerLazyEvent) {
  loading.value = true
  try {
    const { first, last } = event
    stepTemp.value = first
  }
  catch (error) {
    console.error('Error al cargar elementos:', error)
  }
  finally {
    loading.value = false
  }
}

function handleInput() {
  listError.value = []
  // listError.value = listError.value.filter(error => error.key !== 'required'); esta linea elimina solo el error que tiene que ver con la validacion especifica
  // Required
  if (props.properties.isRequired && !fieldModelValue.value.trim()) {
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

  emits('update:modelValue', fieldModelValue.value)
  emits('update:errorMessages', [])
}

function handleScroll(event) {
  const { scrollTop, scrollHeight, clientHeight } = event.target
  if (scrollTop + clientHeight >= scrollHeight) {
    // getList();

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

getList()
</script>

<template>
  <div>
    <!-- <VirtualScroller
    :items="listItems"
    :itemSize="30"
    class="border-1 surface-border border-round mb-5"
    style="width: 200px; height: 200px"
    lazy
    @lazy-load="onLazyLoad"
    :step="10"
    >
        <template v-slot:item="{ item, options }">
            <div :class="['flex align-items-center p-2', { 'surface-hover': options.odd }]" style="height: 30px">{{ item.name }}</div>
        </template>
    </VirtualScroller> -->

    <span class="p-float-label">
      <Dropdown
        :id="fieldId"
        v-model="fieldModelValue"
        :options="listItems"
        option-label="name"
        :required="props.properties.isRequired"
        :placeholder="props.properties.placeholder"
        :class="{ 'p-invalid': listError.length !== 0 }"
        autocomplete="off"
        class="w-full md:w-14rem"
        filter
        size="large"
        :virtual-scroller-options="{
          lazy: true,
          onLazyLoad,
          itemSize: 30,
          showLoader: true,
          loading,

          step: 5,
        }"
        @input="handleInput"
      >
        <template #value="slotProps">
          <div v-if="slotProps.value" class="flex align-items-center">
            <div>{{ slotProps.value.name }}</div>
          </div>
          <span v-else>
            {{ slotProps.placeholder }}
          </span>
        </template>
        <template #option="slotProps">
          <div class="flex align-items-center">
            <div>{{ slotProps.option.name }}</div>
          </div>
        </template>
        <template #loader="slotProps">
          <div class="flex align-items-center">
            <div>{{ slotProps.options }}</div>
          </div>
        </template>
      </Dropdown>
      <label for="fieldId">
        {{ props.properties.label }}
        <span v-if="props.properties.isRequired && props.properties.showRequiredLabel" :class="{ 'required-label': props.properties.isRequired }">*</span>
      </label>
    </span>
    <small :id="`${fieldId}-help`" class="p-error text-start">
      <transition-group name="error-fade">
        <span v-for="textError of listError" :key="textError.key" class="text-error block flex pl-2 mb-0">{{ textError.message }}</span>
      </transition-group>
    </small>
  </div>

  <!-- <div>
    <ul v-for="item of listItems" :key="item">
      <li>{{ item.name }}</li>
    </ul>
  </div> -->
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

<script setup lang="ts">
import { type PropType, onMounted, onUpdated, ref, watch, watchEffect } from 'vue'
import { usePrimeVue } from 'primevue/config'
import type { IFileMetadata, IOptionField } from './interfaces/IFieldInterfaces'
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
  }
})

const emits = defineEmits([
  'update:modelValue',
  'update:errorMessages',
  'invalidField'
])

const $primevue = usePrimeVue()

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

  if (props.properties.isRequired && (fieldModelValue.value === '' || fieldModelValue.value === null || fieldModelValue.value === undefined)) {
    listError.value = [...listError.value, { key: 'required', message: 'This is a required field.' }]
  }

  for (const keyword of props.validationKeywords) {
    if (keyword in methods) {
      const error = methods[keyword](fieldModelValue.value)
      if (error) {
        listError.value = [...listError.value, { key: keyword, message: error }]
      }
    }
  }

  if (listError.value.length === 0) {
    const temp = fieldModelValue.value !== '' ? fieldModelValue.value.split('base64,')[1] : ''
    emits('update:modelValue', temp)
    emits('update:errorMessages', [])
  }
}

function setMessageBackendError() {
  listError.value = listError.value.filter(error => error.key !== 'backend')
  for (const iterator of props.properties.errorMessage) {
    listError.value = [...listError.value, { key: 'backend', message: iterator }]
  }
}

function openFileUpload() {
  document.getElementById('btn-choose')?.click()
}
function clearFile() {
  objFile.value = null
  fieldModelValue.value = null
}

async function customBase64Uploader(event: any) {
  const file = event.files[0]
  objFile.value = event.files[0]
  const reader = new FileReader()
  const blob = await fetch(file.objectURL).then(r => r.blob()) // blob:url

  reader.readAsDataURL(blob)

  reader.onloadend = function () {
    fieldModelValue.value = reader.result
    if (fieldModelValue.value) {
      const temp = fieldModelValue.value.split('base64,')[1]
      emits('update:modelValue', temp)
    }
  }
}

const objFile = ref<IFileMetadata>(fieldModelValue.value)

const refInputEl = ref<HTMLElement>()
const files = ref([])
const totalSize = ref(0)
const totalSizePercent = ref(0)

function formatSize(bytes) {
  const k = 1024
  const dm = 3
  const sizes = $primevue.config.locale.fileSizeTypes

  if (bytes === 0) {
    return `0 ${sizes[0]}`
  }

  const i = Math.floor(Math.log(bytes) / Math.log(k))
  const formattedSize = Number.parseFloat((bytes / k ** i).toFixed(dm))

  return `${formattedSize} ${sizes[i]}`
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
watch(() => fieldModelValue.value, (newValue) => {
  handleInput()
  // if (newValue) {
  // }
})
</script>

<template>
  <div>
    <div class="flex align-items-center justify-content-center flex-column">
      <div>
        <div class="border-2 surface-border border-round-xl border-400 p-1" style="width: 180px; height: 180px;" @click="openFileUpload">
          <!-- {{ typeof objFile }} -->
          <div
            class="relative bg-imagen border-round flex align-items-center justify-content-center"
            style="width: 100%; height: 100%;"
            :style="{ backgroundImage: `url(${objFile !== null && typeof objFile === 'object' ? (objFile.objectURL) : objFile})`, backgroundSize: (objFile !== null && typeof objFile === 'object' && objFile.type === 'image/svg+xml') ? 'contain' : 'cover' }"
          >
            <i v-if="!objFile" class="pi pi-image p-5 text-8xl text-400 border-400" />
          </div>
        </div>
        <div class="flex justify-content-center align-items-center my-2">
          <span>
            <Button icon="pi pi-upload" class="ml-2" text @click="openFileUpload" />
            <Button icon="pi pi-times" class="ml-2" severity="danger" :disabled="!objFile" text @click="clearFile" />
          </span>
        </div>
        <div>
          <small :id="`${fieldId}-help`" class="p-error text-start">
            <transition-group name="error-fade">
              <span v-for="textError of listError" :key="textError.key" class="text-error block flex pl-2 mb-0">{{ textError.message }}</span>
            </transition-group>
          </small>
        </div>
      </div>
    </div>
    <FileUpload hidden auto custom-upload accept="image/*" :max-file-size="10000000" @uploader="customBase64Uploader($event)">
      <template #header="{ chooseCallback, clearCallback }">
        <div class="flex flex-wrap justify-content-between align-items-center flex-1 gap-2">
          <div class="flex gap-2">
            <Button id="btn-choose" class="p-2" icon="pi pi-images" rounded outlined @click="chooseCallback()" />
          </div>
        </div>
      </template>
    </FileUpload>
  </div>
</template>

<style scoped>
.bg-imagen {
  background-position: center;
  background-repeat: no-repeat;
  /* background-size: contain; */
}

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

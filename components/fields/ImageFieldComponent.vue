<script setup lang="ts">
import { type PropType, reactive, ref, watch, watchEffect } from 'vue'
import { usePrimeVue } from 'primevue/config'
import type { IFileMetadata, IOptionField } from './interfaces/IFieldInterfaces'
import { convertFromByteToKBOrMB, esURLValida, validEmail } from '~/utils/valid_form_fields'

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
  'update:uploadFile',
  'update:errorMessages',
  'invalidField'
])

const $primevue = usePrimeVue()

const fieldModelValue = ref<any>(props.properties.value || null)
const fieldId = `text-field-${Math.random().toString(36).substring(7)}`
const fileObj = reactive({
  name: '',
  size: '',
  type: '',
})
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
    if (typeof fieldModelValue.value === 'string' && fieldModelValue.value !== '' && esURLValida(fieldModelValue.value)) {
      emits('update:modelValue', fieldModelValue.value)
      emits('update:errorMessages', [])
    }
    else {
      const temp = (typeof fieldModelValue.value === 'string' && fieldModelValue.value !== '') ? fieldModelValue.value.split('base64,')[1] : ''
      emits('update:modelValue', { name: fileObj.name, type: fileObj.type, value: temp })
      emits('update:errorMessages', [])
    }
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
  fileObj.name = ''
  fileObj.size = ''
}

async function fileToBase64(file: File) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => {
      resolve(reader.result) // Elimina el prefijo 'data:image/png;base64,'
    }
    reader.onerror = error => reject(error)
  })
}

async function customBase64Uploader(event: any) {
  const file = event.files[0]
  objFile.value = event.files[0]
  fileObj.name = file.name
  fileObj.size = convertFromByteToKBOrMB(file.size)
  fileObj.type = file.type

  const base64String = await fileToBase64(file)
  fieldModelValue.value = base64String

  emits('update:modelValue', { name: fileObj.name, type: fileObj.type, value: base64String })
  listError.value = []
  emits('update:errorMessages', [])
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
</script>

<template>
  <div>
    <div class="flex align-items-center justify-content-center flex-column">
      <div>
        <div class="grid p-fluid formgrid">
          <div class="col-12 flex justify-content-center">
            <div class="border-2 surface-border border-round-xl border-400 p-1" style="width: 180px; height: 180px;" @click="openFileUpload">
              <div
                v-if="properties.type === 'image'"
                class="relative bg-imagen border-round flex align-items-center justify-content-center"
                style="width: 100%; height: 100%;"
                :style="{ backgroundImage: `url(${objFile !== null && typeof objFile === 'object' ? (objFile.objectURL) : objFile})`, backgroundSize: (objFile !== null && typeof objFile === 'object' && objFile.type === 'image/svg+xml') ? 'contain' : 'cover' }"
              >
                <i v-if="!objFile" class="pi pi-image p-5 text-8xl text-400 border-400" />
              </div>
              <div
                v-else
                class="relative border-round flex align-items-center justify-content-center"
                style="width: 100%; height: 100%;"
              >
                <i v-if="!objFile" class="pi pi-file p-5 text-8xl text-400 border-400" />
                <Image v-else src="/img/xml.svg" alt="Image" width="100" />
              </div>
            </div>
          </div>
          <div class="col-12 flex justify-content-center">
            <div v-if="fileObj.name && fileObj.size" class="flex justify-content-center align-items-center my-2">
              <span class="w-17rem flex justify-content-center align-items-center">
                <Chip class="py-1 px-2 chip-container">
                  <span class="font-medium text-center">{{ fileObj.name }} ({{ fileObj.size }})</span>
                </Chip>
              </span>
            </div>
          </div>
          <div class="col-12 flex justify-content-center mt-4">
            <div class="grid">
              <div v-if="listError.length > 0" class="col-12 flex justify-content-center">
                <small :id="`${fieldId}-help`" class="p-error text-start">
                  <transition-group name="error-fade">
                    <span v-for="textError of listError" :key="textError.key" class="text-error block flex pl-2 mb-0">{{ textError.message }}</span>
                  </transition-group>
                </small>
              </div>
              <div class="col-12">
                <div class="flex justify-content-center align-items-center my-2">
                  <span>
                    <Button icon="pi pi-upload" class="ml-2" text @click="openFileUpload" />
                    <Button icon="pi pi-times" class="ml-2" severity="danger" :disabled="!objFile" text @click="clearFile" />
                  </span>
                </div>
              </div>
            </div>
            <div />
          </div>
        </div>
      </div>
    </div>
    <!-- accept="image/*" -->
    <FileUpload hidden auto custom-upload :accept="(properties.hasOwnProperty('fileAccept') && properties.type === 'file') ? properties.fileAccept : 'image/*' " :max-file-size="10000000" @uploader="customBase64Uploader($event)">
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
.chip-container {
    display: inline-block;
    max-width: 100%;
    word-break: break-all;
    white-space: normal;
    overflow-wrap: break-word;
}

.chip-container span {
    display: inline-block;
    word-break: break-all;
    white-space: normal;
    overflow-wrap: break-word;
}
</style>

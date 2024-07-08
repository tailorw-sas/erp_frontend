// EditForm.vue

<script setup lang="ts">
import { usePrimeVue } from 'primevue/config'
import type { IFileMetadata } from '../fields/interfaces/IFieldInterfaces'
import type { Container, FieldDefinitionType } from './EditFormV2WithContainer'

const props = defineProps<{
  fieldsWithContainers: Array<Container>
  item: any
  loadingSave?: boolean
  loadingDelete?: boolean
  showActions?: boolean
  forceSave?: boolean
  formClass?: string
  containerClass?: string
  backendValidation?: { [key: string]: string[] }
  hideDeleteButton?: boolean
}>()

const emit = defineEmits(['update:field', 'clearField', 'submit', 'cancel', 'delete', 'forceSave'])

const fields = ref<FieldDefinitionType[]>([])

props.fieldsWithContainers.forEach((container: Container) => {
  fields.value.push(...container.childs)
})

const $primevue = usePrimeVue()
const fieldValues = reactive<{ [key: string]: any }>({})
const errors = reactive<{ [key: string]: string[] }>({})
const objFile = ref<IFileMetadata | null>(null)

fields.value.forEach((field: { field: string | number }) => {
  if (typeof field.field === 'string') {
    fieldValues[field.field] = props.item[field.field]
  }
})

function updateField(fieldKey: string, value: any) {
  const field = fields.value.find((f: { field: string }) => f.field === fieldKey)
  if (field?.dataType === 'code' || field?.toUppercase) {
    fieldValues[fieldKey] = value.toUpperCase()
  }
  else {
    fieldValues[fieldKey] = value
  }
  emit('update:field', { fieldKey, value: fieldValues[fieldKey] })
  validateField(fieldKey, value)
}

function clearField(fieldKey: string) {
  fieldValues[fieldKey] = null
  emit('clearField', fieldKey)
}

function cancelForm() {
  emit('cancel')
}

function deleteItem($event: Event) {
  $event.preventDefault()
  emit('delete', $event)
}

function submitForm($event: Event = new Event('')) {
  $event.preventDefault()
  if (validateForm()) {
    emit('submit', { ...fieldValues, event: $event })
  }
}

async function customBase64Uploader(event: any, listFields: any, fieldKey: any) {
  const file = event.files[0]
  objFile.value = file
  listFields[fieldKey] = file
}

function openFileUpload() {
  document.getElementById('btn-choose')?.click()
}
function clearFile(listField: any, fieldKey: any) {
  listField[fieldKey] = null
  objFile.value = null
}

function validateField(fieldKey: string, value: any) {
  const field = fields.value.find((f: { field: string }) => f.field === fieldKey)
  if (field && field.validation) {
    const result = field.validation.safeParse(value)
    if (!result.success) {
      errors[fieldKey] = result.error.issues.map((e: { message: any }) => e.message)
    }
    else {
      delete errors[fieldKey]
    }
  }
}

function validateForm() {
  let isValid = true
  fields.value.forEach((field: any) => {
    if (field.validation) {
      const result = field.validation.safeParse(fieldValues[field.field])
      if (!result.success) {
        isValid = false
        const messages = result.error.issues.map((e: { message: any }) => e.message)
        errors[field.field] = messages
      }
      else {
        delete errors[field.field]
      }
    }
  })
  return isValid
}

function formatSize(bytes: number) {
  const k = 1024
  const dm = 3
  const sizes = $primevue.config.locale?.fileSizeTypes || ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']

  if (bytes === 0) {
    return `0 ${sizes[0]}`
  }

  const i = Math.floor(Math.log(bytes) / Math.log(k))
  const formattedSize = Number.parseFloat((bytes / k ** i).toFixed(dm))

  return `${formattedSize} ${sizes[i]}`
}

watch(() => props.forceSave, () => {
  if (props.forceSave) {
    submitForm()
    emit('forceSave')
  }
})
</script>

<template>
  <div :class="formClass || 'p-fluid formgrid grid'">
    <div :class="props.containerClass ? `${props.containerClass}` : 'col-12 p-0'" style="max-height: 65vh; overflow-y: auto;">
      <div
        v-for="(container, index) in fieldsWithContainers" :class="container.containerClass ? `${container.containerClass} mb-2` : 'mb-2'"
      >
        <div
          v-for="(field, index) in container.childs" v-show="!field.hidden" :key="field.field || index" :style="field.style"
          :class="field.class ? `${field.class} mb-2` : 'mb-2'"
        >
          <!-- Header slot -->
          <div v-if="field.header && field.dataType !== 'check'" :style="field.headerStyle" :class="field.headerClass">
            <slot :name="`header-${field.field}`" :field="field">
              <strong>
                {{ typeof field.header === 'function' ? field.header() : field.header }}
              </strong>
              <span v-if="field.class.includes('required')" class="p-error">*</span>
            </slot>
          </div>

          <!-- Field slot -->
          <div :class="field.containerFieldClass">
            <slot :name="`field-${field.field}`" :item="fieldValues" :field="field.field" :fields="fields" :on-update="updateField" :errors="errors">
              <div v-if="field.dataType === 'image'" class="flex flex-wrap justify-content-center">
                <FileUpload hidden auto custom-upload accept="image/*" :max-file-size="1000000" @uploader="customBase64Uploader($event, fieldValues, field.field)">
                  <template #header="{ chooseCallback }">
                    <div class="flex flex-wrap justify-content-between align-items-center flex-1 gap-2">
                      <div class="flex gap-2">
                        <Button id="btn-choose" class="p-2" icon="pi pi-images" rounded outlined @click="chooseCallback()" />
                      </div>
                    </div>
                  </template>
                </FileUpload>
                <div v-if="!loadingSave" class="border-2 surface-border border-round-xl border-400 p-1 flex align-items-center justify-content-center" style="width: 180px; height: 180px;">
                  <i v-if="!fieldValues[field.field]" class="pi pi-image p-5 text-8xl text-400 border-400" />
                  <div
                    v-else
                    class="relative bg-imagen border-round flex align-items-center justify-content-center"
                    style="width: 100%; height: 100%;"
                    :style="{ backgroundImage: `url(${typeof fieldValues[field.field] === 'string' ? fieldValues[field.field] : (fieldValues[field.field].hasOwnProperty('objectURL') ? fieldValues[field.field].objectURL : '')})`, backgroundSize: (objFile !== null && typeof objFile === 'object' && objFile.type === 'image/svg+xml') ? 'contain' : 'cover' }"
                  />
                </div>
                <Skeleton v-else height="2rem" />
                <div class="col-12">
                  <div class="flex justify-content-center align-items-center my-2">
                    <span>
                      <Button icon="pi pi-upload" class="ml-2" text @click="openFileUpload" />
                      <Button icon="pi pi-times" class="ml-2" severity="danger" :disabled="!objFile" text @click="clearFile(fieldValues, field.field)" />
                    </span>
                  </div>
                </div>
              </div>

              <span v-if="field.dataType === 'text' || field.dataType === 'code'">
                <InputText
                  v-if="!loadingSave" v-model="fieldValues[field.field]"
                  show-clear :disabled="field?.disabled"
                  @update:model-value="updateField(field.field, $event)"
                />
                <Skeleton v-else height="2rem" />
              </span>

              <span v-else-if="field.dataType === 'textarea'">
                <Textarea
                  v-if="!loadingSave" v-model="fieldValues[field.field]" rows="5"
                  :disabled="field?.disabled"
                  show-clear
                  @update:model-value="updateField(field.field, $event)"
                />
                <Skeleton v-else height="6rem" />
              </span>

              <span v-else-if="field.dataType === 'date'">
                <Calendar
                  v-if="!loadingSave"
                  v-model="fieldValues[field.field]"
                  date-format="yy-mm-dd" :disabled="field?.disabled"
                  @update:model-value="(value: any) => updateField(field.field, value)"
                />
                <Skeleton v-else height="2rem" />
              </span>

              <span v-else-if="field.dataType === 'number'">
                <InputNumber
                  v-if="!loadingSave"
                  v-model="fieldValues[field.field]"
                  :disabled="field?.disabled"
                  @update:model-value="updateField(field.field, $event)"
                />
                <Skeleton v-else height="2rem" />
              </span>

              <span v-else-if="field.dataType === 'password'">
                <Password
                  v-if="!loadingSave"
                  v-model="fieldValues[field.field]" :disabled="field?.disabled"
                  toggle-mask
                  @update:model-value="updateField(field.field, $event)"
                />
                <Skeleton v-else height="2rem" />
              </span>

              <span v-else-if="field.dataType === 'file'">
                <FileUpload
                  v-if="!loadingSave" accept=".jrxml" :max-file-size="1000000" :multiple="false" auto custom-upload
                  @uploader="customBase64Uploader($event, fieldValues, field.field)"
                >
                  <!-- <template #header="{ chooseCallback }">
                  <div class="flex flex-wrap justify-content-between align-items-center flex-1 gap-2">
                    <div class="flex gap-2">
                      <Button id="btn-choose" class="p-2" icon="pi pi-images" rounded outlined @click="chooseCallback()" />
                    </div>
                  </div>
                </template> -->
                  <template v-if="!fieldValues[field.field]" #empty>
                    <div class="flex align-items-center justify-content-center">
                      <p>Arrastre y suelte el archivo <strong>.jrxml</strong> aquí.</p>
                    </div>
                  </template>

                  <template #content="{ files }">
                    <ul v-if="files[0] || fieldValues[field.field]" class="list-none p-0 m-0">
                      <li class="p-3 surface-border flex align-items-start sm:align-items-center">
                        <img src="../../assets/images/xml.png" class="avatar w-4rem h-4rem mr-3" alt="Avatar">
                        <div class="flex flex-column">
                          <span class="text-900 font-semibold text-xl mb-2">{{ fieldValues[field.field].name }}</span>
                          <span class="text-900 font-medium">
                            <Badge severity="warning">
                              {{ formatSize(fieldValues[field.field].size) }}
                            </Badge>
                          </span>
                        </div>
                      </li>
                    </ul>
                  </template>

                </FileUpload>
                <Skeleton v-else height="7rem" />
              </span>

              <span v-else-if="field.dataType === 'fileupload'">
                <FileUpload
                  v-if="!loadingSave" :max-file-size="1000000" :multiple="false" auto custom-upload
                  @uploader="customBase64Uploader($event, fieldValues, field.field)"
                >
                  <template #header="{ chooseCallback }">
                    <div class="flex flex-wrap justify-content-between align-items-center flex-1 gap-2">
                      <div class="flex gap-2">
                        <Button id="btn-choose" class="p-2" icon="pi pi-plus" text @click="chooseCallback()" />
                        <Button icon="pi pi-times" class="ml-2" severity="danger" :disabled="!objFile" text @click="clearFile(fieldValues, field.field)" />
                      </div>
                    </div>
                  </template>
                  <template #content="{ files }">
                    <ul v-if="files[0] || fieldValues[field.field]" class="list-none p-0 m-0">
                      <li class="p-3 surface-border flex align-items-start sm:align-items-center">

                        <div class="flex flex-column">
                          <span class="text-900 font-semibold text-xl mb-2">{{ fieldValues[field.field].name }}</span>
                          <span class="text-900 font-medium">
                            <Badge severity="warning">
                              {{ formatSize(fieldValues[field.field].size) }}
                            </Badge>
                          </span>
                        </div>
                      </li>
                    </ul>
                  </template>
                </FileUpload>

                <Skeleton v-else height="7rem" />

              </span>

              <span v-else-if="field.dataType === 'check'">
                <Checkbox
                  v-model="fieldValues[field.field]"
                  :binary="true"
                  :disabled="field?.disabled"
                  @update:model-value="updateField(field.field, $event)"
                />
                <label :for="field.field" class="ml-2"> {{ typeof field.header === 'function' ? field.header() : field.header }} </label>
              </span>
            </slot>

            <!-- Validation errors -->
            <div v-if="true" class="flex">
              <div class="w-full">
                <div v-if="errors[field.field]" :class="field.errorClass ? field.errorClass : 'p-error text-xs'" class="w-full">
                  <div v-for="error in errors[field.field]" :key="error" class="error-message">
                    {{ error }}
                  </div>
                </div>
              </div>
            <!-- <div v-if="(field.dataType === 'text' || field.dataType === 'textarea' || field.dataType === 'password') && getMaxValidation(field.validation)" class="flex justify-content-end py-0 my-0" style="font-size: 10px;">
              {{ fieldValues[field.field].length }}/{{ getMaxValidation(field.validation) }}
            </div> -->
            </div>

            <!-- Footer slot -->
            <div v-if="field.footer" :style="field.footerStyle" :class="field.footerClass ? field.footerClass : ''">
              <slot :name="`footer-${field.field}`" :field="field">
                <small>{{ field.footer }}</small>
              </slot>
            </div>
          </div>

          <!-- Footer slot -->

          <!-- Clear button -->
          <button v-if="field.showClearButton" @click="clearField(field.field)">
            Clear
          </button>
        </div>
      </div>
    </div>
    <!-- General Footer slot of the form -->
    <div v-if="showActions" class="col-12 form-footer  flex justify-content-between md:justify-content-end">
      <slot name="form-footer" :item="{ fieldValues, submitForm }">
        <Button v-tooltip.top="'Save'" class="w-3rem mx-2" icon="pi pi-save" :loading="loadingSave" @click="submitForm($event)" />
        <Button v-if="!hideDeleteButton" v-tooltip.top="'Delete'" outlined class="w-3rem" severity="danger" :disabled="item?.id === null || item?.id === undefined" :loading="loadingDelete" icon="pi pi-trash" @click="deleteItem($event)" />
        <Button v-if="hideDeleteButton" v-tooltip.top="'Cancel'" class="w-3rem mx-2" icon="pi pi-times" severity="secondary" :loading="loadingSave" @click="cancelForm" />
      </slot>
    </div>
  </div>
</template>

<style lang="scss">
::-webkit-scrollbar {
    width: 8px; /* Ancho del scrollbar vertical */
    height: 12px; /* Altura del scrollbar horizontal */
}

/* Estilo para la track del scrollbar (la base de la barra de desplazamiento) */
::-webkit-scrollbar-track {
    background: #f1f1f1;
}

/* Estilo para el handle del scrollbar (la parte que se puede mover) */
::-webkit-scrollbar-thumb {
    background: #888;
    border-radius: 6px; /* Bordes redondeados */
}

/* Estilo para el handle del scrollbar al pasar el mouse sobre él */
::-webkit-scrollbar-thumb:hover {
    background: var(--primary-color);
}
</style>

// EditForm.vue

<script setup lang="ts">
import { ZodString, type ZodStringCheck, type ZodTypeAny } from 'zod'
import { reactive, ref } from 'vue'
import type { IFileMetadata } from '../fields/interfaces/IFieldInterfaces'
import type { FieldDefinitionType } from './EditFormV2'

const props = defineProps<{
  fields: Array<FieldDefinitionType>
  item: any
  loadingSave?: boolean
  loadingDelete?: boolean
  fieldWithImage: string[]
  classFirstGrid: string
  classSecondGrid: string
  showActions?: boolean
  forceSave?: boolean
  formClass?: string
}>()

const emit = defineEmits(['update:field', 'clearField', 'submit', 'forceSave', 'cancel', 'delete'])

const listKeyFormWithImage = ref([...props.fieldWithImage])
const fieldValues = reactive<{ [key: string]: any }>({})
const errors = reactive<{ [key: string]: string[] }>({})
const objFile = ref<IFileMetadata | null>(null)

const listObjGroupedImageFields = ref<any[]>([])
const listObjGroupedFields = ref<any[]>([])

props.fields.forEach((field) => {
  if (listKeyFormWithImage.value.includes(field.field)) {
    listObjGroupedImageFields.value.push(field)
  }
  else {
    listObjGroupedFields.value.push(field)
  }

  if (typeof field.field === 'string') {
    fieldValues[field.field] = props.item[field.field]
  }
})

function updateField(fieldKey: string, value: any) {
  fieldValues[fieldKey] = value
  emit('update:field', { fieldKey, value })
  validateField(fieldKey, value)
}

function clearField(fieldKey: string) {
  fieldValues[fieldKey] = null
  emit('clearField', fieldKey)
}

function submitForm($event: Event = new Event('')) {
  // $event.preventDefault()
  if (validateForm()) {
    emit('submit', { ...fieldValues, event: $event })
  }
}

// function cancelForm() {
//   emit('cancel', { ...fieldValues })
// }
function deleteItem($event: Event) {
  $event.preventDefault()
  emit('delete', $event)
}

function hasMinValidation(validation?: ZodTypeAny): boolean {
  if (!validation || !(validation instanceof ZodString)) { return false }
  const checks = validation._def.checks || []
  return checks.some((check: any) => check.kind === 'min')
}

function getMaxValidation(validation?: ZodTypeAny): number | false {
  if (!validation || !(validation instanceof ZodString)) { return false }

  const checks: ZodStringCheck[] = validation._def.checks || []
  const maxCheck: ZodStringCheck | any = checks.find((check: any) => check.kind === 'max')

  if (maxCheck) {
    return maxCheck.value
  }
  else {
    return false
  }
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

async function customBase64Uploader(event: any, listFields: any, fieldKey: any) {
  const file = event.files[0]
  objFile.value = file
  // fileObj.name = file.name
  // fileObj.size = convertFromByteToKBOrMB(file.size)
  // fileObj.type = file.type

  // const base64String = await fileToBase64(file)
  listFields[fieldKey] = file
}

function openFileUpload() {
  document.getElementById('btn-choose')?.click()
}
function clearFile(listField: any, fieldKey: any) {
  listField[fieldKey] = null
  objFile.value = null
  // fileObj.name = ''
  // fileObj.size = ''
}

function hasCustomValidation(validation?: ZodTypeAny): boolean {
  if (!validation || !(validation instanceof ZodString)) { return false }
  const refinements = validation._def.checks || []
  return refinements.some((refinement: any) => refinement.kind === 'refine' && refinement.message === 'This field is required')
}

function validateField(fieldKey: string, value: any) {
  const field = props.fields.find(f => f.field === fieldKey)
  if (field && field.validation) {
    const result = field.validation.safeParse(value)
    if (!result.success) {
      errors[fieldKey] = result.error.issues.map(e => e.message)
    }
    else {
      delete errors[fieldKey]
    }
  }
}

function validateForm() {
  let isValid = true
  props.fields.forEach((field) => {
    if (field.validation) {
      const result = field.validation.safeParse(fieldValues[field.field])
      if (!result.success) {
        isValid = false
        const messages = result.error.issues.map(e => e.message)
        errors[field.field] = messages
      }
      else {
        delete errors[field.field]
      }
    }
  })
  return isValid
}

watch(() => props.forceSave, () => {
  if (props.forceSave) {
    submitForm()
    emit('forceSave')
  }
})
</script>

<template>
  <div class="p-fluid formgrid grid">
    <div v-if="listObjGroupedImageFields.length > 0" :class="classFirstGrid ? classFirstGrid : 'col-12 md:col-3'">
      <div v-if="listObjGroupedImageFields[0].header && listObjGroupedImageFields[0].dataType !== 'check'" :style="listObjGroupedImageFields[0].headerStyle" :class="listObjGroupedImageFields[0].headerClass">
        <slot :name="`header-${listObjGroupedImageFields[0].field}`" :field="listObjGroupedImageFields[0]">
          {{ typeof listObjGroupedImageFields[0].header === 'function' ? listObjGroupedImageFields[0].header() : listObjGroupedImageFields[0].header }}
          <span :class="listObjGroupedFields[0].class.includes('required') ? 'p-error font-semibold' : ''">*</span>
        </slot>
      </div>

      <div :class="listObjGroupedImageFields[0].containerFieldClass">
        <slot :name="`field-${listObjGroupedImageFields[0].field}`" :item="fieldValues" :field="listObjGroupedImageFields[0].field" :fields="fields" :on-update="updateField">
          <div v-if="listObjGroupedImageFields[0].dataType === 'image'" class="flex flex-wrap justify-content-center">
            <FileUpload hidden auto custom-upload accept="image/*" :max-file-size="1000000" @uploader="customBase64Uploader($event, fieldValues, listObjGroupedImageFields[0].field)">
              <template #header="{ chooseCallback }">
                <div class="flex flex-wrap justify-content-between align-items-center flex-1 gap-2">
                  <div class="flex gap-2">
                    <Button id="btn-choose" class="p-2" icon="pi pi-images" rounded outlined @click="chooseCallback()" />
                  </div>
                </div>
              </template>
            </FileUpload>
            <div v-if="!loadingSave" class="border-2 surface-border border-round-xl border-400 p-1 flex align-items-center justify-content-center" style="width: 180px; height: 180px;">
              <i v-if="!fieldValues[listObjGroupedImageFields[0].field]" class="pi pi-image p-5 text-8xl text-400 border-400" />
              <div
                v-else
                class="relative bg-imagen border-round flex align-items-center justify-content-center"
                style="width: 100%; height: 100%;"
                :style="{ backgroundImage: `url(${typeof fieldValues[listObjGroupedImageFields[0].field] === 'string' ? fieldValues[listObjGroupedImageFields[0].field] : (fieldValues[listObjGroupedImageFields[0].field].hasOwnProperty('objectURL') ? fieldValues[listObjGroupedImageFields[0].field].objectURL : '')})`, backgroundSize: (objFile !== null && typeof objFile === 'object' && objFile.type === 'image/svg+xml') ? 'contain' : 'cover' }"
              />
            </div>
            <div v-else class="border-2 surface-border border-round-xl border-400 p-1 flex align-items-center justify-content-center" style="width: 180px; height: 180px;">
              <Skeleton height="100%" width="100%" />
            </div>
            <div class="col-12">
              <div class="flex justify-content-center align-items-center my-2">
                <span>
                  <Button :disabled="loadingSave" icon="pi pi-upload" class="ml-2" text @click="openFileUpload" />
                  <Button icon="pi pi-times" class="ml-2" severity="danger" :disabled="!objFile || loadingSave" text @click="clearFile(fieldValues, listObjGroupedImageFields[0].field)" />
                </span>
              </div>
            </div>
          </div>

          <span v-if="listObjGroupedImageFields[0].dataType === 'text'">
            <InputText
              v-if="!loadingSave" v-model="fieldValues[listObjGroupedImageFields[0].field]"
              show-clear :disabled="listObjGroupedImageFields[0]?.disabled"
              @update:model-value="updateField(listObjGroupedImageFields[0].field, $event)"
            />
            <Skeleton v-else height="2rem" />
          </span>

          <span v-else-if="listObjGroupedImageFields[0].dataType === 'textarea'">
            <Textarea
              v-if="!loadingSave" v-model="fieldValues[listObjGroupedImageFields[0].field]" rows="5"
              :disabled="listObjGroupedImageFields[0]?.disabled"
              show-clear @update:model-value="updateField(listObjGroupedImageFields[0].field, $event)"
            />
            <Skeleton v-else height="6rem" />
          </span>

          <span v-else-if="listObjGroupedImageFields[0].dataType === 'date'">
            <Calendar
              v-if="!loadingSave"
              v-model="fieldValues[listObjGroupedImageFields[0].field]"
              date-format="yy-mm-dd" :disabled="listObjGroupedImageFields[0]?.disabled"

              @update:model-value="(value) => updateField(listObjGroupedImageFields[0].field, value)"
            />
            <Skeleton v-else height="2rem" />
          </span>

          <span v-else-if="listObjGroupedImageFields[0].dataType === 'number'">
            <InputNumber
              v-model="fieldValues[listObjGroupedImageFields[0].field]"
              :disabled="listObjGroupedImageFields[0]?.disabled"
              @update:model-value="updateField(listObjGroupedImageFields[0].field, $event)"
            />
          </span>

          <span v-else-if="listObjGroupedImageFields[0].dataType === 'password'">
            <Password
              v-model="fieldValues[listObjGroupedImageFields[0].field]" :disabled="listObjGroupedImageFields[0]?.disabled"
              toggle-mask
              @update:model-value="updateField(listObjGroupedImageFields[0].field, $event)"
            />
          </span>

          <span v-else-if="listObjGroupedImageFields[0].dataType === 'check'">
            <Checkbox
              :id="listObjGroupedImageFields[0].field"
              v-model="fieldValues[listObjGroupedImageFields[0].field]"
              :binary="true"
              :disabled="listObjGroupedImageFields[0]?.disabled"
              @update:model-value="updateField(listObjGroupedImageFields[0].field, $event)"
            />
            <label :for="listObjGroupedImageFields[0].field" class="ml-2">
              <span v-if="typeof listObjGroupedImageFields[0].header === 'function'">
                {{ listObjGroupedImageFields[0].header() }}
              </span>
              <span v-else>
                {{ listObjGroupedImageFields[0].header }}
              </span>
            </label>
          </span>
        </slot>

        <!-- Validation errors -->
        <div class="flex">
          <div class="w-full">
            <div v-if="errors[listObjGroupedImageFields[0].field]" :class="listObjGroupedImageFields[0].errorClass ? listObjGroupedImageFields[0].errorClass : 'p-error text-xs'" class="w-full">
              <div v-for="error in errors[listObjGroupedImageFields[0].field]" :key="error" class="error-message">
                {{ error }}
              </div>
            </div>
          </div>
          <div v-if="(listObjGroupedImageFields[0].dataType === 'text' || listObjGroupedImageFields[0].dataType === 'textarea') && getMaxValidation(listObjGroupedImageFields[0].validation)" class="flex justify-content-end py-0 my-0" style="font-size: 10px;">
            {{ fieldValues[listObjGroupedImageFields[0].field].length }}/{{ getMaxValidation(listObjGroupedImageFields[0].validation) }}
          </div>
        </div>

        <!-- Footer slot -->
        <div v-if="listObjGroupedImageFields[0].footer" :style="listObjGroupedImageFields[0].footerStyle" :class="listObjGroupedImageFields[0].footerClass ? listObjGroupedImageFields[0].footerClass : ''">
          <slot :name="`footer-${listObjGroupedImageFields[0].field}`" :field="listObjGroupedImageFields[0]">
            <small>{{ listObjGroupedImageFields[0].footer }}</small>
          </slot>
        </div>
      </div>
    </div>
    <div :class="classSecondGrid ? classSecondGrid : 'col-12 xl:col-9'" style="max-height: 500px">
      <div :class="formClass || 'p-fluid formgrid grid'">
        <div v-for="(field, index) in listObjGroupedImageFields.slice(1)" v-show="!field.hidden" :key="field.field || index" :style="field.style" :class="field.class ? `${field.class} mb-1` : 'mb-1'">
          <div v-if="field.header && field.dataType !== 'check'" :style="field.headerStyle" :class="field.headerClass">
            <slot :name="`header-${field.field}`" :field="field">
              <strong>
                {{ typeof field.header === 'function' ? field.header() : field.header }}
              </strong>
              <span v-if="field.class.includes('required')" class="p-error font-semibold">*</span>
            </slot>
          </div>

          <!-- Field slot -->
          <div :class="field.containerFieldClass">
            <slot :name="`field-${field.field}`" :item="fieldValues" :field="field.field" :fields="fields" :on-update="updateField">
              <!-- <div v-if="field.dataType === 'image'" class="flex flex-wrap justify-content-center">
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
                <Skeleton v-else height="2rem"  />
                <div class="col-12">
                  <div class="flex justify-content-center align-items-center my-2">
                    <span>
                      <Button icon="pi pi-upload" class="ml-2" text @click="openFileUpload" />
                      <Button icon="pi pi-times" class="ml-2" severity="danger" :disabled="!objFile" text @click="clearFile(fieldValues, field.field)" />
                    </span>
                  </div>
                </div>
              </div> -->

              <span v-if="field.dataType === 'text'">
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
                  @update:model-value="(value) => updateField(field.field, value)"
                />
                <Skeleton v-else height="2rem" />
              </span>

              <span v-else-if="field.dataType === 'number'">
                <InputNumber
                  v-model="fieldValues[field.field]"
                  :disabled="field?.disabled"
                  @update:model-value="updateField(field.field, $event)"
                />
              </span>

              <span v-else-if="field.dataType === 'password'">
                <Password
                  v-model="fieldValues[field.field]"
                  :disabled="field?.disabled"
                  toggle-mask
                  @update:model-value="updateField(field.field, $event)"
                />
              </span>

              <span v-else-if="field.dataType === 'check'">
                <Checkbox
                  :id="field.field"
                  v-model="fieldValues[field.field]"
                  :binary="true"
                  :disabled="field?.disabled"
                  @update:model-value="updateField(field.field, $event)"
                />
                <label :for="field.field" class="ml-2">
                  {{ typeof field.header === 'function' ? field.header() : field.header }}
                  <span :class="field.class.includes('required') ? 'p-error' : ''">*</span>
                </label>
              </span>
            </slot>

            <!-- Validation errors -->
            <div class="flex">
              <div class="w-full">
                <div v-if="errors[field.field]" :class="field.errorClass ? field.errorClass : 'p-error text-xs'" class="w-full">
                  <div v-for="error in errors[field.field]" :key="error" class="error-message">
                    {{ error }}
                  </div>
                </div>
              </div>
              <!-- <div v-if="(field.dataType === 'text' || field.dataType === 'textarea') && getMaxValidation(field.validation)" class="flex justify-content-end py-0 my-0" style="font-size: 10px;">
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
        </div>
      </div>
    </div>

    <div
      v-for="(field, index) in listObjGroupedFields" v-show="!field.hidden" :key="field.field || index" :style="field.style"
      :class="field.class ? `${field.class} mb-1` : 'mb-1'"
    >
      <!-- Header slot -->
      <div v-if="field.header && field.dataType !== 'check'" :style="field.headerStyle" :class="field.headerClass">
        <slot :name="`header-${field.field}`" :field="field">
          <strong>
            {{ typeof field.header === 'function' ? field.header() : field.header }}
          </strong>
          <span v-if="hasMinValidation(field.validation) || hasCustomValidation(field.validation)" class="text-red-500">*</span>
        </slot>
      </div>

      <!-- Field slot -->
      <div :class="field.containerFieldClass">
        <slot :name="`field-${field.field}`" :item="fieldValues" :field="field.field" :fields="fields" :on-update="updateField">
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

          <span v-if="field.dataType === 'text'">
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
              show-clear @update:model-value="updateField(field.field, $event)"
            />
            <Skeleton v-else height="6rem" />
          </span>

          <span v-else-if="field.dataType === 'date'">
            <Calendar
              v-if="!loadingSave"
              v-model="fieldValues[field.field]"
              date-format="yy-mm-dd" :disabled="field?.disabled"
              @update:model-value="(value) => updateField(field.field, value)"
            />
            <Skeleton v-else height="2rem" />
          </span>

          <span v-else-if="field.dataType === 'number'">
            <InputNumber
              v-model="fieldValues[field.field]"
              :disabled="field?.disabled"
              @update:model-value="updateField(field.field, $event)"
            />
          </span>

          <span v-else-if="field.dataType === 'check'">
            <Checkbox
              :id="field.field"
              v-model="fieldValues[field.field]"
              :binary="true"
              :disabled="field?.disabled"
              @update:model-value="updateField(field.field, $event)"
            />
            <label :for="field.field" class="ml-2"> {{ typeof field.header === 'function' ? field.header() : field.header }} </label>
          </span>
        </slot>

        <!-- Validation errors -->
        <div class="flex">
          <div class="w-full">
            <div v-if="errors[field.field]" :class="field.errorClass ? field.errorClass : 'p-error text-xs'" class="w-full">
              <div v-for="error in errors[field.field]" :key="error" class="error-message">
                {{ error }}
              </div>
            </div>
          </div>
          <div v-if="(field.dataType === 'text' || field.dataType === 'textarea') && getMaxValidation(field.validation)" class="flex justify-content-end py-0 my-0" style="font-size: 10px;">
            {{ fieldValues[field.field].length }}/{{ getMaxValidation(field.validation) }}
          </div>
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
    <!-- General Footer slot of the form -->
    <div v-if="showActions" class="col-12 form-footer mt-4 flex justify-content-between md:justify-content-end">
      <slot name="form-footer">
        <Button v-tooltip.top="'Save'" class="w-3rem mx-2" icon="pi pi-save" :loading="loadingSave" @click="submitForm($event)" />
        <Button v-tooltip.top="'Delete'" class="w-3rem" severity="danger" :loading="loadingDelete" icon="pi pi-trash" @click="deleteItem($event)" />
      </slot>
    </div>
  </div>
</template>

<style lang="scss">
</style>

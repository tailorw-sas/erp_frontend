<script setup lang="ts">
import { type PropType, reactive, ref, watch, watchEffect } from 'vue'
import pkg from 'lodash'
import type { IFormField } from '../fields/interfaces/IFieldInterfaces'
import { GenericService } from '@/services/generic-services'

const props = defineProps({
  listFields: {
    type: Object as PropType<IFormField>,
    default: {}
  },
  formOptions: {
    type: Object as PropType<{
      moduleApi: string
      uriApi: string
      itemId: string
      btnTextSave?: string
      btnTextCancel?: string
      formWithImage?: string[]
      showBtnCancel?: boolean
      showBtnSave?: boolean
      showBtnActions?: boolean
      showSecondGrid?: boolean
      saveExternally?: boolean
    }>,
    required: true
  },
  forceSave: {
    type: Boolean,
    required: false
  }
})

const emit = defineEmits(['close', 'save', 'update:formConfig', 'forceSave', 'haveErrorInForm', 'onFetch'])

const { cloneDeep } = pkg

const loadingForm = ref(false)
const submitForm = ref(false)
const haveAnyError = ref(false)
const disabledForm = ref(false)
const refKey = ref(0)
const objModelValue = reactive<IFormField>(cloneDeep({ ...props.listFields }))
const listKeyFormWithImage = ref<string[]>(cloneDeep(props.formOptions.formWithImage || []))

async function getItemById() {
  try {
    if (props.formOptions.moduleApi && props.formOptions.uriApi && props.formOptions.itemId) {
      loadingForm.value = true

      const response = await GenericService.getById(props.formOptions.moduleApi, props.formOptions.uriApi, props.formOptions.itemId)
      const updatedObjModelValue = reactive(objModelValue)
      emit('onFetch', response)
      for (const prop in response) {
        if (response.hasOwnProperty(prop) && objModelValue.hasOwnProperty(prop) || (prop === 'geolocation') && objModelValue.hasOwnProperty('parroquiaDto')) {
          let idParroquia = ''
          switch (prop) {
            case 'geolocation':
              if (typeof response[prop] === 'object') {
                idParroquia = response[prop].id
              }
              else {
                idParroquia = response[prop]
              }
              if (idParroquia) {
                const responseLocations = await GenericService.getById('patients', 'locations', idParroquia, 'parroquia')

                for (const prop in responseLocations) {
                  if (responseLocations.hasOwnProperty(prop) && objModelValue.hasOwnProperty(prop)) {
                    updatedObjModelValue[prop].value = responseLocations[prop]
                  }
                }
              }
              break
            default:
              // Cuando envian solo el id y no el objeto
              if (updatedObjModelValue[prop].type === 'select' && typeof response[prop] === 'string' && (updatedObjModelValue[prop].localItems?.length === 0 || updatedObjModelValue[prop].localItems?.length === undefined)) {
                const payload
                  = {
                    filter: [
                      {
                        key: 'id',
                        operator: 'EQUALS',
                        value: response[prop],
                        logicalOperation: 'AND'
                      }
                    ],
                    query: '',
                    pageSize: 200,
                    page: 0
                  }
                const response2 = await GenericService.search(updatedObjModelValue[prop].moduleApi, updatedObjModelValue[prop].uriApi, payload)

                if (response2 && response2.data && response2.data.length > 0) {
                  updatedObjModelValue[prop].value = { id: response2.data[0].id, name: response2.data[0].name }
                }
              }
              else {
                updatedObjModelValue[prop].value = response[prop]
              }
              break
          }
        }
      }
      Object.assign(objModelValue, updatedObjModelValue)

      refKey.value += 1
    }
  }
  catch (error) {
    console.error('Error:', error)
  }
  finally {
    loadingForm.value = false
  }
}

async function submitAndReloadForm() {
  submitForm.value = true
}

function base64ToFile(base64String: string, filename: string, mimeType: string) {
  if (base64String) {
    const byteCharacters = atob(base64String)
    const byteNumbers = Array.from({ length: byteCharacters.length })

    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i)
    }

    const byteArray = new Uint8Array(byteNumbers)
    const blob = new Blob([byteArray], { type: mimeType })

    return new File([blob], filename, { type: mimeType })
  }
  else {
    return null
  }
}

async function getUrlByImage(file: File) {
  if (file) {
    const response = await GenericService.uploadFile('cloudbridges', 'files', file)
    return response.data.url
  }
}

async function saveItem() {
  try {
    if (props.formOptions.moduleApi && props.formOptions.uriApi && props.formOptions.itemId) {
      loadingForm.value = true
      const payload: { [key: string]: any } = {}
      await submitAndReloadForm()

      let countError = 0
      for (const key in objModelValue) {
        // Detecta si algunos de los campos tiene errores
        if (objModelValue.hasOwnProperty(key)) {
          if (objModelValue[key].haveError) {
            countError += 1
          }
          // Tratar las imagenes o files
          if ((objModelValue[key].type === 'image' || objModelValue[key].type === 'file') && objModelValue[key].value.value) {
            const fileCovnerted = await base64ToFile(objModelValue[key].value.value, objModelValue[key].value.name, objModelValue[key].value.type)
            if (fileCovnerted) {
              objModelValue[key].value = await getUrlByImage(fileCovnerted)
            }
          }
          const value = objModelValue[key].value
          if (key === 'parroquiaDto') {
            payload.geographicLocation = typeof value === 'object' ? value.id : value
          }
          else if (key !== 'provinceDto' && key !== 'cantonDto') {
            if (typeof value === 'object' && value !== null && !Array.isArray(value)) {
              payload[key] = value ? value.id : value
            }
            else {
              payload[key] = value
            }
          }
        }
      }

      if (countError === 0 && (props.formOptions.hasOwnProperty('saveExternally') ? props.formOptions.saveExternally : true)) {
        await GenericService.update(props.formOptions.moduleApi, props.formOptions.uriApi, props.formOptions.itemId, payload)

        emit('save', true)
        emit('forceSave', false)
        // onClose()
      }
    }
  }
  catch (error) {
    emit('forceSave', false)
  }
  finally {
    loadingForm.value = false
  }
}

function onClose() {
  emit('close')
}

function hasErrorInObject(obj) {
  for (const value of Object.values(obj)) {
    if (value.haveError === true) {
      return true
    }
  }
  return false
}

function showFileOrImage() {
  if (listKeyFormWithImage.value.includes('image')) {
    return 'image'
  }
  if (listKeyFormWithImage.value.includes('file')) {
    return 'file'
  }
}

function resizeFormColum() {
  if (showFileOrImage() === 'image' && objModelValue.hasOwnProperty('image') && objModelValue.image.hasOwnProperty('visible') && objModelValue.image.visible === false) {
    return false
  }
  else {
    return true
  }
}

function areContented(arr1: [], arr2: []) {
  if (arr1.length === 0) {
    return true
  }
  else if (arr1.length > 0 && arr2) {
    return arr1.every(elemento => arr2.includes(elemento))
  }
  else {
    return false
  }
}

function onFormDataChanged(newValue: any, oldValue: any) {
  emit('update:formConfig', newValue)
  disabledForm.value = hasErrorInObject(newValue)
  emit('haveErrorInForm', disabledForm.value)
}

watch(objModelValue, onFormDataChanged)

watch(() => props.forceSave, (newValue) => {
  if (newValue) {
    saveItem()
  }
})

onMounted(() => {
  getItemById()
})
</script>

<template>
  <div class="p-4">
    <div v-if="listKeyFormWithImage.includes('image') || listKeyFormWithImage.includes('file')" class="grid p-fluid formgrid" style="margin-bottom: -15px;">
      <div class="col-12 xl:col-3 flex align-items-center justify-content-center">
        <ImageFieldComponent
          v-if="showFileOrImage() === 'image' && resizeFormColum()"
          :key="refKey"
          :submit="submitForm"
          :properties="objModelValue.image"
          :validation-keywords="objModelValue.image.validationKeywords"
          @update:model-value="objModelValue.image.value = $event"
          @update:error-messages="objModelValue.image.errorMessage = $event"
          @invalid-field="objModelValue.image.haveError = $event; haveAnyError = $event"
        />
        <ImageFieldComponent
          v-if="showFileOrImage() === 'file'"
          :key="refKey"
          :submit="submitForm"
          :properties="objModelValue.file"
          :validation-keywords="objModelValue.file.validationKeywords"
          @update:model-value="objModelValue.file.value = $event"
          @update:error-messages="objModelValue.file.errorMessage = $event"
          @invalid-field="objModelValue.file.haveError = $event; haveAnyError = $event"
        />
      </div>
      <div class="col-12" :class="resizeFormColum() ? 'xl:col-9' : ''">
        <div class="grid p-fluid formgrid">
          <div v-for="item of listKeyFormWithImage" :key="item" :class="`col-12 lg:col-${objModelValue[item].col} field`">
            <TextFieldComponent
              v-if="item !== 'image' && (objModelValue[item].type === 'text' || objModelValue[item].type === 'time')"
              :key="refKey"
              :submit="submitForm"
              :properties="objModelValue[item]"
              :validation-keywords="objModelValue[item].validationKeywords"
              @update:model-value="objModelValue[item].value = $event"
              @update:error-messages="objModelValue[item].errorMessage = $event"
              @invalid-field="objModelValue[item].haveError = $event; haveAnyError = $event"
            />
            <TextFieldComponent
              v-if="item !== 'image' && objModelValue[item].type === 'number'"
              :key="refKey"
              :submit="submitForm"
              :properties="objModelValue[item]"
              :validation-keywords="[...objModelValue[item].validationKeywords, 'onlyNumbers']"
              @update:model-value="objModelValue[item].value = $event"
              @update:error-messages="objModelValue[item].errorMessage = $event"
              @invalid-field="objModelValue[item].haveError = $event; haveAnyError = $event"
            />
            <TextAreaFieldComponent
              v-if="item !== 'image' && objModelValue[item].type === 'textarea'"
              :key="refKey"
              :submit="submitForm"
              :properties="objModelValue[item]"
              :validation-keywords="objModelValue[item].validationKeywords"
              @update:model-value="objModelValue[item].value = $event"
              @update:error-messages="objModelValue[item].errorMessage = $event"
              @invalid-field="objModelValue[item].haveError = $event; haveAnyError = $event"
            />
            <SelectFieldComponent
              v-if="item !== 'image' && objModelValue[item].type === 'select'"
              :key="refKey"
              :properties="objModelValue[item]"
              :submit="submitForm"
              :local-items="objModelValue[item].localItems"
              :validation-keywords="objModelValue[item].validationKeywords"
              @update:model-value="objModelValue[item].value = $event"
              @update:error-messages="objModelValue[item].errorMessage = $event"
              @invalid-field="objModelValue[item].haveError = $event; haveAnyError = $event"
            />
            <SearchSelectFieldComponent
              v-if="item !== 'image' && objModelValue[item].type === 'select-search'"
              :key="refKey"
              :properties="objModelValue[item]"
              :submit="submitForm"
              :local-items="objModelValue[item].localItems"
              :validation-keywords="objModelValue[item].validationKeywords"
              @update:model-value="objModelValue[item].value = $event"
              @update:error-messages="objModelValue[item].errorMessage = $event"
              @invalid-field="objModelValue[item].haveError = $event; haveAnyError = $event"
            />
            <MultiSelectFieldComponent
              v-if="item !== 'image' && objModelValue[item].type === 'multi-select' && objModelValue[item].visible"
              :key="refKey"
              :properties="objModelValue[item]"
              :submit="submitForm"
              :local-items="objModelValue[item].localItems"
              :validation-keywords="objModelValue[item].validationKeywords"
              @update:model-value="objModelValue[item].value = $event"
              @update:error-messages="objModelValue[item].errorMessage = $event"
              @invalid-field="objModelValue[item].haveError = $event; haveAnyError = $event"
            />
            <SelectFieldDependedComponent
              v-if="item !== 'image' && objModelValue[item].type === 'dependent-select'"
              :key="refKey"
              :properties="objModelValue[item]"
              :validation-keywords="objModelValue[item].validationKeywords"
              :parent-value="objModelValue[item].keyValue ? (typeof objModelValue[objModelValue[item].keyValue].value === 'object' ? objModelValue[objModelValue[item].keyValue].value.id : 'null') : 'null'"
              @update:model-value="objModelValue[item].value = $event"
              @update:error-messages="objModelValue[item].errorMessage = $event"
              @invalid-field="objModelValue[item].haveError = $event; haveAnyError = $event"
            />
            <TextFieldCustomComponent
              v-if="item !== 'image' && objModelValue[item].type === 'text-custom'"
              :key="refKey"
              :submit="submitForm"
              :properties="objModelValue[item]"
              :parent-value="objModelValue[item].parentValues && objModelValue[item].parentValues[0] && objModelValue[item].parentValues[1] ? `${objModelValue[objField.parentValues[0]].value ? `${objModelValue[objField.parentValues[0]].value.name.toLocaleUpperCase()}:` : ''}${objModelValue[objField.parentValues[1]].value ? objModelValue[objField.parentValues[1]].value.toLocaleUpperCase() : ''}` : '' "
              :validation-keywords="objModelValue[item].validationKeywords"
              @update:model-value="objModelValue[item].value = $event"
              @update:error-messages="objModelValue[item].errorMessage = $event"
              @invalid-field="objModelValue[item].haveError = $event; haveAnyError = $event"
            />
            <TextFieldComponent
              v-if="item !== 'image' && objModelValue[item].type === 'password'"
              :key="refKey"
              :submit="submitForm"
              :properties="objModelValue[item]"
              :validation-keywords="objModelValue[item].validationKeywords"
              @update:model-value="objModelValue[item].value = $event"
              @update:error-messages="objModelValue[item].errorMessage = $event"
              @invalid-field="objModelValue[item].haveError = $event; haveAnyError = $event"
            />
            <CheckBoxFieldComponent
              v-if="item !== 'image' && objModelValue[item].type === 'check'"
              :key="refKey"
              :submit="submitForm"
              :properties="objModelValue[item]"
              :validation-keywords="objModelValue[item].validationKeywords"
              class="mt-4 mb-4"
              @update:model-value="objModelValue[item].value = $event"
              @update:error-messages="objModelValue[item].errorMessage = $event"
              @invalid-field="objModelValue[item].haveError = $event; haveAnyError = $event"
            />
            <DateFieldComponent
              v-if="item !== 'image' && objModelValue[item].type === 'date'"
              :key="refKey"
              :submit="submitForm"
              :properties="objModelValue[item]"
              :validation-keywords="objModelValue[item].validationKeywords"
              @update:model-value="objModelValue[item].value = $event"
              @update:error-messages="objModelValue[item].errorMessage = $event"
              @invalid-field="objModelValue[item].haveError = $event; haveAnyError = $event"
            />
            <InputMaskFieldComponent
              v-if="item !== 'image' && objModelValue[item].type === 'text-mask'"
              :key="refKey"
              :submit="submitForm"
              :properties="objModelValue[item]"
              :validation-keywords="objModelValue[item].validationKeywords"
              @update:model-value="objModelValue[item].value = $event"
              @update:error-messages="objModelValue[item].errorMessage = $event"
              @invalid-field="objModelValue[item].haveError = $event; haveAnyError = $event"
            />
          </div>
        </div>
      </div>
    </div>

    <div v-if="formOptions.hasOwnProperty('showSecondGrid') ? formOptions.showSecondGrid : true" class="grid p-fluid formgrid">
      <div v-for="(objField, index) of objModelValue" :key="index" :class="`col-12 lg:col-${objField.col}`" class="field">
        <TextFieldComponent
          v-if="!listKeyFormWithImage.includes(index.toString()) && (objField.type === 'text' || objField.type === 'time') && (objField.hasOwnProperty('visible') ? objField.visible : true)"
          :key="refKey"
          :submit="submitForm"
          :properties="objField"
          :validation-keywords="objField.validationKeywords"
          @update:model-value="objField.value = $event"
          @update:error-messages="objField.errorMessage = $event"
          @invalid-field="objField.haveError = $event; haveAnyError = $event"
        />
        <TextFieldComponent
          v-if="!listKeyFormWithImage.includes(index.toString()) && (objField.type === 'number')"
          :key="refKey"
          :submit="submitForm"
          :properties="objField"
          :validation-keywords="[...objField.validationKeywords, 'onlyNumbers']"
          @update:model-value="objField.value = $event"
          @update:error-messages="objField.errorMessage = $event"
          @invalid-field="objField.haveError = $event; haveAnyError = $event"
        />
        <TextAreaFieldComponent
          v-if="!listKeyFormWithImage.includes(index.toString()) && (objField.type === 'textarea')"
          :key="refKey"
          :submit="submitForm"
          :properties="objField"
          :validation-keywords="objField.validationKeywords"
          @update:model-value="objField.value = $event"
          @update:error-messages="objField.errorMessage = $event"
          @invalid-field="objField.haveError = $event; haveAnyError = $event"
        />
        <SelectFieldComponent
          v-if="!listKeyFormWithImage.includes(index.toString()) && (objField.type === 'select') && (objField.hasOwnProperty('visible') ? objField.visible : true)"
          :key="refKey"
          :properties="objField"
          :submit="submitForm"
          :local-items="objField.localItems"
          :validation-keywords="objField.validationKeywords"
          @update:model-value="objField.value = $event"
          @update:error-messages="objField.errorMessage = $event"
          @invalid-field="objField.haveError = $event; haveAnyError = $event"
        />
        <SearchSelectFieldComponent
          v-if="!listKeyFormWithImage.includes(index.toString()) && (objField.type === 'select-search') && (objField.hasOwnProperty('visible') ? objField.visible : true)"
          :key="refKey"
          :properties="objField"
          :submit="submitForm"
          :local-items="objField.localItems"
          :validation-keywords="objField.validationKeywords"
          @update:model-value="objField.value = $event"
          @update:error-messages="objField.errorMessage = $event"
          @invalid-field="objField.haveError = $event; haveAnyError = $event"
        />
        <MultiSelectFieldComponent
          v-if="!listKeyFormWithImage.includes(index.toString()) && (objField.type === 'multi-select' && objField.visible)"
          :key="refKey"
          :properties="objField"
          :submit="submitForm"
          :local-items="objField.localItems"
          :validation-keywords="objField.validationKeywords"
          @update:model-value="objField.value = $event"
          @update:error-messages="objField.errorMessage = $event"
          @invalid-field="objField.haveError = $event; haveAnyError = $event"
        />
        <SelectFieldDependedComponent
          v-if="!listKeyFormWithImage.includes(index.toString()) && (objField.type === 'dependent-select')"
          :key="refKey"
          :properties="objField"
          :validation-keywords="objField.validationKeywords"
          :parent-value="objField.keyValue ? ((typeof objModelValue[objField.keyValue].value === 'object' && objModelValue[objField.keyValue].value !== null) ? objModelValue[objField.keyValue].value.id : objModelValue[objField.keyValue].value) : ''"
          @update:model-value="objField.value = $event"
          @update:error-messages="objField.errorMessage = $event"
          @invalid-field="objField.haveError = $event; haveAnyError = $event"
        />
        <ImageFieldComponent
          v-if="!listKeyFormWithImage.includes(index.toString()) && (objField.type === 'image' || objField.type === 'file')"
          :key="refKey"
          :submit="submitForm"
          :properties="objField"
          :validation-keywords="objField.validationKeywords"
          @update:model-value="objField.value = $event"
          @update:error-messages="objField.errorMessage = $event"
          @invalid-field="objField.haveError = $event; haveAnyError = $event"
        />
        <TextFieldCustomComponent
          v-if="!listKeyFormWithImage.includes(index.toString()) && (objField.type === 'text-custom')"
          :key="refKey"
          :submit="submitForm"
          :properties="objField"
          :parent-value="objField.parentValues && objField.parentValues[0] && objField.parentValues[1] ? `${objModelValue[objField.parentValues[0]].value ? `${objModelValue[objField.parentValues[0]].value.name.toLocaleUpperCase()}:` : ''}${objModelValue[objField.parentValues[1]].value ? objModelValue[objField.parentValues[1]].value.toLocaleUpperCase() : ''}` : '' "
          :validation-keywords="objField.validationKeywords"
          @update:model-value="objField.value = $event"
          @update:error-messages="objField.errorMessage = $event"
          @invalid-field="objField.haveError = $event; haveAnyError = $event"
        />
        <!--        <TextFieldComponent
          v-if="!listKeyFormWithImage.includes(index.toString()) && (objField.type === 'password')"
          :key="refKey"
          :submit="submitForm"
          :properties="objField"
          :validation-keywords="objField.validationKeywords"
          @update:model-value="objField.value = $event"
          @update:error-messages="objField.errorMessage = $event"
          @invalid-field="objField.haveError = $event; haveAnyError = $event"
        /> -->
        <PasswordFieldComponent
          v-if="!listKeyFormWithImage.includes(index.toString()) && (objField.type === 'password') && (objField.hasOwnProperty('visible') ? objField.visible : true)"
          :key="refKey"
          :submit="submitForm"
          :properties="objField"
          :validation-keywords="objField.validationKeywords"
          @update:model-value="objField.value = $event"
          @update:error-messages="objField.errorMessage = $event"
          @invalid-field="objField.haveError = $event; haveAnyError = $event"
        />
        <CheckBoxFieldComponent
          v-if="!listKeyFormWithImage.includes(index.toString()) && (objField.type === 'check')"
          :key="refKey"
          :submit="submitForm"
          :properties="objField"
          :validation-keywords="objField.validationKeywords"
          class="mt-4"
          @update:model-value="objField.value = $event"
          @update:error-messages="objField.errorMessage = $event"
          @invalid-field="objField.haveError = $event; haveAnyError = $event"
        />

        <DateFieldComponent
          v-if="!listKeyFormWithImage.includes(index.toString()) && (objField.type === 'date')"
          :key="refKey"
          :submit="submitForm"
          :properties="objField"
          :validation-keywords="objField.validationKeywords"
          @update:model-value="objField.value = $event"
          @update:error-messages="objField.errorMessage = $event"
          @invalid-field="objField.haveError = $event; haveAnyError = $event"
        />
        <InputMaskFieldComponent
          v-if="!listKeyFormWithImage.includes(index.toString()) && (objField.type === 'text-mask')"
          :key="refKey"
          :submit="submitForm"
          :properties="objField"
          :validation-keywords="objField.validationKeywords"
          @update:model-value="objField.value = $event"
          @update:error-messages="objField.errorMessage = $event"
          @invalid-field="objField.haveError = $event; haveAnyError = $event"
        />
      </div>
    </div>

    <div v-if="props.formOptions.hasOwnProperty('showBtnActions') ? props.formOptions.showBtnActions : true" class="grid p-fluid formgrid">
      <div class="col-12 flex justify-content-end mt-4">
        <Button v-if="props.formOptions.hasOwnProperty('showBtnCancel') ? props.formOptions.showBtnCancel : true" v-tooltip.top="'Cancelar'" class="w-8rem mr-3" outlined severity="secondary" icon="pi pi-times" :label="props.formOptions.btnTextCancel ? props.formOptions.btnTextCancel : 'Cancel'" @click="onClose()" />
        <Button v-if="props.formOptions.hasOwnProperty('showBtnSave') ? props.formOptions.showBtnSave : true" v-tooltip.top="'Guardar'" class="w-8rem" icon="pi pi-check" :label="props.formOptions.btnTextSave ? props.formOptions.btnTextSave : 'Save'" :loading="loadingForm" :disabled="disabledForm" @click="saveItem()" />
      </div>
    </div>
  </div>
</template>

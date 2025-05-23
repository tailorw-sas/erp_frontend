<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import getUrlByImage from '~/composables/files'
import type { IData } from '~/components/table/interfaces/IModelData'
import { statusToBoolean, statusToString, updateFieldProperty } from '~/utils/helpers'
import { ENUM_SHORT_TYPE } from '~/utils/Enums'
// VARIABLES -----------------------------------------------------------------------------------------
const openDialogPass = ref(false)
const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const formReload = ref(0)
const loadingSaveAll = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const loadingDelete = ref(false)
const loadingSearch = ref(false)
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})
const confApi = reactive({
  moduleApi: 'report',
  uriApi: 'db-connection',
})

const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'username', name: 'User Name' },
]

const item = ref<GenericObject>({
  code: '',
  name: '',
  url: '',
  username: '',
  password: '',
  status: true,
})

const itemPassword = ref<GenericObject>({
  oldPassword: '',
  newPassword: '',
})
const confirmPassword = ref('')
const itemTemp = ref<GenericObject>({
  code: '',
  name: '',
  url: '',
  username: '',
  password: '',
  status: true,
})

const fieldsEdit: Array<FieldDefinitionType> = [
  {
    field: 'code',
    header: 'Code',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The code field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'name',
    header: 'Name',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The name field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'url',
    header: 'Url',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The url field is required').max(255, 'Maximum 255 characters')
  },
  {
    field: 'username',
    header: 'User Name',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The username field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    class: 'field col-12 required mb-3 mt-3',
  },
]
const fields: Array<FieldDefinitionType> = [
  {
    field: 'code',
    header: 'Code',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The code field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'name',
    header: 'Name',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The name field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'url',
    header: 'Url',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The url field is required').max(255, 'Maximum 255 characters')
  },
  {
    field: 'username',
    header: 'User Name',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The username field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'password',
    header: 'Password',
    dataType: 'text',
    hidden: false,
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The username field is required').max(250, 'Maximum 250 characters')
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    disabled: true,
    class: 'field col-12 required mb-3 mt-3',
  },
]
// FORM CONFIG -------------------------------------------------------------------------------------------

// -------------------------------------------------------------------------------------------------------

// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns = ref<IColumn[]>([
  { field: 'code', header: 'Code', type: 'text' },
  { field: 'name', header: 'Name', type: 'text' },
  { field: 'url', header: 'Url', type: 'text' },
  { field: 'username', header: 'User Name', type: 'text' },
  { field: 'createdAt', header: 'Created At', type: 'date' },
  { field: 'status', header: 'Active', type: 'bool' },
])
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Manage Db Connection',
  moduleApi: 'report',
  uriApi: 'db-connection',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})

const currentFields = computed(() => {
  return formTitle.value === 'Edit' ? fieldsEdit : fields
})

function searchAndFilter() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    payload.value.filter = [...payload.value.filter, {
      key: filterToSearch.value.criterial ? filterToSearch.value.criterial.id : '',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'AND',
      type: 'filterSearch',
    }]
  }
  getList()
}

function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value.criterial = ENUM_FILTER[0]
  filterToSearch.value.search = ''
  getList()
}
// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------
function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''
  fieldsEdit[0].disabled = false
  fields[4].hidden = false
  updateFieldProperty(fields, 'status', 'disabled', true)
  formReload.value++
}

async function getList() {
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    listItems.value = []
    const newListItems = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = statusToBoolean(iterator.status)
      }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator, loadingEdit: false, loadingDelete: false })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }
    listItems.value = [...listItems.value, ...newListItems]

    if (listItems.value.length > 0) {
      idItemToLoadFirstTime.value = listItems.value[0].id
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}

async function resetListItems() {
  payload.value.page = 0
  getList()
}

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    // openDialogPass.value = true;
    try {
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)
      if (response) {
        item.value.code = response.code
        item.value.name = response.name
        item.value.id = response.id
        item.value.url = response.url
        item.value.username = response.username
        item.value.password = response.password
        item.value.status = statusToBoolean(response.status)
      }
      updateFieldProperty(fields, 'status', 'disabled', false)
      formReload.value += 1
      //  fields[4].hidden=true
      fieldsEdit[0].disabled = true
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Could not load database configuration', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    payload.status = statusToString(payload.status)
    return await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.status = statusToString(payload.status)
  return await GenericService.update(confApi.moduleApi, confApi.uriApi, idItem.value || '', payload)
}

async function updatePassword(itemPassword: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...itemPassword }

  try {
    await GenericService.update('report', 'db-connection/password', idItem.value || '', payload)
    openDialogPass.value = false
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
  }
  catch (error) {
    // Manejar el error de la actualización
    toast.add({ severity: 'error', summary: 'Error', detail: 'Password is wrong', life: 3000 })
  }
  finally {
    loadingSaveAll.value = false
  }
}

async function deleteItem(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    clearForm()
    getList()
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 3000 })
    loadingDelete.value = false
  }
  finally {
    loadingDelete.value = false
  }
}

async function saveItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  let successOperation = null
  if (idItem.value) {
    try {
      const response = await updateItem(item)
      if (response) {
        successOperation = true
        toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
      }
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
    idItem.value = ''
  }
  else {
    try {
      const response = await createItem(item)
      if (response) {
        successOperation = true
        toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
      }
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  if (successOperation) {
    clearForm()
    getList()
  }
}

async function savePassword(itemPassword: { [key: string]: any }) {
  loadingSaveAll.value = true
  let successOperation = null

  try {
    if (idItem.value) {
      const response: any = await updatePassword(itemPassword)
      if (response) {
        successOperation = true
      }
    }
  }
  catch (error: any) {
    successOperation = false
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: error.data?.data?.error?.errorMessage || 'An error occurred',
      life: 10000,
    })
  }
  finally {
    loadingSaveAll.value = false
    if (successOperation) {
      // Cerrar el diálogo y limpiar el formulario
      openDialogPass.value = false
      clearPasswordFields()
      toast.add({
        severity: 'success',
        summary: 'Success',
        detail: 'Password updated successfully',
        life: 3000,
      })
    }
  }
}

function requireConfirmationToSave(item: any) {
  const { event } = item
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      saveItem(item)
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}
function requireConfirmationToDelete(event: any) {
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    acceptClass: 'p-button-danger',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      deleteItem(idItem.value)
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns.value)
  // const templateCode = parseFilter?.find((item: IFilter) => item?.key === 'url')
  // const templateName = parseFilter?.find((item: IFilter) => item?.key === 'name')
  // const templateDescription = parseFilter?.find((item: IFilter) => item?.key === 'description')

  // if (templateCode) {
  //   templateCode.key = 'templateCode'
  // }
  // if (templateName) {
  //   templateName.key = 'templateName'
  // }
  // if (templateDescription) {
  //   templateDescription.key = 'templateDescription'
  // }

  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    // switch (event.sortField) {
    //   case 'code':
    //     event.sortField = 'templateCode'
    //     break
    //   case 'name':
    //     event.sortField = 'templateName'
    //     break
    //   case 'description':
    //     event.sortField = 'templateDescription'
    //     break
    //   case 'type':
    //     if (event.sortOrder === 'ASC') {
    //       event.sortOrder = 'ASC'
    //     }
    //     else {
    //       event.sortOrder = ENUM_SHORT_TYPE.DESC
    //     }
    //     break
    // }
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criterial && filterToSearch.value.search)
  return false
})

const disabledClearSearch = computed(() => {
  return !(filterToSearch.value.criterial && filterToSearch.value.search)
})
// -------------------------------------------------------------------------------------------------------

// WATCH FUNCTIONS -------------------------------------------------------------------------------------
watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10
  getList()
})

watch(() => idItemToLoadFirstTime.value, async (newValue) => {
  if (!newValue) {
    clearForm()
  }
  else {
    await getItemById(newValue)
  }
})
// -------------------------------------------------------------------------------------------------------
async function openModalPass() {
  openDialogPass.value = true
  clearPasswordFields()
}

function validateAndSavePassword() {
  if (itemPassword.value.newPassword !== confirmPassword.value) {
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: 'New password and confirm password must be the same.',
      life: 3000,
    })
    return
  }

  savePassword(itemPassword.value)
}

function clearPasswordFields() {
  itemPassword.value.oldPassword = ''
  itemPassword.value.newPassword = ''
  confirmPassword.value = ''
}
// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
onMounted(() => {
  filterToSearch.value.criterial = ENUM_FILTER[0]
  if (useRuntimeConfig().public.loadTableData) {
    getList()
    document.title = 'DB Connection'
  }
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h3 class="mb-0">
      Manage DB Connection
    </h3>
    <div
      v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
      class="my-2 flex justify-content-end px-0"
    >
      <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" @click="clearForm" />
    </div>
  </div>
  <div class="grid">
    <div class="col-12 md:col-6 xl:col-9">
      <div class="card p-0">
        <Accordion :active-index="0" class="mb-2">
          <AccordionTab>
            <template #header>
              <div class="text-white font-bold custom-accordion-header">
                Filters
              </div>
            </template>
            <div class="flex gap-4 flex-column lg:flex-row">
              <div class="flex align-items-center gap-2">
                <label for="email3">Criteria</label>
                <div class="w-full lg:w-auto">
                  <Dropdown
                    v-model="filterToSearch.criterial" :options="[...ENUM_FILTER]" option-label="name"
                    placeholder="Criteria" return-object="false" class="align-items-center w-full" show-clear
                  />
                </div>
              </div>
              <div class="flex align-items-center gap-2">
                <label for="email">Search</label>
                <div class="w-full lg:w-auto">
                  <IconField icon-position="left" class="w-full">
                    <InputText v-model="filterToSearch.search" type="text" placeholder="Search" class="w-full" />
                    <InputIcon class="pi pi-search" />
                  </IconField>
                </div>
              </div>
              <div class="flex align-items-center">
                <Button v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :disabled="disabledSearch" :loading="loadingSearch" @click="searchAndFilter" />
                <Button v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash" :disabled="disabledClearSearch" :loading="loadingSearch" @click="clearFilterToSearch" />
              </div>
            </div>
          </AccordionTab>
        </Accordion>
      </div>
      <DynamicTable
        :data="listItems"
        :columns="columns"
        :options="options"
        :pagination="pagination"
        @on-confirm-create="clearForm"
        @open-edit-dialog="getItemById($event)"
        @update:clicked-item="getItemById($event)"
        @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems"
        @on-sort-field="onSortField"
      />
    </div>
    <div class="col-12 md:col-6 xl:col-3">
      <div>
        <div class="font-bold text-lg px-4 bg-primary custom-card-header">
          {{ formTitle }}
        </div>
        <div class="card p-4">
          <EditFormV2
            :key="formReload" :fields="currentFields" :item="item" :show-actions="true"
            :loading-save="loadingSaveAll" @cancel="clearForm" @delete="requireConfirmationToDelete($event)"
            @submit="requireConfirmationToSave($event)"
          >
            <template #field-type="{ item: data, onUpdate }">
              <Dropdown
                v-if="!loadingSaveAll" v-model="data.type" :options="[...ENUM_REPORT_TYPE]" option-label="name"
                return-object="false" @update:model-value="($event) => {
                  onUpdate('type', $event)
                }"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #form-footer="props">
              <div class="flex justify-content-end">
                <Button v-tooltip.top="'Save'" class="w-3rem mx-2" icon="pi pi-save" :loading="loadingSaveAll" @click="props.item.submitForm($event)" />

                <Button v-if="idItem" v-tooltip.top="'Config Password'" class="w-3rem mr-2" icon="pi pi-eye" :loading="loadingSaveAll" @click="openModalPass" />

                <Button v-tooltip.top="'Delete'" class="w-3rem" severity="danger" outlined :loading="loadingDelete" icon="pi pi-trash" @click="props.item.deleteItem($event)" />
              </div>
            </template>
          </EditFormV2>
          <!-- Dialogo de cambiar password -->
          <Dialog
            v-model:visible="openDialogPass"
            :id-item="idItem"
            modal
            class="mx-3 sm:mx-0"

            content-class="border-round-bottom border-top-1 surface-border"
            :style="{ width: '30%' }"
            :pt="{
              root: {
                class: 'custom-dialog',
              },
              header: {
                style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
              },
              mask: {
                style: 'backdrop-filter: blur(5px)',
              },
            }"
            @update:password="savePassword"
            @hide="openDialogPass = false"
          >
            <template #header>
              <div class="flex justify-content-between">
                <h5 class="m-0">
                  Change Password
                </h5>
              </div>
            </template>
            <template #default>
              <form @submit.prevent="validateAndSavePassword">
                <div class="field mb-2 mt-4 ">
                  <label for="old-password" class="block font-bold mb-2 required">Old Password <span class="required-indicator ml-2"> * </span></label>

                  <InputText
                    id="old-password"
                    v-model="itemPassword.oldPassword"
                    type="password"
                    class="w-full"
                    placeholder="Enter old password"
                    required
                  />
                </div>
                <div class="field mb-2">
                  <label for="new-password" class="block font-bold mb-2 required">New Password <span class="required-indicator ml-2"> * </span></label>
                  <InputText
                    id="confirm-password"
                    v-model="itemPassword.newPassword"
                    type="password"
                    class="w-full "
                    placeholder="Enter new password"
                    required
                  />
                </div>
                <div class="field mb-4">
                  <label for="confirm-password" class="block font-bold mb-2 required">Confirm Password <span class="required-indicator ml-2"> * </span></label>
                  <InputText
                    id="confirm-password"
                    v-model="confirmPassword"
                    type="password"
                    class="w-full"
                    placeholder="Confirm new password"
                    required
                  />
                </div>
                <div class="field flex justify-content-end" style="margin-bottom: 0rem !important;">
                  <Button type="submit" icon="pi pi-save" label="Save" class="p-button-primary mr-2" />
                  <Button label="Cancel" icon="pi pi-times" severity="secondary" @click="openDialogPass = false" />
                </div>
              </form>
            </template>
          </Dialog>
        </div>
      </div>
    </div>
    <Toast position="top-center" :base-z-index="5" group="tc" />
    <ConfirmPopup group="headless" />
  </div>
</template>

<style scoped lang="scss">
.custom-card-header {
  margin-bottom: 0px;
  border-top-left-radius: 5px;
  border-top-right-radius: 5px;
  padding-top: 10px;
  padding-bottom: 10px;
}
.required {
  position: relative;
}
.required-indicator {
  color: rgba(199, 17, 17, 0.863);
  font-size: 16px;
  position: absolute;
  top: 0;
}
</style>

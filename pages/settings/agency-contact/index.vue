<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import { get } from 'lodash'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import type { IData } from '~/components/table/interfaces/IModelData'
import { validateEntityStatus } from '~/utils/schemaValidations'

const props = defineProps({
  agency: {
    type: Object,
    required: false
  },
})

// VARIABLES -----------------------------------------------------------------------------------------
const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const loadingSearch = ref(false)
const regionList = ref<any[]>([])
const regionSelected = ref('')
const agencyList = ref<any[]>([])
const hotelList = ref<any[]>([])
const formReload = ref(0)
const loadingSaveAll = ref(false)
const loadingDelete = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})
const confApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency-contact',
})

const confRegionApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-region',
})

const confHotelApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'manageAgency',
    header: 'Agency',
    dataType: 'select',
    class: 'field col-12 required',
    validation: z.object({
      id: z.string().min(1, 'The agency field is required'),
      name: z.string().min(1, 'The agency field is required'),
    })
  },
  {
    field: 'manageRegion',
    header: 'Region',
    dataType: 'select',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.object({
      id: z.string().min(1, 'The region field is required'),
      name: z.string().min(1, 'The region field is required'),
    }).nullable().refine(value => value && value.id && value.name, { message: 'The region field is a required' }),
  },
  {
    field: 'manageHotel',
    header: 'Hotels',
    dataType: 'multi-select',
    class: 'field col-12 required',
    validation: validateEntitiesForSelectMultiple('hotel').nonempty('The hotel field is required'),
  },
  {
    field: 'emailContact',
    header: 'Email Contact',
    dataType: 'textarea',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The email contact field is required').max(500, 'Maximum 500 characters')
  },
]

const item = ref<GenericObject>({
  manageAgency: props.agency ? props.agency : null,
  manageRegion: null,
  manageHotel: [],
  emailContact: ''
})

const itemTemp = ref<GenericObject>({
  manageAgency: props.agency ? props.agency : null,
  manageRegion: null,
  manageHotel: [],
  emailContact: ''
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})

// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------

const ENUM_FILTER = [
  { id: 'manageRegion', name: 'Region' },
]

const columns: IColumn[] = [
  { field: 'manageAgency', header: 'Agency', type: 'text', objApi: { moduleApi: 'settings', uriApi: 'manage-agency' }, showFilter: false, sortable: false },
  { field: 'manageRegion', header: 'Region', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-region' } },
  { field: 'emailContact', header: 'Email Contact', type: 'text' },
]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Manage Agency Contact',
  moduleApi: 'settings',
  uriApi: 'manage-agency-contact',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 20,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})
const pagination = ref<IPagination>({
  page: 0,
  limit: 20,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
// -------------------------------------------------------------------------------------------------------

const payloadRegion = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 200,
  page: 0,
  sortBy: 'name',
  sortType: ENUM_SHORT_TYPE.ASC
})
const payloadHotel = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 200,
  page: 0,
  sortBy: 'name',
  sortType: ENUM_SHORT_TYPE.ASC
})
// FUNCTIONS ---------------------------------------------------------------------------------------------
function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''
  fields[0].disabled = true
  formReload.value++
  formReload.value++
  if (!props.agency) {
    agencyList.value = []
  }
  else {
    item.value.manageAgency = props.agency
  }
  regionList.value = []
  hotelList.value = []
}

async function getList() {
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    listItems.value = []
    const newListItems = []

    const staticPayload = [{
      key: 'manageAgency.id',
      operator: 'EQUALS',
      value: item.value?.manageAgency?.id,
      logicalOperation: 'AND'
    }]
    payload.value.filter = [...payload.value.filter, ...staticPayload]

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'manageAgency')) {
        iterator.manageAgency = iterator.manageAgency.name
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

function searchAndFilter() {
  const newPayload: IQueryRequest = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.ASC
  }
  // newPayload.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    newPayload.filter = [{
      key: 'manageRegion.id',
      operator: 'LIKE',
      value: filterToSearch.value.search.id,
      logicalOperation: 'AND',
      type: 'filterSearch',
    }]
  }
  payload.value.filter = newPayload.filter // Asignar el nuevo payload al newPayload
  getList()
}

function clearFilterToSearch() {
  // payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value.criterial = ENUM_FILTER[0]
  filterToSearch.value.search = ''
  payload.value = {
    filter: [],
    query: '',
    pageSize: 20,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.DESC
  }

  getList()
}

async function resetListItems() {
  payload.value.page = 0
  getList()
}

async function getAgencyList(query: string) {
  try {
    const payload
        = {
          filter: [
            {
              key: 'name',
              operator: 'LIKE',
              value: query,
              logicalOperation: 'AND'
            },
          ],
          query: '',
          pageSize: 20,
          page: 0,
          sortBy: 'name',
          sortType: ENUM_SHORT_TYPE.DESC
        }

    const response = await GenericService.search('settings', 'manage-agency', payload)
    const { data: dataList } = response
    agencyList.value = []
    for (const iterator of dataList) {
      agencyList.value = [...agencyList.value, { id: iterator.id, name: iterator.name, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error on loading hotels list:', error)
  }
}

async function getRegionList(query: string = '') {
  try {
    payloadRegion.value = {
      filter: [
        {
          key: 'name',
          operator: 'LIKE',
          value: query,
          logicalOperation: 'AND'
        },
        {
          key: 'code',
          operator: 'LIKE',
          value: query,
          logicalOperation: 'OR'
        },
        {
          key: 'status',
          operator: 'EQUALS',
          value: 'ACTIVE',
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 200,
      page: 0,
      sortBy: 'name',
      sortType: ENUM_SHORT_TYPE.ASC
    }

    regionList.value = []
    const response = await GenericService.search(confRegionApi.moduleApi, confRegionApi.uriApi, payloadRegion.value)
    const { data: dataList } = response

    for (const iterator of dataList) {
      regionList.value = [...regionList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error on loading region list:', error)
  }
}

async function getHotelList(query: string = '') {
  try {
    payloadHotel.value = {
      filter: [
        {
          key: 'manageRegion.id',
          operator: 'EQUALS',
          value: regionSelected.value,
          logicalOperation: 'AND'
        },
        {
          key: 'status',
          operator: 'EQUALS',
          value: 'ACTIVE',
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 200,
      page: 0,
      sortBy: 'name',
      sortType: ENUM_SHORT_TYPE.ASC
    }
    if (query) {
      payloadHotel.value.filter.push({
        key: 'name',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'OR'
      }, {
        key: 'code',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'OR'
      },)
    }

    hotelList.value = []
    const response = await GenericService.search(confHotelApi.moduleApi, confHotelApi.uriApi, payloadHotel.value)
    const { data: dataList } = response
    hotelList.value = []
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error on loading hotel list:', error)
  }
}

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)
      if (response) {
        item.value.id = response.id
        item.value.manageAgency = response.manageAgency
        item.value.manageRegion = response.manageRegion
        regionSelected.value = response.manageRegion.id

        await getHotelList()
        item.value.manageHotel = response.manageHotel.map((hotel: any) => {
          return { id: hotel.id, name: `${hotel.code} - ${hotel.name}`, status: hotel.status }
        })
        item.value.emailContact = response.emailContact
      }
      fields[0].disabled = true
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Item could not be loaded', life: 3000 })
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
    payload.manageAgency = typeof payload.manageAgency === 'object' ? payload.manageAgency.id : payload.manageAgency
    payload.manageRegion = typeof payload.manageRegion === 'object' ? payload.manageRegion.id : payload.manageRegion
    payload.manageHotel = payload.manageHotel.map((p: any) => p.id)
    await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.manageAgency = typeof payload.manageAgency === 'object' ? payload.manageAgency.id : payload.manageAgency
  payload.manageRegion = typeof payload.manageRegion === 'object' ? payload.manageRegion.id : payload.manageRegion
  payload.manageHotel = payload.manageHotel.map((p: any) => p.id)
  await GenericService.update(confApi.moduleApi, confApi.uriApi, idItem.value || '', payload)
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
  let successOperation = true
  if (idItem.value) {
    try {
      await updateItem(item)
      idItem.value = ''
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  else {
    try {
      await createItem(item)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
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
    reject: () => {}
  })
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
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
  return !(filterToSearch.value.criterial || filterToSearch.value.search)
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
function initData() {
  if (props.agency) {
    // ENUM_FILTER = [{ id: 'code', name: 'Code' }, { id: 'name', name: 'Name' },]
    updateFieldProperty(fields, 'manageAgency', 'disabled', true)
    agencyList.value = [props.agency]
    item.value.manageHotel = props.agency
  }
}
// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
onMounted(async () => {
  initData()
  filterToSearch.value.criterial = ENUM_FILTER[0]
  await getRegionList()
  await getList()
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h3 class="mb-0">
      Manage Agency Contact
    </h3>
    <IfCan :perms="['AGENCY:CREATE']">
      <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true" class="my-2 flex justify-content-end px-0">
        <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" @click="clearForm" />
      </div>
    </IfCan>
  </div>
  <div class="grid">
    <div class="col-12 md:order-1 md:col-6 xl:col-9">
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
                    v-model="filterToSearch.criterial"
                    :options="[...ENUM_FILTER]"
                    option-label="name"
                    placeholder="Criteria"
                    return-object="false"
                    class="align-items-center w-full"
                    show-clear
                  />
                </div>
              </div>
              <div class="flex align-items-center gap-2">
                <label for="email">Search</label>
                <div class="w-full lg:w-auto">
                  <DebouncedAutoCompleteComponent
                    v-if="!loadingSaveAll"
                    id="autocomplete"
                    field="name"
                    item-value="id"
                    :model="filterToSearch.search"
                    :suggestions="regionList"
                    @change="($event) => {
                      filterToSearch.search = $event
                    }"
                    @load="($event) => getRegionList($event)"
                  />
                  <Skeleton v-else height="2rem" class="mb-2" />
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
    <div class="col-12 md:order-0 md:col-6 xl:col-3">
      <div>
        <div class="font-bold text-lg px-4 bg-primary custom-card-header">
          {{ formTitle }}
        </div>
        <div class="card p-4">
          <EditFormV2
            :key="formReload"
            :fields="fields"
            :item="item"
            :show-actions="true"
            :loading-save="loadingSaveAll"
            :loading-delete="loadingDelete"
            @cancel="clearForm"
            @delete="requireConfirmationToDelete($event)"
            @submit="requireConfirmationToSave($event)"
          >
            <template #field-manageAgency="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.manageAgency"
                :suggestions="agencyList"
                :disabled="fields[0].disabled"
                @change="($event) => onUpdate('manageAgency', $event)"
                @load="($event) => getAgencyList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-manageRegion="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                return-object="false"
                :model="data.manageRegion"
                :suggestions="regionList"
                @change="($event) => {
                  onUpdate('manageRegion', $event)
                  regionSelected = $event
                }"
                @load="($event) => getRegionList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-manageHotel="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :multiple="true"
                :model="data.manageHotel"
                :suggestions="[...hotelList]"
                @change="($event) => {
                  onUpdate('manageHotel', $event)
                }"
                @load="($event) => {
                  if (regionSelected) getHotelList($event)
                }"
              />

              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #form-footer="props">
              <div class="flex justify-content-end">
                <IfCan :perms="idItem ? ['AGENCY:EDIT'] : ['AGENCY:CREATE']">
                  <Button v-tooltip.top="'Save'" class="w-3rem mx-2" icon="pi pi-save" :loading="loadingSaveAll" @click="props.item.submitForm($event)" />
                </IfCan>
                <IfCan :perms="['AGENCY:DELETE']">
                  <Button v-tooltip.top="'Delete'" class="w-3rem" severity="danger" outlined :loading="loadingDelete" icon="pi pi-trash" @click="props.item.deleteItem($event)" />
                </IfCan>
              </div>
            </template>
          </EditFormV2>
        </div>
      </div>
    </div>
  </div>
</template>

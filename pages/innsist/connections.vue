<script setup lang="ts">
import { onMounted, ref } from 'vue'
import type { PageState } from 'primevue/paginator'
import { useToast } from 'primevue/usetoast'
import type { IData } from '~/components/table/interfaces/IModelData'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'

const props = defineProps({
  tradingCompanySelected: {
    type: Object,
    required: false
  },
})

const idItem = ref('')
const loadingSearch = ref(false)
const listItems = ref<any[]>([])
const payloadOnChangePage = ref<PageState>()
const idItemToLoadFirstTime = ref('')
const loadingSave = ref(false)
const formReload = ref(0)
const loadingDelete = ref(false)
const toast = useToast()
const listTradingCompanyItems = ref<any[]>([])
const innsistConnectionHasTradingCompany = ref(false)
const innsistConnectionOwnTradingCompany = ref(false)
// const confirm = useConfirm()

const options = ref({
  tableName: 'Manage Innsist Connections',
  moduleApi: 'innsist',
  uriApi: 'innsist-connection-params',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})

const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})

const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

// TABLE COLUMNS -----------------------------------------------------------------------------------------

const ENUM_FILTER = [
  { id: 'hostName', name: 'Host name' },
  { id: 'portNumber', name: 'Port' },
  { id: 'dataBaseName', name: 'Database name' },
]

const columns: IColumn[] = [
  { field: 'hostName', header: 'Server', type: 'text' },
  { field: 'portNumber', header: 'Port', type: 'text' },
  { field: 'dataBaseName', header: 'Database Name', type: 'text' },
  { field: 'status', header: 'Active', type: 'bool' },
]

const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'servername',
    header: 'Server name',
    dataType: 'text',
    class: 'field col-12 required',
    disabled: true,
  },
  {
    field: 'port',
    header: 'Port number',
    dataType: 'text',
    class: 'field col-12 required',
    disabled: true,
  },
  {
    field: 'databasename',
    header: 'Database name',
    dataType: 'text',
    class: 'field col-12 required',
    disabled: true,
  },
  {
    field: 'tradingCompany',
    header: 'Trading Company',
    dataType: 'text',
    class: 'field col-12 required',
    disabled: true,
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12 required',
    disabled: true,
  }
]

const item = ref<GenericObject>({
  id: '',
  servername: '',
  port: '',
  databasename: '',
  status: false,
  tradingCompany: null,
  description: ''
})

const tradingCompany = ref<GenericObject>({
  id: '',
  code: '',
  company: '',
  innsistCode: '',
  status: false,
  connectionId: ''
})

const showUnassingButton = computed(() => {
  if (innsistConnectionHasTradingCompany.value && innsistConnectionOwnTradingCompany.value) {
    return true
  }
  return false
})

const showAssingButton = computed(() => {
  return !innsistConnectionHasTradingCompany.value
})

// FUNCTIONS -----------------------------------------------------------------------------------------
function searchAndFilter() {
  if (!filterToSearch.value.criterial || filterToSearch.value.search.trim() === '') {
    toast.add({ severity: 'warn', summary: 'Alert', detail: 'Please fill out all the fields before searching.', life: 3000 })
    return
  }

  const newPayload: IQueryRequest = {
    filter: [],
    query: '',
    pageSize: 100,
    page: 0,
    sortBy: '',
    sortType: ENUM_SHORT_TYPE.ASC
  }
  // Mantener los filtros existentes
  newPayload.filter = [
    ...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')
  ]

  const statusList = ['ACTIVE']
  statusList.forEach((element: any) => {
    newPayload.filter.push({
      key: 'status',
      operator: 'EQUALS',
      value: element,
      logicalOperation: 'AND',
      type: 'filterSearch'
    })
  })

  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    newPayload.filter.push({
      key: filterToSearch.value.criterial ? filterToSearch.value.criterial.id : '',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'AND',
      type: 'filterSearch',
    })
  }

  payload.value = newPayload
  getList()
}

function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]

  // Reiniciar los valores de búsqueda a sus estados iniciales
  filterToSearch.value = {
    criterial: ENUM_FILTER[0],
    search: '',
  }
  listItems.value = []
  pagination.value.totalElements = 0
  getList()
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getList()
}

async function resetListItems() {
  payload.value.page = 0
  // getList()
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
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
      const responseCheck = await GenericService.getById(options.value.moduleApi, options.value.uriApi, iterator.id, 'innsist-connection-has-trading-company')
      if (!responseCheck.id) {
        // Verificar si el ID ya existe en la lista
        if (!existingIds.has(iterator.id)) {
          const newItem = {
            ...iterator,
            loadingEdit: false,
            loadingDelete: false,
          }

          if (iterator.id === idItem.value) {
            newListItems.unshift(newItem)
          }
          else {
            newListItems.push(newItem)
          }

          existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
        }
      }
      else {
        if (responseCheck.id === props.tradingCompanySelected?.id) {
          if (!existingIds.has(iterator.id)) {
            const newItem = {
              ...iterator,
              loadingEdit: false,
              loadingDelete: false,
            }

            if (iterator.id === idItem.value) {
              newListItems.unshift(newItem)
            }
            else {
              newListItems.push(newItem)
            }

            existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
          }
        }
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

async function getItemById(id: string) {
  if (id) {
    loadingSave.value = true
    try {
      const response = await GenericService.getById(options.value.moduleApi, options.value.uriApi, id)
      if (response) {
        item.value.id = response.id
        item.value.servername = response.hostName
        item.value.port = response.portNumber
        item.value.databasename = response.dataBaseName
        item.value.description = response.description
        item.value.status = statusToBoolean(response.status)

        // validar si esa conexion tiene algun trading company
        const responseCheck = await GenericService.getById(options.value.moduleApi, options.value.uriApi, id, 'innsist-connection-has-trading-company')
        if (responseCheck.id) {
          item.value.tradingCompany = {
            id: responseCheck.id,
            code: responseCheck.code,
            company: responseCheck.company,
            innsistCode: responseCheck.innsistCode,
            status: responseCheck.status
          }
          innsistConnectionHasTradingCompany.value = true
          listTradingCompanyItems.value[0] = item.value.tradingCompany

          if (responseCheck.id === props.tradingCompanySelected?.id) {
            innsistConnectionOwnTradingCompany.value = true
          }
          else {
            innsistConnectionOwnTradingCompany.value = false
          }
        }
        else {
          item.value.tradingCompany = null
          innsistConnectionHasTradingCompany.value = false
          listTradingCompanyItems.value = []
        }
      }

      fields[0].disabled = true
      updateFieldProperty(fields, 'status', 'disabled', false)
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: `Innsist Connection could not be loaded: ${error}`, life: 3000 })
      }
    }
    finally {
      loadingSave.value = false
    }
  }
}

async function assignItem() {
  loadingSave.value = true
  let successOperation = true

  const payload = {
    tradingCompanyId: tradingCompany.value.id,
    connectionParmId: item.value.id
  }

  try {
    await GenericService.assign(options.value.moduleApi, options.value.uriApi, payload)
    idItem.value = item.value.id
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
  }
  catch (error: any) {
    successOperation = false
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 3000 })
  }

  loadingSave.value = false
  if (successOperation) {
    clearForm()
    getList()
    tradingCompany.value.connectionId = item.value.id
  }
}

async function unassingItem() {
  loadingSave.value = true
  let successOperation = true

  const payload = {
    tradingCompanyId: tradingCompany.value.id
  }

  try {
    await GenericService.unassign(options.value.moduleApi, options.value.uriApi, payload)
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
  }
  catch (error: any) {
    successOperation = false
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 3000 })
  }

  loadingSave.value = false
  if (successOperation) {
    clearForm()
    getList()
    tradingCompany.value.connectionId = item.value.id
  }
}

function clearForm() {

}

// WATCH FUNCTIONS -------------------------------------------------------------------------------------
watch(() => idItemToLoadFirstTime.value, async (newValue) => {
  if (newValue) {
    await getItemById(newValue)
  }
})

onMounted(() => {
  if (useRuntimeConfig().public.loadTableData) {
    tradingCompany.value = {
      id: props.tradingCompanySelected?.id || '',
      code: props.tradingCompanySelected?.code || '',
      company: props.tradingCompanySelected?.name || '',
      innsistCode: props.tradingCompanySelected?.innsistCode || '',
      status: props.tradingCompanySelected?.status || false,
      connectionId: props.tradingCompanySelected?.connectionId || ''
    }

    if (props.tradingCompanySelected?.connectionId) {
      idItem.value = props.tradingCompanySelected?.connectionId
    }
    getList()
  }
})
</script>

<template>
  <div class="grid mt-0">
    <div class="col-12 md:order-1 md:col-12 xl:col-12">
      <div class="card p-0">
        <Accordion :active-index="0" class="mb-2">
          <AccordionTab>
            <template #header>
              <div class="text-white font-bold custom-accordion-header">
                Filters
              </div>
            </template>

            <div style="display: flex; height: 23%;" class="responsive-height">
              <div class="p-5 m-0 py-0 px-0 mt-0 ml-0" style="flex: 1;">
                <div class="flex justify-content-between align-items-center gap-4">
                  <div class="flex align-items-center w-full gap-2">
                    <label for="">Criteria</label>
                    <div class="w-full">
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
                  <div class="flex align-items-center w-full gap-2">
                    <label for="">Search</label>
                    <div class="w-full">
                      <IconField icon-position="left" class="w-full">
                        <InputText v-model="filterToSearch.search" type="text" placeholder="Search" class="w-full" />
                        <InputIcon class="pi pi-search" />
                      </IconField>
                    </div>
                  </div>
                  <div class="flex align-items-center">
                    <Button v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :loading="loadingSearch" @click="searchAndFilter" />
                    <Button v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash" :loading="loadingSearch" @click="clearFilterToSearch" />
                  </div>
                </div>
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
        @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems"
        @on-sort-field="onSortField"
        @update:clicked-item="getItemById($event)"
      />
    </div>

    <div class="col-12 order-1 md:order-0 md:col-12 xl:col-12">
      <div class="flex justify-content-end align-items-end">
        <Button v-tooltip.top="'Assign'" class="w-3rem mx-2" icon="pi pi-save" :loading="loadingSave" :disabled="!showAssingButton" @click="assignItem" />
        <Button v-tooltip.top="'Unassign'" outlined class="w-3rem" severity="danger" :disabled="!showUnassingButton" :loading="loadingDelete" icon="pi pi-trash" @click="unassingItem" />
      </div>
    </div>
  </div>
</template>

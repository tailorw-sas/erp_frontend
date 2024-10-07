<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'

import dayjs from 'dayjs'
import type { PageState } from 'primevue/paginator'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'

import type { IData } from '~/components/table/interfaces/IModelData'

const sendType = ref('')
const { data: userData } = useAuth()
const listItems = ref<any[]>([])
const tableRef = ref()
const route = useRoute()
const toast = useToast()
const startOfMonth = ref<any>(null)
const endOfMonth = ref<any>(null)
const filterAllDateRange = ref(false)
const loadingSearch = ref(false)
const loadingSaveAll = ref(false)

const allDefaultItem = { id: 'All', name: 'All', code: 'All' }

const filterToSearch = ref<IData>({
  criteria: null,
  search: '',
  allFromAndTo: false,
  agency: [],
  hotel: [],
  from: new Date(new Date().getFullYear(), new Date().getMonth(), 1),
  to: new Date(),
})

const clickedItem = ref<string[]>([])
const hotelList = ref<any[]>([])
const agencyList = ref<any[]>([])

const confApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
})

const confHotelApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})
const confAgencyApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})
const confSendApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice/send',
})

// VARIABLES -----------------------------------------------------------------------------------------

//
const idItem = ref('')
const ENUM_FILTER = [
  { id: 'invoiceId', name: 'Invoice Id' },
]
// -------------------------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'invoiceId', header: 'Invoice Id', type: 'text', width: '130px' },
  { field: 'manageHotelCode', header: 'Hotel', type: 'text' },
  { field: 'manageinvoiceCode', header: 'Invoice No', type: 'text', width: '130px' },
  { field: 'manageAgencyCode', header: 'Agency CD', type: 'text', width: '150px' },
  { field: 'manageAgencyName', header: 'Agency', type: 'text' },

  { field: 'invoiceDate', header: 'Generation Date', type: 'date', width: '150px' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text', width: '150px' },
  { field: 'status', header: 'Status', width: '100px', frozen: true, type: 'slot-select', localItems: ENUM_INVOICE_STATUS, sortable: false, showFilter: false },
  { field: 'sendStatusError', header: 'Sent Status', frozen: true, type: 'slot-text', sortable: false, showFilter: false, width: '250px' },
]

// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Send invoices',
  moduleApi: 'invoicing',
  uriApi: 'send',
  selectionMode: 'multiple',
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  showEdit: false,
  showAcctions: false,
  messageToDelete: 'Do you want to save the change?',
  showTitleBar: false
})

const type = route.query.type || ''
sendType.value = type === ENUM_INVOICE_SEND_TYPE.FTP
  ? 'Invoice to Send by FTP'
  : type === ENUM_INVOICE_SEND_TYPE.EMAIL
    ? 'Invoice to Send by Email'
    : type === ENUM_INVOICE_SEND_TYPE.BAVEL
      ? 'Invoice to Send by Bavel'
      : ''

const payload = ref<IQueryRequest>({
  filter: [

  ],
  query: '',
  pageSize: 10,
  page: 0,
  // sortBy: 'name',
  // sortType: ENUM_SHORT_TYPE.ASC
})

const payloadOnChangePage = ref<PageState>()
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------

async function getList() {
  try {
    // payload.value = { ...payload.value, query: idItem.value }
    const staticPayload = [{
      key: 'invoiceStatus',
      operator: 'IN',
      value: filterAllDateRange.value ? ['SENT'] : ['RECONCILED'],
      logicalOperation: 'AND'
    }, {
      key: 'agency.sentB2BPartner.b2bPartnerType.code',
      operator: 'EQUALS',
      value: type.toString(),
      logicalOperation: 'AND'
    }]
    payload.value.filter = [...payload.value.filter, ...staticPayload]

    listItems.value = []
    clickedItem.value = []
    const newListItems = []
    const response = await GenericService.search(confApi.moduleApi, confApi.uriApi, payload.value)
    const { data: dataList, page, size, totalElements, totalPages } = response
    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      if (!existingIds.has(iterator.id)) {
        let invoiceNumber
        if (iterator?.invoiceNumber?.split('-')?.length === 3) {
          invoiceNumber = `${iterator?.invoiceNumber?.split('-')[0]}-${iterator?.invoiceNumber?.split('-')[2]}`
        }
        else {
          invoiceNumber = iterator?.invoiceNumber
        }
        newListItems.push({
          ...iterator,
          loadingEdit: false,
          loadingDelete: false,
          invoiceDate: dayjs(iterator?.invoiceDate).format('YYYY-MM-DD'),
          agencyCd: iterator?.agency?.code,
          manageHotelCode: `${iterator?.hotel?.code}-${iterator?.hotel?.name}`,
          manageinvoiceCode: invoiceNumber.replace('OLD', 'CRE'),
          manageAgencyCode: iterator?.agency?.code,
          manageAgencyName: iterator?.agency?.name,
          dueAmount: iterator?.dueAmount || 0,
          invoiceNumber: invoiceNumber.replace('OLD', 'CRE'),
          status: iterator?.status,
          hotel: { ...iterator?.hotel, name: `${iterator?.hotel?.code || ''}-${iterator?.hotel?.name || ''}` }
        })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    listItems.value = [...listItems.value, ...newListItems]
  }
  catch (error) {
    console.error('Error loading file:', error)
  }
}

async function getHotelList(query: string = '') {
  try {
    const payload = {
      filter: [
        {
          key: 'name',
          operator: 'LIKE',
          value: query,
          logicalOperation: 'OR'
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
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confHotelApi.moduleApi, confHotelApi.uriApi, payload)
    const { data: dataList } = response
    hotelList.value = []
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getAgencyList(query: string = '') {
  try {
    const payload = {
      filter: [
        {
          key: 'name',
          operator: 'LIKE',
          value: query,
          logicalOperation: 'OR'
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
        },
        // {
        //   key: 'autoReconcile',
        //   operator: 'EQUALS',
        //   value: true,
        //   logicalOperation: 'AND'
        // }
        {
          key: 'sentB2BPartner.b2bPartnerType.code',
          operator: 'EQUALS',
          value: type.toString(),
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confAgencyApi.moduleApi, confAgencyApi.uriApi, payload)
    const { data: dataList } = response
    agencyList.value = []
    for (const iterator of dataList) {
      agencyList.value = [...agencyList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function clearForm() {
  await goToList()
}

async function resetListItems() {
  payload.value.page = 0
  getList()
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  if (parseFilter && parseFilter?.length > 0) {
    for (let i = 0; i < parseFilter?.length; i++) {
      if (parseFilter[i]?.key === 'manageAgencyCode') {
        parseFilter[i].key = 'agency.code'
      }
      if (parseFilter[i]?.key === 'manageAgencyName') {
        parseFilter[i].key = 'agency.name'
      }
      if (parseFilter[i]?.key === 'manageHotelCode') {
        parseFilter[i].key = 'hotel.code'
      }
      if (parseFilter[i]?.key === 'manageinvoiceCode') {
        parseFilter[i].key = 'invoiceNumber'
      }
    }
  }
  payload.value.filter = [...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    getList()
  }
}

function searchAndFilter() {
  payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.DESC
  }
  if (filterToSearch.value.criteria && filterToSearch.value.search) {
    // newPayload.filter = [{
    payload.value.filter = [...payload.value.filter, {
      key: filterToSearch.value.criteria ? filterToSearch.value.criteria.id : '',
      operator: 'EQUALS',
      value: filterToSearch.value.search,
      logicalOperation: 'AND',
      type: 'filterSearch'
    }]
  }
  else {
    payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
    // Date
    if (filterToSearch.value.from) {
      payload.value.filter = [...payload.value.filter, {
        key: 'invoiceDate',
        operator: 'GREATER_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.from).format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      }]
    }
    if (filterToSearch.value.to) {
      payload.value.filter = [...payload.value.filter, {
        key: 'invoiceDate',
        operator: 'LESS_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.to).format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      }]
    }
    if (filterToSearch.value.agency?.length > 0) {
      const filteredItems = filterToSearch.value.agency.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        payload.value.filter = [...payload.value.filter, {
          key: 'agency.id',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND',
          type: 'filterSearch'
        }]
      }
    }
    if (filterToSearch.value.hotel?.length > 0) {
      const filteredItems = filterToSearch.value.hotel.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        payload.value.filter = [...payload.value.filter, {
          key: 'hotel.id',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND',
          type: 'filterSearch'
        }]
      }
    }
  }
  // payload.value = { ...payload.value, ...newPayload }
  getList()
}

function clearFilterToSearch() {
  // payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.DESC
  }
  filterToSearch.value = {
    criteria: null,
    search: '',
    agency: [],
    hotel: [],
    from: new Date(new Date().getFullYear(), new Date().getMonth(), 1),
    to: new Date(),
  }
  filterAllDateRange.value = false
  filterToSearch.value.criteria = ENUM_FILTER[0]
  getList()
}

async function goToList() {
  await navigateTo('/invoice')
}

function getStatusBadgeBackgroundColor(code: string) {
  switch (code) {
    case 'PROCECSED': return '#FF8D00'
    case 'RECONCILED': return '#005FB7'
    case 'SENT': return '#006400'
    case 'CANCELED': return '#F90303'
    case 'PENDING': return '#686868'

    default:
      return '#686868'
  }
}

function getStatusName(code: string) {
  switch (code) {
    case 'PROCECSED': return 'Processed'

    case 'RECONCILED': return 'Reconciled'
    case 'SENT': return 'Sent'
    case 'CANCELED': return 'Canceled'
    case 'PENDING': return 'Pending'

    default:
      return ''
  }
}

async function onMultipleSelect(data: any) {
  clickedItem.value = []
  clickedItem.value = data
}

async function send() {
  loadingSaveAll.value = true
  options.value.loading = true
  let completed = false
  try {
    if (!clickedItem.value) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Please select at least one item', life: 10000 })
      return
    }
    const payload = {
      invoice: clickedItem.value,
      employee: userData?.value?.user?.userId
    }
    await GenericService.create(confSendApi.moduleApi, confSendApi.uriApi, payload)
    completed = true
    tableRef.value?.clearSelectedItems()
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Could not send invoices', life: 10000 })
  }
  finally {
    options.value.loading = false
    if (completed) {
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Invoices sent successfully', life: 10000 })
      if (clickedItem.value.length === listItems.value.length) {
        clickedItem.value = []
        navigateTo('/invoice')
      }
      else {
        clickedItem.value = []
        await getList()
      }
    }
  }

  loadingSaveAll.value = false // Opcional: Puedes manejar el estado de carga aquí
}

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criteria && filterToSearch.value.search)
  return false
})

watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10

  getList()
})

onMounted(async () => {
  filterToSearch.value.criteria = ENUM_FILTER[0]
  // loadInvoiceType()
  await getList()
})
</script>

<template>
  <div class="grid">
    <div class="col-12 order-0 w-full md:order-1 md:col-6 xl:col-9">
      <div class=" p-0">
        <Accordion :active-index="0" class="mb-2">
          <AccordionTab>
            <template #header>
              <div class="text-white font-bold custom-accordion-header flex justify-content-between w-full align-items-center">
                <div>
                  {{ sendType }}
                </div>
              </div>
            </template>

            <div class="grid">
              <div class="col-12 md:col-6 lg:col-3 flex pb-0">
                <div class="flex flex-column gap-2 w-full">
                  <div class="flex align-items-center gap-2 w-full" style=" z-index:5 ">
                    <label class="filter-label font-bold" for="">Agency:</label>
                    <div class="w-full" style=" z-index:5 ">
                      <DebouncedAutoCompleteComponent
                        v-if="!loadingSaveAll" id="autocomplete"
                        :multiple="true" class="w-full" field="name"
                        item-value="id" :model="filterToSearch.agency"
                        :suggestions="agencyList"
                        @load="($event) => getAgencyList($event)"
                        @change="($event) => {
                          filterToSearch.agency = $event.filter((element: any) => element?.id !== 'All')
                        }"
                      >
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <label class="filter-label font-bold ml-3" for="">Hotel:</label>
                    <div class="w-full">
                      <DebouncedAutoCompleteComponent
                        v-if="!loadingSaveAll" id="autocomplete"
                        :multiple="true" class="w-full" field="name"
                        item-value="id" :model="filterToSearch.hotel" :suggestions="hotelList"
                        @load="($event) => getHotelList($event)" @change="($event) => {
                          filterToSearch.hotel = $event.filter((element: any) => element?.id !== 'All')
                        }"
                      >
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                </div>
              </div>

              <div class="col-12 md:col-6 lg:col-2 flex pb-0">
                <div class="flex flex-column gap-2 w-full">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <label class="filter-label font-bold" for="">From:</label>
                    <div class="w-full" style=" z-index:5 ">
                      <Calendar
                        v-model="filterToSearch.from" date-format="yy-mm-dd" icon="pi pi-calendar-plus"
                        show-icon icon-display="input" class="w-full" :max-date="new Date()"
                      />
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2 ml-4">
                    <label class="filter-label font-bold" for="">To:</label>
                    <div class="w-full">
                      <Calendar
                        v-model="filterToSearch.to" date-format="yy-mm-dd" icon="pi pi-calendar-plus" show-icon
                        icon-display="input" class="w-full" :max-date="new Date()" :min-date="filterToSearch.from"
                      />
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-12 md:col-6 lg:col-3 flex pb-0">
                <div class="flex w-full">
                  <div class="flex flex-row w-full">
                    <div class="flex flex-column gap-2 w-full">
                      <div class="flex align-items-center gap-2" style=" z-index:5 ">
                        <label class="filter-label font-bold" for="">Criteria:</label>
                        <div class="w-full">
                          <Dropdown
                            v-model="filterToSearch.criteria" :options="[...ENUM_FILTER]" option-label="name"
                            placeholder="Criteria" return-object="false" class="align-items-center w-full" show-clear
                          />
                        </div>
                      </div>
                      <div class="flex align-items-center gap-2">
                        <label class="filter-label font-bold ml-1" for="">Search:</label>
                        <div class="w-full">
                          <IconField icon-position="left">
                            <InputText v-model="filterToSearch.search" type="text" style="width: 100% !important;" />
                            <InputIcon class="pi pi-search" />
                          </IconField>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="col-12 md:col-1 mt-6 w-auto">
                <div class="flex justify-content-end">
                  <Checkbox
                    v-model="filterAllDateRange"
                    binary
                    class="mr-2"
                    @change="() => {
                      console.log(filterAllDateRange);

                      if (!filterAllDateRange) {
                        filterToSearch.from = startOfMonth
                        filterToSearch.to = endOfMonth
                      }
                    }"
                  />
                  <label for="" class="mr-2 mt-1 font-bold">Re-Send</label>
                </div>
              </div>

              <div class="flex align-items-center ">
                <Button
                  v-tooltip.top="'Search'" class="w-3rem mx-2 " icon="pi pi-search" :disabled="disabledSearch"
                  :loading="loadingSearch" @click="searchAndFilter"
                />
                <Button
                  v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash"
                  :loading="loadingSearch" @click="clearFilterToSearch"
                />
              </div>
            </div>
          </AccordionTab>
        </Accordion>
      </div>

      <DynamicTable
        ref="tableRef"
        :data="listItems"
        :columns="columns"
        :options="options"
        :pagination="pagination"
        @on-confirm-create="clearForm"
        @update:clicked-item="onMultipleSelect"
        @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems"
        @on-sort-field="onSortField"
      >
        <template #column-sendStatusError="{ data }">
          <div id="fieldError" v-tooltip.bottom="data.sendStatusError" class="ellipsis-text">
            <span style="color: red;">{{ data.sendStatusError }}</span>
          </div>
        </template>

        <template #column-status="{ data }">
          <Badge
            :value="getStatusName(data?.status)"
            :style="`background-color: ${getStatusBadgeBackgroundColor(data?.status)}`"
          />
        </template>
      </DynamicTable>

      <div class="flex align-items-end justify-content-end">
        <Button v-tooltip.top="'Apply'" class="w-3rem mx-2" icon="pi pi-check" :disabled="listItems.length === 0 || clickedItem.length === 0" @click="send" />
        <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem p-button" icon="pi pi-times" @click="clearForm" />
      </div>
    </div>
  </div>
</template>

<style lang="scss">
.custom-file-upload {
  background-color: transparent !important;
  border: none !important;
  text-align: left !important;
}
.custom-width {
    width: 300px; /* Ajusta el ancho como desees, por ejemplo 300px, 50%, etc. */
}
.ellipsis-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
  max-width: 200px; /* Ajusta el ancho máximo según tus necesidades */
}
</style>

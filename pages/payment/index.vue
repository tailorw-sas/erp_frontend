<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import { statusToBoolean, statusToString } from '~/utils/helpers'
import type { IData } from '~/components/table/interfaces/IModelData'

// VARIABLES -----------------------------------------------------------------------------------------

const listItems = ref<any[]>([])
const clientItemsList = ref<any[]>([])
const agencyItemsList = ref<any[]>([])
const hotelItemsList = ref<any[]>([])

const idItemToLoadFirstTime = ref('')
const objApis = ref({
  client: { moduleApi: 'settings', uriApi: 'manage-client' },
  agency: { moduleApi: 'settings', uriApi: 'manage-agency' },
  hotel: { moduleApi: 'settings', uriApi: 'manage-hotel' },
})

const legend = ref(
  [
    {
      name: 'Transit',
      color: '#ff002b',
      colClass: 'pr-4',
    },
    {
      name: 'Cancelled',
      color: '#888888',
      colClass: 'pr-4',
    },
    {
      name: 'Confirmed',
      color: '#0c2bff',
      colClass: 'pr-4',
    },
    {
      name: 'Applied',
      color: '#00b816',
      colClass: '',
    },
  ]
)

const menu = ref()
const items = ref([
  {
    items: [
      {
        label: 'Payment',
        command: () => navigateTo('/payment/form'),
      },
      {
        label: 'Income',
      },
      {
        label: 'Group',
      }
    ]
  }
])

const itemFilter = ref<GenericObject>({
  client: null,
  agency: null,
  allClientAndAgency: false,
  hotel: '',
  status: [],
  from: '',
  to: '',
  allFromAndTo: false,
  criteria: null,
  value: '',
  type: '',
  payApplied: false,
  detail: false,
})

// -------------------------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'description', name: 'Description' },
]
const ENUM_FILTER_STATUS = [
  { id: 'TRANSIT', name: 'TRANSIT' },
  { id: 'CANCELLED', name: 'CANCELLED' },
  { id: 'CONFIRMED', name: 'CONFIRMED' },
  { id: 'APPLIED', name: 'APPLIED' },
]

const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})

// TABLE COLUMNS -----------------------------------------------------------------------------------------

const columns: IColumn[] = [
  { field: 'icon', header: '', width: '25px', type: 'icon', icon: 'pi pi-paperclip', sortable: false },
  { field: 'paymentId', header: 'ID', tooltip: 'Payment ID', width: '60px', type: 'text' },
  { field: 'paymentSource', header: 'P.Source', tooltip: 'Payment Source', width: '60px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-payment-source' } },
  { field: 'transactionDate', header: 'Trans. Date', tooltip: 'Transaction Date', width: '60px', type: 'date' },
  { field: 'paymentStatus', header: 'Status', width: '50px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-payment-status' } },
  { field: 'hotel', header: 'Hotel', width: '80px', widthTruncate: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-hotel' } },
  { field: 'client', header: 'Client', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-client' } },
  { field: 'agency', header: 'Agency Cd', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-agency' } },
  { field: 'agencyType', header: 'Agency Type', tooltip: 'Agency Type', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-agency-type' } },
  { field: 'bankAccount', header: 'Bank Acc', tooltip: 'Bank Account', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-bank-account', keyValue: 'accountNumber' } },
  { field: 'totalAmount', header: 'T.Amount', tooltip: 'Total Amount', width: '60px', type: 'text' },
  { field: 'depositBalance', header: 'D.Balance', tooltip: 'Deposit Balance', width: '60px', type: 'text' },
  { field: 'applied', header: 'Applied', tooltip: 'Applied', width: '60px', type: 'text' },
  { field: 'noApplied', header: 'Not Applied', tooltip: 'Not Applied', width: '60px', type: 'text' },
  { field: 'remark', header: 'Remark', width: '200px', type: 'text' },
  // { field: 'attachmentStatus', header: 'Attachment Status', width: '100px', type: 'select' },
  // { field: 'paymentAmount', header: 'Payment Amount', tooltip: 'Payment Amount', width: '200px', type: 'text' },
  // { field: 'paymentBalance', header: 'Payment Balance', width: '200px', type: 'text' },
  // { field: 'depositAmount', header: 'Deposit Amount', width: '200px', type: 'text' },
  // { field: 'otherDeductions', header: 'Other Deductions', width: '200px', type: 'text' },
  // { field: 'identified', header: 'Identified', width: '200px', type: 'text' },
  // { field: 'notIdentified', header: 'Not Identified', width: '200px', type: 'text' },
]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Manage Payment',
  moduleApi: 'payment',
  uriApi: 'payment',
  loading: false,
  actionsAsMenu: true,
  messageToDelete: 'Are you sure you want to delete the account type: {{name}}?'
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: 'DES'
})
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------

// Go To Create Form
function goToCreateForm() {
  navigateTo('/payment/create')
}
function goToForm(item: any) {
  navigateTo({ path: '/payment/form', query: { id: item.hasOwnProperty('id') ? item.id : item } })
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
  filterToSearch.value.criterial = null
  filterToSearch.value.search = ''
  getList()
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

function toggle(event) {
  menu.value.toggle(event)
}

async function getClientList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  clientItemsList.value = await getDataList(moduleApi, uriApi, [], queryObj)
}
async function getAgencyList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  agencyItemsList.value = await getDataList(moduleApi, uriApi, [], queryObj)
}
async function getHotelList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  hotelItemsList.value = await getDataList(moduleApi, uriApi, [], queryObj)
}

// -------------------------------------------------------------------------------------------------------

// WATCH FUNCTIONS -------------------------------------------------------------------------------------
watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10
  getList()
})

// -------------------------------------------------------------------------------------------------------

// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
onMounted(async () => {
  itemFilter.value.criteria = ENUM_FILTER[0]
  // if (useRuntimeConfig().public.loadTableData) {
  // }
  await getList()
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h3 class="mb-0">
      Payment Management
    </h3>
    <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true" class="my-2 flex justify-content-end px-0">
      <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" aria-haspopup="true" aria-controls="overlay_menu" @click="toggle" />
      <Menu id="overlay_menu" ref="menu" :model="items" :popup="true" />
    </div>
  </div>
  <div class="grid">
    <div class="col-12 order-0">
      <div class="card p-0 m-0">
        <Accordion :active-index="0">
          <AccordionTab>
            <template #header>
              <div class="text-white font-bold custom-accordion-header flex justify-content-between w-full align-items-center">
                <div>
                  Filters
                </div>
                <div>
                  <PaymentLegend :legend="legend" />
                </div>
              </div>
            </template>
            <div class="grid pt-4 pb-0">
              <!-- first filter -->
              <div class="col-12 md:col-2 align-items-center py-0 mr-2">
                <div class="grid align-items-center justify-content-center">
                  <div class="col-12 md:col-10">
                    <div class="flex align-items-center mb-2">
                      <label for="" class="mr-2 font-bold"> Client</label>
                      <div class="w-full">
                        <DebouncedAutoCompleteComponent
                          id="autocomplete"
                          field="name"
                          item-value="id"
                          class="w-full"
                          :model="itemFilter.client"
                          :suggestions="[...clientItemsList]"
                          @load="async($event) => {
                            const objQueryToSearch = {
                              query: $event,
                              keys: ['name', 'code'],
                            }
                            await getClientList(objApis.client.moduleApi, objApis.client.uriApi, objQueryToSearch)
                          }"
                        />
                      </div>
                    </div>
                    <div class="flex align-items-center">
                      <label for="" class="mr-2 font-bold"> Agency</label>
                      <div class="w-full">
                        <DebouncedAutoCompleteComponent
                          id="autocomplete"
                          field="name"
                          item-value="id"
                          class="w-full"
                          :model="itemFilter.agency"
                          :suggestions="[...agencyItemsList]"
                          @load="async($event) => {
                            const objQueryToSearch = {
                              query: $event,
                              keys: ['name', 'code'],
                            }
                            await getAgencyList(objApis.agency.moduleApi, objApis.agency.uriApi, objQueryToSearch)
                          }"
                        />
                      </div>
                    </div>
                  </div>

                  <div class="col-12 md:col-2 align-items-center">
                    <div class="flex align-items-center">
                      <Checkbox
                        id="allClientAndAgency"
                        v-model="itemFilter.allClientAndAgency"
                        :binary="true"
                      />
                      <label for="allClientAndAgency" class="ml-2"> All</label>
                    </div>
                  </div>
                </div>
              </div>

              <!-- second filter -->
              <div class="col-12 md:col-2 align-items-center py-0 mr-2">
                <div class="grid align-items-center justify-content-center">
                  <div class="col-12">
                    <div class="flex align-items-center mb-2">
                      <label for="" class="mr-2 font-bold"> Hotels</label>
                      <div class="w-full">
                        <DebouncedAutoCompleteComponent
                          id="autocomplete"
                          class="w-full"
                          field="name"
                          item-value="id"
                          :model="itemFilter.hotel"
                          :suggestions="[...hotelItemsList]"
                          @load="async($event) => {
                            const objQueryToSearch = {
                              query: $event,
                              keys: ['name', 'code'],
                            }
                            await getHotelList(objApis.hotel.moduleApi, objApis.hotel.uriApi, objQueryToSearch)
                          }"
                        />
                      </div>
                    </div>
                    <div class="flex align-items-center">
                      <label for="" class="mr-2 font-bold"> Status</label>
                      <div class="w-full">
                        <MultiSelect
                          v-model="itemFilter.status"
                          :options="[...ENUM_FILTER_STATUS]"
                          option-label="name"
                          placeholder="Status"
                          display="chip"
                          return-object="false"
                          class="align-items-center w-full"
                          show-clear
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- third filter -->
              <div class="col-12 md:col-2 align-items-center py-0 mr-2">
                <div class="grid align-items-center justify-content-center">
                  <div class="col-12 md:col-10">
                    <div class="flex align-items-center mb-2">
                      <label for="" class="mr-2 font-bold"> From</label>
                      <div class="w-full">
                        <Calendar id="from" v-model="itemFilter.from" class="w-full" date-format="dd/mm/yy" />
                      </div>
                    </div>
                    <div class="flex align-items-center">
                      <label for="" class="mr-2 font-bold" style="padding-right: 17px;"> To</label>
                      <div class="w-full">
                        <Calendar id="from" v-model="itemFilter.to" class="w-full" date-format="dd/mm/yy" />
                      </div>
                    </div>
                  </div>
                  <div class="col-12 md:col-2 align-items-center">
                    <div class="flex align-items-center">
                      <Checkbox
                        id="allFromAndTo"
                        v-model="itemFilter.allFromAndTo"
                        :binary="true"
                      />
                      <label for="allClientAndAgency" class="ml-2"> All</label>
                    </div>
                  </div>
                </div>
              </div>

              <!-- fourth filter -->
              <div class="col-12 md:col-2 align-items-center py-0">
                <div class="grid align-items-center justify-content-center">
                  <div class="col-12">
                    <div class="flex align-items-center mb-2">
                      <label for="" class="mr-2 font-bold"> Criteria</label>
                      <div class="w-full">
                        <Dropdown
                          v-model="itemFilter.criteria"
                          :options="[...ENUM_FILTER]"
                          option-label="name"
                          placeholder="Criteria"
                          return-object="false"
                          class="align-items-center w-full"
                          show-clear
                        />
                      </div>
                    </div>
                    <div class="flex align-items-center">
                      <label for="" class="mr-2 font-bold">Search</label>
                      <InputText v-model="itemFilter.value" type="text" placeholder="" class="w-full" />
                    </div>
                  </div>
                </div>
              </div>

              <!-- fifth filter -->
              <div class="col-12 md:col-2 align-items-center py-0">
                <div class="grid align-items-center justify-content-center">
                  <div class="col-12">
                    <div class="flex align-items-center mb-2">
                      <label for="" class="mr-2 font-bold"> Type</label>
                      <div class="w-full">
                        <Dropdown
                          v-model="itemFilter.type"
                          :options="[...ENUM_FILTER]"
                          option-label="name"
                          placeholder="Type"
                          return-object="false"
                          class="align-items-center w-full"
                          show-clear
                        />
                      </div>
                    </div>
                    <div class="flex align-items-center justify-content-between pl-6">
                      <div class="flex align-items-center">
                        <label for="payApplied" class="mr-2 font-bold"> Pay Applied</label>
                        <Checkbox
                          id="payApplied"
                          v-model="itemFilter.payApplied"
                          :binary="true"
                        />
                      </div>

                      <div class="flex align-items-center ml-2">
                        <label for="detail" class="mr-2 font-bold"> Details</label>
                        <Checkbox
                          id="detail"
                          v-model="itemFilter.detail"
                          :binary="true"
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Button filter -->
              <div class="col-12 md:col-1 flex align-items-center justify-content-center">
                <Button
                  label="Filter"
                  class="p-button-lg"
                  icon="pi pi-filter"
                  @click="searchAndFilter"
                />
              </div>
            </div>
          </AccordionTab>
        </Accordion>
      </div>
      <div v-if="false" class="card py-2 my-2 flex justify-content-between flex-column md:flex-row" style="border: 0.5px solid #e6e6e6;">
        <div class="text-xl font-bold flex align-items-center text-primary">
          Payment
        </div>
      </div>
      <DynamicTable

        :data="listItems"
        :columns="columns"
        :options="options"
        :pagination="pagination"
        @on-confirm-create="goToCreateForm"
        @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems"
        @on-sort-field="onSortField"
        @open-edit-dialog="goToForm($event)"
        @on-row-double-click="goToForm($event)"
      />
    </div>
  </div>
</template>

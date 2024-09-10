<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'

import dayjs from 'dayjs'
import type { PageState } from 'primevue/paginator'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'

import type { IData } from '~/components/table/interfaces/IModelData'

const senttype = ref('');
const toast = useToast()
const listItems = ref<any[]>([])
const route = useRoute()
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
  agency: [allDefaultItem],
  hotel: [allDefaultItem],
  from: new Date(new Date().getFullYear(), new Date().getMonth(), 1),
  to: new Date(),
})

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

// VARIABLES -----------------------------------------------------------------------------------------

//
const idItem = ref('')
const ENUM_FILTER = [
  { id: 'id', name: 'InvoiceId' },
]
// -------------------------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'invoiceId', header: 'Invoice Id', type: 'text' },
  { field: 'manageHotelCode', header: 'Hotel', type: 'text' },
  { field: 'manageAgencyCode', header: 'Invoice No', type: 'text' },
  { field: 'manageAgencyCode', header: 'Agency CD', type: 'text' },
  { field: 'bookingDate', header: 'Agency', type: 'text' },
  

 
  { field: 'fullName', header: 'Generation Date', type: 'date' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text' },
  { field: 'impstatus', header: 'Sent Status', type: 'slot-text' },
  { field: 'status', header: 'Status', type: 'text' },
]

// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Send invoices',
  moduleApi: 'invoicing',
  uriApi: 'send',
  loading: false,
  showDelete: false,
  selectionMode: 'multiple',
  showFilters: true,
  expandableRows: false,
  messageToDelete: 'Do you want to save the change?'
})

const payload = ref<IQueryRequest>({
  filter: [],
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

async function getErrorList() {
  try {
    payload.value = { ...payload.value, query: idItem.value }
    let rowError = ''
    listItems.value = []
    const newListItems = []
    const response = await GenericService.importSearch(confApi.moduleApi, confApi.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response.paginatedResponse

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      rowError = ''
      const rowExpandable = []
      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        for (const err of iterator.errorFields) {
          rowError += `- ${err.message} \n`
        }
        rowExpandable.push({ ...iterator.row })
        newListItems.push({ ...iterator.row, id: iterator.id, fullName: `${iterator.row?.firstName} ${iterator.row?.lastName}`, impSta: `Warning row ${iterator.rowNumber}: \n ${rowError}`, rowExpandable, loadingEdit: false, loadingDelete: false })
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
    hotelList.value = [allDefaultItem]
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
        {
          key: 'autoReconcile',
          operator: 'EQUALS',
          value: true,
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
    agencyList.value = [allDefaultItem]
    for (const iterator of dataList) {
      agencyList.value = [...agencyList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getAgency(query: string = '') {
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
        {
          key: 'autoReconcile',
          operator: 'EQUALS',
          value: true,
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
    console.log(response, 'Respuesta de la API');
    const { data: dataList } = response
    agencyList.value = [allDefaultItem]
    for (const iterator of dataList) {
      // Asegúrate de que sentB2BPartner sea una lista
      const b2bPartners = iterator.sentB2BPartner || [];
      const firstB2BPartner = b2bPartners.length > 0 ? b2bPartners[0].code : null;

      agencyList.value.push({
        id: iterator.id,
        name: iterator.name,
        code: iterator.code,
        b2bPartner: b2bPartners// Guarda solo el código del primer b2bPartner
      });
    }

    return agencyList.value; 
  } 
  catch (error) {
    console.error('Error loading hotel list:', error)
    return [];
  }
}

async function getTypeInvoiceSent(selectedInvoiceId:any) {
  try {
    // Paso 1: Obtener la factura usando su ID
    const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, selectedInvoiceId);

    if (response) {
      // Paso 2: Suponiendo que el campo 'agency' está presente en la respuesta
      const agency = response.agency;
      console.log(agency, 'Agencia');

      if (!agency) {
        console.error('No se encontró la agencia en la factura');
        senttype.value = 'No agency found';
        return;
      }

      // Paso 3: Obtener el código de la agencia
      const agencyCode = agency.code; // O cualquier otro identificador que uses para buscar la agencia

      // Llamar a getAgencyList para obtener la lista de agencias
      const agencyList = await getAgency(); // Obtener todas las agencias

      // Buscar el campo sentB2BPartner en la lista de agencias
      const foundAgency = agencyList.find(item => item.code === agencyCode);
console.log(foundAgency,'Encontrar agencia')
if (foundAgency) {
        const b2bPartner = foundAgency.b2bPartner; // Accede al b2bPartner
        const b2bPartnerTypeCode = b2bPartner ? b2bPartner.b2BPartnerType.code : null; // Obtener el código del tipo

        console.log(b2bPartnerTypeCode, 'tipo de envío');
        // Determinar el texto a mostrar según el valor de b2bPartnerCode
        if (b2bPartnerTypeCode) {
          switch (b2bPartnerTypeCode) {
            case 'FTP':
              senttype.value = 'Invoice to Send by FTP';
              break;
            case 'EML':
              senttype.value = 'Send by Email';
              break;
            case 'BVL':
              senttype.value = 'Send by Babel';
              break;
            default:
              senttype.value = 'Invoices to Send';
              break;
          }
        } else {
          console.error('No se encontró el método de envío (b2bPartner)');
          senttype.value = 'No sending method found';
        }
      } else {
        console.error('Agencia no encontrada en la lista');
        senttype.value = 'Invoices to Send';
      }
    } else {
      console.error('No se encontró la respuesta para el ID de la factura');
      senttype.value = 'Invoice not found';
    }
  } catch (error) {
    console.error('Error retrieving invoice type:', error);
    senttype.value = 'Error retrieving sending method';
  }
}


async function clearForm() {
  await goToList()
}

async function resetListItems() {
  payload.value.page = 0
  getErrorList()
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...parseFilter || []]
  getErrorList()
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    getErrorList()
  }
}

function searchAndFilter() {
  const newPayload: IQueryRequest = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.DESC
  }
  if (filterToSearch.value.criteria && filterToSearch.value.search) {
    newPayload.filter = [{
      key: filterToSearch.value.criteria ? filterToSearch.value.criteria.id : '',
      operator: 'EQUALS',
      value: filterToSearch.value.search,
      logicalOperation: 'AND',
      type: 'filterSearch'
    }]
  }
  else {
    newPayload.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
    // Date
    if (filterToSearch.value.from) {
      newPayload.filter = [...newPayload.filter, {
        key: 'checkIn',
        operator: 'GREATER_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.from).format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      }]
    }
    if (filterToSearch.value.to) {
      newPayload.filter = [...newPayload.filter, {
        key: 'checkIn',
        operator: 'LESS_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.to).format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      }]
    }
    if (filterToSearch.value.merchant?.length > 0) {
      const filteredItems = filterToSearch.value.agency.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        newPayload.filter = [...newPayload.filter, {
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
        newPayload.filter = [...newPayload.filter, {
          key: 'hotel.id',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND',
          type: 'filterSearch'
        }]
      }
    }
  }
  payload.value = newPayload
  getList()
}

function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value = {
    criteria: null,
    search: '',
    agency: [allDefaultItem],
    hotel: [allDefaultItem],
    from: new Date(new Date().getFullYear(), new Date().getMonth(), 1),
    to: new Date(),
  }
  filterToSearch.value.criterial = ENUM_FILTER[0]
  getList()
}

async function goToList() {
  await navigateTo('/invoice')
}

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criteria && filterToSearch.value.search)
  return false
})

watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10

  getErrorList()
})
async function loadInvoiceType() {
  await getTypeInvoiceSent(route.query.selected);
}
onMounted(async () => {
  filterToSearch.value.criterial = ENUM_FILTER[0]
  loadInvoiceType();
  // getErrorList()
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
                  {{ senttype }}
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
                        item-value="id" :model="filterToSearch.agency" :suggestions="agencyList"
                        @load="($event) => getAgencyList($event)" @change="($event) => {
                          if (!filterToSearch.agency.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                            filterToSearch.agency = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {
                            filterToSearch.agency = $event.filter((element: any) => element?.id !== 'All')
                          }
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
                          if (!filterToSearch.hotel.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                            filterToSearch.hotel = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {
                            filterToSearch.hotel = $event.filter((element: any) => element?.id !== 'All')
                          }
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

                <div class="col-12 md:col-1 mt-2 w-auto">
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
                    <label for="" class="mr-2 font-bold">All</label>
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
                            v-model="filterToSearch.criterial" :options="[...ENUM_FILTER]" option-label="name"
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
        :data="listItems"
        :columns="columns"
        :options="options"
        :pagination="pagination"
        @on-confirm-create="clearForm"
        @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems"
        @on-sort-field="onSortField"
      >
        <template #column-impSta="{ data }">
          <div id="fieldError" v-tooltip.bottom="data.impSta" class="ellipsis-text">
            <span style="color: red;">{{ data.impSta }}</span>
          </div>
        </template>

        <!-- <template #datatable-footer>
          <ColumnGroup type="footer" class="flex align-items-center font-bold font-500" style="font-weight: 700">
            <Row>
              <Column footer="Totals:" :colspan="6" footer-style="text-align:right; font-weight: 700" />

              <Column :colspan="8" />
            </Row>
          </ColumnGroup>
        </template> -->
      </DynamicTable>

      <div class="flex align-items-end justify-content-end">
        <Button v-tooltip.top="'Apply'" class="w-3rem mx-2" icon="pi pi-check" @click="clearForm" />
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
  max-width: 150px; /* Ajusta el ancho máximo según tus necesidades */
}
</style>

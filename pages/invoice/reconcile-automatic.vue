<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'
import { v4 as uuidv4 } from 'uuid'
import dayjs from 'dayjs'
import type { PageState } from 'primevue/paginator'
import { reject } from 'lodash'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'

import type { IData } from '~/components/table/interfaces/IModelData'

const emit = defineEmits(['close'])
const { data: userData } = useAuth()
const multiSelectLoading = ref({
  agency: false,
  hotel: false,
})
const idItemToLoadFirstTime = ref('')
const toast = useToast()
const listItems = ref<any[]>([])
const selectedElements = ref<string[]>([])
const inputFile = ref()
const startOfMonth = ref<any>(null)
const endOfMonth = ref<any>(null)
const filterAllDateRange = ref(false)
const loadingSearch = ref(false)
const fileUpload = ref()
const loadingSaveAll = ref(false)
const resultTable = ref()

const errorList = ref<any[]>([])
const reviewError = ref(false)

const filterToSearch = ref<IData>({
  criteria: null,
  search: '',
  allFromAndTo: false,
  agency: [],
  hotel: [],
  from: dayjs(new Date()).startOf('month').toDate(),
  to: dayjs(new Date()).endOf('month').toDate(),
})
const invList = ref<any[]>([])
const hotelList = ref<any[]>([])
const agencyList = ref<any[]>([])

const confApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice/import-reconcile-auto',
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
const importModel = ref({
  importFile: '',
  // totalAmount: 0,

})
//
const idItem = ref('')
const ENUM_FILTER = [
  { id: 'invoiceId', name: 'Invoice Id' },
]

const confhotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const confagencyListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})
// -------------------------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'invoiceId', header: 'Id', type: 'text', width: '6%' },
  { field: 'hotel', header: 'Hotel', type: 'select', objApi: confhotelListApi, width: '15%' },
  { field: 'invoiceNumber', header: 'Invoice. No', tooltip: 'Invoice Number', type: 'text', width: '8%' },
  { field: 'agency', header: 'Agency', type: 'select', objApi: confagencyListApi, width: '15%' },
  { field: 'invoiceDate', header: 'Gen.  Date', tooltip: 'Generation Date', type: 'date', width: '10%' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text', width: '10%' },
  { field: 'status', header: 'Status', width: '100px', frozen: true, type: 'slot-select', showFilter: false, localItems: ENUM_INVOICE_STATUS, sortable: true },
  { field: 'recStatus', header: 'Rec. Status', tooltip: 'Reconcile Status', type: 'text', width: '15%' },
  // { field: 'status', header: 'Status', width: '100px', frozen: true, type: 'slot-select', sortable: true , objApi: { moduleApi: 'settings', uriApi: 'manage-invoice-status'}},

]

// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Reconcile',
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
  loading: false,
  showDelete: false,
  selectionMode: 'multiple' as 'multiple' | 'single',
  showFilters: true,
  selectAllItemByDefault: false,
  expandableRows: false,
  showSelectedItems: true,
  messageToDelete: 'Do you want to save the change?'
})

const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'invoiceId',
  sortType: ENUM_SHORT_TYPE.ASC
})

const payloadOnChangePage = ref<PageState>()
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const uploadComplete = ref(false)
// -------------------------------------------------------------------------------------------------------
async function onMultipleSelect(data: any) {
  selectedElements.value = data
}
async function activeImport() {
  if (
    importModel.value.importFile !== ''

    && listItems.value.length === 0 // Verificar que la longitud de listItems sea mayor que cero
  ) {
    uploadComplete.value = false
  }
  else {
    uploadComplete.value = true
  }
}
async function onChangeFile(event: any) {
  if (event.target.files && event.target.files.length > 0) {
    inputFile.value = event.target.files[0]
    importModel.value.importFile = inputFile.value.name
    event.target.value = ''
    await activeImport()
  }
}
// FUNCTIONS ---------------------------------------------------------------------------------------------
async function getList() {
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    resultTable.value?.clearSelectedItems()
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    listItems.value = []
    const newListItems = []
    payload.value.filter = [...payload.value.filter, {
      key: 'invoiceStatus',
      operator: 'IN',
      value: ['PROCESSED'],
      logicalOperation: 'AND'
    }, {
      key: 'agency.autoReconcile',
      operator: 'EQUALS',
      value: true,
      logicalOperation: 'AND'
    }]

    // Agregar filtros de fecha solo si se especifican
    if (filterToSearch.value.from) {
      payload.value.filter.push({
        key: 'invoiceDate',
        operator: 'GREATER_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.from).startOf('day').format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      })
    }

    if (filterToSearch.value.to) {
      payload.value.filter.push({
        key: 'invoiceDate',
        operator: 'LESS_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.to).endOf('day').format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      })
    }

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        let invoiceNumber
        if (iterator?.invoiceNumber?.split('-')?.length === 3) {
          invoiceNumber = `${iterator?.invoiceNumber?.split('-')[0]}-${iterator?.invoiceNumber?.split('-')[2]}`
        }
        else {
          invoiceNumber = iterator?.invoiceNumber
        }

        let recStatus = ''
        const haveError = errorList.value.find(item => item.invoiceNo === iterator.id)
        if (haveError) {
          recStatus = haveError?.errorMessage
        }
        else {
          recStatus = ''
        }

        newListItems.push({
          ...iterator,
          loadingEdit: false,
          loadingDelete: false,
          // invoiceDate: new Date(iterator?.invoiceDate),
          agencyCd: iterator?.agency?.code,
          dueAmount: iterator?.dueAmount ? formatNumber(Number(iterator?.dueAmount)) : 0,
          invoiceAmount: iterator?.invoiceAmount ? formatNumber(Number(iterator?.invoiceAmount)) : 0,
          invoiceNumber: invoiceNumber ? invoiceNumber.replace('OLD', 'CRE') : '',

          hotel: { ...iterator?.hotel, name: `${iterator?.hotel?.code || ''}-${iterator?.hotel?.name || ''}` },
          agency: { ...iterator?.agency, name: `${iterator?.agency?.code || ''}-${iterator?.agency?.name || ''}` },
          recStatus: recStatus || '',
        })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    listItems.value = [...listItems.value, ...newListItems]
    // console.log('listItems', listItems.value)
    return listItems
  }

  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}

async function getErrorList() {
  try {
    const errorPayload
    = {
      filter: [],
      query: idItem.value,
      pageSize: 200,
      page: 0,
      sortBy: 'invoiceId',
      sortType: ENUM_SHORT_TYPE.ASC
    }

    let rowError = ''
    errorList.value = []
    const newListItems = []
    const response = await GenericService.importSearchAuto(options.value.moduleApi, options.value.uriApi, errorPayload)

    const { data: dataList } = response

    const existingIds = new Set(errorList.value.map(item => item.id))

    for (const iterator of dataList) {
      rowError = ''
      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        for (const err of iterator.errorFields) {
          rowError += `- ${err.message} \n`
        }

        // const datTemp = new Date(iterator.row.transactionDate)
        newListItems.push(
          {
            ...iterator.row,
            id: iterator.id,
            invoiceNo: iterator.invoiceId,
            errorMessage: `Warning: \n ${rowError}`,
            // errorMessage: `Warning row ${iterator.rowNumber}: \n ${rowError}`,
            loadingEdit: false,
            loadingDelete: false
          }
        )
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    errorList.value = [...errorList.value, ...newListItems]
    reviewError.value = true
  }
  catch (error) {
    console.error('Error loading file:', error)
    reviewError.value = false
  }
}

function ApplyImport2() {
  options.value.loading = true
  setTimeout(() => {
    options.value.loading = false
  }, 10000)
}

async function ApplyImport() {
  loadingSaveAll.value = true
  let successOperation = true
  let count = 0
  uploadComplete.value = true
  errorList.value = []
  try {
    options.value.loading = true
    if (!inputFile.value) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Please select a file', life: 10000 })
      return
    }
    const uuid = uuidv4()
    idItem.value = uuid
    let invoiceIdsTemp: string[] = []
    const file = await inputFile.value
    const formData = new FormData()
    const hasSomeObject = selectedElements.value.some(
      item => typeof item === 'object' || item === null || Array.isArray(item)
    )

    if (hasSomeObject) {
      for (const item of selectedElements.value) {
        if (typeof item === 'object' && item !== null) {
          invoiceIdsTemp.push(item.id)
        }
      }
    }
    else {
      invoiceIdsTemp = [...selectedElements.value]
    }
    count = invoiceIdsTemp.length
    formData.append('file', file)
    formData.append('invoiceIds', JSON.stringify(invoiceIdsTemp.toString()))
    formData.append('employee', userData?.value?.user?.name || '')
    formData.append('employeeId', userData?.value?.user?.userId || '')
    formData.append('importProcessId', uuid)
    await GenericService.importReconcileAuto(confApi.moduleApi, confApi.uriApi, formData)

    // if (response) {
    //   console.log(response)

    //   const { errorsResponse, totalInvoicesRec } = response
    //   if (errorsResponse && errorsResponse.length === 0) {
    //     if (totalInvoicesRec === 0) {
    //       getErrorList(errorsResponse)
    //     }
    //     else {
    //       navigateTo('/invoice')
    //       toast.add({
    //         severity: 'info',
    //         summary: 'Confirmed',
    //         detail: `The invoices have been reconciled successfully. Total invoices reconciled: ${totalInvoicesRec}`,
    //         life: 0
    //       })
    //     }
    //   }
    //   else if (errorsResponse && errorsResponse.length > 0) {
    //     if (totalInvoicesRec > 0) {
    //       toast.add({
    //         severity: 'info',
    //         summary: 'Confirmed',
    //         detail: `The invoices have been reconciled successfully. Total invoices reconciled: ${totalInvoicesRec}`,
    //         life: 0
    //       })
    //     }
    //     getErrorList(errorsResponse)
    //   }
    // }
  }
  catch (error: any) {
    successOperation = false
    uploadComplete.value = false
    options.value.loading = false
  }
  finally {
    loadingSaveAll.value = false
    options.value.loading = false
  }
  if (successOperation) {
    try {
      await validateStatusImport()

      await getErrorList()
      if (errorList.value.length > 0) {
        await getList()
      }
      else {
        navigateTo('/invoice')
        const successMessage = `The files were uploaded successfully, total attachments imported: ${count}!`
        toast.add({ severity: 'info', summary: 'Confirmed', detail: successMessage, life: 5000 })
        onClose()
      }
    }
    catch (error) {

    }
  }
}

function onClose() {
  emit('close')
}

async function validateStatusImport() {
  options.value.loading = true

  return new Promise<void>((resolve, reject) => {
    let status = 'RUNNING'

    const intervalID = setInterval(async () => {
      try {
        const response = await GenericService.getById(
          options.value.moduleApi,
          options.value.uriApi,
          idItem.value,
          'import-status-auto'
        )
        status = response.status
      }
      catch (error: any) {
        toast.add({
          severity: 'error',
          summary: 'Error',
          detail: error.data.data.error.errorMessage,
          life: 10000,
        })

        // Detiene el intervalo y rechaza la promesa
        clearInterval(intervalID)
        options.value.loading = false
        uploadComplete.value = false
        reject(error) // Rechaza la promesa con el error
        return // Asegura que no continúe la ejecución
      }

      if (status === 'FINISHED') {
        clearInterval(intervalID) // Detiene el intervalo
        options.value.loading = false // Detiene la carga
        resolve() // Resuelve la promesa
      }
    }, 10000)
  })
}

async function getInvoiceList(query: string = '') {
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
          key: 'status',
          operator: 'EQUALS',
          value: 'ACTIVE',
          logicalOperation: 'AND'
        },

      ],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search('settings', 'manage-invoice-status', payload)
    const { data: dataList } = response

    for (const iterator of dataList) {
      invList.value = [...invList.value, { id: iterator.id, name: iterator.name }]
    }
  }
  catch (error) {
    console.error('Error loading invoice list:', error)
  }
}

async function getHotelList(query: string = '') {
  try {
    multiSelectLoading.value.hotel = true
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

      ],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confhotelListApi.moduleApi, confhotelListApi.uriApi, payload)
    const { data: dataList } = response
    hotelList.value = []
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
  finally {
    multiSelectLoading.value.hotel = false
  }
}

async function getAgencyList(query: string) {
  try {
    multiSelectLoading.value.agency = true
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

    const response = await GenericService.search(confagencyListApi.moduleApi, confagencyListApi.uriApi, payload)
    const { data: dataList } = response
    agencyList.value = []
    for (const iterator of dataList) {
      agencyList.value = [...agencyList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading agency list:', error)
  }
  finally {
    multiSelectLoading.value.agency = false
  }
}

async function clearForm() {
  navigateTo('/invoice')
}

async function resetListItems() {
  payload.value.page = 0
  getList()
}
function getStatusName(code: string) {
  switch (code) {
    case 'PROCESSED': return 'Processed'

    case 'RECONCILED': return 'Reconciled'
    case 'SENT': return 'Sent'
    case 'CANCELED': return 'Cancelled'
    case 'PENDING': return 'Pending'

    default:
      return ''
  }
}
function getStatusBadgeBackgroundColor(code: string) {
  switch (code) {
    case 'PROCESSED': return '#FF8D00'
    case 'RECONCILED': return '#005FB7'
    case 'SENT': return '#006400'
    case 'CANCELED': return '#888888'
    case 'PENDING': return '#686868'

    default:
      return '#686868'
  }
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  if (parseFilter && parseFilter?.length > 0) {
    for (let i = 0; i < parseFilter?.length; i++) {
      /*   if (parseFilter[i]?.key === 'status') {
        parseFilter[i].key = 'invoiceStatus'
      }
*/

      if (parseFilter[i]?.key === 'invoiceNumber') {
        parseFilter[i].key = 'invoiceNumberPrefix'
      }
    }
  }

  payload.value.filter = [...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    if (event.sortField === 'hotel') {
      event.sortField = 'hotel.name'
    }
    if (event.sortField === 'agency') {
      event.sortField = 'agency.name'
    }
    if (event.sortField === 'invoiceNumber') {
      event.sortField = 'invoiceNumberPrefix'
    }
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    getList()
  }
}

async function searchAndFilter() {
  const newPayload: IQueryRequest = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.ASC
  }
  // Mantener los filtros existentes
  newPayload.filter = [
    ...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')
  ]
  // Filtro por el ID de la factura basado en el criterio seleccionado
  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    newPayload.filter.push({
      key: filterToSearch.value.criterial.id, // Utiliza el id del criterio seleccionado
      operator: 'LIKE', // Cambia a 'LIKE' si es necesario para tu búsqueda
      value: filterToSearch.value.search,
      logicalOperation: 'AND',
      type: 'filterSearch',
    })
  }

  // Filtros de rango de fechas usando 'from' y 'to'
  if (filterToSearch.value.from) {
    newPayload.filter.push({
      key: 'invoiceDate',
      operator: 'GREATER_THAN_OR_EQUAL_TO',
      value: dayjs(filterToSearch.value.from).startOf('day').format('YYYY-MM-DD'),
      logicalOperation: 'AND',
      type: 'filterSearch'
    })
  }
  if (filterToSearch.value.to) {
    newPayload.filter.push({
      key: 'invoiceDate',
      operator: 'LESS_THAN_OR_EQUAL_TO',
      value: dayjs(filterToSearch.value.to).endOf('day').format('YYYY-MM-DD'),
      logicalOperation: 'AND',
      type: 'filterSearch'
    })
  }
  // Filtro por invoiceType con valor INVOICE
  newPayload.filter.push({
    key: 'invoiceType',
    operator: 'EQUALS',
    value: 'INVOICE', // Valor específico para el filtro
    logicalOperation: 'AND',
    type: 'filterSearch',
  })

  // Filtrar agencias que tienen autoReconcile en true
  if (filterToSearch.value.agency?.length > 0) {
    const selectedAgencyIds = filterToSearch.value.agency
      .filter((item: any) => item?.id !== 'All')
      .map((item: any) => item?.id)

    if (selectedAgencyIds.length > 0) {
      newPayload.filter.push({
        key: 'agency.id',
        operator: 'IN',
        value: selectedAgencyIds,
        logicalOperation: 'AND',
        type: 'filterSearch'
      })
    }
  }

  // Siempre agregar el filtro para autoReconcile en true
  newPayload.filter.push({
    key: 'agency.autoReconcile',
    operator: 'EQUALS',
    value: true,
    logicalOperation: 'AND',
    type: 'filterSearch'
  })

  // Filtros de hoteles
  if (filterToSearch.value.hotel?.length > 0) {
    const selectedHotelIds = filterToSearch.value.hotel
      .filter((item: any) => item?.id !== 'All')
      .map((item: any) => item?.id)

    if (selectedHotelIds.length > 0) {
      newPayload.filter.push({
        key: 'hotel.id',
        operator: 'IN',
        value: selectedHotelIds,
        logicalOperation: 'AND',
        type: 'filterSearch'
      })
    }
  }

  // Incluir el filtro para el estado de la factura
  newPayload.filter.push({
    key: 'invoiceStatus',
    operator: 'IN',
    value: ['PROCESSED'], // Asegúrate de que esté correctamente escrito
    logicalOperation: 'AND'
  })

  payload.value = newPayload
  // Obtener la lista de facturas
  options.value.selectAllItemByDefault = true
  const dataList = await getList()

  // Seleccionar automáticamente todos los elementos retornados
  if (dataList && dataList.value.length > 0) {
    selectedElements.value = dataList.value // Llenar selectedElements con los elementos obtenidos
  }
  else {
    selectedElements.value = [] // Asegurarse de que esté vacío si no hay resultados
  }

  // // Verificar si no hay resultados
  if (!dataList || dataList.value.length === 0) {
    toast.add({
      severity: 'info',
      summary: 'Confirmed',
      detail: `No invoices available in processed status `,
      life: 1000 // Duración del toast en milisegundos
    })
  }
}

function clearFilterToSearch() {
  // Limpiar los filtros existentes
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]

  // Reiniciar los valores de búsqueda a sus estados iniciales
  filterToSearch.value = {
    criterial: ENUM_FILTER[0], // Mantener el primer elemento del enum como valor predeterminado
    search: '', // Dejar el campo de búsqueda en blanco
    agency: [], // Restablecer a valor predeterminado
    hotel: [], // Restablecer a valor predeterminado
    from: dayjs(new Date()).startOf('month').toDate(), // Limpiar el campo de fecha 'from'
    // to: dayjs(new Date()).startOf('month').toDate(), // Limpiar el campo de fecha 'to'
    to: dayjs(new Date()).endOf('month').toDate(),
  }
  listItems.value = []
  importModel.value.importFile = ''
  pagination.value.totalElements = 0
}

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criteria && filterToSearch.value.search)
  return false
})

watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 50

  getList()
})

onMounted(async () => {
  filterToSearch.value.criterial = ENUM_FILTER[0]

  getList()
})
</script>

<template>
  <div class="grid">
    <div class="col-12 order-0 w-full md:order-1 md:col-6 xl:col-9">
      <div class="mt-3">
        <!-- <Accordion :active-index="0" class="mb-2"> -->
        <AccordionTab>
          <!-- <template #header>
              <div
                class="text-white font-bold custom-accordion-header flex justify-content-between w-full align-items-center"
              >
                <div>
                  Invoice to Reconcile Automatic
                </div>
              </div>
            </template> -->

          <div class="grid">
            <div class="col-12 md:col-6 lg:col-3 flex pb-0">
              <div class="flex flex-column gap-2 w-full">
                <div class="flex align-items-center gap-2 w-full" style=" z-index:5 ">
                  <label class="filter-label font-bold" for="">Agency:</label>
                  <div class="w-full" style=" z-index:5 ">
                    <DebouncedMultiSelectComponent
                      id="autocomplete"
                      field="name"
                      item-value="id"
                      :model="filterToSearch.agency"
                      :suggestions="agencyList"
                      :loading="multiSelectLoading.agency"
                      @change="($event) => {
                        filterToSearch.agency = $event
                      }"
                      @load="($event) => getAgencyList($event)"
                    />

                    <!--  <DebouncedAutoCompleteComponent
                        id="autocomplete" :multiple="true"
                        class="w-full" field="name" item-value="id" :model="filterToSearch.agency"
                        :suggestions="agencyList" @load="($event) => getAgencyList($event)" @change="($event) => {
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
                      -->
                  </div>
                </div>
                <div class="flex align-items-center gap-2">
                  <label class="filter-label font-bold ml-3" for="">Hotel:</label>
                  <div class="w-full">
                    <DebouncedMultiSelectComponent
                      id="autocomplete"
                      field="name"
                      item-value="id"
                      :model="filterToSearch.hotel"
                      :suggestions="hotelList"
                      :loading="multiSelectLoading.hotel"
                      @change="($event) => {

                        filterToSearch.hotel = $event
                      }"
                      @load="($event) => getHotelList($event)"
                    />
                    <!-- <DebouncedAutoCompleteComponent
                        id="autocomplete" :multiple="true"
                        class="w-full" field="name" item-value="id" :model="filterToSearch.hotel"
                        :suggestions="hotelList" @load="($event) => getHotelList($event)" @change="($event) => {
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
                    -->
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
                      show-icon icon-display="input" class="w-full" :min-date="new Date(startOfMonth)" :max-date="filterToSearch.to ? new Date(filterToSearch.to) : new Date(endOfMonth)"
                    />
                  </div>
                </div>
                <div class="flex align-items-center gap-2 ml-4">
                  <label class="filter-label font-bold" for="">To:</label>
                  <div class="w-full">
                    <Calendar
                      v-model="filterToSearch.to" date-format="yy-mm-dd" icon="pi pi-calendar-plus" show-icon
                      icon-display="input" class="w-full" :min-date="filterToSearch.from ? new Date(filterToSearch.from) : new Date(startOfMonth)"
                    />
                  </div>
                </div>
              </div>
            </div>
            <div class="col-12 md:col-6 lg:col-3 flex pb-0 pr-2">
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
                      <label class="filter-label font-bold" for="">Search:</label>
                      <div class="w-full">
                        <InputText v-model="filterToSearch.search" type="text" style="width: 100% !important;" />
                      </div>
                    </div>
                  </div>
                  <div class="flex align-items-center mx-3">
                    <Button
                      v-tooltip.top="'Search'" class="w-3rem mx-2 " icon="pi pi-search"
                      :disabled="disabledSearch" :loading="options.loading" @click="searchAndFilter"
                    />
                    <Button
                      v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash"
                      :loading="loadingSearch" @click="clearFilterToSearch"
                    />
                  </div>
                </div>
              </div>
            </div>
            <div class="col-12 md:col-6 lg:col-3 flex pb-0 ml-8">
              <div class="flex w-full">
                <div class="flex flex-row w-full">
                  <div class="flex flex-column gap-2 w-full">
                    <div class="flex align-items-center gap-2 mt-3 ml-2">
                      <label class="w-18rem" for="">Import (XLS or XLSX)<span class="p-error">*</span>:
                      </label>
                      <div class="w-full">
                        <div class="p-inputgroup w-full">
                          <InputText
                            ref="fileUpload" v-model="importModel.importFile" placeholder="Choose file"
                            class="w-full" show-clear aria-describedby="inputtext-help"
                          />
                          <span class="p-inputgroup-addon p-0 m-0">
                            <Button
                              icon="pi pi-file-import" severity="secondary" :disabled="listItems.length === 0"
                              class="w-2rem h-2rem p-0 m-0" @click="fileUpload.click()"
                            />
                          </span>
                        </div>
                        <!-- <small id="username-help" style="color: #808080;">Select a file of type XLS or XLSX</small> -->
                        <input
                          ref="fileUpload" type="file" style="display: none;"
                          accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
                          @change="onChangeFile($event)"
                        >
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </AccordionTab>
        <!-- </Accordion> -->
      </div>
      <div class="mt-1">
        <DynamicTable
          ref="resultTable"
          :data="listItems"
          :columns="columns"
          :options="options"
          :pagination="pagination"
          @on-confirm-create="clearForm"
          @on-change-pagination="payloadOnChangePage = $event"
          @on-change-filter="parseDataTableFilter"
          @on-list-item="resetListItems"
          @on-sort-field="onSortField"
          @update:clicked-item="onMultipleSelect($event)"
        >
          <template #column-status="{ data: item }">
            <Badge
              :value="getStatusName(item?.status)"
              :style="`background-color: ${getStatusBadgeBackgroundColor(item.status)}`"
            />
          </template>

          <template #column-sendStatusError="{ data }">
            <div id="fieldError">
              <span v-tooltip.bottom="data.sendStatusError" style="color: red;">{{ data.sendStatusError }}</span>
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
      </div>
      <div class="flex align-items-end justify-content-end">
        <Button
          v-tooltip.top="'Apply'" class="w-3rem mx-2" icon="pi pi-check"
          :loading="options.loading"
          :disabled="!importModel.importFile || selectedElements.length === 0" @click="ApplyImport"
        />
        <!-- <Button
          v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem p-button" icon="pi pi-times"
          @click="clearForm"
        /> -->
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
  width: 300px;
  /* Ajusta el ancho como desees, por ejemplo 300px, 50%, etc. */
}

.ellipsis-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
  max-width: 150px;
  /* Ajusta el ancho máximo según tus necesidades */
}
</style>

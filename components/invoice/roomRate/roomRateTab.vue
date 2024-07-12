<script setup lang="ts">
import { ref, watch } from 'vue'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import type { PageState } from 'primevue/paginator'
import { v4 } from 'uuid'
import dayjs from 'dayjs'
import getUrlByImage from '~/composables/files'
import { ModulesService } from '~/services/modules-services'
import { GenericService } from '~/services/generic-services'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import type { GenericObject } from '~/types'
import type { IData } from '~/components/table/interfaces/IModelData'

const props = defineProps({
  isDialogOpen: {
    type: Boolean,
    required: true
  },
  forceUpdate: {
    type: Boolean,
    required: true
  },
  toggleForceUpdate: {
    type: Function as any,
    required: true,

  },
  selectedInvoice: {
    type: String,
    required: false
  },
  closeDialog: {
    type: Function as any,
    required: true
  },
  openDialog: {
    type: Function as any,
    required: true
  },

  selectedBooking: {
    type: String,
    required: true
  },
  openAdjustmentDialog: {
    type: Function as any,
    required: true
  },
  isCreationDialog: {
    type: Boolean,
    required: true
  },
  listItems: {
    type: Array,
    required: false
  },
  addItem: {
    type: Function as any,
    required: false
  },
  updateItem: {
    type: Function as any,
    required: false
  },
  showTotals: {
    type: Boolean,
    required: false,
    default: false
  },
  isTabView: {
    type: Boolean,
    default: false
  },

  isDetailView: {
    type: Boolean,
    required: false,
    default: false,
  },

})

const totalInvoiceAmount = ref<number>(0)
const totalHotelAmount = ref<number>(0)

const toast = useToast()
const loadingSaveAll = ref(false)
const confirm = useConfirm()
const ListItems = ref<any[]>([])
const formReload = ref(0)
const active = ref(0)
const forceSave = ref(false)
const LoadingSaveAll = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const loadingSearch = ref(false)
const loadingDelete = ref(false)
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})

const bookingList = ref<any[]>([])

const confbookingListApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-booking',
})

const dialogOpen = ref<boolean>(props.isDialogOpen)

async function openEditDialog(item: any) {
  props.openDialog()
  if (item?.id) {
    idItem.value = item?.id
    idItemToLoadFirstTime.value = item?.id
    await GetItemById(item?.id)
  }

  if (typeof item === 'string') {
    idItem.value = item
    idItemToLoadFirstTime.value = item
    await GetItemById(item)
  }
}

async function deleteRoomRateOption(item: any) {
  if (item?.id) {
    await deleteRoomRate(item?.id)
  }
}

const menuModel = ref([
  { label: 'Add Adjustment', command: props.openAdjustmentDialog },
  { label: 'Edit Room Rate', command: openEditDialog },

])

const Fields: Array<Container> = [
  {
    childs: [
      {
        field: 'room_rate_id',
        header: 'Id',
        dataType: 'text',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        disabled: true

      },

      {
        field: 'checkIn',
        header: 'Check In',
        dataType: 'date',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        validation: z.date({
          required_error: 'The Check In field is required',
          invalid_type_error: 'The Check In field is required',
        })

      },
      {
        field: 'checkOut',
        header: 'Check Out',
        dataType: 'date',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        validation: z.date({
          required_error: 'The Check Out field is required',
          invalid_type_error: 'The Check Out field is required',
        })

      },

      {
        field: 'invoiceAmount',
        header: 'Rate Amount',
        dataType: 'number',
        class: 'field col-12 required',
        headerClass: 'mb-1',

      },
      {
        field: 'roomNumber',
        header: 'Room Number',
        dataType: 'text',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        validation: z.string().refine((val: string) => {
          if (typeof val === 'number') {
            if (!(Number(val) < 0)) {
              return false
            }
          }
          return true
        }, { message: 'The Room Number field must not be negative' }).nullable()

      },

      {
        field: 'adults',
        header: 'Adults',
        dataType: 'number',
        class: 'field col-12 ',
        headerClass: 'mb-1',

      },
      {
        field: 'children',
        header: 'Children',
        dataType: 'number',
        class: 'field col-12 ',
        headerClass: 'mb-1',

      },
      {
        field: 'rateAdult',
        header: 'Rate Adult',
        dataType: 'number',
        class: 'field col-12 ',
        headerClass: 'mb-1',

      },
      {
        field: 'rateChild',
        header: 'Rate Child',
        dataType: 'number',
        class: 'field col-12 ',
        headerClass: 'mb-1',

      },

      {
        field: 'hotelAmount',
        header: 'Hotel Amount',
        dataType: 'number',
        class: 'field col-12 ',
        headerClass: 'mb-1',

      },
      {
        field: 'remark',
        header: 'Description',
        dataType: 'textarea',
        class: 'field col-12 ',
        headerClass: 'mb-1',

      }

    ],
    containerClass: 'flex flex-column w-full'
  },

]

const item = ref<GenericObject>({
  room_rate_id: '',
  checkIn: new Date(),
  checkOut: new Date(),
  invoiceAmount: 0,
  roomNumber: 0,
  adults: 0,
  children: 0,
  rateAdult: 0,
  rateChild: 0,
  hotelAmount: 0,
  remark: '',
  booking: props.selectedBooking,
})

const itemTemp = ref<GenericObject>({
  room_rate_id: '',
  checkIn: new Date(),
  checkOut: new Date(),
  invoiceAmount: 0,
  roomNumber: 0,
  adults: 0,
  children: 0,
  rateAdult: 0,
  rateChild: 0,
  hotelAmount: 0,
  remark: '',
  booking: '',
})

const confApi = reactive({
  roomrate: {
    moduleApi: 'invoicing',
    uriApi: 'manage-room-rate',
  },

  invoice: {
    moduleApi: 'invoicing',
    uriApi: 'manage-invoice',
  },
})

const confroomTypeApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-room-type',
  keyValue: 'name'
})
const confAgencyApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
  keyValue: 'name'
})
const confnightTypeApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-night-type',
  keyValue: 'name'
})
const confratePlanApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-rate-plan',
  keyValue: 'name'
})

const Columns: IColumn[] = [
  { field: 'room_rate_id', header: 'Id', type: 'text' },

  { field: 'fullName', header: 'Full Name', type: 'text' },

  { field: 'checkIn', header: 'Check In', type: 'date' },
  { field: 'checkOut', header: 'Check Out', type: 'date' },
  { field: 'adults', header: 'Adults', type: 'text' },
  { field: 'children', header: 'Children', type: 'text' },
  { field: 'roomType', header: 'Room Type', type: 'select', objApi: confAgencyApi },
  { field: 'nights', header: 'Nights', type: 'text' },
  { field: 'ratePlan', header: 'Rate Plan', type: 'select', objApi: confratePlanApi },
  { field: 'hotelAmount', header: 'Hotel Amount', type: 'select' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text' },

]

const finalColumns = ref<IColumn[]>(Columns)

const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'name', name: 'Name' },
  { id: 'description', name: 'Description' },
]

const Options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-room-rate',
  loading: false,
  showDelete: false,
  showAcctions: !props.isDetailView,
  showEdit: false,
  showFilters: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?',
  showContextMenu: !props.isDetailView,
  menuModel
})

const PayloadOnChangePage = ref<PageState>()
const Payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'code',
  sortType: 'ASC'
})
const Pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

// FUNCTIONS ---------------------------------------------------------------------------------------------
function ClearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''
  formReload.value++
}

const errorInTab = ref({
  tabGeneralData: false,
  tabMedicalInfo: false,
  tabServices: false,
  tabPermissions: false
})

const OpenCreateDialog = async () => await navigateTo({ path: 'invoice/create' })

const OpenEditDialog = async (item: any) => await navigateTo({ path: `invoice/edit/${item}` })

async function getRoomRateList() {
  try {
    idItemToLoadFirstTime.value = ''
    Options.value.loading = true
    ListItems.value = []

    const response = await GenericService.search(Options.value.moduleApi, Options.value.uriApi, Payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    let countRR = 0
    for (const iterator of dataList) {
      countRR++

      ListItems.value = [...ListItems.value, {
        ...iterator,
        checkIn: new Date(iterator?.checkIn),
        checkOut: new Date(iterator?.checkOut),
        nights: dayjs(iterator?.checkOut).diff(dayjs(iterator?.checkIn), 'day', false),
        room_rate_id: iterator?.room_rate_id ? +iterator?.room_rate_id + +iterator?.booking?.booking_id : countRR + +iterator?.booking?.booking_id,
        loadingEdit: false,
        loadingDelete: false,
        fullName: `${iterator.booking.fistName ?? ''} ${iterator.booking.lastName ?? ''}`,
        booking_id: iterator.booking.booking_id,
        roomType: iterator.booking.roomType,
        nightType: iterator.booking.nightType,
        ratePlan: iterator.booking.ratePlan,
        agency: iterator?.booking?.invoice?.agency
      }]

      if (typeof +iterator.invoiceAmount === 'number') {
        totalInvoiceAmount.value += +iterator.invoiceAmount
      }

      if (typeof +iterator.hotelAmount === 'number') {
        totalHotelAmount.value += +iterator.hotelAmount
      }
    }
    if (ListItems.value.length > 0) {
      idItemToLoadFirstTime.value = ListItems.value[0].id
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    Options.value.loading = false
  }
}

const goToList = async () => await navigateTo('/invoice')

async function ResetListItems() {
  Payload.value.page = 0
  getRoomRateList()
}

function searchAndFilter() {
  Payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: 'DES'
  }
  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    Payload.value.filter = [...Payload.value.filter, {
      key: filterToSearch.value.criterial ? filterToSearch.value.criterial.id : '',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'AND'
    }]
  }
  getRoomRateList()
}

function clearFilterToSearch() {
  Payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: 'DES'
  }
  filterToSearch.value.criterial = ENUM_FILTER[0]
  filterToSearch.value.search = ''
  getRoomRateList()
}
async function GetItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true

    if (props.isCreationDialog) {
      // @ts-expect-error
      const element: any = props.listItems.find((item: any) => item.id === id)
      item.value.id = element.id
      item.value.room_rate_id = element.room_rate_id
      item.value.checkIn = new Date(element.checkIn)
      item.value.checkOut = new Date(element.checkOut)
      item.value.invoiceAmount = element.invoiceAmount ? element.invoiceAmount : 0
      item.value.roomNumber = element.roomNumber
      item.value.adults = element.adults
      item.value.children = element.children
      item.value.rateAdult = element.rateAdult
      item.value.rateChild = element.rateChild
      item.value.hotelAmount = element.hotelAmount ? element.hotelAmount : 0
      item.value.remark = element.remark
      item.value.booking = element.booking
    }

    try {
      const response = await GenericService.getById(confApi.roomrate.moduleApi, confApi.roomrate.uriApi, id)
      if (response) {
        item.value.id = response.id
        item.value.room_rate_id = response.room_rate_id
        item.value.checkIn = new Date(response.checkIn)
        item.value.checkOut = new Date(response.checkOut)
        item.value.invoiceAmount = response.invoiceAmount ? response.invoiceAmount : 0
        item.value.roomNumber = response.roomNumber
        item.value.adults = response.adults
        item.value.children = response.children
        item.value.rateAdult = response.rateAdult
        item.value.rateChild = response.rateChild
        item.value.hotelAmount = response.hotelAmount ? response.hotelAmount : 0
        item.value.remark = response.remark
        item.value.booking = response.booking
      }
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Invoice could not be loaded', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}

async function createRoomRate(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }

    await GenericService.create(confApi.roomrate.moduleApi, confApi.roomrate.uriApi, payload)
  }
}

async function updateRoomRate(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }

  await GenericService.update(confApi.roomrate.moduleApi, confApi.roomrate.uriApi, idItem.value || '', payload)
}

async function deleteRoomRate(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(Options.value.moduleApi, Options.value.uriApi, id)
    ClearForm()
    getRoomRateList()
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Could not delete invoice', life: 3000 })
    loadingDelete.value = false
  }
  finally {
    loadingDelete.value = false
  }
}

async function saveRoomRate(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  let successOperation = true
  item.booking = props.selectedBooking
  if (idItem.value || item?.id) {
    try {
      if (!item?.id) {
        item.id = idItem.value
      }

      if (props?.isCreationDialog) {
        props.updateItem(item)
      }
      else {
        await updateRoomRate(item)
      }

      props.closeDialog()
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
    idItem.value = ''
  }
  else {
    try {
      if (props?.isCreationDialog) {
        item.id = v4()
        props.addItem(item)
      }
      else {
        await createRoomRate(item)
      }

      props.closeDialog()
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  if (successOperation) {
    ClearForm()
    if (!props.isCreationDialog) {
      getRoomRateList()
    }
  }
}

function requireConfirmationToSaveRoomRate(item: any) {
  const { event } = item
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      saveRoomRate(item)
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}

function requireConfirmationToDeleteRoomRate(event: any) {
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    acceptClass: 'p-button-danger',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      deleteRoomRate(idItem.value)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}

async function ParseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, Columns)
  Payload.value.filter = [...parseFilter || []]
  getRoomRateList()
}

function OnSortField(event: any) {
  if (event) {
    Payload.value.sortBy = event.sortField
    Payload.value.sortType = event.sortOrder
    ParseDataTableFilter(event.filter)
  }
}

watch(() => props.forceUpdate, () => {
  if (props.forceUpdate) {
    getRoomRateList()
    props.toggleForceUpdate()
  }
})

onMounted(() => {
  if (props.selectedInvoice) {
    Payload.value.filter = [{
      key: 'booking.invoice.id',
      operator: 'EQUALS',
      value: props.selectedInvoice,
      logicalOperation: 'AND'
    }]
  }

  if (props.isDetailView) {
    finalColumns.value = [
      { field: 'room_rate_id', header: 'Id', type: 'text' },
      { field: 'booking_id', header: 'Booking id', type: 'text' },
      { field: 'fullName', header: 'Full Name', type: 'text' },
      { field: 'checkIn', header: 'Check In', type: 'date' },
      { field: 'checkOut', header: 'Check Out', type: 'date' },
      { field: 'nights', header: 'Nights', type: 'date' },
      { field: 'adults', header: 'Adult', type: 'text' },
      { field: 'children', header: 'Children', type: 'text' },
      { field: 'roomType', header: 'Room Type', type: 'select' },
      { field: 'ratePlan', header: 'Rate Plan', type: 'select' },
      { field: 'hotelAmount', header: 'Hotel Amount', type: 'text' },
      { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text' },

    ]
  }

  if (!props.isCreationDialog) {
    getRoomRateList()
  }
})
</script>

<template>
  <div>
    <DynamicTableWithContextMenu
      :data="isCreationDialog ? listItems as any : ListItems" :columns="finalColumns"
      :options="Options" :pagination="Pagination" @on-confirm-create="ClearForm"
      @open-edit-dialog="OpenEditDialog($event)"
      @on-change-pagination="PayloadOnChangePage = $event" @on-change-filter="ParseDataTableFilter"
      @on-list-item="ResetListItems" @on-sort-field="OnSortField" @update:double-clicked="($event) => {

        openEditDialog({ id: $event })

      }"
    >
      <template v-if="!isDetailView" #row-selector-body="itemProps">
        <span class="pi pi-pen-to-square" @click="openEditDialog(itemProps.item)" />
      </template>

      <template v-if="showTotals" #pagination-right="props">
        <div class="flex align-items-center  justify-content-end pr-4 rounded-full" style="height: 10px;">
          <Badge class="px-2 mr-8 flex align-items-center text-xs text-white" severity="contrast">
            <span class="font-bold">

              Total hotel amount: ${{ totalHotelAmount }}
            </span>
          </Badge>
          <Badge class="px-2 mx-8  flex align-items-center text-xs text-white" severity="contrast">
            <span class="font-bold">
              Total invoice amount: ${{ totalInvoiceAmount }}
            </span>
          </Badge>
        </div>
      </template>
    </DynamicTableWithContextMenu>
  </div>

  <div v-if="isDialogOpen">
    <RoomRateDialog
      :fields="Fields" :item="item" :open-dialog="isDialogOpen" :form-reload="formReload"
      :loading-save-all="loadingSaveAll" :clear-form="ClearForm"
      :require-confirmation-to-save="requireConfirmationToSaveRoomRate"
      :require-confirmation-to-delete="requireConfirmationToDeleteRoomRate" :header="isCreationDialog || !idItem ? 'New Room Rate' : 'Update Room Rate'"
      :close-dialog="closeDialog" container-class="flex flex-row justify-content-between mx-4 my-2 w-full"
      class="h-fit p-2 overflow-y-hidden" content-class="w-full h-full" :booking-list="bookingList"
    />
  </div>
</template>

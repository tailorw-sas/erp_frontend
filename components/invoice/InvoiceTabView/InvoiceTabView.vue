<script setup lang="ts">
import { ref, watch } from 'vue'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import type { PageState } from 'primevue/paginator'
import getUrlByImage from '~/composables/files'
import { ModulesService } from '~/services/modules-services'
import { GenericService } from '~/services/generic-services'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import type { GenericObject } from '~/types'
import type { IData } from '~/components/table/interfaces/IModelData'

const props = defineProps({
  selectedInvoice: {
    type: String,
    default: '',
    required: true
  }
})

const toast = useToast()

const forceSave = ref(false)
const forceUpdate = ref(false)
const active = ref(0)

const selectedBooking = ref<string>('')
const selectedRoomRate = ref<string>('')

const loadingSaveAll = ref(false)
const loadingDelete = ref(false)

const bookingDialogOpen = ref<boolean>(false)
const roomRateDialogOpen = ref<boolean>(false)
const adjustmentDialogOpen = ref<boolean>(false)

const errorInTab = ref({
  tabGeneralData: false,
  tabMedicalInfo: false,
  tabServices: false,
  tabPermissions: false
})

const hotelList = ref<any[]>([])
const agencyList = ref<any[]>([])
const invoiceTypeList = ref<any[]>([])

const confhotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const confagencyListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})

const confinvoiceTypeListtApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-invoice-type',
})

// VARIABLES -----------------------------------------------------------------------------------------

//
const confirm = useConfirm()
const ListItems = ref<any[]>([])
const formReload = ref(0)

const LoadingSaveAll = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const loadingSearch = ref(false)
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})

const item = ref<GenericObject>({
  invoice_id: '',
  invoiceNumber: '',
  invoiceDate: new Date(),
  isManual: true,
  invoiceAmount: '0.00',
  hotel: null,
  agency: null,
  invoiceType: null,
})

const itemTemp = ref<GenericObject>({
  invoice_id: '',
  invoiceNumber: '',
  invoiceDate: new Date(),
  isManual: true,
  invoiceAmount: '0.00',
  hotel: null,
  agency: null,
  invoiceType: null,
})

const formTitle = computed(() => {
  return idItem.value ? 'Edit' : 'Create'
})

// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const Columns: IColumn[] = [
  { field: 'invoice.invoice_id', header: 'Id', type: 'text' },
  { field: 'agency', header: 'Agency', type: 'select' },
  { field: 'firstName', header: 'First Name', type: 'text' },
  { field: 'lastName', header: 'Last Name', type: 'text' },
  { field: 'roomType', header: 'Room Type', type: 'select' },
  { field: 'checkIn', header: 'Check In', type: 'date' },
  { field: 'checkOut', header: 'Check Out', type: 'date' },
  { field: 'nightType', header: 'Night Type', type: 'select' },
  { field: 'ratePlan', header: 'Rate Plan', type: 'select' },
  { field: 'hotelAmount', header: 'Hotel Amount', type: 'select' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text' },

]
// -------------------------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'name', name: 'Name' },
  { id: 'description', name: 'Description' },
]
// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})

// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------

function loadInvoice(id: string) {
  idItem.value = id
  idItemToLoadFirstTime.value = id
  forceSave.value = true
}

function handleDialogOpen() {
  console.log(active)
  switch (active.value) {
    case 0:
      bookingDialogOpen.value = true
      break

    case 1:
      roomRateDialogOpen.value = true
      break

    case 2:
      adjustmentDialogOpen.value = true
      break

    default:
      break
  }
}

async function getHotelList() {
  try {
    const payload
      = {
        filter: [],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: 'DES'
      }

    hotelList.value = []
    const response = await GenericService.search(confhotelListApi.moduleApi, confhotelListApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getAgencyList() {
  try {
    const payload
      = {
        filter: [],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: 'DES'
      }

    agencyList.value = []
    const response = await GenericService.search(confagencyListApi.moduleApi, confagencyListApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      agencyList.value = [...agencyList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading agency list:', error)
  }
}

async function getInvoiceTypeList() {
  try {
    const payload
      = {
        filter: [],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: 'DES'
      }

    invoiceTypeList.value = []
    const response = await GenericService.search(confinvoiceTypeListtApi.moduleApi, confinvoiceTypeListtApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      invoiceTypeList.value = [...invoiceTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''

  formReload.value++
}

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(options.value.moduleApi, options.value.uriApi, id)

      if (response) {
        item.value.id = response.id
        item.value.invoice_id = response.invoice_id
        item.value.invoiceNumber = response.invoiceNumber
        item.value.invoiceDate = new Date(response.invoiceDate)
        item.value.isManual = response.isManual
        item.value.invoiceAmount = response.invoiceAmount
        item.value.hotel = response.hotel
        item.value.agency = response.agency
        item.value.invoiceType = response.invoiceType
      }

      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Invoice methods could not be loaded', life: 3000 })
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

    payload.invoice_id = item.invoice_id
    payload.invoiceNumber = item.invoiceNumber
    payload.invoiceDate = item.invoiceDate
    payload.isManual = item.isManual
    payload.invoiceAmount = 0.00
    payload.hotel = item.hotel.id
    payload.agency = item.agency.id
    payload.invoiceType = item.invoiceType.id
    await GenericService.create(options.value.moduleApi, options.value.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.invoice_id = item.invoice_id
  payload.invoiceNumber = item.invoiceNumber
  payload.invoiceDate = item.invoiceDate
  payload.isManual = item.isManual
  payload.invoiceAmount = item.invoiceAmount
  payload.hotel = item.hotel.id
  payload.agency = item.agency.id
  payload.invoiceType = item.invoiceType.id
  await GenericService.update(options.value.moduleApi, options.value.uriApi, idItem.value || '', payload)
}

async function deleteItem(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
    clearForm()
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Could not delete invoice', life: 3000 })
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
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
    idItem.value = ''
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
  }
}
const goToList = async () => await navigateTo('/invoice')

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
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}

function toggleForceUpdate() {
  forceUpdate.value = !forceUpdate.value
}

function update() {
  forceUpdate.value = true

  setTimeout(() => {
    forceUpdate.value = false
  }, 100)
}

function openRoomRateDialog(booking?: any) {
  active.value = 1

  if (booking?.id) {
    selectedBooking.value = booking?.id
  }

  roomRateDialogOpen.value = true
}

function openAdjustmentDialog(roomRate?: any) {
  active.value = 2

  if (roomRate?.id) {
    selectedRoomRate.value = roomRate?.id
  }

  adjustmentDialogOpen.value = true
}

watch(() => idItemToLoadFirstTime.value, async (newValue) => {
  if (!newValue) {
    clearForm()
  }
  else {
    await getItemById(newValue)
  }
})

onMounted(async () => {
  filterToSearch.value.criterial = ENUM_FILTER[0]
})
</script>

<template>
  <div class="justify-content-center align-center ">
    <div class="px-4 pt-2" style="width: 100%; height: 100%;">
      <TabView v-model:activeIndex="active" class="tabview-custom">
        <TabPanel>
          <template #header>
            <div class="flex align-items-center gap-2 p-2 " :style="`${active === 0 && 'background-color: white; color: #0F8BFD;'} border-radius: 5px 5px 0 0;  width: 130px`">
              <i class="pi pi-calendar" style="font-size: 1.5rem" />
              <span class="font-bold white-space-nowrap">
                Bookings
                <i
                  v-if="errorInTab.tabGeneralData" class="pi p-error pi-question-circle"
                  style="font-size: 1.2rem"
                />
              </span>
            </div>
          </template>
          <BookingTab
            :is-dialog-open="bookingDialogOpen" :close-dialog="() => { bookingDialogOpen = false }"
            :open-dialog="handleDialogOpen" :open-room-rate-dialog="openRoomRateDialog" :force-update="forceUpdate"
            :toggle-force-update="() => { }" :selected-invoice="selectedInvoice" :is-creation-dialog="false" :is-tab-view="true"
          />
        </TabPanel>
        <TabPanel>
          <template #header>
            <div class="flex align-items-center gap-2 p-2" :style="`${active === 1 && 'background-color: white; color: #0F8BFD;'} border-radius: 5px 5px 0 0;  width: 130px`">
              <i class="pi pi-receipt" style="font-size: 1.5rem" />
              <span class="font-bold white-space-nowrap">
                Room Rates
                <i
                  v-if="errorInTab.tabGeneralData" class="pi p-error pi-question-circle"
                  style="font-size: 1.2rem"
                />
              </span>
            </div>
          </template>
          <RoomRateTab
            :is-dialog-open="roomRateDialogOpen" :close-dialog="() => { roomRateDialogOpen = false }"
            :open-dialog="handleDialogOpen" :selected-booking="selectedBooking"
            :open-adjustment-dialog="openAdjustmentDialog" :selected-invoice="selectedInvoice"
            :force-update="forceUpdate" :is-creation-dialog="false" :toggle-force-update="() => { }" :is-tab-view="true"
          />
        </TabPanel>
        <TabPanel>
          <template #header>
            <div class="flex align-items-center gap-2 p-2" :style="`${active === 2 && 'background-color: white; color: #0F8BFD;'} border-radius: 5px 5px 0 0;  width: 130px`">
              <i class="pi pi-sliders-v" style="font-size: 1.5rem" />
              <span class="font-bold white-space-nowrap">
                Adjustments
                <i
                  v-if="errorInTab.tabGeneralData" class="pi p-error pi-question-circle"
                  style="font-size: 1.2rem"
                />
              </span>
            </div>
          </template>
          <AdjustmentTab
            :is-dialog-open="adjustmentDialogOpen" :close-dialog="() => { adjustmentDialogOpen = false }"
            :open-dialog="handleDialogOpen" :selected-room-rate="selectedRoomRate" :is-creation-dialog="false"
            :selected-invoice="selectedInvoice" :force-update="forceUpdate" :toggle-force-update="() => { }" :is-tab-view="true"
          />
        </TabPanel>
      </TabView>
    </div>

    <Toast position="top-center" :base-z-index="5" />
  </div>
</template>

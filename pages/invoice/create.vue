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

const toast = useToast()

const forceUpdate = ref(false)
const active = ref(0)
const route = useRoute()

const selectedInvoice = ref({})
const selectedBooking = ref<string>('')
const selectedRoomRate = ref<string>('')

const loadingSaveAll = ref(false)
const loadingDelete = ref(false)

const bookingDialogOpen = ref<boolean>(false)
const roomRateDialogOpen = ref<boolean>(false)
const adjustmentDialogOpen = ref<boolean>(false)
const attachmentDialogOpen = ref<boolean>(false)

const bookingList = ref<any[]>([])
const roomRateList = ref<any[]>([])
const adjustmentList = ref<any[]>([])
const attachmentList = ref<any[]>([])

const errorInTab = ref({
  tabGeneralData: false,
  tabMedicalInfo: false,
  tabServices: false,
  tabPermissions: false
})

const fields: Array<Container> = [
  {
    childs: [
      {
        field: 'invoice_id',
        header: 'Id',
        dataType: 'text',
        class: `w-full ${String(route.query.type) as any === ENUM_INVOICE_TYPE[3] ?? 'required'}`,
        disabled: true,
        containerFieldClass: 'ml-10'

      },
      {
        field: 'invoiceNumber',
        header: 'Invoice Number',
        dataType: 'text',
        class: 'w-full',
        disabled: true,

      },

    ],
    containerClass: 'flex flex-column justify-content-evenly w-full'
  },

  {
    childs: [

      {
        field: 'hotel',
        header: 'Hotel',
        dataType: 'select',
        class: 'w-full px-4 required',

      },
      {
        field: 'agency',
        header: 'Agency',
        dataType: 'select',
        class: 'w-full px-4 required',

      },

    ],

    containerClass: 'flex flex-column justify-content-evenly w-full '
  },
  {
    childs: [
      {
        field: 'invoiceDate',
        header: 'Invoice Date',
        dataType: 'date',
        class: 'w-full required',
        validation: z.date({ required_error: 'The Invoice Date field is required' }).max(dayjs().endOf('day').toDate(), 'The Invoice Date field cannot be greater than current date')
      },
      {
        field: 'isManual',
        header: 'Manual',
        dataType: 'check',
        class: `w-full ${String(route.query.type) as any === ENUM_INVOICE_TYPE[3] ?? 'required'}`,
        disabled: true
      },

    ],
    containerClass: 'flex flex-column justify-content-evenly w-full'
  },
  {
    childs: [
      {
        field: 'invoiceAmount',
        header: 'Invoice Amount',
        dataType: 'text',
        class: 'w-full px-4 required',
        disabled: true
      },
      {
        field: 'invoiceType',
        header: 'Type',
        dataType: 'select',
        class: 'w-full px-4 ',
        containerFieldClass: '',
        disabled: true
      },

    ],
    containerClass: 'flex flex-column justify-content-evenly w-full'
  },

]

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
const formReload = ref(0)

const idItem = ref('')
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})

const item = ref<GenericObject>({
  invoice_id: 0,
  invoiceNumber: '',
  invoiceDate: new Date(),
  isManual: true,
  invoiceAmount: '0.00',
  hotel: null,
  agency: null,
  invoiceType: ENUM_INVOICE_TYPE.find((element => element.id === route.query.type)),
})

const itemTemp = ref<GenericObject>({
  invoice_id: 0,
  invoiceNumber: '',
  invoiceDate: new Date(),
  isManual: true,
  invoiceAmount: '0.00',
  hotel: null,
  agency: null,
  invoiceType: ENUM_INVOICE_TYPE.find((element => element.id === route.query.type)),
})

// -------------------------------------------------------------------------------------------------------

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

function handleDialogOpen() {
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

function handleAttachmentDialogOpen() {
  attachmentDialogOpen.value = true
}

async function getHotelList() {
  try {
    const payload
      = {
        filter: [{
          key: 'status',
          operator: 'EQUALS',
          value: 'ACTIVE',
          logicalOperation: 'AND'
        }],
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
      hotelList.value = [...hotelList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status }]
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
        filter: [{
          key: 'status',
          operator: 'EQUALS',
          value: 'ACTIVE',
          logicalOperation: 'AND'
        }],
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
      agencyList.value = [...agencyList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading agency list:', error)
  }
}

function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''

  formReload.value++
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
    payload.hotel = item.hotel?.id
    payload.agency = item.agency?.id
    payload.invoiceType = item.invoiceType?.id
    payload.id = v4()

    for (let i = 0; i < bookingList?.value?.length; i++) {
      bookingList.value[i].invoice = payload.id
      bookingList.value[i].ratePlan = bookingList.value[i].ratePlan?.id
      bookingList.value[i].roomCategory = bookingList.value[i].roomCategory?.id
      bookingList.value[i].roomType = bookingList.value[i].roomType?.id
      bookingList.value[i].nightType = bookingList.value[i].nightType?.id
    }

    for (let i = 0; i < adjustmentList?.value.length; i++) {
      adjustmentList.value[i].transactionType = adjustmentList.value[i].transactionType?.id
    }

    for (let i = 0; i < attachmentList.value.length; i++) {
      attachmentList.value[i].type = attachmentList.value[i]?.type?.id
      const fileurl: any = await GenericService.getUrlByImage(attachmentList.value[i]?.file)
      attachmentList.value[i].file = fileurl
    }

    await GenericService.createBulk('invoicing', 'manage-invoice', { bookings: bookingList.value, invoice: payload, roomRates: roomRateList?.value, adjustments: adjustmentList?.value, attachments: attachmentList?.value })
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
  payload.invoiceType = item.invoiceType?.id
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
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'The invoice was updated created successfully', life: 10000 })
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
      navigateTo('/invoice')
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'The invoice was created successfully', life: 10000 })
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

function addBooking(booking: any) {
  bookingList.value = [...bookingList.value, booking]
  roomRateList.value = [...roomRateList.value, {
    checkIn: new Date(booking?.checkIn),
    checkOut: new Date(booking?.checkOut),
    invoiceAmount: booking?.invoiceAmount,
    roomNumber: booking?.roomNumber,
    adults: booking?.adults,
    children: booking?.children,
    rateAdult: booking?.rateAdult,
    rateChild: booking?.rateChild,
    hotelAmount: booking?.hotelAmount,
    remark: booking?.description,
    booking: booking?.id
  }]
}

function updateBooking(booking: any) {
  const index = bookingList.value.findIndex(item => item.id === booking.id)

  bookingList.value[index] = booking
}

function addRoomRate(booking: any) {
  roomRateList.value = [...roomRateList.value, booking]
}

function updateRoomRate(booking: any) {
  const index = roomRateList.value.findIndex(item => item.id === booking.id)
  roomRateList.value[index] = booking
}

function addAdjustment(booking: any) {
  adjustmentList.value = [...adjustmentList.value, booking]
}

function updateAdjustment(booking: any) {
  const index = adjustmentList.value.findIndex(item => item.id === booking.id)
  adjustmentList.value[index] = booking
}

function addAttachment(attachment: any) {
  attachmentList.value = [...attachmentList.value, attachment]
}

function updateAttachment(attachment: any) {
  const index = attachmentList.value.findIndex(item => item.id === attachment.id)
  attachmentList.value[index] = attachment
}

onMounted(async () => {
  filterToSearch.value.criterial = ENUM_FILTER[0]

  item.value.invoiceType = ENUM_INVOICE_TYPE.find((element => element.id === route.query.type))
})
</script>

<template>
  <div class="font-bold text-lg px-4 bg-primary custom-card-header">
    {{ OBJ_INVOICE_TITLE[String(route.query.type)] }}
  </div>
  <div class="card">
    <EditFormV2WithContainer
      :key="formReload" :fields-with-containers="fields" :item="item" :show-actions="true"
      :loading-save="loadingSaveAll" :loading-delete="loadingDelete" container-class="flex flex-row justify-content-evenly card w-full"
      @cancel="clearForm" @delete="requireConfirmationToDelete($event)"
    >
      <template #field-invoiceType="{ item: data, onUpdate }">
        <Dropdown
          v-if="!loadingSaveAll"
          v-model="data.invoiceType"
          :options="[...ENUM_INVOICE_TYPE]"
          option-label="name"
          return-object="false"
          show-clear
          disabled
          @update:model-value="($event) => {
            onUpdate('invoiceType', $event)
          }"
        />
        <Skeleton v-else height="2rem" class="mb-2" />
      </template>
      <template #field-hotel="{ item: data, onUpdate }">
        <DebouncedAutoCompleteComponent
          v-if="!loadingSaveAll" id="autocomplete" field="name" item-value="id"
          :model="data.hotel" :suggestions="hotelList" @change="($event) => {
            onUpdate('hotel', $event)
          }" @load="($event) => getHotelList($event)"
        >
          <template #option="props">
            <span>{{ props.item.code }} - {{ props.item.name }}</span>
          </template>
        </DebouncedAutoCompleteComponent>
      </template>
      <template #field-agency="{ item: data, onUpdate }">
        <DebouncedAutoCompleteComponent
          v-if="!loadingSaveAll" id="autocomplete" field="name" item-value="id"
          :model="data.agency" :suggestions="agencyList" @change="($event) => {
            onUpdate('agency', $event)
          }" @load="($event) => getAgencyList($event)"
        >
          <template #option="props">
            <span>{{ props.item.code }} - {{ props.item.name }}</span>
          </template>
        </DebouncedAutoCompleteComponent>
      </template>

      <template #form-footer="props">
        <div style="width: 100%; height: 100%; padding-right: -50px;">
          <div class="flex justify-content-end mb-3">
            <Button
              v-if="active === 0" v-tooltip.top="'Add Booking'" class="w-fit mx-1" icon="pi pi-plus"
              :loading="loadingSaveAll" label="New" @click="handleDialogOpen()"
            />
            <Button
              v-if="active === 0" v-tooltip.top="'Add Attachment'" class="w-3rem mx-1" icon="pi pi-paperclip"
              :loading="loadingSaveAll" @click="handleAttachmentDialogOpen()"
            />
            <Button v-tooltip.top="'Update'" class="w-3rem mx-1" icon="pi pi-replay" :loading="loadingSaveAll" />
          </div>
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
                :toggle-force-update="toggleForceUpdate" :selected-invoice="selectedInvoice as any"
                :add-item="addBooking" :update-item="updateBooking" :list-items="bookingList"
                :is-creation-dialog="true" :invoice-obj="item" :invoice-agency="{}" :invoice-hotel="{}"
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
                :open-adjustment-dialog="openAdjustmentDialog" :force-update="forceUpdate"
                :toggle-force-update="toggleForceUpdate" :list-items="roomRateList" :add-item="addRoomRate"
                :update-item="updateRoomRate" :is-creation-dialog="true" :selected-invoice="selectedInvoice as any"
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
                :is-dialog-open="adjustmentDialogOpen"
                :close-dialog="() => { adjustmentDialogOpen = false }" :open-dialog="handleDialogOpen"
                :selected-room-rate="selectedRoomRate" :force-update="forceUpdate"
                :toggle-force-update="toggleForceUpdate" :list-items="adjustmentList" :add-item="addAdjustment"
                :update-item="updateAdjustment" :is-creation-dialog="true" :selected-invoice="selectedInvoice as any"
              />
            </TabPanel>
          </TabView>

          <div>
            <div class="flex justify-content-end">
              <Button v-tooltip.top="'Cancel'" severity="danger" class="w-fit mx-1" icon="pi pi-times" label="Cancel" @click="goToList" />
              <Button
                v-tooltip.top="'Save'" class="w-fit mx-1" icon="pi pi-save" :loading="loadingSaveAll" label="Save" :disabled="bookingList.length === 0" @click="() => {
                  saveItem(props.item.fieldValues)
                }"
              />
            </div>
          </div>
        </div>
        <div v-if="attachmentDialogOpen">
          <AttachmentDialog :add-item="addAttachment" :close-dialog="() => { attachmentDialogOpen = false }" :is-creation-dialog="true" header="Master Invoice Attachment" :list-items="attachmentList" :open-dialog="attachmentDialogOpen" :update-item="updateAttachment" selected-invoice="" />
        </div>
      </template>
    </EditFormV2WithContainer>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import type { PageState } from 'primevue/paginator'
import { v4 } from 'uuid'
import dayjs from 'dayjs'

import { GenericService } from '~/services/generic-services'

import type { GenericObject } from '~/types'
import type { IData } from '~/components/table/interfaces/IModelData'
import InvoiceTabView from '~/components/invoice/InvoiceTabView/InvoiceTabView.vue'
import { fields } from '~/components/invoice/form/invoice.fields'

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

const invoiceAmount = ref(0)

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
  bookingDialogOpen.value = true
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
    payload.invoiceType = route.query.type
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

  try {
    await createItem(item)
    navigateTo('/invoice')
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'The invoice was created successfully', life: 10000 })
  }
  catch (error: any) {
    successOperation = false
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
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
  bookingList.value = [...bookingList.value, { ...booking, nights: dayjs(booking?.checkOut).diff(dayjs(booking?.checkIn), 'day', false) }]
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
    booking: booking?.id,
    nights: dayjs(booking?.checkOut).diff(dayjs(booking?.checkIn), 'day', false)
  }]

  calcInvoiceAmount()
}

async function getItemById(id: any) {
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
        item.value.invoiceType = response.invoiceType ? ENUM_INVOICE_TYPE.find((element => element.id === response?.invoiceType)) : ENUM_INVOICE_TYPE[0]
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

function calcInvoiceAmount() {
  invoiceAmount.value = 0

  bookingList.value.forEach((b) => {
    invoiceAmount.value += +b?.invoiceAmount
  })
}

function updateBooking(booking: any) {
  const index = bookingList.value.findIndex(item => item.id === booking.id)

  bookingList.value[index] = booking
  calcInvoiceAmount()
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

  if (route.query.type === ENUM_INVOICE_TYPE[2]?.id && route.query.selected) {
    await getItemById(route.query.selected)
  }
})
</script>

<template>
  <div class="font-bold text-lg px-4 bg-primary custom-card-header">
    {{ OBJ_INVOICE_TITLE[String(route.query.type)] }} {{ route.query.type === ENUM_INVOICE_TYPE[2]?.id ? item?.invoice_id : "" }}
  </div>
  <div cactiveTab>
    <EditFormV2WithContainer
      :key="formReload" :fields-with-containers="fields" :item="item" :show-actions="true"
      :loading-save="loadingSaveAll" :loading-delete="loadingDelete" container-class="flex flex-row justify-content-evenly card w-full"
      @cancel="clearForm" @delete="requireConfirmationToDelete($event)"
    >
      <template #field-invoiceDate="{ item: data, onUpdate }">
        <Calendar
          v-if="!loadingSaveAll"
          v-model="data.invoiceDate"
          date-format="yy-mm-dd"
          :max-date="new Date()"
          @update:model-value="($event) => {
            onUpdate('invoiceDate', $event)
          }"
        />
      </template>
      <template #field-invoiceAmount="{ onUpdate, item: data }">
        <InputText
          v-model="invoiceAmount"
          show-clear :disabled="true"
          @update:model-value="onUpdate('invoiceAmount', $event)"
        />
      </template>
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
        >
          <template #option="props">
            {{ props.option?.code }}-{{ props.option?.name }}
          </template>
          <template #value="props">
            {{ props.value?.code }}-{{ props.value?.name }}
          </template>
        </Dropdown>
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
          <template #chip="{ value }">
            <div>
              {{ value?.code }}-{{ value?.name }}
            </div>
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
          <template #chip="{ value }">
            <div>
              {{ value?.code }}-{{ value?.name }}
            </div>
          </template>
        </DebouncedAutoCompleteComponent>
      </template>

      <template #form-footer="props">
        <div style="width: 100%; height: 100%;">
          <InvoiceTabView
            :is-dialog-open="bookingDialogOpen" :close-dialog="() => { bookingDialogOpen = false }"
            :open-dialog="handleDialogOpen" :selected-booking="selectedBooking"
            :open-adjustment-dialog="openAdjustmentDialog" :force-update="forceUpdate"
            :toggle-force-update="toggleForceUpdate" :room-rate-list="roomRateList" :add-room-rate="addRoomRate"
            :update-room-rate="updateRoomRate" :is-creation-dialog="true" :selected-invoice="selectedInvoice as any"
            :booking-list="bookingList" :add-booking="addBooking" :update-booking="updateBooking" :adjustment-list="adjustmentList"
            :add-adjustment="addAdjustment" :update-adjustment="updateAdjustment" :active="active" :set-active="($event) => {

              active = $event
            }"
          />

          <div>
            <div v-if="route.query.type === ENUM_INVOICE_TYPE[2]?.id" class="flex justify-content-end">
              <Button
                v-tooltip.top="'Apply'" class="w-3rem mx-1" icon="pi pi-check" :loading="loadingSaveAll" :disabled="bookingList.length === 0" @click="() => {
                  saveItem(props.item.fieldValues)
                }"
              />
              <Button
                v-if="active === 0" v-tooltip.top="'Add Booking'" class="w-3rem mx-1" icon="pi pi-plus"
                :loading="loadingSaveAll" @click="handleDialogOpen()"
              />
              <Button v-tooltip.top="'Cancel'" severity="danger" class="w-3rem mx-1" icon="pi pi-times" @click="goToList" />
            </div>
            <div v-else class="flex justify-content-end">
              <Button
                v-tooltip.top="'Save'" class="w-3rem mx-1" icon="pi pi-save" :loading="loadingSaveAll" :disabled="bookingList.length === 0" @click="() => {
                  saveItem(props.item.fieldValues)
                }"
              />
              <Button
                v-tooltip.top="'Export'" class="w-3rem mx-1" icon="pi pi-print"
                :loading="loadingSaveAll" disabled
              />

              <Button
                v-tooltip.top="'Add Attachment'" class="w-3rem mx-1" icon="pi pi-paperclip"
                :loading="loadingSaveAll" disabled @click="handleAttachmentDialogOpen()"
              />
              <Button
                v-tooltip.top="'Export'" class="w-3rem mx-1" icon="pi pi-file"
                :loading="loadingSaveAll" disabled
              />
              <Button
                v-if="active === 0" v-tooltip.top="'Add Booking'" class="w-3rem mx-1" icon="pi pi-plus"
                :loading="loadingSaveAll" @click="handleDialogOpen()"
              />
              <Button
                v-tooltip.top="'Export'" class="w-3rem mx-1" icon="pi pi-dollar"
                :loading="loadingSaveAll" disabled
              />
              <Button v-tooltip.top="'Update'" class="w-3rem mx-1" icon="pi pi-replay" :loading="loadingSaveAll" />
              <Button v-tooltip.top="'Cancel'" severity="danger" class="w-3rem mx-1" icon="pi pi-times" @click="goToList" />
            </div>
          </div>
        </div>
        <div v-if="attachmentDialogOpen">
          <AttachmentDialog :add-item="addAttachment" :close-dialog="() => { attachmentDialogOpen = false }" :is-creation-dialog="true" header="Master Invoice Attachment" :list-items="attachmentList" :open-dialog="attachmentDialogOpen" :update-item="updateAttachment" selected-invoice="" :selected-invoice-obj="{}" />
        </div>
      </template>
    </EditFormV2WithContainer>
  </div>
</template>

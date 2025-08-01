<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import dayjs from 'dayjs'
import InputNumber from 'primevue/inputnumber'
import { GenericService } from '~/services/generic-services'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import type { GenericObject } from '~/types'
import type { IData } from '~/components/table/interfaces/IModelData'
import AttachmentDialog from '~/components/invoice/attachment/AttachmentDialog.vue'
import AttachmentHistoryDialog from '~/components/invoice/attachment/AttachmentHistoryDialog.vue'

const toast = useToast()

const forceSave = ref(false)
const forceUpdate = ref(false)
const active = ref(0)

const route = useRoute()
const id = computed(() => {
  return 'id' in route.params ? String(route.params.id) : ''
})
const invoiceType = ref<string>(route.query.type as string)

const selectedInvoice = ref<{ id: string } | null>(null)
const selectedBooking = ref<{ id: string } | null>(null)

const loadingSaveAll = ref(false)
const loadingDelete = ref(false)

const invoiceAmount = ref(0)
const requiresFlatRate = ref(false)

const bookingDialogOpen = ref<boolean>(false)
const roomRateDialogOpen = ref<boolean>(false)
const adjustmentDialogOpen = ref<boolean>(false)
const attachmentHistoryDialogOpen = ref<boolean>(false)
const exportAttachmentsDialogOpen = ref<boolean>(false)

const idClientForAgencyFilter = ref<string>('')

const invoiceAgency = ref<any>(null)
const invoiceHotel = ref<any>(null)

const attachmentDialogOpen = ref<boolean>(false)

const hotelList = ref<any[]>([])
const agencyList = ref<any[]>([])

const invoiceStatus = ref<any>(null)
const dueAmount = ref<number>(0)
const isInCloseOperation = ref<boolean>(false)

const confhotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const confagencyListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})

const Fields = ref<FieldDefinitionType[]>([
  {
    field: 'invoiceId',
    header: 'ID',
    dataType: 'text',
    class: `field col-12 md:col-3  ${String(route.query.type) as any === InvoiceType.OLD_CREDIT ? '' : ''}`,
    disabled: true,
  },
  {
    field: 'invoiceDate',
    header: 'Invoice Date',
    dataType: 'date',
    class: 'field col-12 md:col-3  required',
    validation: z.date({ required_error: 'The Invoice Date field is required' }).max(dayjs().endOf('day').toDate(), 'The Invoice Date field cannot be greater than current date')
  },
  {
    field: 'hotel',
    header: 'Hotel',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    disabled: String(route.query.type) as any === InvoiceType.CREDIT,
    validation: z.object({
      id: z.string(),
      name: z.string(),

    })
      .required()
      .refine((value: any) => value && value.id && value.name, { message: `The Hotel field is required` })
  },
  {
    field: 'invoiceType',
    header: 'Invoice Type',
    dataType: 'select',
    class: 'field col-12 md:col-3',
    containerFieldClass: '',
    disabled: true
  },
  {
    field: 'invoiceNumber',
    header: 'Invoice Number',
    dataType: 'text',
    class: 'field col-12 md:col-3 ',
    disabled: true,

  },
  {
    field: 'invoiceAmount',
    header: 'Invoice Amount',
    dataType: 'text',
    class: 'field col-12 md:col-3  required',
    disabled: true,
    ...(route.query.type === InvoiceType.OLD_CREDIT && { valdation: z.string().refine(val => +val < 0, 'Invoice amount must have negative values') })
  },

  {
    field: 'agency',
    header: 'Agency',
    dataType: 'select',
    class: 'field col-12 md:col-3 required',
    disabled: String(route.query.type) as any === InvoiceType.CREDIT,
    validation: validateEntityForAgency('agency')
  },
  // {
  //   field: 'agency',
  //   header: 'Agency',
  //   dataType: 'select',
  //   class: 'field col-12 md:col-3 required',
  //   disabled: String(route.query.type) as any === InvoiceType.CREDIT,
  //   validation: z.object({
  //     id: z.string(),
  //     name: z.string(),

  //   }).required()
  //     .refine((value: any) => value && value.id && value.name, { message: `The agency field is required` })
  // },
  {
    field: 'invoiceStatus',
    header: 'Status',
    dataType: 'select',
    class: 'field col-12 md:col-2',
    containerFieldClass: '',
    disabled: true
  },
  {
    field: 'isManual',
    header: 'Manual',
    dataType: 'check',
    class: `field col-12 md:col-1  flex align-items-center pt-4 ${String(route.query.type) as any === InvoiceType.OLD_CREDIT ? 'required' : ''}`,
    disabled: true
  },
])

// VARIABLES -----------------------------------------------------------------------------------------

const objApis = ref({
  invoiceType: { moduleApi: 'settings', uriApi: 'manage-invoice-type' },
  invoiceStatus: { moduleApi: 'invoicing', uriApi: 'manage-invoice-status' },
  agency: { moduleApi: 'settings', uriApi: 'manage-agency' },
  hotel: { moduleApi: 'settings', uriApi: 'manage-hotel' },
  bankAccount: { moduleApi: 'settings', uriApi: 'manage-bank-account' },
  paymentStatus: { moduleApi: 'settings', uriApi: 'manage-payment-status' },
  attachmentStatus: { moduleApi: 'settings', uriApi: 'manage-payment-attachment-status' },
})

//
const confirm = useConfirm()
const formReload = ref(0)

const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})

const item = ref<GenericObject>({
  invoiceId: '',
  invoiceNumber: '',
  invoiceDate: new Date(),
  isManual: true,
  invoiceAmount: '0.00',
  hotel: null,
  agency: null,
  invoiceType: null,
  dueAmount: '0.00'
})

const itemTemp = ref<GenericObject>({
  invoiceId: '',
  invoiceNumber: '',
  invoiceDate: new Date(),
  isManual: true,
  invoiceAmount: '0.00',
  hotel: null,
  agency: null,
  invoiceType: null,
  dueAmount: '0.00'
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
  Logger.log(active)
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

  Logger.log(bookingDialogOpen)
}

async function getHotelList(query = '') {
  try {
    const payload
      = {
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
            key: 'isVirtual',
            logicalOperation: 'AND',
            operator: 'EQUALS',
            value: false,
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
        sortBy: 'createdAt',
        sortType: ENUM_SHORT_TYPE.DESC
      }

    const response = await GenericService.search(confhotelListApi.moduleApi, confhotelListApi.uriApi, payload)
    const { data: dataList } = response
    hotelList.value = []
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status, fullName: `${iterator.code} - ${iterator.name}` }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

function handleAttachmentHistoryDialogOpen() {
  attachmentHistoryDialogOpen.value = true
}

async function getAgencyList(query = '') {
  try {
    const listFilter = invoiceStatus.value !== InvoiceStatus.PROCESSED
      ? [
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
            key: 'client.id',
            operator: 'EQUALS',
            value: `${idClientForAgencyFilter.value}`,
            logicalOperation: 'AND'
          },
          {
            key: 'client.status',
            operator: 'EQUALS',
            value: 'ACTIVE',
            logicalOperation: 'AND'
          },
          {
            key: 'status',
            operator: 'EQUALS',
            value: 'ACTIVE',
            logicalOperation: 'AND'
          }
        ]
      : [
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
        ]
    const payload
      = {
        filter: [
          ...listFilter
        ],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: ENUM_SHORT_TYPE.DESC
      }

    const response = await GenericService.search(confagencyListApi.moduleApi, confagencyListApi.uriApi, payload)
    const { data: dataList } = response
    agencyList.value = []
    for (const iterator of dataList) {
      agencyList.value = [
        ...agencyList.value,
        {
          id: iterator.id,
          name: iterator.name,
          code: iterator.code,
          status: iterator.status,
          fullName: `${iterator.code} - ${iterator.name}`,
          client: iterator.client
        }
      ]
    }
  }
  catch (error) {
    console.error('Error loading agency list:', error)
  }
}

function refetchInvoice() {
  Logger.log('REFETCH')
  getInvoiceAmountById(id.value)
  update()
}

function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''

  formReload.value++
}

function mapFunction(data: any): any {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status,
    processStatus: data.processStatus,
    sentStatus: data.sentStatus,
    reconciledStatus: data.reconciledStatus,
    canceledStatus: data.canceledStatus
  }
}

async function getInvoiceAmountById(id: string) {
  if (id) {
    idItem.value = id

    try {
      const response = await GenericService.getById(options.value.moduleApi, options.value.uriApi, id)
      if (response) {
        item.value.invoiceAmount = response.invoiceAmount
        invoiceAmount.value = response.invoiceAmount
      }
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Invoice methods could not be loaded', life: 3000 })
      }
    }
  }
}

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(options.value.moduleApi, options.value.uriApi, id)

      if (response) {
        item.value.id = response.id
        item.value.invoiceId = response.invoiceId
        item.value.dueDate = response.dueDate

        const invoiceNumber = `${response?.invoiceNumber?.split('-')[0]}-${response?.invoiceNumber?.split('-')[2]}`

        item.value.invoiceNumber = response?.invoiceNumber?.split('-')?.length === 3 ? invoiceNumber : response.invoiceNumber
        item.value.invoiceNumber = item.value.invoiceNumber.replace('OLD', 'CRE')

        const date = response.invoiceDate ? dayjs(response.invoiceDate).format('YYYY-MM-DD') : ''
        item.value.invoiceDate = date ? new Date(`${date}T00:00:00`) : null

        item.value.isManual = response.isManual
        item.value.invoiceAmount = response.invoiceAmount
        invoiceAmount.value = response.invoiceAmount
        dueAmount.value = response.dueAmount
        item.value.reSend = response.reSend
        item.value.reSendDate = response.reSendDate ? dayjs(response.reSendDate).toDate() : response.reSendDate
        item.value.hotel = response.hotel
        item.value.hotel.fullName = `${response.hotel.code} - ${response.hotel.name}`
        item.value.agency = response.agency
        item.value.hasAttachments = response.hasAttachments
        item.value.agency.fullName = `${response.agency.code} - ${response.agency.name}`
        item.value.invoiceType = response.invoiceType === InvoiceType.OLD_CREDIT ? ENUM_INVOICE_TYPE[0] : ENUM_INVOICE_TYPE.find((element => element.id === response?.invoiceType))
        item.value.invoiceType.income = response.manageInvoiceType.income // Se agrega propiedad para identificar si es invoice
        invoiceStatus.value = response.status
        item.value.status = response.status ? ENUM_INVOICE_STATUS.find((element => element.id === response?.status)) : ENUM_INVOICE_STATUS[0]

        item.value.invoiceStatus = response.manageInvoiceStatus
          ? {
              id: response.manageInvoiceStatus.id,
              name: `${response.manageInvoiceStatus.code} - ${response.manageInvoiceStatus.name}`,
              status: response.manageInvoiceStatus.status,
              processStatus: response.manageInvoiceStatus.processStatus,
              sentStatus: response.manageInvoiceStatus.sentStatus,
              reconciledStatus: response.manageInvoiceStatus.reconciledStatus,
              canceledStatus: response.manageInvoiceStatus.canceledStatus
            }
          : null
        idClientForAgencyFilter.value = response.agency?.client?.id
        await getInvoiceAgency(response.agency?.id)
        await getInvoiceHotel(response.hotel?.id)
        isInCloseOperation.value = response.isInCloseOperation

        // Esto se debe solucionar haciendo que en la respueta se envie el status del cliente
        if (response?.agency?.client?.id) {
          const objClient = await GenericService.getById('settings', 'manage-client', response?.agency?.client?.id)
          if (objClient) {
            item.value.agency.client = {
              ...item.value.agency?.client,
              status: objClient?.status,
            }
          }
        }
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

const nightTypeRequired = ref(false)

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

async function goToList() {
  if (window.opener) {
    window.close()
  }
  else {
    await navigateTo('/invoice')
  }
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

function update() {
  forceUpdate.value = true

  setTimeout(() => {
    forceUpdate.value = false
  }, 100)
}

async function getInvoiceHotel(id: string) {
  try {
    const hotel = await GenericService.getById(confhotelListApi.moduleApi, confhotelListApi.uriApi, id)

    if (hotel) {
      invoiceHotel.value = { ...hotel }

      requiresFlatRate.value = hotel?.requiresFlatRate
    }
  }
  catch (err) {

  }
}
async function getInvoiceAgency(id: string) {
  try {
    const agency = await GenericService.getById(confagencyListApi.moduleApi, confagencyListApi.uriApi, id)
    if (agency) {
      invoiceAgency.value = { ...agency }
      nightTypeRequired.value = agency?.client?.isNightType
    }
  }
  catch (err) {

  }
}

function handleAttachmentDialogOpen() {
  attachmentDialogOpen.value = true
}

const invoiceStatusList = ref<any[]>([])

async function getInvoiceStatusList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  const additionalFilter: FilterCriteria[] = [
    {
      key: 'processStatus',
      logicalOperation: 'OR',
      operator: 'EQUALS',
      value: 'true'
    },
    {
      key: 'canceledStatus',
      logicalOperation: 'OR',
      operator: 'EQUALS',
      value: 'true'
    }
  ]

  invoiceStatusList.value = await getDataList<any, any>(moduleApi, uriApi, [...(filter || []), ...additionalFilter], queryObj, mapFunction)
}

function disabledInvoiceStatus(payload: any) {
  let result = true
  if (item.value.invoiceAmount !== dueAmount.value || !isInCloseOperation.value) {
    result = true
  }
  // Verificar si esta en estado Sent o Reconciled (En estos estados solo se puede editar la agencia)
  else if (payload && (payload.sentStatus || payload.reconciledStatus)) {
    result = true
  }
  else if (payload && payload.processStatus) {
    result = false
  }

  return result
}

function disabledFieldAgency() {
  if (item.value?.invoiceType?.code === 'CRE' || item.value?.invoiceType?.code === 'INC') {
    return true
  }
  else {
    let result = true
    if (invoiceStatus.value !== InvoiceStatus.PROCESSED && invoiceStatus.value !== InvoiceStatus.SENT && invoiceStatus.value !== InvoiceStatus.RECONCILED) {
      result = true
    }
    else {
      result = false
    }
    return result
  }
}

function disableBtnSave() {
  if (item.value?.invoiceType?.code === 'CRE' || item.value?.invoiceType?.code === 'INC') {
    return true
  }
  else {
    let result = true
    if (invoiceStatus.value !== InvoiceStatus.PROCESSED && invoiceStatus.value !== InvoiceStatus.SENT && invoiceStatus.value !== InvoiceStatus.RECONCILED) {
      result = true
    }
    else {
      result = false
    }
    return result
  }
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
  if (route.params && 'id' in route.params && id.value) {
    await getItemById(id.value)
  }
})
</script>

<template>
  <div class="justify-content-center align-center ">
    <div class="font-bold text-lg px-4 bg-primary custom-card-header">
      {{ OBJ_UPDATE_INVOICE_TITLE[String(item?.invoiceType)] || "Edit Invoice" }}
    </div>
    <div class="pt-3">
      <EditFormV2
        :key="formReload"
        :fields="Fields"
        :item="item"
        :show-actions="true"
        :loading-save="loadingSaveAll"
        :loading-delete="loadingDelete"
        :force-save="forceSave"
        container-class="grid py-3"
        @cancel="clearForm"
        @delete="requireConfirmationToDelete($event)"
        @force-save="forceSave = $event"
      >
        <template #field-invoiceDate="{ item: data, onUpdate }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.invoiceDate"
            date-format="yy-mm-dd"
            :max-date="new Date()"
            :disabled="invoiceStatus !== InvoiceStatus.PROCESSED "
            @update:model-value="($event) => {
              onUpdate('invoiceDate', $event)
            }"
          />
        </template>
        <template #field-invoiceStatus="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.invoiceStatus"
            :suggestions="[...invoiceStatusList]"
            :disabled="disabledInvoiceStatus(item.invoiceStatus)"
            @change="async ($event) => {
              onUpdate('invoiceStatus', $event)
            }"
            @load="async ($event) => {
              const objQueryToSearch = {
                query: $event,
                keys: ['name', 'code'],
              }
              const filter: FilterCriteria[] = [
                {
                  key: 'status',
                  logicalOperation: 'AND',
                  operator: 'EQUALS',
                  value: 'ACTIVE',
                },
              ]
              await getInvoiceStatusList(objApis.invoiceStatus.moduleApi, objApis.invoiceStatus.uriApi, objQueryToSearch, filter)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-status="{ item: data, onUpdate }">
          <!-- :disabled="data?.status?.id !== InvoiceStatus.PROCESSED"  -->
          <Dropdown
            v-if="!loadingSaveAll"
            v-model="data.status"
            :options="[...invoiceStatusList]"
            option-label="name"
            return-object="false"
            show-clear
            :disabled="(data.invoiceAmount !== dueAmount || !isInCloseOperation)"
            @update:model-value="($event) => {
              onUpdate('status', $event)
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
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="fullName"
            item-value="id"
            :disabled="invoiceStatus === InvoiceStatus.PROCESSED || invoiceStatus === InvoiceStatus.SENT || invoiceStatus === InvoiceStatus.RECONCILED || invoiceStatus === InvoiceStatus.CANCELED || invoiceStatus === 'CANCELED' "
            :model="data.hotel"
            :suggestions="hotelList"
            @change="($event) => {
              onUpdate('hotel', $event)
            }" @load="($event) => getHotelList($event)"
          >
            <template #option="props">
              <span>{{ props.item.fullName }}</span>
            </template>
            <template #chip="{ value }">
              <div>
                {{ value?.fullName }}
              </div>
            </template>
          </DebouncedAutoCompleteComponent>
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-agency="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="fullName"
            item-value="id"
            :disabled="disabledFieldAgency()"
            :model="data.agency"
            :suggestions="agencyList"
            @change="($event) => {
              onUpdate('agency', $event)
            }"
            @load="($event) => getAgencyList($event)"
          >
            <template #option="props">
              <span>{{ props.item.fullName }}</span>
            </template>
            <template #chip="{ value }">
              <div>
                {{ value?.fullName }}
              </div>
            </template>
          </DebouncedAutoCompleteComponent>
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #form-footer="props">
          <div style="width: 100%; height: 100%;">
            <InvoiceTabViewForIncome
              :requires-flat-rate="requiresFlatRate"
              :get-invoice-hotel="getInvoiceHotel"
              :get-invoice-agency="getInvoiceAgency"
              :invoice-obj-amount="invoiceAmount"
              :is-dialog-open="bookingDialogOpen"
              :close-dialog="() => { bookingDialogOpen = false }"
              :open-dialog="handleDialogOpen"
              :selected-booking="selectedBooking"
              :force-update="forceUpdate"
              :toggle-force-update="update"
              :invoice-obj="item"
              :refetch-invoice="refetchInvoice"
              :is-creation-dialog="false"
              :selected-invoice="selectedInvoice as any"
              :active="active"
              :set-active="($event: number) => { active = $event }"
              :show-totals="true"
              :night-type-required="nightTypeRequired"
              :invoice-type="invoiceType"
            />
            <div>
              <div class="flex justify-content-end">
                <IfCan :perms="['INVOICE-MANAGEMENT:EDIT']">
                  <Button
                    v-tooltip.top="'Save'"
                    class="w-3rem mx-1"
                    icon="pi pi-save"
                    :disabled="disableBtnSave()"
                    :loading="loadingSaveAll"
                    @click="props.item.submitForm($event)"
                  />
                  <!-- @click="() => {
                        saveItem(props.item.fieldValues)
                      }" -->
                </IfCan>

                <Button
                  v-tooltip.top="'Print'"
                  class="w-3rem mx-1"
                  icon="pi pi-print"
                  :loading="loadingSaveAll"
                  @click="() => {
                    exportAttachmentsDialogOpen = true
                  }"
                />

                <IfCan :perms="['INVOICE-MANAGEMENT:SHOW-BTN-ATTACHMENT']">
                  <Button
                    v-tooltip.top="'Add Attachment'" class="w-3rem mx-1" icon="pi pi-paperclip"
                    :loading="loadingSaveAll" @click="handleAttachmentDialogOpen()"
                  />
                </IfCan>
                <IfCan :perms="['INVOICE-MANAGEMENT:BOOKING-SHOW-HISTORY']">
                  <Button
                    v-tooltip.top="'Show History'" class="w-3rem mx-1" :loading="loadingSaveAll"
                    :disabled="!item?.hasAttachments" @click="handleAttachmentHistoryDialogOpen()"
                  >
                    <template #icon>
                      <span class="flex align-items-center justify-content-center p-0">
                        <svg
                          xmlns="http://www.w3.org/2000/svg" height="15px" viewBox="0 -960 960 960" width="15px"
                          fill="#e8eaed"
                        >
                          <path
                            d="M320-240h320v-80H320v80Zm0-160h320v-80H320v80ZM240-80q-33 0-56.5-23.5T160-160v-640q0-33 23.5-56.5T240-880h320l240 240v480q0 33-23.5 56.5T720-80H240Zm280-520v-200H240v640h480v-440H520ZM240-800v200-200 640-640Z"
                          />
                        </svg>
                      </span>
                    </template>
                  </Button>
                </IfCan>

                <IfCan :perms="['INVOICE-MANAGEMENT:BOOKING-CREATE']">
                  <Button
                    v-if="active === 0" v-tooltip.top="'Add Booking'" class="w-3rem mx-1" icon="pi pi-plus"
                    :loading="loadingSaveAll" :disabled="item?.invoiceType?.id === InvoiceType.INCOME || invoiceStatus !== InvoiceStatus.PROCESSED" @click="handleDialogOpen()"
                  />
                </IfCan>

                <!-- <Button v-tooltip.top="'Import'" v-if="item?.invoiceType?.id === InvoiceType.INCOME" class="w-3rem ml-1"
                  disabled icon="pi pi-file-import"  /> -->

                <Button
                  v-tooltip.top="'Update'" class="w-3rem mx-1" icon="pi pi-replay" :loading="loadingSaveAll"
                  @click="update"
                />
                <Button
                  v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem mx-1" icon="pi pi-times"
                  @click="goToList"
                />
              </div>
            </div>
          </div>
        </template>
      </EditFormV2>
    </div>
    <div v-if="attachmentDialogOpen">
      <AttachmentDialog
        :close-dialog="() => { attachmentDialogOpen = false }"
        :is-creation-dialog="false" header="Manage Invoice Attachment" :open-dialog="attachmentDialogOpen"
        :selected-invoice="selectedInvoice?.id ?? ''" :selected-invoice-obj="item"
      />
    </div>
  </div>
  <div v-if="attachmentHistoryDialogOpen">
    <AttachmentHistoryDialog
      selected-attachment="" :close-dialog="() => { attachmentHistoryDialogOpen = false }"
      header="Attachment Status History" :open-dialog="attachmentHistoryDialogOpen" :selected-invoice="selectedInvoice?.id ?? ''"
      :selected-invoice-obj="item" :is-creation-dialog="false"
    />
  </div>
  <div v-if="exportAttachmentsDialogOpen">
    <PrintInvoiceDialog
      :close-dialog="() => { exportAttachmentsDialogOpen = false }"
      :open-dialog="exportAttachmentsDialogOpen"
      :invoice="item"
    />
  </div>
</template>

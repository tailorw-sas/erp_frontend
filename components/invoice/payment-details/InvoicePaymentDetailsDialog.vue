<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { usePrimeVue } from 'primevue/config'
import type { PageState } from 'primevue/paginator'
import dayjs from 'dayjs'
import { GenericService } from '~/services/generic-services'
import type { FilterCriteria } from '~/composables/list'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { formatNumber } from '~/pages/payment/utils/helperFilters'

interface SubTotals {
  depositAmount: number
}

const props = defineProps({
  openDialog: {
    type: Boolean,
    required: true
  },
  selectedInvoice: {
    type: Object,
    required: true
  }
})
const emit = defineEmits(['onCloseDialog'])

const $primevue = usePrimeVue()

defineExpose({
  $primevue
})
const dialogVisible = ref(props.openDialog)
const listPaymentDetails = ref<any[]>([])
const payloadOnChangePagePayments = ref<PageState>()

const columnsPayments = ref<IColumn[]>([
  { field: 'paymentDetailId', header: 'Id', tooltip: 'Detail Id', width: 'auto', type: 'text' },
  { field: 'bookingId', header: 'Booking Id', tooltip: 'Booking Id', width: '100px', type: 'text' },
  { field: 'invoiceNumber', header: 'Invoice No.', tooltip: 'Invoice No', width: '100px', type: 'text' },
  { field: 'transactionDate', header: 'Transaction Date', tooltip: 'Transaction Date', width: 'auto', type: 'text' },
  { field: 'fullName', header: 'Full Name', tooltip: 'Full Name', width: '150px', type: 'text' },
  // { field: 'firstName', header: 'First Name', tooltip: 'First Name', width: '150px', type: 'text' },
  // { field: 'lastName', header: 'Last Name', tooltip: 'Last Name', width: '150px', type: 'text' },
  { field: 'reservationNumber', header: 'Reservation No.', tooltip: 'Reservation', width: 'auto', type: 'text' },
  { field: 'couponNumber', header: 'Coupon No.', tooltip: 'Coupon No', width: 'auto', type: 'text' },
  // { field: 'checkIn', header: 'Check In', tooltip: 'Check In', width: 'auto', type: 'text' },
  // { field: 'checkOut', header: 'Check Out', tooltip: 'Check Out', width: 'auto', type: 'text' },
  { field: 'adults', header: 'Adults', tooltip: 'Adults', width: 'auto', type: 'text' },
  { field: 'children', header: 'Children', tooltip: 'Children', width: 'auto', type: 'text' },
  // { field: 'deposit', header: 'Deposit', tooltip: 'Deposit', width: 'auto', type: 'bool' },
  { field: 'amount', header: 'D. Amount', tooltip: 'Deposit Amount', width: 'auto', type: 'text' },
  { field: 'transactionType', header: 'P. Trans Type', tooltip: 'Payment Transaction Type', width: '150px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-payment-transaction-type' } },
  { field: 'parentId', header: 'Parent Id', width: 'auto', type: 'text' },
  { field: 'reverseFrom', header: 'Reverse From', width: 'auto', type: 'text' },
  { field: 'remark', header: 'Remark', width: 'auto', type: 'text' },
])

const optionsOfTablePayments = ref({
  tableName: 'Payment Details',
  moduleApi: 'payment',
  uriApi: 'payment-detail',
  expandableRows: false,
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})

const payloadPayments = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})

const paginationPayments = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

function onClose(isCancel: boolean = true) {
  dialogVisible.value = false
  emit('onCloseDialog', isCancel)
}

async function getPaymentDetailList() {
  const count: SubTotals = { depositAmount: 0 }
  if (optionsOfTablePayments.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    optionsOfTablePayments.value.loading = true
    listPaymentDetails.value = []
    const newListItems = []
    payloadPayments.value.filter = []
    const filter: FilterCriteria[] = [
      {
        key: 'manageBooking.invoice.id',
        logicalOperation: 'AND',
        operator: 'EQUALS',
        value: props.selectedInvoice.id,
      },
    ]
    payloadPayments.value.filter = [...payloadPayments.value.filter, ...filter]

    const response = await GenericService.search(optionsOfTablePayments.value.moduleApi, optionsOfTablePayments.value.uriApi, payloadPayments.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    paginationPayments.value.page = page
    paginationPayments.value.limit = size
    paginationPayments.value.totalElements = totalElements
    paginationPayments.value.totalPages = totalPages

    const existingIds = new Set(listPaymentDetails.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'amount')) {
        count.depositAmount += iterator.amount
        iterator.amount = (!Number.isNaN(iterator.amount) && iterator.amount !== null && iterator.amount !== '')
          ? Number.parseFloat(iterator.amount).toString()
          : '0'

        iterator.amount = formatNumber(iterator.amount)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = statusToBoolean(iterator.status)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'transactionDate')) {
        iterator.transactionDate = iterator.transactionDate ? dayjs(iterator.transactionDate).format('YYYY-MM-DD') : null
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'parentId')) {
        iterator.parentId = iterator.parentId ? iterator.parentId.toString() : ''
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'transactionType')) {
        iterator.deposit = iterator.transactionType.deposit
        iterator.transactionType.name = `${iterator.transactionType.code} - ${iterator.transactionType.name}`

        if (iterator.deposit) {
          iterator.rowClass = 'row-deposit' // Clase CSS para las transacciones de tipo deposit
        }
      }

      if (Object.prototype.hasOwnProperty.call(iterator, 'manageBooking')) {
        iterator.adults = iterator.manageBooking?.adults?.toString()
        iterator.childrens = iterator.manageBooking?.children?.toString()
        iterator.couponNumber = iterator.manageBooking?.couponNumber?.toString()
        iterator.fullName = iterator.manageBooking?.fullName
        iterator.reservationNumber = iterator.manageBooking?.reservationNumber?.toString()

        if (iterator?.manageBooking?.invoice?.invoiceType === 'CREDIT' && iterator?.transactionType?.cash) {
          iterator.bookingId = iterator.manageBooking?.bookingId?.toString()
          iterator.invoiceNumber = iterator.manageBooking?.invoice?.invoiceNumber?.toString()
        }
        else if (iterator?.manageBooking?.invoice?.invoiceType === 'CREDIT' && !iterator?.transactionType?.cash) {
          iterator.bookingId = iterator?.manageBooking?.parentResponse?.bookingId?.toString()
          iterator.invoiceNumber = iterator?.manageBooking?.invoice?.parent?.invoiceNumber?.toString()
          iterator.bookingId = iterator?.manageBooking.parentResponse?.bookingId
          iterator.invoiceNumber = iterator?.manageBooking.invoice?.parent?.invoiceNumber
        }
        else {
          iterator.bookingId = iterator.manageBooking?.bookingId
          iterator.invoiceNumber = iterator.manageBooking?.invoice?.invoiceNumber
        }
      }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    listPaymentDetails.value = [...listPaymentDetails.value, ...newListItems]
  }
  catch (error) {
    optionsOfTablePayments.value.loading = false
    console.error(error)
  }
  finally {
    optionsOfTablePayments.value.loading = false
  }
}

async function onRowDoubleClickInDataTable(item: any) {
  if (item.hasOwnProperty('paymentId')) {
    const url = `/payment/form?id=${encodeURIComponent(item.paymentId)}`
    window.open(url, '_blank')
  }
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columnsPayments.value)
  payloadPayments.value.filter = [...payloadPayments.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payloadPayments.value.filter = [...payloadPayments.value.filter, ...parseFilter || []]
  await getPaymentDetailList()
}

function onSortField(event: any) {
  if (event) {
    payloadPayments.value.sortBy = event.sortField
    payloadPayments.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

watch(payloadOnChangePagePayments, (newValue) => {
  payloadPayments.value.page = newValue?.page ? newValue?.page : 0
  payloadPayments.value.pageSize = newValue?.rows ? newValue.rows : 10
  getPaymentDetailList()
})

watch(() => props.openDialog, (newValue) => {
  dialogVisible.value = newValue
})

onMounted(async () => {
  await getPaymentDetailList()
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible"
    modal
    class="mx-3 sm:mx-0"
    content-class="border-round-bottom border-top-1 surface-border"
    :style="{ width: '60%' }"
    :pt="{
      root: {
        class: 'custom-dialog-history',
      },
      header: {
        style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
      },
    }"
    @hide="onClose(true)"
  >
    <template #header>
      <div class="flex justify-content-between">
        <h5 class="m-0">
          Payment Details
        </h5>
      </div>
    </template>
    <template #default>
      <div class="p-fluid pt-3">
        <!-- // Label -->
        <div class="flex justify-content-end mb-2">
          <div class="bg-primary w-auto h-2rem flex align-items-center px-2" style="border-radius: 5px">
            <strong class="mr-2 w-auto">Invoice Id:</strong>
            <span class="w-auto text-white font-semibold">{{ props.selectedInvoice.invoiceId ?? '' }}</span>
          </div>
        </div>

        <DynamicTable
          class="card p-0"
          :data="listPaymentDetails"
          :columns="columnsPayments"
          :options="optionsOfTablePayments"
          :pagination="paginationPayments"
          @on-sort-field="onSortField"
          @on-change-filter="parseDataTableFilter"
          @on-change-pagination="payloadOnChangePagePayments = $event"
          @on-row-double-click="onRowDoubleClickInDataTable"
        />
      </div>
      <div class="flex justify-content-end">
        <div>
          <!-- idInvoicesSelectedToApplyPaymentForOtherDeduction.length === 0 -->
          <Button v-tooltip.top="'Cancel'" class="w-3rem" icon="pi pi-times" severity="secondary" @click="onClose()" />
        </div>
      </div>
    </template>
  </Dialog>
</template>

<style scoped>
</style>

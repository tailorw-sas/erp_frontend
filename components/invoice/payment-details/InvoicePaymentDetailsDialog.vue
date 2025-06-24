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
import { copyTableToClipboard } from '~/pages/payment/utils/clipboardUtils'

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
const toast = useToast()

const columnsPayments = ref<IColumn[]>([
  { field: 'paymentNo', header: 'Payment Id', type: 'text', width: '90px', sortable: true, showFilter: true },
  { field: 'paymentDetailId', header: 'Detail Id', type: 'text', width: '90px', sortable: true, showFilter: true },
  { field: 'bookingId', header: 'Booking Id', type: 'text', width: '90px', sortable: true, showFilter: true },
  { field: 'fullName', header: 'Full Name', type: 'text', width: '90px', sortable: true, showFilter: true },
  { field: 'transactionType', header: 'P. Trans Type', type: 'select', width: '140px', sortable: true, showFilter: true, objApi: { moduleApi: 'settings', uriApi: 'manage-payment-transaction-type' } },
  { field: 'transactionDate', header: 'Transaction Date', type: 'date', width: '140px', sortable: true, showFilter: true },
  { field: 'amount', header: 'D. Amount', type: 'text', width: '90px', sortable: true, showFilter: true },
  { field: 'remark', header: 'Remark', type: 'text', width: '90px', maxWidth: '190px', sortable: true, showFilter: true },
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
  showPagination: false,
  messageToDelete: 'Do you want to save the change?'
})

const payloadPayments = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10000,
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

    const filterPaymentDetail = payloadPayments.value.filter.find(item => item.key === 'manageBooking.invoice.id')
    if (filterPaymentDetail) {
      filterPaymentDetail.value = props.selectedInvoice.id
    }
    else {
      payloadPayments.value.filter.push({
        key: 'manageBooking.invoice.id',
        operator: 'EQUALS',
        value: props.selectedInvoice.id,
        logicalOperation: 'AND'
      })
    }

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
  if (item.hasOwnProperty('paymentId') && item.hasOwnProperty('bookingId')) {
    const url = `/payment/form?id=${encodeURIComponent(item.paymentId)}&highlightBooking=${encodeURIComponent(item.bookingId)}`
    window.open(url, '_blank')
  }
}

function copiarDatosPaymentDetails() {
  copyTableToClipboard(columnsPayments.value, listPaymentDetails.value, toast)
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columnsPayments.value)

  const objFilterPaymentId = parseFilter?.find((item: IFilter) => item?.key === 'paymentNo')
  if (objFilterPaymentId) {
    objFilterPaymentId.key = 'payment.paymentId'
  }

  const objFilterFullName = parseFilter?.find((item: IFilter) => item?.key === 'fullName')
  if (objFilterFullName) {
    objFilterFullName.key = 'manageBooking.fullName'
  }

  payloadPayments.value.filter = [...payloadPayments.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payloadPayments.value.filter = [...payloadPayments.value.filter, ...parseFilter || []]
  await getPaymentDetailList()
}

function onSortField(event: any) {
  if (event) {
    if (event.sortField === 'paymentNo') {
      event.sortField = 'payment.paymentId'
    }
    if (event.sortField === 'fullName') {
      event.sortField = 'manageBooking.fullName'
    }
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
    :style="{ width: '80%', maxWidth: '98%' }"
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
      <div class="flex justify-content-between align-items-center w-full">
        <div class="flex align-items-center">
          <h5 class="m-0">
            Payment Details Applied
          </h5>
        </div>
        <div class="flex align-items-center">
          <h5 class="m-0 mr-4">
            Invoice Id: {{ props.selectedInvoice.invoiceId }}
          </h5>
        </div>
      </div>
    </template>
    <template #default>
      <div class="p-fluid pt-3">
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
      <Button
        v-tooltip.top="'Copiar tabla'"
        class="p-button-lg w-1rem h-2rem"
        style="margin-left: 1450px; margin-top: 5px"
        icon="pi pi-copy"
        @click="copiarDatosPaymentDetails"
      />
      <div v-if="false" class="flex justify-content-end">
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

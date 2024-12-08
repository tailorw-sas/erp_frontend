<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import type { IColumn, IPagination } from '../table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '../fields/interfaces/IFieldInterfaces'
import { GenericService } from '~/services/generic-services'

const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '' },
  selectedPayment: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update:visible'])
const route = useRoute()
const onOffDialog = ref(props.visible)
const paymentDetailSummaryList = ref<any[]>([])
const idItem = ref(props.selectedPayment.id)
const messageForEmptyTable = ref('The data does not correspond to the selected criteria.')

interface SubTotals {
  amount: number
  depositBalance: number
}
const subTotals = ref<SubTotals>({ amount: 0, depositBalance: 0 })

// Table
const columnsExpandTable: IColumn[] = [
  { field: 'bookingId', header: 'Booking Id', width: '120px', type: 'text' },
  { field: 'invoiceNumber', header: 'Invoice No', width: '150px', type: 'text' },
  { field: 'firstName', header: 'First Name', width: '200px', type: 'text' },
  { field: 'lastName', header: 'Last Name', width: '90px', type: 'text' },
  { field: 'couponCode', header: 'Coupon No', width: '120px', type: 'text' },
  { field: 'adult', header: 'Adult', width: '120px', type: 'text' },
  { field: 'children', header: 'Children', width: '100px', type: 'text' },
  { field: 'paymentAmount', header: 'Payment Amount', width: '100px', type: 'text' },
  { field: 'remark', header: 'Remark', width: '100px', type: 'text' },
]

const columns: IColumn[] = [
  { field: 'paymentDetailId', header: 'Id', width: '80px', type: 'text' },
  { field: 'transactionDate', header: 'Transaction Date', width: '200px', type: 'date' },
  { field: 'createdAt', header: 'Create Date', width: '200px', type: 'date' },
  // { field: 'paymentId', header: 'Payment Id', width: '200px', type: 'text' },

  { field: 'amount', header: 'Deposit Amount', width: '200px', type: 'text', tooltip: 'Deposit Amount', },
  { field: 'depositBalance', header: 'Deposit Balance', width: '200px', type: 'text' },
  { field: 'remark', header: 'Remark', width: '200px', type: 'text' },
]
const options = ref({
  tableName: 'Payment Details',
  moduleApi: 'payment',
  uriApi: 'payment-detail',
  loading: false,
  actionsAsMenu: false,
  expandableRows: false,
  messageToDelete: 'Are you sure you want to delete the account type: {{name}}?'
})

const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [
    {
      key: 'transactionType.deposit',
      logicalOperation: 'OR',
      operator: 'EQUALS',
      value: true,
      type: 'filterTable'
    },
    // {
    //   key: 'transactionType.applyDeposit',
    //   logicalOperation: 'OR',
    //   operator: 'EQUALS',
    //   value: true,
    //   type: 'filterTable'
    // },

  ],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

async function getListPaymentDetailSummary() {
  const count: SubTotals = { amount: 0, depositBalance: 0 }
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    options.value.loading = true
    paymentDetailSummaryList.value = []
    const newListItems = []

    const objFilter = payload.value.filter.find(item => item.key === 'payment.id')

    if (objFilter) {
      objFilter.value = idItem.value
    }
    else {
      payload.value.filter.push(
        {
          key: 'payment.id',
          operator: 'EQUALS',
          value: idItem.value,
          type: 'filterTable',
          logicalOperation: 'AND'
        }
      )
    }

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(paymentDetailSummaryList.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'amount')) {
        count.amount += Number.parseFloat(iterator.amount)
        iterator.amount = (!Number.isNaN(iterator.amount) && iterator.amount !== null && iterator.amount !== '')
          ? Number.parseFloat(iterator.amount).toString()
          : '0'
      }

      for (const child of iterator.children) {
        child.paymentAmount = props.selectedPayment.paymentAmount ? Number.parseFloat(props.selectedPayment.paymentAmount).toString() : '0'
      }

      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = statusToBoolean(iterator.status)
      }

      if (Object.prototype.hasOwnProperty.call(iterator, 'applyDepositValue')) {
        count.depositBalance += Number.parseFloat(iterator.applyDepositValue)
        if (iterator.applyDepositValue !== null && iterator.applyDepositValue !== '' && iterator.applyDepositValue !== undefined && iterator.applyDepositValue !== '0' && iterator.applyDepositValue !== '0.00' && iterator.applyDepositValue !== 0) {
          iterator.depositBalance = Number.parseFloat(iterator.applyDepositValue) * -1
        }
        else {
          iterator.depositBalance = 0
        }
      }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    paymentDetailSummaryList.value = [...paymentDetailSummaryList.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
    subTotals.value = { ...count }
  }
}
function onCloseDialog() {
  onOffDialog.value = false
  emit('update:visible', false)
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  if (parseFilter && parseFilter?.length > 0) {
    for (let i = 0; i < parseFilter?.length; i++) {
      if (parseFilter[i]?.key === 'depositBalance') {
        parseFilter[i].key = 'applyDepositValue'
      }
    }
  }
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterTable')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getListPaymentDetailSummary()
}

function onSortField(event: any) {
  if (event) {
    if (event.sortField === 'transactionType') {
      event.sortField = 'transactionType.name'
    }
    if (event.sortField === 'depositBalance') {
      event.sortField = 'applyDepositValue'
    }
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

watch(() => props.visible, async (newValue) => {
  onOffDialog.value = newValue
  emit('update:visible', newValue)

  if (newValue) {
    if (route?.query?.id) {
      idItem.value = route.query.id.toString() || '0'
      // payload.value.filter = [...payload.value.filter, {
      //   key: 'payment.id',
      //   operator: 'EQUALS',
      //   value: idItem.value,
      //   logicalOperation: 'AND'
      // }]
    }
    else {
      idItem.value = props.selectedPayment.id || '0'
      // payload.value.filter = [...payload.value.filter, {
      //   key: 'payment.id',
      //   operator: 'EQUALS',
      //   value: idItem.value,
      //   logicalOperation: 'AND'
      // }]
    }
    await getListPaymentDetailSummary()
  }
})

onMounted(async () => {
  if (route?.query?.id) {
    idItem.value = route.query.id.toString() || '0'

    // payload.value.filter = [...payload.value.filter, {
    //   key: 'payment.id',
    //   operator: 'EQUALS',
    //   value: idItem.value,
    //   logicalOperation: 'AND'
    // }]
  }
  else {
    idItem.value = props.selectedPayment.id || '0'
    // payload.value.filter = [...payload.value.filter, {
    //   key: 'payment.id',
    //   operator: 'EQUALS',
    //   value: idItem.value,
    //   logicalOperation: 'AND'
    // }]
  }
  await getListPaymentDetailSummary()
})
</script>

<template>
  <Dialog
    id="dialogPaymentDetailSummary"
    v-model:visible="onOffDialog"
    modal
    :closable="true"
    header="Payment Details"
    :style="{ width: '65%' }"
    :breakpoints="{ '1199px': '75vw', '575px': '90vw' }"
    :pt="{
      root: {
        class: 'custom-dialog-summary',
      },
      header: {
        style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
      },
      // mask: {
      //   style: 'backdrop-filter: blur(5px)',
      // },
    }"
    @hide="onCloseDialog"
  >
    <template #header>
      <div class="flex justify-content-between align-items-center justify-content-between w-full">
        <h5 class="m-0 py-2">
          {{ props.title }}
        </h5>
        <div class="font-bold mr-4">
          <strong class="mx-2">Payment:</strong>
          <strong>{{ props.selectedPayment.paymentId }}</strong>
        </div>
      </div>
    </template>

    <template #default>
      <div class="mt-3">
        <DynamicTable
          :data="[...paymentDetailSummaryList]"
          :columns="columns"
          :options="options"
          :pagination="pagination"
          @on-change-pagination="payloadOnChangePage = $event"
          @on-change-filter="parseDataTableFilter"
          @on-sort-field="onSortField"
        >
          <!-- <template #expansion="slotProps">
            <div class="p-0 m-0">
              <DataTable :value="slotProps.data.children" striped-rows>
                <Column v-for="column of columnsExpandTable" :key="column.field" :field="column.field" :header="column.header" :sortable="column?.sortable" />
                <template #empty>
                  <div class="flex flex-column flex-wrap align-items-center justify-content-center py-8">
                    <span v-if="!options?.loading" class="flex flex-column align-items-center justify-content-center">
                      <div class="row">
                        <i class="pi pi-trash mb-3" style="font-size: 2rem;" />
                      </div>
                      <div class="row">
                        <p>{{ messageForEmptyTable }}</p>
                      </div>
                    </span>
                    <span v-else class="flex flex-column align-items-center justify-content-center">
                      <i class="pi pi-spin pi-spinner" style="font-size: 2.6rem" />
                    </span>
                  </div>
                </template>
              </DataTable>
            </div>
          </template> -->

          <template #datatable-footer>
            <ColumnGroup type="footer" class="flex align-items-center">
              <Row>
                <Column footer="Totals:" :colspan="3" footer-style="text-align:right; font-weight: bold;" />
                <Column :footer="(-Math.abs(Math.round((subTotals.amount + Number.EPSILON) * 100) / 100)).toString()" footer-style="font-weight: bold;" />
                <Column :footer="(-Math.abs(Math.round((subTotals.depositBalance + Number.EPSILON) * 100) / 100)).toString()" footer-style="font-weight: bold;" />
                <Column :colspan="0" />
              </Row>
            </ColumnGroup>
          </template>
        </DynamicTable>
      </div>
    </template>

    <!-- <template #footer>
      <div class="flex justify-content-end align-items-center">
        <Button v-tooltip.top="'Cancel'" class="w-3rem ml-3" icon="pi pi-times" severity="secondary" @click="onCloseDialog" />
      </div>
    </template> -->
  </Dialog>
</template>

<style lang="scss">
.custom-dialog-summary .p-dialog-content {
  background-color: #ffffff;
}
.custom-dialog-summary .p-dialog-footer {
  background-color: #ffffff;
}
</style>

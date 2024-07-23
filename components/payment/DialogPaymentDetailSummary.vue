<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import type { IColumn, IPagination } from '../table/interfaces/ITableInterfaces'
import type { IQueryRequest } from '../fields/interfaces/IFieldInterfaces'
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
const paymentDetailsList = ref<any[]>([])
const idItem = ref(props.selectedPayment.id)
const messageForEmptyTable = ref('The data does not correspond to the selected criteria.')

const products = [
  // {
  //   id: '1000',
  //   code: 'f230fh0g3',
  //   name: 'Bamboo Watch',
  //   description: 'Product Description',
  //   price: 65,
  //   category: 'Accessories',
  //   quantity: 24,
  //   inventoryStatus: 'INSTOCK',
  //   rating: 5,
  //   orders: [
  //     {
  //       id: '1000-0',
  //       productCode: 'f230fh0g3',
  //       date: '2020-09-13',
  //       amount: 65,
  //       quantity: 1,
  //       customer: 'David James',
  //       status: 'PENDING'
  //     },
  //     {
  //       id: '1000-1',
  //       productCode: 'f230fh0g3',
  //       date: '2020-09-14',
  //       amount: 130,
  //       quantity: 2,
  //       customer: 'John Doe',
  //       status: 'DELIVERED'
  //     },
  //     {
  //       id: '1000-2',
  //       productCode: 'f230fh0g3',
  //       date: '2020-09-15',
  //       amount: 195,
  //       quantity: 3,
  //       customer: 'Jane Smith',
  //       status: 'CANCELLED'
  //     },
  //     {
  //       id: '1000-3',
  //       productCode: 'f230fh0g3',
  //       date: '2020-09-16',
  //       amount: 260,
  //       quantity: 4,
  //       customer: 'Michael Brown',
  //       status: 'RETURNED'
  //     },
  //     {
  //       id: '1000-4',
  //       productCode: 'f230fh0g3',
  //       date: '2020-09-17',
  //       amount: 325,
  //       quantity: 5,
  //       customer: 'Alice Green',
  //       status: 'DELIVERED'
  //     }
  //   ]
  // },
  // {
  //   id: '1001',
  //   code: 'nvklal433',
  //   name: 'Black Watch',
  //   description: 'Product Description',
  //   price: 72,
  //   category: 'Accessories',
  //   quantity: 61,
  //   inventoryStatus: 'INSTOCK',
  //   rating: 4,
  //   orders: [
  //     {
  //       id: '1001-0',
  //       productCode: 'nvklal433',
  //       date: '2020-05-14',
  //       amount: 72,
  //       quantity: 1,
  //       customer: 'Jane Doe',
  //       status: 'DELIVERED'
  //     },
  //     {
  //       id: '1001-1',
  //       productCode: 'nvklal433',
  //       date: '2020-05-15',
  //       amount: 144,
  //       quantity: 2,
  //       customer: 'Chris Evans',
  //       status: 'PENDING'
  //     },
  //     {
  //       id: '1001-2',
  //       productCode: 'nvklal433',
  //       date: '2020-05-16',
  //       amount: 216,
  //       quantity: 3,
  //       customer: 'Tom Hardy',
  //       status: 'DELIVERED'
  //     },
  //     {
  //       id: '1001-3',
  //       productCode: 'nvklal433',
  //       date: '2020-05-17',
  //       amount: 288,
  //       quantity: 4,
  //       customer: 'Emma Watson',
  //       status: 'RETURNED'
  //     },
  //     {
  //       id: '1001-4',
  //       productCode: 'nvklal433',
  //       date: '2020-05-18',
  //       amount: 360,
  //       quantity: 5,
  //       customer: 'Robert Downey Jr.',
  //       status: 'CANCELLED'
  //     }
  //   ]
  // },
  // {
  //   id: '1002',
  //   code: 'zz21cz3c1',
  //   name: 'Blue Band',
  //   description: 'Product Description',
  //   price: 79,
  //   category: 'Fitness',
  //   quantity: 2,
  //   inventoryStatus: 'LOWSTOCK',
  //   rating: 3,
  //   orders: [
  //     {
  //       id: '1002-0',
  //       productCode: 'zz21cz3c1',
  //       date: '2020-06-17',
  //       amount: 79,
  //       quantity: 1,
  //       customer: 'John Smith',
  //       status: 'CANCELLED'
  //     },
  //     {
  //       id: '1002-1',
  //       productCode: 'zz21cz3c1',
  //       date: '2020-06-18',
  //       amount: 158,
  //       quantity: 2,
  //       customer: 'Bruce Wayne',
  //       status: 'PENDING'
  //     },
  //     {
  //       id: '1002-2',
  //       productCode: 'zz21cz3c1',
  //       date: '2020-06-19',
  //       amount: 237,
  //       quantity: 3,
  //       customer: 'Clark Kent',
  //       status: 'DELIVERED'
  //     },
  //     {
  //       id: '1002-3',
  //       productCode: 'zz21cz3c1',
  //       date: '2020-06-20',
  //       amount: 316,
  //       quantity: 4,
  //       customer: 'Diana Prince',
  //       status: 'RETURNED'
  //     },
  //     {
  //       id: '1002-4',
  //       productCode: 'zz21cz3c1',
  //       date: '2020-06-21',
  //       amount: 395,
  //       quantity: 5,
  //       customer: 'Barry Allen',
  //       status: 'DELIVERED'
  //     }
  //   ]
  // },
  // {
  //   id: '1003',
  //   code: '244wgerg2',
  //   name: 'Workout Equipment',
  //   description: 'Product Description',
  //   price: 35,
  //   category: 'Fitness',
  //   quantity: 5,
  //   inventoryStatus: 'INSTOCK',
  //   rating: 5,
  //   orders: [
  //     {
  //       id: '1003-0',
  //       productCode: '244wgerg2',
  //       date: '2020-08-02',
  //       amount: 35,
  //       quantity: 1,
  //       customer: 'Alice Brown',
  //       status: 'RETURNED'
  //     },
  //     {
  //       id: '1003-1',
  //       productCode: '244wgerg2',
  //       date: '2020-08-03',
  //       amount: 70,
  //       quantity: 2,
  //       customer: 'Steve Rogers',
  //       status: 'PENDING'
  //     },
  //     {
  //       id: '1003-2',
  //       productCode: '244wgerg2',
  //       date: '2020-08-04',
  //       amount: 105,
  //       quantity: 3,
  //       customer: 'Natasha Romanoff',
  //       status: 'DELIVERED'
  //     },
  //     {
  //       id: '1003-3',
  //       productCode: '244wgerg2',
  //       date: '2020-08-05',
  //       amount: 140,
  //       quantity: 4,
  //       customer: 'Sam Wilson',
  //       status: 'RETURNED'
  //     },
  //     {
  //       id: '1003-4',
  //       productCode: '244wgerg2',
  //       date: '2020-08-06',
  //       amount: 175,
  //       quantity: 5,
  //       customer: 'Bucky Barnes',
  //       status: 'DELIVERED'
  //     }
  //   ]
  // },
  // {
  //   id: '1004',
  //   code: 'h456wer53',
  //   name: 'Gaming Laptop',
  //   description: 'Product Description',
  //   price: 1300,
  //   category: 'Electronics',
  //   quantity: 9,
  //   inventoryStatus: 'INSTOCK',
  //   rating: 4,
  //   orders: [
  //     {
  //       id: '1004-0',
  //       productCode: 'h456wer53',
  //       date: '2020-07-20',
  //       amount: 1300,
  //       quantity: 1,
  //       customer: 'Chris Evans',
  //       status: 'DELIVERED'
  //     },
  //     {
  //       id: '1004-1',
  //       productCode: 'h456wer53',
  //       date: '2020-07-21',
  //       amount: 2600,
  //       quantity: 2,
  //       customer: 'Tom Hardy',
  //       status: 'PENDING'
  //     },
  //     {
  //       id: '1004-2',
  //       productCode: 'h456wer53',
  //       date: '2020-07-22',
  //       amount: 3900,
  //       quantity: 3,
  //       customer: 'Emma Watson',
  //       status: 'DELIVERED'
  //     },
  //     {
  //       id: '1004-3',
  //       productCode: 'h456wer53',
  //       date: '2020-07-23',
  //       amount: 5200,
  //       quantity: 4,
  //       customer: 'Robert Downey Jr.',
  //       status: 'RETURNED'
  //     },
  //     {
  //       id: '1004-4',
  //       productCode: 'h456wer53',
  //       date: '2020-07-24',
  //       amount: 6500,
  //       quantity: 5,
  //       customer: 'Scarlett Johansson',
  //       status: 'CANCELLED'
  //     }
  //   ]
  // }
]

// Table
const columnsExpandTable: IColumn[] = [
  // { field: 'id', header: 'ID', width: '80px', type: 'text' },
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

const expandedRows = ref({})

const columns: IColumn[] = [
  { field: 'transactionDate', header: 'Transaction Date', width: '200px', type: 'date' },
  { field: 'createdAt', header: 'Created At', width: '200px', type: 'date' },
  // { field: 'paymentId', header: 'Payment Id', width: '200px', type: 'text' },

  { field: 'amount', header: 'Amount', width: '200px', type: 'text' },
  { field: 'remark', header: 'Remark', width: '200px', type: 'text' },
]
const options = ref({
  tableName: 'Payment Details',
  moduleApi: 'payment',
  uriApi: 'payment-detail',
  loading: false,
  actionsAsMenu: false,
  expandableRows: true,
  messageToDelete: 'Are you sure you want to delete the account type: {{name}}?'
})

const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [
    {
      key: 'transactionType.deposit',
      logicalOperation: 'OR',
      operator: 'EQUALS',
      value: true
    },
    {
      key: 'transactionType.applyDeposit',
      logicalOperation: 'OR',
      operator: 'EQUALS',
      value: true
    },

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

async function getListPaymentDetail() {
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    options.value.loading = true
    paymentDetailsList.value = []
    const newListItems = []

    const objFilter = payload.value.filter.find(item => item.key === 'payment.id')

    if (objFilter) {
      objFilter.value = idItem.value
    }

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(paymentDetailsList.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'amount')) {
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

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    paymentDetailsList.value = [...paymentDetailsList.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}
function onCloseDialog() {
  onOffDialog.value = false
  emit('update:visible', false)
}

watch(() => props.visible, (newValue) => {
  onOffDialog.value = newValue
  emit('update:visible', newValue)
})

onMounted(async () => {
  if (route?.query?.id) {
    idItem.value = route.query.id.toString()
    payload.value.filter = [...payload.value.filter, {
      key: 'payment.id',
      operator: 'EQUALS',
      value: idItem.value,
      logicalOperation: 'AND'
    }]
  }
  await getListPaymentDetail()
})
</script>

<template>
  <Dialog
    id="dialogPaymentDetailSummary"
    v-model:visible="onOffDialog"
    modal
    :closable="false"
    header="Payment Details"
    :style="{ width: '65%' }"
    :breakpoints="{ '1199px': '75vw', '575px': '90vw' }"
    :pt="{
      root: {
        class: 'custom-dialog',
      },
      header: {
        style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
      },
      mask: {
        style: 'backdrop-filter: blur(5px)',
      },
    }"
  >
    <template #header>
      <div class="flex justify-content-between align-items-center justify-content-between w-full">
        <h5 class="m-0 py-2">
          {{ props.title }}
        </h5>
        <div>
          <strong class="mx-2">Payment:</strong>
          <span>{{ props.selectedPayment.paymentId }}</span>
        </div>
      </div>
    </template>

    <template #default>
      <div class="mt-3">
        <DynamicTable
          :data="[...paymentDetailsList]"
          :columns="columns"
          :options="options"
          :pagination="pagination"
          @on-change-pagination="payloadOnChangePage = $event"
        >
          <template #expansion="slotProps">
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
          </template>
        </DynamicTable>
      </div>
    </template>

    <template #footer>
      <div class="flex justify-content-end align-items-center">
        <Button v-tooltip.top="'Cancel'" class="w-3rem ml-3" icon="pi pi-times" severity="secondary" @click="onCloseDialog" />
      </div>
    </template>
  </Dialog>
</template>

<style lang="scss">
.custom-dialog .p-dialog-content {
  background-color: #ffffff;
}
.custom-dialog .p-dialog-footer {
  background-color: #ffffff;
}
</style>

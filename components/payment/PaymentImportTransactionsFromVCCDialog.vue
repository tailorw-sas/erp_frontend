<script setup lang="ts">
import { onMounted, ref } from 'vue'
import type { PageState } from 'primevue/paginator'
import type { IColumn, IPagination, IStatusClass } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import { GenericService } from '~/services/generic-services'

const props = defineProps({
  openDialog: {
    type: Boolean,
    required: true
  },
  closeDialog: {
    type: Function as any,
    required: true
  },
  selectedPayment: {
    type: Object,
    required: true
  }
})

const dialogVisible = ref(props.openDialog)
const listItems = ref<any[]>([])

const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})
const payloadOnChangePage = ref<PageState>()
const activeStatusFilter: IFilter[] = [
  {
    key: 'status',
    operator: 'EQUALS',
    value: 'ACTIVE',
    logicalOperation: 'AND'
  }
]

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Payment Transaction Items',
  moduleApi: 'creditcard',
  uriApi: 'hotel-payment',
  loading: false,
  actionsAsMenu: false,
  expandableRows: true,
  showTitleBar: true,
  messageToDelete: 'Do you want to save the change?',
})

const paymentOptions = ref({
  tableName: 'Manage Payment',
  moduleApi: 'payment',
  uriApi: 'payment',
  loading: false,
  actionsAsMenu: true,
  showFilters: false,
  showSelectedItems: false,
  showTitleBar: true,
  showPagination: false,
  messageToDelete: 'Are you sure you want to delete the account type: {{name}}?'
})

const sClassMapPayment: IStatusClass[] = [
  { status: 'Transit', class: 'text-transit' },
  { status: 'Cancelled', class: 'text-cancelled' },
  { status: 'Confirmed', class: 'text-confirmed' },
  { status: 'Applied', class: 'text-applied' },
]

const sClassMap: IStatusClass[] = [
  { status: 'In Progress', class: 'vcc-text-created' },
  { status: 'Completed', class: 'vcc-text-sent' },
  { status: 'Cancelled', class: 'vcc-text-cancelled' },
  { status: 'Applied', class: 'vcc-text-reconciled' },
]
// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'hotelPaymentId', header: 'Id', type: 'text' },
  { field: 'transactionDate', header: 'Trans Date', type: 'date' },
  { field: 'manageHotel', header: 'Hotel', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-hotel', filter: activeStatusFilter }, sortable: true },
  { field: 'amount', header: 'Amount', type: 'number' },
  { field: 'commission', header: 'Commission', type: 'number' },
  { field: 'netAmount', header: 'Total', type: 'number' },
  { field: 'status', header: 'Status', type: 'slot-select', frozen: true, statusClassMap: sClassMap, objApi: { moduleApi: 'creditcard', uriApi: 'manage-payment-transaction-status', filter: activeStatusFilter }, sortable: true },
]

const paymentColumns: IColumn[] = [
  { field: 'paymentId', header: 'ID', tooltip: 'Payment ID', width: '40px', type: 'text', showFilter: false },
  { field: 'hotel', header: 'Hotel', width: '80px', widthTruncate: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-hotel' } },
  { field: 'agency', header: 'Agency', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-agency' } },
  { field: 'paymentAmount', header: 'P. Amount', tooltip: 'Payment Amount', width: '70px', type: 'number' },
  { field: 'paymentStatus', header: 'Status', width: '100px', type: 'slot-select', statusClassMap: sClassMapPayment, objApi: { moduleApi: 'settings', uriApi: 'manage-payment-status' }, sortable: true },
]

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

async function getList() {
  const count = { amount: 0, commission: 0, net: 0 }
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    options.value.loading = true
    listItems.value = []
    const newListItems = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'manageHotel') && iterator.hotel) {
        iterator.manageHotel = { id: iterator.manageHotel.id, name: `${iterator.manageHotel.code} - ${iterator.manageHotel.name}` }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'amount')) {
        count.amount += iterator.amount
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'commission')) {
        count.commission += iterator.commission
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'netAmount')) {
        count.net += iterator.netAmount
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'manageBankAccount')) {
        iterator.merchantBankAccountNumber = iterator.manageBankAccount?.accountNumber
      }
      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator, loadingEdit: false, loadingDelete: false, invoiceDate: new Date(iterator?.invoiceDate) })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    listItems.value = [...listItems.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}

async function resetListItems() {
  payload.value.page = 0
  payload.value.filter = [{
    key: 'manageHotel.id',
    operator: 'EQUALS',
    value: props.selectedPayment.hotel.id,
    logicalOperation: 'AND',
    type: 'filterSearch'
  }]
  getList()
}

onMounted(() => {
  resetListItems()
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
      // mask: {
      //   style: 'backdrop-filter: blur(5px)',
      // },
    }"
    @hide="closeDialog()"
  >
    <template #header>
      <div class="flex justify-content-between align-items-center w-full">
        <div>
          <h5 class="m-0">
            Import Transactions from VCC
          </h5>
        </div>
        <div class="font-bold mr-5">
          <h5 class="m-0">
            Payment Id: {{ selectedPayment.paymentId }}
          </h5>
        </div>
      </div>
    </template>
    <template #default>
      <div class="p-fluid pt-3">
        <DynamicTable
          component-table-id="import-payment-vcc"
          :data="[props.selectedPayment]" :columns="paymentColumns" :options="paymentOptions"
          :is-custom-sorting="false"
        >
          <template #datatable-toolbar>
            <Toolbar style="border-radius: 6px 6px 0 0; background-color: var(--primary-color)" class="mb-0">
              <template #start>
                <h6 class="my-0 text-white">
                  Payment
                </h6>
              </template>
            </Toolbar>
          </template>
          <template #column-paymentStatus="{ data, column }">
            <Badge
              v-tooltip.top="data.paymentStatus.name.toString()"
              :value="data.paymentStatus.name"
              :class="column.statusClassMap?.find((e: any) => e.status === data.paymentStatus.name)?.class"
            />
          </template>
        </DynamicTable>
        <div class="mt-3">
          <DynamicTable
            component-table-id="import-payment-vcc"
            :data="listItems"
            :columns="columns"
            :options="options"
            :pagination="pagination"
            @on-change-pagination="payloadOnChangePage = $event"
            @on-change-filter="parseDataTableFilter"
            @on-list-item="resetListItems"
            @on-sort-field="onSortField"
          >
            <template #datatable-toolbar>
              <Toolbar style="border-radius: 6px 6px 0 0; background-color: var(--primary-color)" class="mb-0">
                <template #start>
                  <h6 class="my-0 text-white">
                    {{ options.tableName }}
                  </h6>
                </template>
              </Toolbar>
            </template>
            <template #expansion="{ data: item }">
              <!--          <pre>{{item}}</pre> -->
              <HotelPaymentTransactions
                :hotel-payment-id="item.id" :hide-bind-transaction-menu="item.status && (item.status.completed || item.status.cancelled)"
                @update:list="($event) => {
                  getList()
                }"
              />
            </template>
            <template #column-icon="{ data: objData, column }">
              <div class="flex align-items-center justify-content-center p-0 m-0">
                <!-- <pre>{{ objData }}</pre> -->
                <Button
                  v-if="objData.hasAttachments"
                  :icon="column.icon"
                  class="p-button-rounded p-button-text w-2rem h-2rem"
                  aria-label="Submit"
                  :style="{ color: '#000' }"
                />
              </div>
              <!-- style="color: #616161;" -->
              <!-- :style="{ 'background-color': '#00b816' }" -->
            </template>
            <template #column-status="{ data, column }">
              <Badge
                v-tooltip.top="data.status.name.toString()"
                :value="data.status.name"
                :class="column.statusClassMap?.find((e: any) => e.status === data.status.name)?.class"
              />
            </template>
          </DynamicTable>
        </div>
      </div>
    </template>
  </Dialog>
</template>

<style lang="scss">
  #import-payment-vcc .p-datatable-thead > tr > th {
    border: none !important; /* Sobrescribe el borde */
  }
</style>

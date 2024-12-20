<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'

const props = defineProps({
  header: {
    type: String,
    required: true
  },
  openDialog: {
    type: Boolean,
    required: true
  },
  closeDialog: {
    type: Function as any,
    required: true
  },
  selectedInvoice: {
    type: String,
    required: true
  },
  selectedInvoiceObj: {
    type: Object,
    required: true
  },
})

const invoice = ref(props.selectedInvoiceObj)

const Columns: IColumn[] = [
  { field: 'invoiceHistoryId', header: 'Id', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'invoice.invoiceId', header: 'Invoice Id', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'createdAt', header: 'Date', type: 'date', width: '90px', sortable: false, showFilter: false },
  { field: 'employee', header: 'Employee', type: 'text', width: '100px', sortable: false, showFilter: false },
  { field: 'description', header: 'Remark', type: 'text', width: '200px', sortable: false, showFilter: false },
  { field: 'invoiceStatus', header: 'Status', type: 'slot-text', width: '100px', sortable: false, showFilter: false },
]

const dialogVisible = ref(props.openDialog)
const options = ref({
  tableName: 'Income Status History',
  moduleApi: 'invoicing',
  uriApi: 'invoice-status-history',
  loading: false,
  showDelete: false,
  showFilters: false,
  actionsAsMenu: false,
  showPagination: false,
  messageToDelete: 'Do you want to save the change?'
})

const Pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const PayloadOnChangePage = ref<PageState>()

const Payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})

const ListItems = ref<any[]>([])
const idItemToLoadFirstTime = ref('')

function OnSortField(event: any) {
  if (event) {
    Payload.value.sortBy = event.sortField
    Payload.value.sortType = event.sortOrder
    getList()
  }
}

async function getList() {
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    ListItems.value = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, Payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'invoice')) {
        iterator['invoice.invoiceId'] = iterator.invoice.invoiceId
        iterator.invoiceStatus = iterator.invoice.status
      }
      ListItems.value = [...ListItems.value, { ...iterator, loadingEdit: false, loadingDelete: false }]
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}
async function ParseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, Columns)
  Payload.value.filter = [...parseFilter || []]
  getList()
}

function getStatusBadgeBackgroundColorByItem(item: string) {
  if (!item) { return }
  if (item === 'PROCESSED') { return '#FF8D00' }
  if (item === 'SENT') { return '#006400' }
  if (item === 'RECONCILED') { return '#005FB7' }
  if (item === 'CANCELED') { return '#888888' }
}

watch(() => props.selectedInvoiceObj, () => {
  invoice.value = props.selectedInvoiceObj
})

onMounted(() => {
  if (props.selectedInvoice) {
    Payload.value.filter = [{
      key: 'invoice.id',
      operator: 'EQUALS',
      value: props.selectedInvoice,
      logicalOperation: 'AND'
    }]
  }
  getList()
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal
    content-class="border-round-bottom border-top-1 surface-border h-fit"
    :block-scroll="true"
    style="width: 50%;" :pt="{
      header: {
        style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
      },
    }"
    @hide="closeDialog"
  >
    <template #header>
      <div class="flex justify-content-between w-full">
        <h5 class="m-0">
          {{ props.header }}
        </h5>
        <h5 class="m-0 mr-2">
          Invoice Id: {{ props.selectedInvoiceObj?.incomeId }}
        </h5>
      </div>
    </template>
    <template #default>
      <div class="p-fluid mt-4">
        <div class="flex flex-column" style="width: 100%;overflow: auto;">
          <DynamicTable
            class="card p-0"
            :data="ListItems"
            :columns="Columns"
            :options="options"
            :pagination="Pagination"
            @on-change-pagination="PayloadOnChangePage = $event"
            @on-change-filter="ParseDataTableFilter"
            @on-sort-field="OnSortField"
          >
            <template #column-invoiceStatus="{ data }">
              <Badge
                :value="data.invoiceStatus"
                :style="`background-color: ${getStatusBadgeBackgroundColorByItem(data.invoiceStatus)}`"
              />
            </template>
          </DynamicTable>
        </div>
      </div>
    </template>
  </Dialog>
</template>

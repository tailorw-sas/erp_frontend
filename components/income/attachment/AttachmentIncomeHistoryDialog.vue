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
  attachmentType: {
    type: Object,
    required: true
  },
})

const invoice = ref(props.selectedInvoiceObj)

const Columns: IColumn[] = [
  { field: 'invoiceId', header: 'Income Id', type: 'text', width: '100px' },
  { field: 'attachmentId', header: 'Id', type: 'text', width: '70px' },
  { field: 'createdAt', header: 'Date', type: 'date', width: '90px' },
  { field: 'employee', header: 'Employee', type: 'text', width: '100px' },
  { field: 'description', header: 'Remark', type: 'text', width: '200px' },
  { field: 'status', header: 'Status', type: 'text', width: '80px', sortable: false },
]

const dialogVisible = ref(props.openDialog)
const options = ref({
  tableName: 'Income Status History',
  moduleApi: 'invoicing',
  uriApi: 'attachment-status-history',
  loading: false,
  showDelete: false,
  showFilters: false,
  actionsAsMenu: false,
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
        iterator.invoiceId = iterator.invoice.invoiceId
      }
      ListItems.value = [...ListItems.value, { ...iterator, loadingEdit: false, loadingDelete: false, status: props.attachmentType.status }]
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
    v-model:visible="dialogVisible" modal class="mx-3 sm:mx-0"
    content-class="border-round-bottom border-top-1 surface-border" :block-scroll="true"
    :style="{ width: '60%' }"
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
    @hide="closeDialog"
  >
    <template #header>
      <div class="flex justify-content-between">
        <h5 class="m-0">
          {{ props.header }}
        </h5>
      </div>
    </template>
    <template #default>
      <div class="p-fluid pt-3">
        <DynamicTable
          class="card p-0"
          :data="ListItems"
          :columns="Columns"
          :options="options"
          :pagination="Pagination"
          @on-change-pagination="PayloadOnChangePage = $event"
          @on-change-filter="ParseDataTableFilter"
          @on-sort-field="OnSortField"
        />
      </div>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination, IStatusClass } from '~/components/table/interfaces/ITableInterfaces'
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
  selectedTransaction: {
    type: Object,
    required: true
  },
  sClassMap: {
    type: Object as () => IStatusClass[],
    required: true
  }
})

const Columns: IColumn[] = [
  { field: 'historyId', header: 'Id', type: 'text', width: '70px', sortable: false, showFilter: false },
  { field: 'transactionId', header: 'Transaction Id', type: 'text', width: '70px', sortable: false, showFilter: false },
  { field: 'createdAt', header: 'Date', type: 'date', width: '100px', sortable: false, showFilter: false },
  { field: 'employeeName', header: 'Employee', type: 'text', width: '100px', sortable: false, showFilter: false },
  { field: 'description', header: 'Remark', type: 'text', width: '200px', sortable: false, showFilter: false },
  { field: 'statusName', header: 'Status', type: 'custom-badge', statusClassMap: props.sClassMap, width: '100px', sortable: false, showFilter: false },
]

const dialogVisible = ref(props.openDialog)
const options = ref({
  tableName: 'Transactions Status History',
  moduleApi: 'creditcard',
  uriApi: 'transaction-status-history',
  loading: false,
  showDelete: false,
  showFilters: false,
  actionsAsMenu: false,
  showPagination: false,
  messageToDelete: 'Do you want to save the change?'
})

const Pagination = ref<IPagination>({
  page: 0,
  limit: 1000,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const PayloadOnChangePage = ref<PageState>()

const Payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10000,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})

const ListItems = ref<any[]>([])
const idItemToLoadFirstTime = ref('')

async function ResetListItems() {
  Payload.value.page = 0
}

function OnSortField(event: any) {
  if (event) {
    Payload.value.sortBy = getSortField(event.sortField)
    Payload.value.sortType = event.sortOrder
    getList()
  }
}

function getSortField(field: any) {
  switch (field) {
    case 'statusName':
      return 'transactionStatus.name'
    case 'employeeName':
      return 'employee.firstName'
    case 'transactionId':
      return 'transaction.id'

    default: return field
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
      ListItems.value = [
        ...ListItems.value,
        {
          ...iterator,
          loadingEdit: false,
          loadingDelete: false,
          transactionId: iterator?.transaction?.id,
          statusName: iterator?.transactionStatus?.name,
          employeeName: iterator?.employee ? `${iterator.employee.firstName} ${iterator.employee.lastName}` : ''
        }
      ]
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

onMounted(() => {
  if (props.selectedTransaction) {
    Payload.value.filter = [{
      key: 'transaction.id',
      operator: 'EQUALS',
      value: props.selectedTransaction.id,
      logicalOperation: 'AND'
    }]
  }
  getList()
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal
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
    @hide="closeDialog"
  >
    <template #header>
      <div class="flex justify-content-between w-full">
        <div class="flex align-items-center">
          <h5 class="m-0">
            {{ options.tableName }}
          </h5>
        </div>
        <div class="flex align-items-center">
          <h5 class="m-0 mr-4">
            Transaction ID: {{ props.selectedTransaction.id }}
          </h5>
        </div>
      </div>
    </template>
    <template #default>
      <div class="p-fluid pt-3">
        <div class="flex flex-column" style="width: 100%;overflow: auto;">
          <DynamicTable
            :data="ListItems" :columns="Columns"
            :options="options" :pagination="Pagination"
            @on-change-pagination="PayloadOnChangePage = $event" @on-change-filter="ParseDataTableFilter"
            @on-list-item="ResetListItems" @on-sort-field="OnSortField"
          />
        </div>
      </div>
    </template>
  </Dialog>
</template>

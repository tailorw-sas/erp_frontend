<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'

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
  isCreationDialog: {
    type: Boolean,
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
  selectedAttachment: {
    type: String,
    required: true
  },
  listItems: {
    type: Array,
    required: false,
    default: () => []
  }
})

const invoice = ref(props.selectedInvoiceObj)

const formReload = ref(0)

const idItem = ref('')
const item = ref<GenericObject>({
  type: null,
  filename: '',
  file: '',
  remark: '',
  invoice: props.selectedInvoice,
  attachmentId: '',
  resource: invoice.value.invoiceId,
  resourceType: `${invoice.value.invoiceType?.name || OBJ_ENUM_INVOICE[invoice.value.invoiceType]}`
})

const itemTemp = ref<GenericObject>({
  type: null,
  filename: '',
  file: '',
  remark: '',
  invoice: props.selectedInvoice,
  attachmentId: '',
  resource: '',
  resourceType: '',
})
const toast = useToast()

const Columns: IColumn[] = [
  { field: 'attachmentId', header: 'Id', type: 'text', width: '70px' },
  { field: 'invoiceId', header: 'Invoice Id', type: 'text', width: '70px' },
  { field: 'createdAt', header: 'Date', type: 'datetime', width: '90px' },
  { field: 'employee', header: 'Employee', type: 'text', width: '100px' },

  { field: 'description', header: 'Remark', type: 'text', width: '200px' },
  { field: 'status', header: 'Status', type: 'text', width: '100px' },

]

const incomeColumns: IColumn[] = [
  { field: 'attachmentId', header: 'Id', type: 'text', width: '70px' },
  { field: 'invoiceId', header: 'Invoice Id', type: 'text', width: '70px' },
  { field: 'createdAt', header: 'Date', type: 'datetime', width: '90px' },
  { field: 'employee', header: 'Employee', type: 'text', width: '100px' },
  { field: 'status', header: 'Status', type: 'text', width: '100px' },

]

const dialogVisible = ref(props.openDialog)
const options = ref({
  tableName: 'Invoice',
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
    case 'status':
      return 'type.status'

    case 'invoiceId' :
      return 'invoice.invoiceId'

    default: return field
  }
}

function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''

  formReload.value++
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
      ListItems.value = [...ListItems.value, { ...iterator, loadingEdit: false, loadingDelete: false, invoiceId: iterator?.invoice?.invoiceId, status: iterator?.type?.status || 'ACTIVE' }]
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
  if (props.selectedAttachment) {
    Payload.value.filter = [{
      key: 'attachmentId',
      operator: 'EQUALS',
      value: props.selectedAttachment,
      logicalOperation: 'AND'
    }]
  }

  if (props.selectedInvoice && !props.selectedAttachment) {
    Payload.value.filter = [{
      key: 'invoice.id',
      operator: 'EQUALS',
      value: props.selectedInvoice,
      logicalOperation: 'AND'
    }]
  }

  if (!props.isCreationDialog) {
    getList()
  }
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal :header="props.selectedInvoiceObj?.invoiceType === InvoiceType.INVOICE ? 'Income Status History' : header" class="p-4 h-fit w-fit"
    content-class="border-round-bottom border-top-1 surface-border h-fit" :block-scroll="true"
    style="width: 800px;" @hide="closeDialog"
  >
    <div class=" h-fit overflow-hidden mt-4">
      <div class="flex flex-row align-items-center">
        <div class="flex flex-column" style="max-width: 900px;overflow: auto;">
          <DynamicTable
            :data="isCreationDialog ? listItems as any : ListItems" :columns="props.selectedInvoiceObj?.invoiceType === InvoiceType.INVOICE ? incomeColumns : Columns"
            :options="options" :pagination="Pagination"

            @on-confirm-create="clearForm"
            @on-change-pagination="PayloadOnChangePage = $event" @on-change-filter="ParseDataTableFilter" @on-list-item="ResetListItems" @on-sort-field="OnSortField"
          />
          <div class=" flex w-full justify-content-end ">
            <Button
              v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem mx-1" icon="pi pi-times" @click="() => {

                clearForm()
                closeDialog()
              }"
            />
          </div>
        </div>
      </div>
    </div>
  </Dialog>
</template>

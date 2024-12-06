<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { jsPDF } from 'jspdf'
import autoTable from 'jspdf-autotable'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
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

const attachmentTypeList = ref<any[]>([])
const confattachmentTypeListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-attachment-type',
})

const formReload = ref(0)
const loadingSaveAll = ref(false)
const confirm = useConfirm()

console.log(invoice.value)

const loadingDelete = ref(false)

const idItem = ref('')
const item = ref<GenericObject>({
  type: null,
  filename: '',
  file: '',
  remark: '',
  invoice: props.selectedInvoice,
  attachmentId: '',
  resource: invoice.value.invoiceId,
  // @ts-expect-error
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
  { field: 'invoiceId', header: 'Invoice Id', type: 'text', width: '90px', sortable: false, showFilter: false },
  { field: 'createdAt', header: 'Date', type: 'datetime', width: '90px', sortable: false, showFilter: false },
  { field: 'employee', header: 'Employee', type: 'text', width: '100px', sortable: false, showFilter: false },
  { field: 'description', header: 'Remark', type: 'text', width: '200px', sortable: false, showFilter: false },
  { field: 'invoiceStatus', header: 'Status', type: 'slot-text', width: '100px', sortable: false, showFilter: false },

]

const dialogVisible = ref(props.openDialog)
const options = ref({
  tableName: 'Invoice',
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

async function ResetListItems() {
  Payload.value.page = 0
}

function OnSortField(event: any) {
  if (event) {
    console.log(event)
    Payload.value.sortBy = getSortField(event.sortField)
    Payload.value.sortType = event.sortOrder
    getList()
  }
}

function getSortField(field: any) {
  switch (field) {
    case 'status':
      return 'invoiceStatus'

    case 'invoiceId':
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
    console.log(response)

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
          invoiceId: iterator?.invoice?.invoiceId,
          // @ts-expect-error
          invoiceStatus: OBJ_INVOICE_STATUS_NAME[iterator?.invoiceStatus === 'CANCELED' ? 'CANCELLED' : iterator?.invoiceStatus]
        }
      ]
      // status: OBJ_INVOICE_STATUS_NAME[iterator?.invoiceStatus || iterator?.invoice?.status] }]
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

async function getAttachmentTypeList() {
  try {
    const payload
      = {
        filter: [],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: ENUM_SHORT_TYPE.DESC
      }

    attachmentTypeList.value = []
    const response = await GenericService.search(confattachmentTypeListApi.moduleApi, confattachmentTypeListApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      attachmentTypeList.value = [...attachmentTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading Attachment Type list:', error)
  }
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }

    const file = typeof item?.file === 'object' ? await GenericService.getUrlByImage(item?.file) : item?.file

    payload.invoice = props.selectedInvoice

    payload.file = file

    payload.type = item.type?.id
    await GenericService.create(options.value.moduleApi, options.value.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.type = item.type?.id
  await GenericService.update(options.value.moduleApi, options.value.uriApi, idItem.value || '', payload)
}

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
    v-model:visible="dialogVisible"
    modal
    :header="props.selectedInvoiceObj?.invoiceType === InvoiceType.INCOME || props.selectedInvoiceObj?.invoiceType?.id === InvoiceType.INCOME ? 'Income Status History' : header"
    class="p-4"
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
      <div class="flex justify-content-between">
        <h5 class="m-0">
          {{ props.selectedInvoiceObj?.invoiceType === InvoiceType.INCOME || props.selectedInvoiceObj?.invoiceType?.id === InvoiceType.INCOME ? 'Income Status History' : header }}
        </h5>
      </div>
    </template>
    <div class=" h-fit overflow-hidden mt-4">
      <div class="flex flex-row align-items-center">
        <div class="flex flex-column" style="width: 100%;overflow: auto;">
          <DynamicTable
            :data="isCreationDialog ? listItems as any : ListItems" :columns="incomeColumns"
            :options="options" :pagination="Pagination" @on-confirm-create="clearForm"
            @on-change-pagination="PayloadOnChangePage = $event" @on-change-filter="ParseDataTableFilter"
            @on-list-item="ResetListItems" @on-sort-field="OnSortField"
          >
            <template #column-invoiceStatus="{ data }">
              <Badge
                :value="data.invoiceStatus"
                :style="`background-color: ${getStatusBadgeBackgroundColorByItem(data.invoiceStatus)}`"
              />
            </template>
          </DynamicTable>
          <div v-if="false" class=" flex w-full justify-content-end ">
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

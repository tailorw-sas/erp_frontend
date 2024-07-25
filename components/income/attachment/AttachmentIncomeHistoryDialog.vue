<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import {jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';
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
  attachment_id: '',
  resource: invoice.value.incomeId,
  // @ts-expect-error
  resourceType: `${invoice.value.invoiceType?.name || OBJ_ENUM_INVOICE[invoice.value.invoiceType]}`
})

const itemTemp = ref<GenericObject>({
  type: null,
  filename: '',
  file: '',
  remark: '',
  invoice: props.selectedInvoice,
  attachment_id: '',
  resource: '',
  resourceType: '',
})
const toast = useToast()

const Fields: Array<Container> = [
  {
    childs: [
      {
        field: 'resource',
        header: 'Resource',
        dataType: 'number',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        disabled: true
      },
      {
        field: 'resourceType',
        header: 'Resource Type',
        dataType: 'text',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        disabled: true
      },

      {
        field: 'type',
        header: 'Attachment Type',
        dataType: 'select',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        validation: z.object({
          id: z.string(),
          name: z.string(),

        }).nullable()
          .refine((value: any) => value && value.id && value.name, { message: `The Transaction Type field is required` })
          .refine((value: any) => value.status !== 'ACTIVE', {
            message: `This Attachment Type is not active`
          })

      },
      {
        field: 'filename',
        header: 'Filename',
        dataType: 'text',
        class: 'field col-12 required',
        headerClass: 'mb-1',

      },
      {
        field: 'file',
        header: 'Path',
        dataType: 'fileupload',
        class: 'field col-12 required',
        headerClass: 'mb-1',

      },
      {
        field: 'remark',
        header: 'Remark',
        dataType: 'textarea',
        class: 'field col-12',
        headerClass: 'mb-1',

      },

    ],
    containerClass: 'w-full'
  }

]

const Columns: IColumn[] = [
  { field: 'attachment_id', header: 'Id', type: 'text', width: '70px' },
  { field: 'invoice_id', header: 'Income Id', type: 'text', width: '70px' },
  { field: 'createdAt', header: 'Date', type: 'date', width: '90px' },
  { field: 'employee', header: 'Employee', type: 'text', width: '100px' },
  
  { field: 'remark', header: 'Remark', type: 'text', width: '200px' },
  { field: 'status', header: 'Status', type: 'text', width: '100px' },

]

const dialogVisible = ref(props.openDialog)
const options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-attachment',
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
    Payload.value.sortBy = event.sortField
    Payload.value.sortType = event.sortOrder
    getList()
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
      ListItems.value = [...ListItems.value, { ...iterator, loadingEdit: false, loadingDelete: false, invoice_id: iterator?.invoice?.invoiceId, status: 'NON-NONE' }]
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

async function saveItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  let successOperation = true

  if (idItem.value) {
    try {
      if (props.isCreationDialog) {
        await props.updateItem(item)
        clearForm()
        return loadingSaveAll.value = false
      }
      await updateItem(item)
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
    idItem.value = ''
  }
  else {
    try {
      if (props.isCreationDialog) {
        await props.addItem(item)
        clearForm()
        return loadingSaveAll.value = false
      }
      await createItem(item)
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  if (successOperation) {
    clearForm()
    getList()
  }
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
  if (!props.isCreationDialog) {
    getList()
  }
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal :header="header" class="p-4 h-fit w-fit"
    content-class="border-round-bottom border-top-1 surface-border h-fit" :block-scroll="true"
    style="width: 800px;" @hide="closeDialog"
  >
    <div class=" h-fit overflow-hidden mt-4">
      <div class="flex flex-row align-items-center">
        <div class="flex flex-column" style="max-width: 900px;overflow: auto;">
          <DynamicTable
            :data="isCreationDialog ? listItems as any : ListItems" :columns="Columns"
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

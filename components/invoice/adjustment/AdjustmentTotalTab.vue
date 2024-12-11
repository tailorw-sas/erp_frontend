<script setup lang="ts">
import { ref, watch } from 'vue'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import type { PageState } from 'primevue/paginator'
import { v4 } from 'uuid'
import AdjustmentTotalDialog from './AdjustmentTotalDialog.vue'
import getUrlByImage from '~/composables/files'
import { ModulesService } from '~/services/modules-services'
import { GenericService } from '~/services/generic-services'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import type { GenericObject } from '~/types'
import type { IData } from '~/components/table/interfaces/IModelData'

const props = defineProps({
  isDialogOpen: {
    type: Boolean,
    required: true
  },
  closeDialog: {
    type: Function as any,
    required: true
  },
  openDialog: {
    type: Function as any,
    required: true
  },
  selectedRoomRate: {
    type: String,
    required: true
  },
  forceUpdate: {
    type: Boolean,
    required: false
  },
  toggleForceUpdate: {
    type: Function as any,
    required: true,

  },
  selectedInvoice: {
    type: String,
    required: false
  },
  invoiceObj: {
    type: Object,
    required: true,
  },
  isCreationDialog: {
    type: Boolean,
    required: true
  },
  listItems: {
    type: Array,
    required: false
  },
  addItem: {
    type: Function as any,
    required: false
  },
  updateItem: {
    type: Function as any,
    required: false
  },
  showTotals: {
    type: Boolean,
    required: false,
    default: false
  },
  isTabView: {
    type: Boolean,
    default: false
  },
  isDetailView: {
    type: Boolean,
    required: false,
    default: false,
  },
  sortAdjustment: Function as any,
  refetchInvoice: { type: Function, default: () => { } },
  invoiceObjAmount: { type: Number, required: true },
  adjustmentTotalObj: {
    type: Object,
    required: false,
    default: () => {
      return {
        totalAmount: 0,
      }
    }
  }
})

const { data } = useAuth()

const toast = useToast()
const loadingSaveAll = ref(false)
const confirm = useConfirm()
const ListItems = ref<any[]>([])
const formReload = ref(0)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const loadingDelete = ref(false)
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})
const dialogOpen = ref<boolean>(props.isDialogOpen)

const totalAmount = ref<number>(0)

const transactionTypeList = ref<any[]>([])

const selectedAdjustment = ref<any>()
const adjustmentContextMenu = ref()

const transactionTypeApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-invoice-transaction-type',
  keyValue: 'name'
})

const Fields: Array<Container> = [
  {
    childs: [
      {
        field: 'amount',
        header: 'Amount',
        dataType: 'number',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        validation: z.number({ invalid_type_error: 'The Amount field is required' }).refine((value: number) => {
          if (!value) { return false }
          return true
        }, { message: 'The Amount field cannot be 0' })
      },
      {
        field: 'date',
        header: 'Date',
        dataType: 'date',
        class: 'field col-12 md: required ',
        headerClass: 'mb-1',

      },

      {
        field: 'transactionType',
        header: 'Transaction',
        dataType: 'select',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        validation: z.object({
          id: z.string(),
          name: z.string(),

        }).nullable()
          .refine((value: any) => value && value.id && value.name, { message: `The Transaction Type field is required` })

      },
      {
        field: 'description',
        header: 'Remark',
        dataType: 'text',
        class: 'field col-12',
        headerClass: 'mb-1',

      },

    ],
    containerClass: 'w-full'
  }

]

const IncomeAttachmentFields: Array<Container> = [
  {
    childs: [
      {
        field: 'amount',
        header: 'Amount',
        dataType: 'number',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        validation: z.number().refine((value: number) => {
          if (!value) { return false }
          return true
        }, { message: 'The Amount field cannot be 0' })
      },
      {
        field: 'date',
        header: 'Date',
        dataType: 'date',
        class: 'field col-12 md: required ',
        headerClass: 'mb-1',

      },

      {
        field: 'transactionType',
        header: 'Transaction',
        dataType: 'select',
        class: 'field col-12',
        headerClass: 'mb-1',

      },
      {
        field: 'description',
        header: 'Remark',
        dataType: 'text',
        class: 'field col-12',
        headerClass: 'mb-1',

      },

    ],
    containerClass: 'w-full'
  }

]

const item = ref<GenericObject>({
  amount: 0,
  date: new Date(),
  transactionType: null,
  roomRate: '',
  description: '',
})

const itemTemp = ref<GenericObject>({
  amount: 0,
  date: new Date(),
  transactionType: null,
  roomRate: '',
  description: '',
})

const confApi = reactive({
  adjustment: {
    moduleApi: 'invoicing',
    uriApi: 'manage-adjustment',
  },

  invoice: {
    moduleApi: 'invoicing',
    uriApi: 'manage-invoice',
  },
})

const Columns: IColumn[] = [

  { field: 'adjustmentId', header: 'Id', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'amount', header: 'Adjustment Amount', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'roomRateId', header: 'Room Rate Id', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'transaction', header: 'Category', type: 'select', objApi: transactionTypeApi, sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'date', header: 'Transaction Date', type: 'date', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'employee', header: 'Employee', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },
  { field: 'description', header: 'Description', type: 'text', sortable: !props.isDetailView && !props.isCreationDialog },

]

const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'name', name: 'Name' },
  { id: 'description', name: 'Description' },
]

async function openEditDialog(item: any) {
  console.log(item)
  props.openDialog()
  if (item?.id) {
    idItem.value = item?.id
    idItemToLoadFirstTime.value = item?.id
    await GetItemById(item?.id)
  }

  if (typeof item === 'string') {
    idItem.value = item
    idItemToLoadFirstTime.value = item
    await GetItemById(item)
  }
}
const menuModel = ref([

  { label: 'Edit Adjustment', command: () => openEditDialog(selectedAdjustment.value) },

])

const Options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-adjustment',
  messageToDelete: 'Do you want to save the change?',
  loading: false,
  showFilters: false,
  actionsAsMenu: false,

})

const route = useRoute()

const PayloadOnChangePage = ref<PageState>()
const Payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'adjustmentId',
  sortType: ENUM_SHORT_TYPE.ASC
})
const Pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

// FUNCTIONS ---------------------------------------------------------------------------------------------
function ClearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''
  formReload.value++
}

const errorInTab = ref({
  tabGeneralData: false,
  tabMedicalInfo: false,
  tabServices: false,
  tabPermissions: false
})

const OpenCreateDialog = async () => await navigateTo({ path: 'invoice/create' })

const OpenEditDialog = async (item: any) => await navigateTo({ path: `invoice/edit/${item}` })

async function getAdjustmentList() {
  try {
    idItemToLoadFirstTime.value = ''
    Options.value.loading = true
    ListItems.value = []
    totalAmount.value = 0

    const response = await GenericService.search(Options.value.moduleApi, Options.value.uriApi, Payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      let transaction = { ...iterator?.transaction, name: `${iterator?.transaction?.code || ''}-${iterator?.transaction?.name || ''}` }

      if (iterator?.invoice?.invoiceType === InvoiceType.INCOME) {
        transaction = { ...iterator?.paymentTransactionType, name: `${iterator?.paymentTransactionType?.code || ''}-${iterator?.paymentTransactionType?.name || ''}` }
      }

      ListItems.value = [...ListItems.value, {
        ...iterator,
        loadingEdit: false,
        loadingDelete: false,
        roomRateId: iterator?.roomRate?.roomRateId,
        date: iterator?.date,

      }]

      if (typeof +iterator?.amount === 'number') {
        totalAmount.value += Number(iterator?.amount)
      }
    }
    if (ListItems.value.length > 0) {
      idItemToLoadFirstTime.value = ListItems.value[0].id
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    Options.value.loading = false
  }
}

const goToList = async () => await navigateTo('/invoice')

async function ResetListItems() {
  Payload.value.page = 0
}

function searchAndFilter() {
  Payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.DESC
  }
  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    Payload.value.filter = [...Payload.value.filter, {
      key: filterToSearch.value.criterial ? filterToSearch.value.criterial.id : '',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'AND'
    }]
  }
  getAdjustmentList()
}

function clearFilterToSearch() {
  Payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.DESC
  }
  filterToSearch.value.criterial = ENUM_FILTER[0]
  filterToSearch.value.search = ''
  getAdjustmentList()
}
async function GetItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true

    if (props.isCreationDialog) {
      // @ts-expect-error
      const element: any = props.listItems.find((item: any) => item.id === id)
      item.value.id = element.id
      item.value.amount = element.amount
      item.value.date = new Date(element.date)
      item.value.transactionType = { ...element.transactionType, fullName: `${element.transactionType?.code}-${element.transactionType?.name}` }
      item.value.roomRate = element.roomRate
      item.value.description = element.description
      formReload.value += 1
      return loadingSaveAll.value = false
    }

    try {
      const response = await GenericService.getById(confApi.adjustment.moduleApi, confApi.adjustment.uriApi, id)
      if (response) {
        item.value.id = response.id
        item.value.amount = response.amount
        item.value.date = new Date(response.date)
        item.value.transactionType = { ...response.transaction, fullName: `${response.transaction?.code}-${response.transaction?.name}` }
        item.value.roomRate = response.roomRate
        item.value.description = response.description
      }
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Invoice could not be loaded', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}

async function createAdjustment(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }

    const response = await GenericService.create(confApi.adjustment.moduleApi, confApi.adjustment.uriApi, payload)

    return response
  }
}

async function updateAdjustment(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }

  await GenericService.update(confApi.adjustment.moduleApi, confApi.adjustment.uriApi, idItem.value || '', payload)
}

async function deleteAdjustment(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(Options.value.moduleApi, Options.value.uriApi, id)
    ClearForm()
    getAdjustmentList()
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Could not delete invoice', life: 3000 })
    loadingDelete.value = false
  }
  finally {
    loadingDelete.value = false
  }
}

async function saveAdjustment(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  let successOperation = true
  item.roomRate = props?.selectedRoomRate
  item.employee = data?.value?.user?.name

  if (idItem.value || item?.id) {
    try {
      if (!item?.id) {
        item.id = idItem.value
      }

      if (props?.isCreationDialog) {
        props.updateItem(item)
      }
      else {
        item.transactionType = item.transactionType?.id || null
        await updateAdjustment(item)
      }
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  else {
    try {
      if (props?.isCreationDialog) {
        item.id = v4()
        props.addItem(item)

        idItem.value = item.id
      }
      else {
        item.transactionType = item.transactionType?.id || null
        const response = await createAdjustment(item)

        idItem.value = response?.id
      }
    }
    catch (error: any) {
      console.log(error)
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  if (successOperation) {
    if (!props.isCreationDialog) {
      props.refetchInvoice()
    }
    ClearForm()
    props.closeDialog()
  }
}

function requireConfirmationToSaveAdjustment(item: any) {
  const { event } = item
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      saveAdjustment(item)
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}

async function getTransactionTypeList(query = '') {
  try {
    const payload
      = {
        filter: [
          {
            key: 'name',
            operator: 'LIKE',
            value: query,
            logicalOperation: 'OR'
          },
          {
            key: 'code',
            operator: 'LIKE',
            value: query,
            logicalOperation: 'OR'
          },
          {
            key: 'status',
            operator: 'EQUALS',
            value: 'ACTIVE',
            logicalOperation: 'AND'
          }
        ],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: ENUM_SHORT_TYPE.DESC
      }

    transactionTypeList.value = []
    const response = await GenericService.search(transactionTypeApi.moduleApi, transactionTypeApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      transactionTypeList.value = [...transactionTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code, fullName: `${iterator?.code}-${iterator?.name}` }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

function onRowRightClick(event: any) {
  return

  if (route.query.type === InvoiceType.INCOME || props.invoiceObj?.invoiceType?.id === InvoiceType.INCOME) {
    return
  }

  selectedAdjustment.value = event.data
  adjustmentContextMenu.value.show(event.originalEvent)
}

function requireConfirmationToDeleteAdjustment(event: any) {
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    acceptClass: 'p-button-danger',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      deleteAdjustment(idItem.value)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}

async function ParseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, Columns)
  Payload.value.filter = [...parseFilter || []]
  getAdjustmentList()
}

function OnSortField(event: any) {
  if (event) {
    if (props?.isCreationDialog) {
      return props.sortAdjustment(event)
    }
    Payload.value.sortBy = event.sortField
    Payload.value.sortType = event.sortOrder
    getAdjustmentList()
  }
}

function openDialog() {
  dialogOpen.value = true
  console.log(dialogOpen)
}

watch(() => props.forceUpdate, () => {
  if (props.forceUpdate) {
    getAdjustmentList()
    props.toggleForceUpdate()
  }
})

watch(() => props.listItems, () => {
  if (props.isCreationDialog) {
    totalAmount.value = 0
    props?.listItems?.forEach((listItem: any) => {
      totalAmount.value += listItem?.amount ? Number(listItem?.amount) : 0
    })
  }
}, { deep: true })

watch(PayloadOnChangePage, (newValue) => {
  Payload.value.page = newValue?.page ? newValue?.page : 0
  Payload.value.pageSize = newValue?.rows ? newValue.rows : 10
  getAdjustmentList()
})

onMounted(() => {
  if (props.selectedInvoice) {
    Payload.value.filter = [{
      key: 'roomRate.booking.invoice.id',
      operator: 'EQUALS',
      value: props.selectedInvoice,
      logicalOperation: 'AND'
    }]
  }
  if (!props.isCreationDialog) {
    getAdjustmentList()
  }
})
</script>

<template>
  <div>
    <DynamicTable
      :data="isCreationDialog ? listItems as any : ListItems"
      :columns="Columns"
      :options="Options"
      :pagination="Pagination"
      @on-confirm-create="ClearForm"
      @open-edit-dialog="OpenEditDialog($event)"
      @on-change-pagination="PayloadOnChangePage = $event"
      @on-change-filter="ParseDataTableFilter"
      @on-list-item="ResetListItems"
      @on-row-right-click="onRowRightClick"
      @on-sort-field="OnSortField"
      @on-row-double-click="($event) => {}"
    >
      <template v-if="isCreationDialog" #pagination-total="props">
        <span class="font-bold font">
          {{ listItems?.length }}
        </span>
      </template>

      <template #datatable-footer>
        <ColumnGroup type="footer" class="flex align-items-center">
          <Row>
            <Column footer="Totals:" :colspan="1" footer-style="text-align:right; font-weight: 700" />
            <Column :footer="props.adjustmentTotalObj.totalAmount" footer-style="font-weight: 700" />
            <Column :colspan="6" />
          </Row>
        </ColumnGroup>
      </template>
    </DynamicTable>
  </div>
  <ContextMenu v-if="!isDetailView" ref="adjustmentContextMenu" :model="menuModel" />

  <div v-if="isDialogOpen">
    <AdjustmentTotalDialog
      :invoice-obj="invoiceObj"
      :invoice-amount="invoiceObjAmount"
      :fields="route.query.type === InvoiceType.INCOME ? IncomeAttachmentFields : Fields"
      :item="item"
      :open-dialog="isDialogOpen"
      :form-reload="formReload"
      :loading-save-all="loadingSaveAll"
      :clear-form="ClearForm"
      :require-confirmation-to-save="saveAdjustment"
      :require-confirmation-to-delete="requireConfirmationToDeleteAdjustment"
      :header="isCreationDialog || !idItem ? 'New Adjustment' : 'Edit Adjustment'"
      :id-item="idItem"
      :close-dialog="() => {
        ClearForm()
        idItem = ''
        closeDialog()
      }"
      container-class="flex flex-row justify-content-between mx-4 my-2 w-full"
      class="h-fit p-2 overflow-y-hidden"
      content-class="w-full h-fit"
      :transaction-type-list="transactionTypeList"
      :get-transaction-type-list="getTransactionTypeList"
    />
  </div>
</template>

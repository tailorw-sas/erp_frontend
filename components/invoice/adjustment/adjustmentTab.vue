<script setup lang="ts">
import { ref, watch } from 'vue'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import type { PageState } from 'primevue/paginator'
import { v4 } from 'uuid'
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
        validation: z.date({
          required_error: 'The Date field is required',
          invalid_type_error: 'The Date field is required',
        }).max(new Date(), 'The Date field cannot be greater than current date')

      },

      {
        field: 'transactionType',
        header: 'Transaction Type',
        dataType: 'select',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',
        validation: z.object({
          id: z.string(),
          name: z.string(),

        }).nullable()
          .refine((value: any) => value && value.id && value.name, { message: `The Transaction Type field is required` })
          .refine((value: any) => value.status !== 'ACTIVE', {
            message: `This Transaction Type is not active`
          })

      },
      {
        field: 'description',
        header: 'Remark',
        dataType: 'text',
        class: 'field col-12 md: required',
        headerClass: 'mb-1',

      },

    ],
    containerClass: 'w-full'
  }

]

const item = ref<GenericObject>({
  amount: 0,
  date: new Date(),
  transactionType: '',
  roomRate: '',
  description: '',
})

const itemTemp = ref<GenericObject>({
  amount: 0,
  date: new Date(),
  transactionType: '',
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
  { field: 'adjustment_id', header: 'Id', type: 'text' },
  { field: 'amount', header: 'Amount', type: 'text' },
  { field: 'room_rate_id', header: 'Rate', type: 'text' },
  { field: 'transaction', header: 'Category', type: 'select', objApi: transactionTypeApi },
  { field: 'date', header: 'Transaction Date', type: 'date' },
  { field: 'employee', header: 'Employee', type: 'text' },
  { field: 'description', header: 'Description', type: 'text' },

]

const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'name', name: 'Name' },
  { id: 'description', name: 'Description' },
]

async function openEditDialog(item: any) {
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

  { label: 'Edit Adjustment', command: openEditDialog },

])

const Options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-adjustment',
  loading: false,
  showDelete: !props.isDetailView,
  showAcctions: !props.isDetailView,
  showEdit: false,
  showFilters: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?',
  showContextMenu: !props.isDetailView,
  menuModel
})

const PayloadOnChangePage = ref<PageState>()
const Payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'code',
  sortType: 'ASC'
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

    const response = await GenericService.search(Options.value.moduleApi, Options.value.uriApi, Payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      if (iterator.hasOwnProperty('status')) {
        iterator.status = statusToBoolean(iterator.status)
      }
      ListItems.value = [...ListItems.value, { ...iterator, loadingEdit: false, loadingDelete: false, room_rate_id: iterator?.roomRate?.room_rate_id, date: new Date(iterator?.date) }]

      if (typeof +iterator?.amount === 'number') {
        totalAmount.value += +iterator?.amount
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
    sortType: 'DES'
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
    sortType: 'DES'
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
      item.value.transactionType = element.transactionType
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
        item.value.transactionType = response.transaction
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

    await GenericService.create(confApi.adjustment.moduleApi, confApi.adjustment.uriApi, payload)
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
  item.transactionType = item.transactionType?.id || null
  if (idItem.value || item?.id) {
    try {
      if (!item?.id) {
        item.id = idItem.value
      }

      if (props?.isCreationDialog) {
        props.updateItem(item)
      }
      else {
        await updateAdjustment(item)
      }

      props.closeDialog()
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
    idItem.value = ''
  }
  else {
    item.employee = data?.value?.user?.name
    try {
      if (props?.isCreationDialog) {
        item.id = v4()
        props.addItem(item)
      }
      else {
        await createAdjustment(item)
      }
      props.closeDialog()
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  if (successOperation) {
    ClearForm()

    if (!props.isCreationDialog) {
      getAdjustmentList()
    }
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

async function getTransactionTypeList() {
  try {
    const payload
      = {
        filter: [],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: 'DES'
      }

    transactionTypeList.value = []
    const response = await GenericService.search(transactionTypeApi.moduleApi, transactionTypeApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      transactionTypeList.value = [...transactionTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
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
    Payload.value.sortBy = event.sortField
    Payload.value.sortType = event.sortOrder
    ParseDataTableFilter(event.filter)
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
    <DynamicTableWithContextMenu
      :data="isCreationDialog ? listItems as any : ListItems" :columns="Columns"
      :options="Options" :pagination="Pagination" @on-confirm-create="ClearForm"
      @open-edit-dialog="OpenEditDialog($event)"
      @on-change-pagination="PayloadOnChangePage = $event" @on-change-filter="ParseDataTableFilter"
      @on-list-item="ResetListItems" @on-sort-field="OnSortField" @update:double-clicked="($event) => {

        openEditDialog({ id: $event })

      }"
    >
      <template v-if="!isDetailView" #row-selector-body="itemProps">
        <span class="pi pi-pen-to-square" @click="openEditDialog(itemProps.item)" />
      </template>

      <template v-if="showTotals" #pagination-left="">
        <div class="flex pl-1 ml-8 align-items-center " style="height: 10px; width: 20%;">
          <Badge class="px-2 ml-1 flex align-items-center text-xs text-white" severity="contrast">
            <span class="font-bold">
              Total amount:${{ totalAmount }}
            </span>
          </Badge>
        </div>
      </template>
    </DynamicTableWithContextMenu>
  </div>

  <div v-if="isDialogOpen">
    <AdjustmentDialog
      :fields="Fields" :item="item" :open-dialog="isDialogOpen" :form-reload="formReload"
      :loading-save-all="loadingSaveAll" :clear-form="ClearForm"
      :require-confirmation-to-save="requireConfirmationToSaveAdjustment"
      :require-confirmation-to-delete="requireConfirmationToDeleteAdjustment" :header="isCreationDialog || !idItem ? 'New Adjustment' : 'Edit Adjustment'"
      :close-dialog="closeDialog" container-class="flex flex-row justify-content-between mx-4 my-2 w-full"
      class="h-fit p-2 overflow-y-hidden" content-class="w-full h-fit" :transaction-type-list="transactionTypeList" :get-transaction-type-list="getTransactionTypeList"
    />
  </div>
</template>

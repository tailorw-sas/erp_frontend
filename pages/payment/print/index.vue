<script setup lang="ts">
import dayjs from 'dayjs'
import type { PageState } from 'primevue/paginator'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { IColumn, IPagination, IStatusClass } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'

interface SubTotals {
  paymentAmount: number
  depositBalance: number
  applied: number
  noApplied: number
}
const toast = useToast()

const listItems = ref<any[]>([])
const idPaymentSelectedForPrint = ref('')
const paymentSelectedForPrintList = ref<any[]>([])
const loadingPrintDetail = ref(false)
const openPrint = ref(false)
const formReload = ref(0)
const confApiPaymentDetailPrint = reactive({
  moduleApi: 'payment',
  uriApi: 'payment/report',
})

const subTotals = ref<SubTotals>({ paymentAmount: 0, depositBalance: 0, applied: 0, noApplied: 0 })

const sClassMap: IStatusClass[] = [
  { status: 'Transit', class: 'text-transit' },
  { status: 'Cancelled', class: 'text-cancelled' },
  { status: 'Confirmed', class: 'text-confirmed' },
  { status: 'Applied', class: 'text-applied' },
]
interface DataListItemForBankAccount {
  id: string
  accountNumber: string
  manageBank: {
    id: string
    name: string
  }
  status: string
}

interface ListItemForBankAccount {
  id: string
  name: string
  status: string
}
function mapFunctionForBankAccount(data: DataListItemForBankAccount): ListItemForBankAccount {
  return {
    id: data.id,
    name: `${data.manageBank.name} (${data.accountNumber}) `,
    status: data.status
  }
}
const columns: IColumn[] = [
  { field: 'icon', header: '', width: '25px', type: 'slot-icon', icon: 'pi pi-paperclip', sortable: false, showFilter: false, hidden: false },
  { field: 'paymentId', header: 'ID', tooltip: 'Payment ID', width: '40px', type: 'text', showFilter: true },
  { field: 'paymentSource', header: 'P.Source', tooltip: 'Payment Source', width: '60px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-payment-source' } },
  { field: 'transactionDate', header: 'Trans. Date', tooltip: 'Transaction Date', width: '60px', type: 'date' },
  { field: 'hotel', header: 'Hotel', width: '80px', widthTruncate: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-hotel' } },
  { field: 'client', header: 'Client', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-client' } },
  { field: 'agency', header: 'Agency', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-agency' } },
  { field: 'agencyType', header: 'Agency Type', tooltip: 'Agency Type', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-agency-type' } },
  // { field: 'agencyTypeResponse', header: 'Agency Type', tooltip: 'Agency Type', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-agency-type' } },
  { field: 'bankAccount', header: 'Bank Acc', tooltip: 'Bank Account', width: '80px', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-bank-account', keyValue: 'name', mapFunction: mapFunctionForBankAccount, sortOption: { sortBy: 'manageBank.name', sortType: ENUM_SHORT_TYPE.ASC } } },
  { field: 'paymentAmount', header: 'P. Amount', tooltip: 'Payment Amount', width: '70px', type: 'number' },
  { field: 'depositBalance', header: 'D.Balance', tooltip: 'Deposit Balance', width: '60px', type: 'number' },
  { field: 'applied', header: 'Applied', tooltip: 'Applied', width: '60px', type: 'number' },
  { field: 'notApplied', header: 'Not Applied', tooltip: 'Not Applied', width: '60px', type: 'number' },
  { field: 'remark', header: 'Remark', width: '100px', maxWidth: '200px', type: 'text' },
  { field: 'paymentStatus', header: 'Status', width: '100px', frozen: true, type: 'slot-select', statusClassMap: sClassMap, objApi: { moduleApi: 'settings', uriApi: 'manage-payment-status' }, sortable: true },
]
const options = ref({
  tableName: 'Manage Payment',
  moduleApi: 'payment',
  uriApi: 'payment',
  loading: false,
  actionsAsMenu: true,
  selectionMode: 'multiple',
  selectAllItemByDefault: false,
  showSelectedItems: true,
  messageToDelete: 'Are you sure you want to delete the account type: {{name}}?'
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'paymentId',
  sortType: ENUM_SHORT_TYPE.DESC
})
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const fieldPrint = ref<FieldDefinitionType[]>([
  {
    field: 'paymentAndDetails',
    header: 'Payment and Details',
    dataType: 'check',
    disabled: true,
    class: 'field col-12 md:col-6 required mb-2',
  },
  {
    field: 'invoiceRelated',
    header: 'Invoice Related',
    dataType: 'check',
    disabled: false,
    class: 'field col-12 md:col-6 required mb-2',
  },
  {
    field: 'paymentSupport',
    header: 'Payment Support',
    dataType: 'check',
    disabled: false,
    class: 'field col-12 md:col-6 required mb-2',
  },
  {
    field: 'invoiceRelatedWithSupport',
    header: 'Invoice Related With Support',
    dataType: 'check',
    disabled: false,
    class: 'field col-12 md:col-6 required mb-2',
  },
  {
    field: 'allPaymentsSupport',
    header: 'All Payment Support',
    dataType: 'check',
    disabled: false,
    class: 'field col-12 md:col-6 required mb-2',
  },
])

const itemPrint = ref<GenericObject>({
  paymentAndDetails: true,
  paymentSupport: false,
  allPaymentsSupport: false,
  invoiceRelated: false,
  invoiceRelatedWithSupport: false
})

const itemTempPrint = ref<GenericObject>({
  paymentAndDetails: true,
  paymentSupport: false,
  allPaymentsSupport: false,
  invoiceRelated: false,
  invoiceRelatedWithSupport: false
})

async function getList() {
  const count: SubTotals = { paymentAmount: 0, depositBalance: 0, applied: 0, noApplied: 0 }
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

    interface ListColor {
      NONE: string
      ATTACHMENT_WITH_ERROR: string
      ATTACHMENT_WITHOUT_ERROR: string
    }

    const listColor: ListColor = {
      NONE: '#616161',
      ATTACHMENT_WITH_ERROR: '#ff002b',
      ATTACHMENT_WITHOUT_ERROR: '#00b816',
    }
    let color = listColor.NONE

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'agency')) {
        iterator.agencyType = iterator.agency.agencyTypeResponse
        iterator.agency = {
          ...iterator.agency,
          name: `${iterator.agency.code} - ${iterator.agency.name}`
        }
      }

      if (Object.prototype.hasOwnProperty.call(iterator, 'client')) {
        iterator.client = {
          ...iterator.client,
          name: `${iterator.client.code} - ${iterator.client.name}`
        }
      }

      if (Object.prototype.hasOwnProperty.call(iterator, 'hotel')) {
        iterator.hotel = {
          ...iterator.hotel,
          name: `${iterator.hotel.code} - ${iterator.hotel.name}`
        }
      }

      if (Object.prototype.hasOwnProperty.call(iterator, 'bankAccount')) {
        if (iterator.bankAccount && iterator.bankAccount.id) {
          iterator.bankAccount = {
            id: iterator.bankAccount.id,
            name: `${iterator.bankAccount.accountNumber} - ${iterator.bankAccount.nameOfBank}`,
            accountNumber: `${iterator.bankAccount.accountNumber} - ${iterator.bankAccount.nameOfBank}`,
          }
        }
      }

      if (Object.prototype.hasOwnProperty.call(iterator, 'paymentId')) {
        iterator.paymentId = String(iterator.paymentId)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'paymentAmount')) {
        count.paymentAmount = count.paymentAmount + iterator.paymentAmount
        iterator.paymentAmount = iterator.paymentAmount || 0
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'depositBalance')) {
        count.depositBalance += iterator.depositBalance
        iterator.depositBalance = iterator.depositBalance || 0
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'applied')) {
        count.applied += iterator.applied
        iterator.applied = iterator.applied || 0
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'notApplied')) {
        count.noApplied += iterator.notApplied
        iterator.notApplied = iterator.notApplied || 0
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'depositAmount')) {
        iterator.depositAmount = iterator.depositAmount || 0
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'otherDeductions')) {
        iterator.otherDeductions = String(iterator.otherDeductions)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'bankAccount')) {
        iterator.bankAccount = {
          id: iterator?.bankAccount?.id,
          name: iterator?.bankAccount?.accountNumber
        }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = String(iterator.status)
      }

      if (Object.prototype.hasOwnProperty.call(iterator, 'attachmentStatus')) {
        if (iterator.attachmentStatus?.patWithAttachment) {
          color = listColor.ATTACHMENT_WITHOUT_ERROR
        }
        else if (iterator.attachmentStatus?.pwaWithOutAttachment) {
          color = listColor.ATTACHMENT_WITH_ERROR
        }
        else if (iterator.attachmentStatus?.nonNone) {
          color = listColor.NONE
        }
        else {
          color = listColor.NONE
        }
      }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push(
          {
            ...iterator,
            loadingEdit: false,
            loadingDelete: false,
            color,
          }

        )
        existingIds.add(iterator.id)
      }
    }

    listItems.value = [...listItems.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
    subTotals.value = { ...count }
  }
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  const objFilter = parseFilter?.find((item: IFilter) => item?.key === 'agencyType.id')
  if (objFilter) {
    objFilter.key = 'agency.agencyType.id'
  }

  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  await getList()
}
function onSortField(event: any) {
  if (event) {
    if (event.sortField === 'paymentStatus') {
      event.sortField = 'paymentStatus.name'
    }
    if (event.sortField === 'paymentSource') {
      event.sortField = 'paymentSource.name'
    }
    if (event.sortField === 'bankAccount') {
      event.sortField = 'bankAccount.accountNumber'
    }
    if (event.sortField === 'client') {
      event.sortField = 'client.name'
    }
    if (event.sortField === 'agencyType') {
      event.sortField = 'agency.agencyType.name'
    }
    if (event.sortField === 'agency') {
      event.sortField = 'agency.name'
    }
    if (event.sortField === 'hotel') {
      event.sortField = 'hotel.name'
    }
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}
async function resetListItems() {
  payload.value.page = 0
  getList()
}
function showInconAttachment(objData: any) {
  if (objData.hasAttachment) {
    return true
  }
  else if (objData.attachmentStatus.pwaWithOutAttachment) {
    return true
  }
  else if (objData.attachmentStatus.patWithAttachment) {
    return true
  }
  return false
}

function assingFuntionsForPrint(itemId: any) {
  if (itemId && itemId.length > 0) {
    idPaymentSelectedForPrint.value = itemId[0]
    paymentSelectedForPrintList.value = itemId
  }
  else {
    idPaymentSelectedForPrint.value = ''
    paymentSelectedForPrintList.value = []
  }
}

async function paymentPrint(event: any) {
  try {
    loadingPrintDetail.value = true
    let nameOfPdf = ''
    let paymentTypeArray: string[] = []
    const payloadTemp: { paymentId: string[], paymentType: string[] } = {
      paymentId: paymentSelectedForPrintList.value,
      paymentType: [],
    }
    // En caso de que solo este marcado el paymentAndDetails
    if (event && event.paymentAndDetails) {
      paymentTypeArray = [...paymentTypeArray, 'PAYMENT_DETAILS']
    }
    if (event && event.paymentSupport) {
      paymentTypeArray = [...paymentTypeArray, 'PAYMENT_SUPPORT']
    }
    if (event && event.allPaymentsSupport) {
      paymentTypeArray = [...paymentTypeArray, 'ALL_SUPPORT']
    }
    if (event && event.invoiceRelated) {
      paymentTypeArray = [...paymentTypeArray, 'INVOICE_RELATED']
    }
    if (event && event.invoiceRelatedWithSupport) {
      paymentTypeArray = [...paymentTypeArray, 'INVOICE_RELATED_SUPPORT']
    }

    nameOfPdf = `payment-list-${dayjs().format('YYYY-MM-DD')}.pdf`
    payloadTemp.paymentType = paymentTypeArray
    const response: any = await GenericService.create(confApiPaymentDetailPrint.moduleApi, confApiPaymentDetailPrint.uriApi, payloadTemp)

    const url = window.URL.createObjectURL(new Blob([response]))
    const a = document.createElement('a')
    a.href = url
    a.download = nameOfPdf
    document.body.appendChild(a)
    a.click()
    window.URL.revokeObjectURL(url)
    document.body.removeChild(a)
    loadingPrintDetail.value = false
  }
  catch (error) {
    loadingPrintDetail.value = false
    toast.add({ severity: 'error', summary: 'Error', detail: 'Transaction was failed', life: 3000 })
  }
  finally {
    loadingPrintDetail.value = false
    openPrint.value = false
  }
}

async function openDialogPrint() {
  itemPrint.value = JSON.parse(JSON.stringify(itemTempPrint.value))
  openPrint.value = true
}

async function closeDialogPrint() {
  navigateTo({ path: '/payment' })
}

watch(payloadOnChangePage, async (newValue) => {
  payload.value = localStorage.getItem('payloadOfPaymentList')
    ? JSON.parse(localStorage.getItem('payloadOfPaymentList') || '{}')
    : payload.value
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 50
  await getList()
})

onMounted(async () => {
  payload.value = localStorage.getItem('payloadOfPaymentList')
    ? JSON.parse(localStorage.getItem('payloadOfPaymentList') || '{}')
    : payload.value
  await getList()
})
</script>

<template>
  <div class="font-bold text-lg px-4 bg-primary custom-card-header">
    Payments to Print
  </div>
  <div class="grid">
    <div class="col-12 order-0 w-full md:order-1 md:col-6 xl:col-9 mt-2">
      <div class="p-fluid pt-3">
        <DynamicTable
          :data="listItems"
          :columns="columns"
          :options="options"
          :pagination="pagination"
          @on-change-pagination="payloadOnChangePage = $event"
          @on-change-filter="parseDataTableFilter"
          @on-list-item="resetListItems"
          @on-sort-field="onSortField"
          @update:clicked-item="assingFuntionsForPrint($event)"
        >
          <template #column-icon="{ data: objData, column }">
            <div class="flex align-items-center justify-content-center p-0 m-0">
              <!-- <pre>{{ objData }}</pre> -->
              <Button
                v-if="showInconAttachment(objData)"
                :icon="column.icon"
                class="p-button-rounded p-button-text w-2rem h-2rem"
                aria-label="Submit"
                :disabled="objData?.attachmentStatus?.nonNone"
                :style="{ color: objData.color }"
              />
            </div>
            <!-- style="color: #616161;" -->
            <!-- :style="{ 'background-color': '#00b816' }" -->
          </template>

          <template #column-paymentStatus="{ data, column }">
            <Badge
              v-tooltip.top="data.paymentStatus.name.toString()"
              :value="data.paymentStatus.name"
              :class="column.statusClassMap?.find(e => e.status === data.paymentStatus.name)?.class"
            />
          </template>
          <template #datatable-footer>
            <ColumnGroup type="footer" class="flex align-items-center">
              <Row>
                <Column footer="Totals:" :colspan="options.selectionMode === 'multiple' ? 10 : 9" footer-style="text-align:right; font-weight: bold;" />
                <Column :footer="formatNumber(Math.round((subTotals.paymentAmount + Number.EPSILON) * 100) / 100)" footer-style="font-weight: bold;" />
                <Column :footer="formatNumber(Math.round((subTotals.depositBalance + Number.EPSILON) * 100) / 100)" footer-style="font-weight: bold;" />
                <Column :footer="formatNumber(Math.round((subTotals.applied + Number.EPSILON) * 100) / 100)" footer-style="font-weight: bold;" />
                <Column :footer="formatNumber(Math.round((subTotals.noApplied + Number.EPSILON) * 100) / 100)" footer-style="font-weight: bold;" />
                <Column :colspan="0" />
                <Column :colspan="0" />
              </Row>
            </ColumnGroup>
          </template>
        </DynamicTable>
      </div>
      <div class="flex justify-content-end">
        <div class="flex align-items-end justify-content-end">
          <Button v-tooltip.top="'Print'" class="w-3rem mx-2" icon="pi pi-print" :disabled="paymentSelectedForPrintList.length === 0" @click="openDialogPrint" />
          <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem p-button" icon="pi pi-times" @click="closeDialogPrint" />
        </div>
      </div>
    </div>
    <!-- PRINT -->
    <Dialog
      v-model:visible="openPrint"
      modal
      class="mx-3 sm:mx-0"
      content-class="border-round-bottom border-top-1 surface-border"
      :style="{ width: '37%' }"
      :pt="{
        root: {
          class: 'custom-dialog',
        },
        header: {
          style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
        },
        // mask: {
        //   style: 'backdrop-filter: blur(5px)',
        // },
      }"
      @hide="closeDialogPrint()"
    >
      <template #header>
        <div class="flex justify-content-between w-full">
          <h5 class="m-0">
            Payment To Print
          </h5>
          <!-- <div class="flex align-items-center">
            <h5 class="m-0 mr-4">
              Payment Id: {{ objItemSelectedForRightClickChangeAgency.paymentId }}
            </h5>
          </div> -->
        </div>
      </template>
      <template #default>
        <div class="p-fluid pt-3">
          <EditFormV2
            :key="formReload"
            class="mt-3"
            :fields="fieldPrint"
            :item="itemPrint"
            container-class="grid pt-3"
            :show-actions="true"
            :loading-save="loadingPrintDetail"
            @cancel="closeDialogPrint"
            @submit="paymentPrint($event)"
          >
            <template #field-paymentSupport="{ item: data, onUpdate }">
              <Checkbox
                id="paymentSupport"
                v-model="data.paymentSupport"
                :binary="true"
                @update:model-value="($event) => {
                  onUpdate('paymentSupport', $event)
                  if ($event) {
                    onUpdate('allPaymentsSupport', false)
                  }
                }"
              />
              <label for="paymentSupport" class="ml-2 font-bold">
                Payment Support
              </label>
            </template>

            <template #field-allPaymentsSupport="{ item: data, onUpdate }">
              <Checkbox
                id="allPaymentsSupport"
                v-model="data.allPaymentsSupport"
                :binary="true"
                @update:model-value="($event) => {
                  onUpdate('allPaymentsSupport', $event)
                  if ($event) {
                    onUpdate('paymentSupport', false)
                  }
                }"
              />
              <label for="allPaymentsSupport" class="ml-2 font-bold">
                All Payment Supports
              </label>
            </template>

            <template #field-invoiceRelated="{ item: data, onUpdate }">
              <Checkbox
                id="invoiceRelated"
                v-model="data.invoiceRelated"
                :binary="true"
                @update:model-value="($event) => {
                  onUpdate('invoiceRelated', $event)
                  if ($event) {
                    onUpdate('invoiceRelatedWithSupport', false)
                  }
                }"
              />
              <label for="invoiceRelated" class="ml-2 font-bold">
                Invoice Related
              </label>
            </template>

            <template #field-invoiceRelatedWithSupport="{ item: data, onUpdate }">
              <Checkbox
                id="invoiceRelatedWithSupport"
                v-model="data.invoiceRelatedWithSupport"
                :binary="true"
                @update:model-value="($event) => {
                  onUpdate('invoiceRelatedWithSupport', $event)
                  if ($event) {
                    onUpdate('invoiceRelated', false)
                  }
                }"
              />
              <label for="invoiceRelatedWithSupport" class="ml-2 font-bold">
                Invoice Related With Supports
              </label>
            </template>

            <template #form-footer="props">
              <Button v-tooltip.top="'Print'" :loading="loadingPrintDetail" class="w-3rem ml-1 sticky" icon="pi pi-print" @click="props.item.submitForm($event)" />
              <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem ml-3 sticky" icon="pi pi-times" @click="closeDialogPrint" />
            </template>
          </EditFormV2>
        </div>
      </template>
    </Dialog>
  </div>
</template>

<style lang="scss">
.custom-file-upload {
  background-color: transparent !important;
  border: none !important;
  text-align: left !important;
}
.custom-width {
    width: 300px; /* Ajusta el ancho como desees, por ejemplo 300px, 50%, etc. */
}
.ellipsis-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
  max-width: 150px; /* Ajusta el ancho máximo según tus necesidades */
}
.text-transit {
  background-color: #ff002b;
  color: #fff;
}
.text-cancelled {
  background-color: #888888;
  color: #fff;
}
.text-confirmed {
  background-color: #0c2bff;
  color: #fff;
}
.text-applied {
  background-color: #00b816;
  color: #fff;
}
.p-text-disabled {
  color: #888888;
}
</style>

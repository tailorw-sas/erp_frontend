<script setup lang="ts">
import { ref } from 'vue'
import type { PageState } from 'primevue/paginator'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import { formatNumber } from '~/pages/payment/utils/helperFilters'

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
  currentBankPayment: {
    type: Object,
    required: true
  },
})
const subTotals: any = ref({ amount: 0, commission: 0, net: 0 })
const BindTransactionList = ref<any[]>([])
const loadingSaveAll = ref(false)
const forceSave = ref(false)
const dialogVisible = ref(props.openDialog)

const item = ref({
  manageMerchantBankAccount: null,
  manageHotel: null,
  amount: 0,
  paidDate: '',
  remark: '',
} as GenericObject)

const columns: IColumn[] = [
  { field: 'id', header: 'Id', type: 'text' },
  { field: 'merchant', header: 'Merchant', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-merchant' }, sortable: true },
  { field: 'creditCardType', header: 'CC Type', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-credit-card-type' }, sortable: true },
  { field: 'referenceNumber', header: 'Reference', type: 'text' },
  { field: 'checkIn', header: 'Trans Date', type: 'date' },
  { field: 'amount', header: 'Amount', type: 'text' },
]

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Transactions',
  moduleApi: 'creditcard',
  uriApi: 'transactions',
  loading: false,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?',
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

// FUNCTIONS ---------------------------------------------------------------------------------------------

async function getList() {
  const count = { amount: 0, commission: 0, net: 0 }
  subTotals.value = { ...count }
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    options.value.loading = true
    BindTransactionList.value = []
    const newListItems = []

    const merchantIds = props.currentBankPayment.manageMerchantBankAccount.merchantData.map((item: any) => item.id)
    const creditCardTypeIds = props.currentBankPayment.manageMerchantBankAccount.creditCardTypes.map((item: any) => item.id)

    payload.value.filter = [{
      key: 'hotel.id',
      operator: 'EQUALS',
      value: props.currentBankPayment.manageHotel.id,
      logicalOperation: 'AND'
    }, {
      key: 'merchant.id',
      operator: 'IN',
      value: merchantIds,
      logicalOperation: 'AND'
    }, {
      key: 'creditCardType.id',
      operator: 'IN',
      value: creditCardTypeIds,
      logicalOperation: 'AND'
    }, {
      key: 'amount',
      operator: 'LESS_THAN_OR_EQUAL_TO',
      value: props.currentBankPayment.amount,
      logicalOperation: 'AND'
    }]

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(BindTransactionList.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = iterator.status.name
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'merchant') && iterator.hotel) {
        iterator.merchant = { id: iterator.merchant.id, name: `${iterator.merchant.code} - ${iterator.merchant.description}` }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'hotel') && iterator.hotel) {
        iterator.hotel = { id: iterator.hotel.id, name: `${iterator.hotel.code} - ${iterator.hotel.name}` }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'creditCardType') && iterator.creditCardType) {
        iterator.creditCardType = { id: iterator.creditCardType.id, name: `${iterator.creditCardType.code} - ${iterator.creditCardType.name}` }
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'parent')) {
        iterator.parent = (iterator.parent) ? String(iterator.parent?.id) : null
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'id')) {
        iterator.id = String(iterator.id)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'amount')) {
        count.amount += iterator.amount
        iterator.amount = formatNumber(iterator.amount)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'commission')) {
        count.commission += iterator.commission
        iterator.commission = formatNumber(iterator.commission)
      }
      if (Object.prototype.hasOwnProperty.call(iterator, 'netAmount')) {
        count.net += iterator.netAmount
        iterator.netAmount = iterator.netAmount ? formatNumber(iterator.netAmount) : '0.00'
      }
      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator, loadingEdit: false, loadingDelete: false, invoiceDate: new Date(iterator?.invoiceDate) })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    BindTransactionList.value = [...BindTransactionList.value, ...newListItems]
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
    subTotals.value = { ...count }
  }
}

async function handleSave(event: any) {
  if (event) {
    // await saveItem(event)
    forceSave.value = false
  }
}

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

onMounted(() => {
  getList()
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal :header="header" class="h-screen"
    content-class="border-round-bottom border-top-1 surface-border h-fit" :block-scroll="true"
    :style="{ width: '80%' }"
    :breakpoints="{ '1199px': '75vw', '575px': '90vw' }"
    :pt="{
      root: {
        class: 'custom-dialog',
      },
      header: {
        style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
      },
    }"
    @hide="closeDialog()"
  >
    <!-- <div class="card p-0 mb-0">
      <Accordion :active-index="0">
        <AccordionTab>
          <template #header>
            <div class="text-white font-bold custom-accordion-header flex justify-content-between w-full align-items-center">
              <div>
                Search Fields
              </div>
            </div>
          </template>
          <div class="grid">
            <div class="col-12 md:col-6 lg:col-3 flex pb-0">
              <div class="flex flex-column gap-2 w-full">
                <div class="flex align-items-center gap-2 w-full" style=" z-index:5 ">
                  <label class="filter-label font-bold" for="">Merchant:</label>
                  <div class="w-full" style=" z-index:5 ">
                    <DebouncedAutoCompleteComponent
                      v-if="!loadingSaveAll" id="autocomplete"
                      :multiple="true" class="w-full" field="name"
                      item-value="id" :model="filterToSearch.merchant" :suggestions="merchantList"
                      @load="($event) => getMerchantList($event)" @change="($event) => {
                        if (!filterToSearch.merchant.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                          filterToSearch.merchant = $event.filter((element: any) => element?.id === 'All')
                        }
                        else {
                          filterToSearch.merchant = $event.filter((element: any) => element?.id !== 'All')
                        }
                      }"
                    >
                      <template #option="props">
                        <span>{{ props.item.code }} - {{ props.item.name }}</span>
                      </template>
                    </DebouncedAutoCompleteComponent>
                  </div>
                </div>
                <div class="flex align-items-center gap-2">
                  <label class="filter-label font-bold" for="">Hotel:</label>
                  <div class="w-full">
                    <DebouncedAutoCompleteComponent
                      v-if="!loadingSaveAll" id="autocomplete"
                      :multiple="true" class="w-full" field="name"
                      item-value="id" :model="filterToSearch.hotel" :suggestions="hotelList"
                      @load="($event) => getHotelList($event)" @change="($event) => {
                        if (!filterToSearch.hotel.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                          filterToSearch.hotel = $event.filter((element: any) => element?.id === 'All')
                        }
                        else {
                          filterToSearch.hotel = $event.filter((element: any) => element?.id !== 'All')
                        }
                      }"
                    >
                      <template #option="props">
                        <span>{{ props.item.code }} - {{ props.item.name }}</span>
                      </template>
                    </DebouncedAutoCompleteComponent>
                  </div>
                </div>
              </div>
            </div>
            <div class="col-12 md:col-6 lg:col-3 flex pb-0">
              <div class="flex flex-column gap-2 w-full">
                <div class="flex align-items-center gap-2" style=" z-index:5 ">
                  <label class="filter-label font-bold" for="">CC Type:</label>
                  <div class="w-full" style=" z-index:5 ">
                    <DebouncedAutoCompleteComponent
                      v-if="!loadingSaveAll" id="autocomplete"
                      :multiple="true" class="w-full" field="name"
                      item-value="id" :model="filterToSearch.ccType" :suggestions="ccTypeList" @change="($event) => {
                        if (!filterToSearch.ccType.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                          filterToSearch.ccType = $event.filter((element: any) => element?.id === 'All')
                        }
                        else {
                          filterToSearch.ccType = $event.filter((element: any) => element?.id !== 'All')
                        }
                      }" @load="($event) => getCCTypeList($event)"
                    >
                      <template #option="props">
                        <span>{{ props.item.code }} - {{ props.item.name }}</span>
                      </template>
                    </DebouncedAutoCompleteComponent>
                  </div>
                </div>
                <div class="flex align-items-center gap-2">
                  <label class="filter-label font-bold" for="">Status:</label>
                  <div class="w-full">
                    <DebouncedAutoCompleteComponent
                      v-if="!loadingSaveAll" id="autocomplete"
                      :multiple="true" class="w-full" field="name"
                      item-value="id" :model="filterToSearch.status" :suggestions="statusList"
                      @load="($event) => getStatusList($event)" @change="($event) => {
                        if (!filterToSearch.status.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                          filterToSearch.status = $event.filter((element: any) => element?.id === 'All')
                        }
                        else {
                          filterToSearch.status = $event.filter((element: any) => element?.id !== 'All')
                        }
                      }"
                    >
                      <template #option="props">
                        <span>{{ props.item.code }} - {{ props.item.name }}</span>
                      </template>
                    </DebouncedAutoCompleteComponent>
                  </div>
                </div>
              </div>
            </div>
            <div class="col-12 md:col-6 lg:col-2 flex pb-0">
              <div class="flex flex-column gap-2 w-full">
                <div class="flex align-items-center gap-2" style=" z-index:5 ">
                  <label class="filter-label font-bold" for="">From:</label>
                  <div class="w-full" style=" z-index:5 ">
                    <Calendar
                      v-model="filterToSearch.from" date-format="yy-mm-dd" icon="pi pi-calendar-plus"
                      show-icon icon-display="input" class="w-full" :max-date="new Date()"
                    />
                  </div>
                </div>
                <div class="flex align-items-center gap-2">
                  <label class="filter-label font-bold" for="">To:</label>
                  <div class="w-full">
                    <Calendar
                      v-model="filterToSearch.to" date-format="yy-mm-dd" icon="pi pi-calendar-plus" show-icon
                      icon-display="input" class="w-full" :max-date="new Date()" :min-date="filterToSearch.from"
                    />
                  </div>
                </div>
              </div>
            </div>
            <div class="col-12 md:col-6 lg:col-2 flex pb-0">
              <div class="flex w-full">
                <div class="flex flex-row w-full">
                  <div class="flex flex-column gap-2 w-full">
                    <div class="flex align-items-center gap-2" style=" z-index:5 ">
                      <label class="filter-label font-bold" for="">Criteria:</label>
                      <div class="w-full">
                        <Dropdown
                          v-model="filterToSearch.criteria" :options="[...ENUM_FILTER]" option-label="name"
                          placeholder="Criteria" return-object="false" class="align-items-center w-full" show-clear
                        />
                      </div>
                    </div>
                    <div class="flex align-items-center gap-2">
                      <label class="filter-label font-bold" for="">Search:</label>
                      <div class="w-full">
                        <IconField icon-position="left">
                          <InputText v-model="filterToSearch.search" type="text" style="width: 100% !important;" />
                          <InputIcon class="pi pi-search" />
                        </IconField>
                      </div>
                    </div>
                  </div>
                  <div class="flex align-items-center">
                    <Button
                      v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :disabled="disabledSearch"
                      :loading="loadingSearch" @click="searchAndFilter"
                    />
                    <Button
                      v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash"
                      :loading="loadingSearch" @click="clearFilterToSearch"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </AccordionTab>
      </Accordion>
    </div> -->
    <div class="mt-4" />
    <pre>{{ props.currentBankPayment }}</pre>
    <DynamicTable
      :data="BindTransactionList"
      :columns="columns"
      :options="options"
      :pagination="pagination"
      @on-change-pagination="payloadOnChangePage = $event"
      @on-change-filter="parseDataTableFilter"
      @on-sort-field="onSortField"
    />
    <div class="flex justify-content-end align-items-center mt-3 card p-2 bg-surface-500">
      <Button v-tooltip.top="'Bind Transaction'" class="w-3rem" :disabled="item.amount <= 0" icon="pi pi-lock" @click="() => {}" />
      <Button v-tooltip.top="'Save'" class="w-3rem ml-1" icon="pi pi-save" :loading="loadingSaveAll" @click="forceSave = true" />
      <Button v-tooltip.top="'Cancel'" class="w-3rem ml-3" icon="pi pi-times" severity="secondary" @click="closeDialog()" />
    </div>
  </Dialog>
</template>

<style scoped lang="scss">

</style>

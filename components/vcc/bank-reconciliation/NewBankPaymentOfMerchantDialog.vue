<script setup lang="ts">
import { ref } from 'vue'
import { z } from 'zod'
import dayjs from 'dayjs'
import type { Ref } from 'vue'
import type { PageState } from 'primevue/paginator'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'

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
  selectedBankPaymentId: {
    type: String,
    required: true
  },
})
const HotelList = ref<any[]>([])
const MerchantBankAccountList = ref<any[]>([])
const BindTransactionList = ref<any[]>([])
const loadingSaveAll = ref(false)
const forceSave = ref(false)
const dialogVisible = ref(props.openDialog)
const refForm: Ref = ref(null)
const formReload = ref(0)

const item = ref({
  manageMerchantBankAccount: null,
  manageHotel: null,
  amount: 0,
  paidDate: '',
  remark: '',
} as GenericObject)

const itemTemp = ref({
  manageMerchantBankAccount: null,
  manageHotel: null,
  amount: 0,
  paidDate: '',
  remark: '',
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'manageMerchantBankAccount',
    header: 'Bank Account',
    dataType: 'select',
    class: 'field col-12 md:col-6 required',
    validation: validateEntityStatus('bank account'),
  },
  {
    field: 'manageHotel',
    header: 'Hotel',
    dataType: 'select',
    class: 'field col-12 md:col-6 required',
    validation: validateEntityStatus('hotel'),
  },
  {
    field: 'amount',
    header: 'Amount',
    dataType: 'number',
    disabled: false,
    minFractionDigits: 2,
    maxFractionDigits: 4,
    class: 'field col-12 md:col-3 required',
    validation: z.number({
      invalid_type_error: 'The amount field must be a number',
      required_error: 'The amount field is required',
    })
      .refine(val => Number.parseFloat(String(val)) > 0, {
        message: 'The amount must be greater than zero',
      })
  },
  {
    field: 'paidDate',
    header: 'Paid Date',
    dataType: 'date',
    class: 'field col-12 md:col-3 required ',
    headerClass: 'mb-1',
    validation: z.date({
      required_error: 'The paid date field is required',
      invalid_type_error: 'The paid date field is required',
    }).max(dayjs().endOf('day').toDate(), 'The paid date field cannot be greater than current date')
  },
  {
    field: 'remark',
    header: 'Remark',
    dataType: 'text',
    class: 'field col-12 md:col-6',
  },
]

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

function clearForm() {
  item.value = JSON.parse(JSON.stringify(itemTemp.value))
  formReload.value++
}

async function getHotelList(query: string) {
  try {
    const payload = {
      filter: [{
        key: 'name',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'OR'
      }, {
        key: 'code',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'OR'
      }, {
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }, {
        key: 'isApplyByVCC',
        operator: 'EQUALS',
        value: true,
        logicalOperation: 'AND'
      }],
      query: '',
      sortBy: 'createdAt',
      sortType: 'ASC',
      pageSize: 20,
      page: 0,
    }
    const response: any = await GenericService.search('settings', 'manage-hotel', payload)
    const { data: dataList } = response
    HotelList.value = []

    for (const iterator of dataList) {
      HotelList.value = [...HotelList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getMerchantBankAccountList(query: string) {
  try {
    const payload = {
      filter: [{
        key: 'accountNumber',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'AND'
      }, {
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }],
      query: '',
      sortBy: 'createdAt',
      sortType: 'ASC',
      pageSize: 20,
      page: 0,
    }
    const response: any = await GenericService.search('creditcard', 'manage-merchant-bank-account', payload)
    const { data: dataList } = response
    MerchantBankAccountList.value = []

    for (const iterator of dataList) {
      const merchantNames = iterator.managerMerchant.map((item: any) => item.description).join(' - ')
      MerchantBankAccountList.value = [...MerchantBankAccountList.value, { id: iterator.id, name: `${merchantNames} - ${iterator.description} - ${iterator.accountNumber}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading merchant bank account list:', error)
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
    <!-- <div class="font-bold text-lg px-4 bg-primary custom-card-header">
      Payment Merchant
    </div> -->
    <div class="card p-4 mt-4 mb-0">
      <EditFormV2
        :key="formReload"
        ref="refForm"
        :fields="fields"
        :item="item"
        :show-actions="false"
        container-class="grid pt-3"
        :loading-save="loadingSaveAll"
        :force-save="forceSave"
        @cancel="clearForm"
        @force-save="forceSave = $event"
        @submit="handleSave($event)"
      >
        <template #field-paidDate="{ item: data, onUpdate, fields: listFields, field }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.paidDate"
            date-format="yy-mm-dd"
            :max-date="new Date()"
            :disabled="listFields.find((f: FieldDefinitionType) => f.field === field)?.disabled || false"
            @update:model-value="($event) => {
              onUpdate('paidDate', $event)
            }"
          />
          <Skeleton v-else height="2rem" />
        </template>
        <!-- Agency -->
        <template #field-manageHotel="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.manageHotel"
            :suggestions="HotelList"
            @change="($event) => {
              onUpdate('hotel', $event)
              item.manageHotel = $event
            }"
            @load="($event) => getHotelList($event)"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>

        <template #field-manageMerchantBankAccount="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.manageMerchantBankAccount"
            :suggestions="[...MerchantBankAccountList]"
            @change="($event) => {
              onUpdate('manageMerchantBankAccount', $event)
              item.manageMerchantBankAccount = $event
            }"
            @load="($event) => getMerchantBankAccountList($event)"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
      </EditFormV2>
    </div>
    <DynamicTable
      :data="BindTransactionList"
      :columns="columns"
      :options="options"
      :pagination="pagination"
      @on-change-pagination="payloadOnChangePage = $event"
      @on-change-filter="parseDataTableFilter"
      @on-sort-field="onSortField"
    >
    </DynamicTable>
    <div class="flex justify-content-end align-items-center mt-3 card p-2 bg-surface-500">
      <Button v-tooltip.top="'Bind Transaction'" class="w-3rem" :disabled="item.amount <= 0" icon="pi pi-lock" @click="() => {}" />
      <Button v-tooltip.top="'Save'" class="w-3rem ml-1" icon="pi pi-save" :loading="loadingSaveAll" @click="forceSave = true" />
      <Button v-tooltip.top="'Cancel'" class="w-3rem ml-3" icon="pi pi-times" severity="secondary" @click="closeDialog()" />
    </div>
  </Dialog>
</template>

<style scoped lang="scss">

</style>

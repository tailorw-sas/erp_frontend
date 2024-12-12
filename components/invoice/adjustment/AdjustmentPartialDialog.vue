<script setup lang="ts">
import dayjs from 'dayjs'
import { useToast } from 'primevue/usetoast'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import { GenericService } from '~/services/generic-services'

const props = defineProps({
  fields: {
    type: Array<Container>,
    required: true,

  },
  header: {
    type: String,
    required: true
  },
  class: {
    type: String,
    required: false,
  },
  contentClass: {
    type: String,
    required: false,
  },
  openDialog: {
    type: Boolean,
    required: true
  },
  formReload: {
    type: Number,
    required: true
  },
  item: {
    type: Object
  },
  idItem: {
    type: String
  },
  loadingSaveAll: { type: Boolean, required: true },
  clearForm: {
    required: true,
    type: Function as any
  },
  requireConfirmationToDelete: {
    required: true,
    type: Function as any
  },
  requireConfirmationToSave: {
    required: true,
    type: Function as any
  },
  closeDialog: {
    type: Function as any,
    required: true
  },
  containerClass: {
    type: String,
    required: false
  },
  transactionTypeList: {
    type: Array,
    required: true
  },
  getTransactionTypeList: {
    type: Function,
    required: true
  },
  invoiceObj: {
    type: Object,
    required: true
  },
  invoiceAmount: { type: Number, required: true },
  loadingDefaultTransactionType: {
    type: Boolean,
    required: false,
    default: false
  },
})
const toast = useToast()
const route = useRoute()

const dialogVisible = ref(props.openDialog)
const formFields = ref<FieldDefinitionType[]>([])
const idInvoiceSelected = ref(route.query.selected)
const invoiceType = ref('')
const amountError = ref(false)

const minDate = ref(dayjs().startOf('month').toDate())
const maxDate = ref(dayjs().endOf('month').toDate())

const configInvoiceApi = {
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
  uriApiCloseOperations: 'invoice-close-operation'
}
const payloadForCloseOperation = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})

function validateInvoiceAmount(newAmount: number) {
  let amount = props.invoiceAmount

  if (props.idItem) {
    amount -= props.item?.amount
  }

  amountError.value = false
  if (invoiceType.value === InvoiceType.INVOICE) {
    if (Number(amount) + newAmount < 0) {
      amountError.value = true
    }
  }
  if (invoiceType.value === InvoiceType.CREDIT || invoiceType.value === InvoiceType.OLD_CREDIT) {
    if (Number(amount) + newAmount > 0) {
      amountError.value = true
    }
  }
}

async function getInvoiceById() {
  const result = await GenericService.getById(configInvoiceApi.moduleApi, configInvoiceApi.uriApi, route.query.selected as string)
  return result || null
}

async function getCloseOperationsByHotelId(): Promise<{ minDate: string, maxDate: string }> {
  let objResult: { minDate: string, maxDate: string } = {
    minDate: dayjs().format('YYYY-MM-DD'),
    maxDate: dayjs().format('YYYY-MM-DD')
  }
  try {
    const { hotel } = await getInvoiceById()

    if (hotel && hotel.id) {
      const hotelFilter = payloadForCloseOperation.value.filter.find((item: IFilter) => item.key === 'hotel.id')

      if (hotelFilter) {
        hotelFilter.value = hotel.id
      }
      else {
        payloadForCloseOperation.value.filter = [...payloadForCloseOperation.value.filter, {
          key: 'hotel.id',
          operator: 'EQUALS',
          value: hotel.id,
          logicalOperation: 'AND',
          type: 'filterSearch',
        }]
      }
      const objCloseOperation = await GenericService.search(configInvoiceApi.moduleApi, configInvoiceApi.uriApiCloseOperations, payloadForCloseOperation.value)

      if (objCloseOperation.data.length === 0) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Close operation not found', life: 3000 })
        objResult = {
          minDate: dayjs().format('YYYY-MM-DD'),
          maxDate: dayjs().format('YYYY-MM-DD')
        }
      }
      else if (objCloseOperation.data.length > 0) {
        objResult = {
          minDate: objCloseOperation.data[0]?.beginDate,
          maxDate: objCloseOperation.data[0]?.endDate
        }
      }
    }

    return objResult
  }
  catch (error) {
    console.log(error)
    return objResult
  }
}

watch(() => props.invoiceObj, () => {
  if (props.invoiceObj?.invoiceType?.id) {
    invoiceType.value = props.invoiceObj?.invoiceType?.id
  }
  else {
    invoiceType.value = route.query.type as string
  }
})

onMounted(async () => {
  props?.fields.forEach((container) => {
    formFields.value.push(...container.childs)
  })

  if (props.invoiceObj?.invoiceType?.id) {
    invoiceType.value = props.invoiceObj?.invoiceType?.id
  }
  else {
    invoiceType.value = route.query.type as string
  }

  minDate.value = dayjs((await getCloseOperationsByHotelId()).minDate).toDate()
  maxDate.value = dayjs((await getCloseOperationsByHotelId()).maxDate).toDate()
  props.getTransactionTypeList('', true)
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible"
    modal :header="header"
    :class="props.class || 'p-4 h-fit'"
    :content-class="contentClass || 'border-round-bottom border-top-1 surface-border h-fit'"
    :block-scroll="true"
    @hide="closeDialog"
  >
    <div class="w-full h-full overflow-hidden p-2">
      <EditFormV2WithContainer
        :key="formReload" :fields-with-containers="fields" :item="item" :show-actions="true"
        :loading-save="loadingSaveAll" :container-class="containerClass" @cancel="clearForm"
        @delete="requireConfirmationToDelete($event)" @submit="requireConfirmationToSave($event)"
      >
        <template #field-date="{ item: data, onUpdate }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.date"
            date-format="yy-mm-dd"
            :max-date="dayjs().endOf('day').toDate()"
            @update:model-value="($event) => {

              if (dayjs($event).isValid()){
                if (dayjs($event).isAfter(minDate) || dayjs($event).isSame(minDate) || dayjs($event).isBefore(maxDate) || dayjs($event).isSame(maxDate)) {
                  onUpdate('date', dayjs($event).startOf('day').toDate())
                }
                else {
                  toast.add({ severity: 'error', summary: 'Error', detail: 'The date must be within the Close of Operation.', life: 3000 })
                }
              }
              else {
                toast.add({ severity: 'error', summary: 'Error', detail: 'Invalid date', life: 3000 })
              }
            }"
          />
        </template>

        <template #field-amount="{ item: data, onUpdate }">
          <InputText

            v-model="data.amount" @update:model-value="($event: any) => {

              console.log($event);

              if (isNaN(+$event)){
                $event = 0
              }

              let amount = invoiceAmount

              if (idItem){
                amount -= item?.amount
              }

              amountError = false
              onUpdate('amount', Number($event))

              if (invoiceType === InvoiceType.INVOICE) {
                if (Number(amount) + Number($event) < 0) {
                  amountError = true
                }
              }
              if (invoiceType === InvoiceType.CREDIT || invoiceType === InvoiceType.OLD_CREDIT) {
                if (Number(amount) + Number($event) > 0) {
                  amountError = true
                }
              }

            }"
          />
          <span v-if="amountError" class="error-message p-error text-xs">The sum of the amount field and invoice amount field is {{ invoiceType === InvoiceType.INVOICE ? 'under' : 'over' }} 0</span>
        </template>

        <template #field-transactionType="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll && !loadingDefaultTransactionType"
            id="autocomplete"
            field="fullName"
            item-value="id"
            :model="data.transactionType"
            :suggestions="transactionTypeList"
            @change="($event) => {
              onUpdate('transactionType', $event)
              if ($event !== null && $event?.isRemarkRequired){
                onUpdate('description', $event?.defaultRemark)
              }
              else {
                onUpdate('description', '')
              }
            }" @load="($event) => getTransactionTypeList($event)"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>

        <template #form-footer="props">
          <Button
            v-tooltip.top="'Save'" label="Save" class="w-6rem mx-2 sticky" icon="pi pi-save" @click="($event) => {
              if (!amountError) {
                props.item.submitForm($event)
              }
            }"
          />
          <Button
            v-tooltip.top="'Cancel'" severity="secondary" label="Cancel" class="w-6rem mx-1" icon="pi pi-times" @click="() => {

              clearForm()
              closeDialog()

            }"
          />
        </template>
      </EditFormV2WithContainer>
    </div>
  </Dialog>
</template>

<script setup lang="ts">
import { z } from 'zod'
import type { FieldDefinitionType } from '../form/EditFormV2'

const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '' },
  item: { type: Object, required: true },
  fields: { type: Array<FieldDefinitionType>, required: true },
  loadingSaveAll: { type: Boolean, default: false },
  selectedPayment: { type: Object, required: true },
  isSplitAction: { type: Boolean, default: false },
  action: {
    type: String as PropType<'new-detail' | 'edit-detail' | 'deposit-transfer' | 'split-deposit' | 'apply-deposit' | 'apply-payment'>,
    default: 'new-detail'
  }
})
const emit = defineEmits(['update:visible', 'save', 'applyPayment', 'update:amount'])
const confirm = useConfirm()
const onOffDialog = ref(props.visible)
const transactionTypeList = ref<any[]>([])
const formReload = ref(0)
const disabledBtnApplyPaymentByTransactionType = ref(true)
const forceSave = ref(false)
let submitEvent: Event = new Event('')

const amountLocalTemp = ref('0')

const item = ref({ ...props.item })

const objApis = ref({
  transactionType: { moduleApi: 'settings', uriApi: 'manage-payment-transaction-type' },
})
function closeDialog() {
  onOffDialog.value = false
  emit('update:visible', false)
}

async function handleSave(event: any) {
  item.value = event
  emit('save', item.value)
}

function saveSubmit(event: Event) {
  forceSave.value = true
  submitEvent = event
}
// function openDialogApplyPayment(event: Event) {

//   submitEvent = event
// }

function handleSaveForm($event: any) {
  const idItems = item.value?.id
  if (idItems) {
    forceSave.value = false
    item.value = $event
    item.value.id = idItems
  }
  else {
    forceSave.value = false
    item.value = $event
  }

  emit('save', item.value)
}

function applyPayment(event: Event) {
  emit('applyPayment', { event, amount: amountLocalTemp.value })
}

interface DataListItem {
  id: string
  code: string
  status: string | boolean
  description: string
  name: string
  cash: boolean
  agencyRateAmount: boolean
  negative: boolean
  policyCredit: boolean
  remarkRequired: boolean
  minNumberOfCharacter: number
  defaultRemark: string
  deposit: boolean
  applyDeposit: boolean
  defaults: boolean
}

interface ListItem {
  id: string
  code: string
  status: string | boolean
  description: string
  name: string
  cash: boolean
  agencyRateAmount: boolean
  negative: boolean
  policyCredit: boolean
  remarkRequired: boolean
  minNumberOfCharacter: number
  defaultRemark: string
  deposit: boolean
  applyDeposit: boolean
  default: boolean
}
function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    code: data.code,
    description: data.description,
    minNumberOfCharacter: data.minNumberOfCharacter,
    defaultRemark: data.defaultRemark,
    deposit: data.deposit,
    applyDeposit: data.applyDeposit,
    agencyRateAmount: data.agencyRateAmount,
    negative: data.negative,
    policyCredit: data.policyCredit,
    status: data.status,
    default: data.defaults,
    cash: data.cash,
    remarkRequired: data.remarkRequired

  }
}

async function getTransactionTypeList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[], short?: IQueryToSort) {
  transactionTypeList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, short)
}

function requireConfirmationToSave(item: any) {
  confirm.require({
    target: submitEvent.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: async () => {
      await handleSave(item)
    },
    reject: () => {}
  })
}

async function loadDefaultsValues() {
  const filter: FilterCriteria[] = []
  switch (props.action) {
    case 'new-detail':
      filter.push(
        {
          key: 'deposit',
          logicalOperation: 'AND',
          operator: 'NOT_EQUALS',
          value: true
        },
        // {
        //   key: 'applyDeposit',
        //   logicalOperation: 'AND',
        //   operator: 'NOT_EQUALS',
        //   value: true
        // },
        {
          key: 'status',
          logicalOperation: 'AND',
          operator: 'EQUALS',
          value: 'ACTIVE',
        },
      )
      break
    case 'deposit-transfer':
      filter.push(
        {
          key: 'deposit',
          logicalOperation: 'AND',
          operator: 'EQUALS',
          value: true
        },
        {
          key: 'defaults',
          logicalOperation: 'AND',
          operator: 'EQUALS',
          value: true,
        },
        {
          key: 'status',
          logicalOperation: 'AND',
          operator: 'EQUALS',
          value: 'ACTIVE',
        },
      )
      break
    case 'split-deposit':
      filter.push(
        {
          key: 'deposit',
          logicalOperation: 'AND',
          operator: 'EQUALS',
          value: true
        },
        {
          key: 'defaults',
          logicalOperation: 'AND',
          operator: 'EQUALS',
          value: true,
        },
        {
          key: 'status',
          logicalOperation: 'AND',
          operator: 'EQUALS',
          value: 'ACTIVE',
        },
      )
      break
    case 'apply-deposit':
      filter.push(
        {
          key: 'applyDeposit',
          logicalOperation: 'AND',
          operator: 'EQUALS',
          value: true
        },
        {
          key: 'defaults',
          logicalOperation: 'AND',
          operator: 'EQUALS',
          value: true,
        },
        {
          key: 'status',
          logicalOperation: 'AND',
          operator: 'EQUALS',
          value: 'ACTIVE',
        },
      )
      break
  }

  await getTransactionTypeList(objApis.value.transactionType.moduleApi, objApis.value.transactionType.uriApi, {
    query: '',
    keys: ['name', 'code'],
  }, filter)

  switch (props.action) {
    case 'new-detail': {
      if (item.value.transactionType && item.value.transactionType.remarkRequired === false) {
        const decimalSchema = z.object(
          {
            remark: z
              .string()
          }
        )
        updateFieldProperty(props.fields, 'remark', 'validation', decimalSchema.shape.remark)
      }
      if (item.value.transactionType && item.value.transactionType.remarkRequired === true) {
        const decimalSchema = z.object(
          {
            remark: z
              .string()
              .min(item.value.transactionType.minNumberOfCharacter, { message: `The field "Remark" should have a minimum of ${item.value.transactionType.minNumberOfCharacter} characters.` })
          }
        )
        updateFieldProperty(props.fields, 'remark', 'validation', decimalSchema.shape.remark)
      }
      break
    }
    case 'deposit-transfer':{
      if (item.value.transactionType && item.value.transactionType.remarkRequired === false) {
        const decimalSchema = z.object(
          {
            remark: z
              .string()
          }
        )
        updateFieldProperty(props.fields, 'remark', 'validation', decimalSchema.shape.remark)
      }
      if (item.value.transactionType && item.value.transactionType.remarkRequired === true) {
        const decimalSchema = z.object(
          {
            remark: z
              .string()
              .min(item.value.transactionType.minNumberOfCharacter, { message: `The field "Remark" should have a minimum of ${item.value.transactionType.minNumberOfCharacter} characters.` })
          }
        )
        updateFieldProperty(props.fields, 'remark', 'validation', decimalSchema.shape.remark)
      }
      break
    }
    case 'split-deposit':
      item.value.transactionType = transactionTypeList.value.length > 0 ? transactionTypeList.value[0] : null

      if (item.value.transactionType && item.value.transactionType.remarkRequired === false) {
        const decimalSchema = z.object(
          {
            remark: z
              .string()
          }
        )
        updateFieldProperty(props.fields, 'remark', 'validation', decimalSchema.shape.remark)
      }
      if (item.value.transactionType && item.value.transactionType.remarkRequired === true) {
        // updateFieldProperty(props.fields, 'remark', 'disabled', false)111
        const decimalSchema = z.object(
          {
            remark: z
              .string()
              .min(item.value.transactionType.minNumberOfCharacter, { message: `The field "Remark" should have a minimum of ${item.value.transactionType.minNumberOfCharacter} characters.` })
          }
        )
        updateFieldProperty(props.fields, 'remark', 'validation', decimalSchema.shape.remark)
      }
      break
    case 'apply-deposit':
      if (item.value.transactionType && item.value.transactionType.remarkRequired === false) {
        const decimalSchema = z.object(
          {
            remark: z
              .string()
          }
        )
        updateFieldProperty(props.fields, 'remark', 'validation', decimalSchema.shape.remark)
      }
      if (item.value.transactionType && item.value.transactionType.remarkRequired === true) {
        const decimalSchema = z.object(
          {
            remark: z
              .string()
              .min(item.value.transactionType.minNumberOfCharacter, { message: `The field "Remark" should have a minimum of ${item.value.transactionType.minNumberOfCharacter} characters.` })
          }
        )
        updateFieldProperty(props.fields, 'remark', 'validation', decimalSchema.shape.remark)
      }
      break

    default:
      break
  }
  formReload.value++
}

watch(() => props.visible, (newValue) => {
  onOffDialog.value = newValue
  emit('update:visible', newValue)
})

watch(() => props.item, async (newValue) => {
  if (newValue) {
    item.value = { ...newValue }
  }
})

watch(() => amountLocalTemp.value, async (newValue) => {
  emit('update:amount', newValue)
})

onMounted(async () => {
  // if (!props.item?.id) {
  //   loadDefaultsValues()
  // }
  // else if (props.action === 'apply-deposit') {
  //   loadDefaultsValues()
  // }
  // else if (props.action === 'deposit-transfer') {
  //   loadDefaultsValues()
  // }
  // else if (props.action === 'split-deposit') {
  // loadDefaultsValues()
  // }
  loadDefaultsValues()
})

function processValidation($event: any, data: any) {
  // Validacion del campo Amount
  const decimalRegex = /^(?!0(\.0{1,2})?$)\d+(\.\d{1,2})?$/

  if (props.action === 'new-detail') {
    if ($event.cash === false && $event.deposit === false) {
      const decimalSchema = z.object(
        {
          amount: z
            .string()
            .refine(value => !Number.isNaN(Number.parseFloat(value)) && (Number.parseFloat(value) > 0), { message: 'The amount must be greater than zero' })
        },
      )
      updateFieldProperty(props.fields, 'amount', 'validation', decimalSchema.shape.amount)
      updateFieldProperty(props.fields, 'amount', 'helpText', 'Max amount: ∞')
    }
    else {
      const decimalSchema = z.object(
        {
          amount: z
            .string()
            .refine(value => !Number.isNaN(Number.parseFloat(value)) && (Number.parseFloat(value) > 0) && (Number.parseFloat(value) <= props.selectedPayment.paymentBalance), { message: 'The amount must be greater than zero and lessor equal than Payment Balance' })
        },
      )
      updateFieldProperty(props.fields, 'amount', 'validation', decimalSchema.shape.amount)
      updateFieldProperty(props.fields, 'amount', 'helpText', `Max amount: ${Math.abs(props.selectedPayment.paymentBalance).toFixed(2)}`)
    }
    if ($event.remarkRequired === false) {
      const decimalSchema = z.object(
        {
          remark: z
            .string()
        }
      )
      updateFieldProperty(props.fields, 'remark', 'validation', decimalSchema.shape.remark)
    }
    if ($event.remarkRequired === true) {
      // updateFieldProperty(props.fields, 'remark', 'disabled', false)111
      const decimalSchema = z.object(
        {
          remark: z
            .string()
            .min($event.minNumberOfCharacter, { message: `The field "Remark" should have a minimum of ${$event.minNumberOfCharacter} characters.` })
        }
      )
      updateFieldProperty(props.fields, 'remark', 'validation', decimalSchema.shape.remark)
    }
  }
  else if (props.action === 'apply-deposit') {
    const decimalSchema = z.object(
      {
        amount: z
          .string()
          .refine(value => !Number.isNaN(Number.parseFloat(value)) && (Number.parseFloat(value) <= item.value.oldAmount), { message: 'Deposit Amount must be greather than zero and less or equal than the selected transaction amount' })
      },
    )
    updateFieldProperty(props.fields, 'amount', 'validation', decimalSchema.shape.amount)
    updateFieldProperty(props.fields, 'amount', 'helpText', `Max amount: ${Math.abs(item.value.oldAmount).toFixed(2)}`)
  }
  else {
    const decimalSchema = z.object(
      {
        amount: z
          .string()
          .refine(value => Number.parseFloat(value) <= Math.abs(props.selectedPayment.paymentBalance), { message: 'The amount must be greater than zero and less or equal than Payment Balance' }),
      },
    )
    updateFieldProperty(props.fields, 'amount', 'validation', decimalSchema.shape.amount)
    updateFieldProperty(props.fields, 'amount', 'helpText', `Max amount: ${Math.abs(props.selectedPayment.paymentBalance).toFixed(2)}`)
  }
  // Validacion del campo Remark
  if (props.action === 'deposit-transfer') {
    data.remark = ''
    const decimalSchema = z.object(
      {
        remark: z
          .string()
      }
    )
    updateFieldProperty(props.fields, 'remark', 'validation', decimalSchema.shape.remark)
  }
  else if ($event.remarkRequired === true) {
    // data.remark = $event.remarkRequired ? '' : $event.defaultRemark
    const decimalSchema = z.object(
      {
        remark: z
          .string()
          .min($event.minNumberOfCharacter, { message: `The field "Remark" should have a minimum of ${data.transactionType.minNumberOfCharacter} characters.` })
      }
    )
    updateFieldProperty(props.fields, 'remark', 'validation', decimalSchema.shape.remark)
  }
  else {
    // data.remark = $event.defaultRemark
    const decimalSchema = z.object(
      {
        remark: z
          .string()
      }
    )
    updateFieldProperty(props.fields, 'remark', 'validation', decimalSchema.shape.remark)
  }
}
</script>

<template>
  <Dialog
    id="dialogPaymentDetailForm"
    v-model:visible="onOffDialog"
    modal
    :closable="false"
    header="Payment Details"
    :style="{ width: '50rem' }"
    :breakpoints="{ '1199px': '75vw', '575px': '90vw' }"
    :pt="{
      root: {
        class: 'custom-dialog-detail',
      },
      header: {
        style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
      },
    }"
  >
    <template #header>
      <div class="flex justify-content-between align-items-center justify-content-between w-full">
        <h5 class="m-0 py-2">
          {{ props.title }}
        </h5>
      </div>
    </template>

    <template #default>
      <EditFormV2
        :key="formReload"
        class="mt-3"
        :fields="fields"
        :item="item"
        :show-actions="false"
        :force-save="forceSave"
        container-class="grid pt-5"
        :loading-save="props.loadingSaveAll"
        @cancel="closeDialog"
        @force-save="forceSave = $event"
        @submit="handleSaveForm($event)"
      >
        <!-- || props.action === 'split-deposit' -->
        <template #field-transactionType="{ item: data, onUpdate, fields: listFields, field }">
          <DebouncedAutoCompleteComponent
            v-if="!props.loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.transactionType"
            :disabled="listFields.find((element: any) => element.field === field)?.disabled"
            :suggestions="[...transactionTypeList]"
            @change="($event) => {
              onUpdate('transactionType', $event)
              if ($event) {
                onUpdate('remark', '')
                if ($event.cash || $event.applyDeposit) {
                  disabledBtnApplyPaymentByTransactionType = true
                }
                else {
                  disabledBtnApplyPaymentByTransactionType = false
                }
              }
              processValidation($event, data)
            }"
            @load="async($event) => {
              const objQueryToSearch = {
                query: $event,
                keys: ['name', 'code'],
              }
              const filter: FilterCriteria[] = []
              const sortObj = {
                sortBy: '',
                // sortType: ENUM_SHORT_TYPE.DESC,
              }
              switch (props.action) {
              case 'new-detail':
                // sortObj.sortBy = 'defaults'
                filter.push(
                  {
                    key: 'deposit',
                    logicalOperation: 'AND',
                    operator: 'NOT_EQUALS',
                    value: true,
                  },
                  {
                    key: 'applyDeposit',
                    logicalOperation: 'AND',
                    operator: 'NOT_EQUALS',
                    value: true,
                  },
                  {
                    key: 'status',
                    logicalOperation: 'AND',
                    operator: 'EQUALS',
                    value: 'ACTIVE',
                  },
                )
                break

              case 'split-deposit':
                filter.push(
                  {
                    key: 'deposit',
                    logicalOperation: 'AND',
                    operator: 'EQUALS',
                    value: true,
                  },
                  // {
                  //   key: 'defaults',
                  //   logicalOperation: 'OR',
                  //   operator: 'EQUALS',
                  //   value: true,
                  // },
                )
                break

              case 'deposit-transfer':
                filter.push(
                  {
                    key: 'deposit',
                    logicalOperation: 'AND',
                    operator: 'EQUALS',
                    value: true,
                  },
                  {
                    key: 'status',
                    logicalOperation: 'AND',
                    operator: 'EQUALS',
                    value: 'ACTIVE',
                  },
                )
                break
              case 'apply-deposit':
                filter.push(
                  {
                    key: 'applyDeposit',
                    logicalOperation: 'AND',
                    operator: 'EQUALS',
                    value: true,
                  },
                  {
                    key: 'status',
                    logicalOperation: 'AND',
                    operator: 'EQUALS',
                    value: 'ACTIVE',
                  },
                )
                break
              }
              await getTransactionTypeList(objApis.transactionType.moduleApi, objApis.transactionType.uriApi, objQueryToSearch, filter, sortObj)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-amount="{ item: data, onUpdate, fields: listFields, field }">
          <!-- <pre>{{ listFields }}</pre> -->
          <InputText
            v-if="!props.loadingSaveAll"
            v-model="data.amount"
            class="w-full"
            :disabled="listFields.find((element: any) => element.field === field)?.disabled"
            @update:model-value="($event) => {
              onUpdate('amount', $event)
              amountLocalTemp = $event
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
      </EditFormV2>
    </template>

    <template #footer>
      <!-- <IfCan :perms="['PAYMENT-MANAGEMENT:APPLY-PAYMENT']">
        <Button
          v-if="(action === 'new-detail' || action === 'apply-deposit') && disabledBtnApplyPaymentByTransactionType"
          v-tooltip.top="'Apply Payment'"
          link class="w-auto ml-1 sticky"
          :disabled="Number(amountLocalTemp) === 0 || Number(amountLocalTemp) === 0.00 || Number(amountLocalTemp) <= 0.01"
          @click="applyPayment($event)"
        >
          <Button class="ml-1 w-3rem p-button-primary" icon="pi pi-cog" />
          <span class="ml-2 font-bold">
            Apply Payment
          </span>
        </Button>
      </IfCan> -->
      <!-- <IfCan :perms="['PAYMENT-MANAGEMENT:CREATE-DETAIL']">
      </IfCan> -->
      <Button v-tooltip.top="'Save'" class="w-3rem ml-4 p-button" icon="pi pi-save" :loading="props.loadingSaveAll" @click="saveSubmit($event)" />
      <Button v-tooltip.top="'Cancel'" class="ml-1 w-3rem p-button-secondary" icon="pi pi-times" @click="closeDialog" />
      <!-- <Button v-tooltip.top="'Cancel'" class="w-3rem p-button-danger p-button-outlined" icon="pi pi-trash" @click="closeDialog" /> -->
    </template>
  </Dialog>
</template>

<style lang="scss">
.custom-dialog-detail .p-dialog-content {
  background-color: #ffffff;
}
.custom-dialog-detail .p-dialog-footer {
  background-color: #ffffff;
}
</style>

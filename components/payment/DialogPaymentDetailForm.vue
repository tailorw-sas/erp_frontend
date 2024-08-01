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
    type: String as PropType<'new-detail' | 'deposit-transfer' | 'split-deposit' | 'apply-deposit'>,
    default: 'new-detail'
  }
})
const emit = defineEmits(['update:visible', 'save'])
const confirm = useConfirm()
const onOffDialog = ref(props.visible)
const transactionTypeList = ref<any[]>([])
const formReload = ref(0)

const forceSave = ref(false)
let submitEvent: Event = new Event('')

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
        // {
        //   key: 'defaults',
        //   logicalOperation: 'AND',
        //   operator: 'EQUALS',
        //   value: true,
        // },
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
      if (transactionTypeList.value.length > 0) {
        const objDefault = transactionTypeList.value.find((item: ListItem) => item.default === true)
        if (objDefault) {
          item.value.transactionType = objDefault
        }
        else {
          item.value.transactionType = transactionTypeList.value.length > 0 ? transactionTypeList.value[0] : null
        }
      }
      const decimalRegex = /^(?!0(\.0{1,2})?$)\d+(\.\d{1,2})?$/

      if (item.value.transactionType && item.value.transactionType.cash === false) {
        // const decimalSchema = z.object(
        //   {
        //     amount: z
        //       .string()
        //       .regex(decimalRegex, { message: 'The amount must be greater than zero' })
        //       // .regex(decimalRegex, { message: 'The amount must be greater than zero and less or equal than Payment Balance' })
        //   }
        // )
        const decimalSchema = z.object(
          {
            remark: z
              .string(),
            amount: z
              .string()
              .refine(value => !Number.isNaN(Number.parseFloat(value)) && (Number.parseFloat(value) > 0), { message: 'The amount must be greater than zero' })
          },
        )
        updateFieldProperty(props.fields, 'amount', 'validation', decimalSchema.shape.amount)
      }
      if (item.value.transactionType && item.value.transactionType.cash === true) {
        const decimalSchema = z.object({
          amount: z
            .string()
            .superRefine((value, ctx) => {
              const amount = Number.parseFloat(value)

              const maxPaymentBalance = props.selectedPayment?.paymentBalance
              let count = 0
              if (amount !== undefined && Number.isNaN(amount)) {
                count++
              }
              else if (amount <= 0) {
                count++
              }
              else if (amount > maxPaymentBalance) {
                count++
              }

              if (count > 0) {
                ctx.addIssue({
                  code: z.ZodIssueCode.custom,
                  path: ['amount'],
                  message: 'The amount must be greater than zero and less or equal than Payment Balance',
                })
              }
            })
        })
        updateFieldProperty(props.fields, 'amount', 'validation', decimalSchema.shape.amount)
      }
      if (item.value.transactionType && item.value.transactionType.remarkRequired === false) {
        updateFieldProperty(props.fields, 'remark', 'disabled', true)
      }
      if (item.value.transactionType && item.value.transactionType.remarkRequired === true) {
        updateFieldProperty(props.fields, 'remark', 'disabled', false)
        const decimalSchema = z.object(
          {
            remark: z
              .string()
              .max(item.value.transactionType.minNumberOfCharacter, { message: `The field "Remark" should have a maximum of ${item.value.transactionType.minNumberOfCharacter} characters.` })
          }
        )
        updateFieldProperty(props.fields, 'remark', 'validation', decimalSchema.shape.remark)
      }
    }
      break
    case 'deposit-transfer':{
      if (transactionTypeList.value.length === 1) {
        item.value.transactionType = transactionTypeList.value[0]
      }
      else if (transactionTypeList.value.length > 1) {
        item.value.transactionType = transactionTypeList.value.find((item: any) => item.default === true)
      }
      const decimalSchema = z.object(
        {
          amount: z
            .string()
            .refine(value => !Number.isNaN(Number.parseFloat(value)) && (Number.parseFloat(value) > 0 && Number.parseFloat(value) <= props.selectedPayment.paymentBalance), { message: 'The amount must be greater than zero and less or equal than Payment Balance' })
        },
      )
      updateFieldProperty(props.fields, 'remark', 'disabled', false)
      updateFieldProperty(props.fields, 'amount', 'validation', decimalSchema.shape.amount)
    }
      break
    case 'split-deposit':
      item.value.transactionType = transactionTypeList.value.length > 0 ? transactionTypeList.value[0] : null
      break
    case 'apply-deposit':
      item.value.transactionType = transactionTypeList.value.length > 0 ? transactionTypeList.value[0] : null

      // const decimalSchema = z.object(
      //   {
      //     amount: z
      //       .string()
      //       .refine(value => !Number.isNaN(Number.parseFloat(value)) && (Number.parseFloat(value) > 0 && Number.parseFloat(value) <= item.value.applyDepositValue), { message: 'Deposit Amount must be greather than zero and less or equal than the selected transaction max amount' })
      //   },
      // )
      // updateFieldProperty(props.fields, 'amount', 'validation', decimalSchema.shape.amount)

      break
  }

  // item.value.transactionType = transactionTypeList.value.length > 0 ? transactionTypeList.value[0] : null
  // item.value.amount = props.selectedPayment.paymentBalance || 0
  if (item.value.transactionType && item.value.transactionType.remarkRequired === false && props.action !== 'deposit-transfer') {
    item.value.remark = item.value.transactionType.defaultRemark
  }
  formReload.value++
}

watch(() => props.visible, (newValue) => {
  onOffDialog.value = newValue
  emit('update:visible', newValue)
})

watch(() => props.item, async (newValue) => {
  if (newValue) {
    // newValue.transactionType.status = 'ACTIVE'
    item.value = { ...newValue }
  }
})

onMounted(async () => {
  if (!props.item?.id) {
    loadDefaultsValues()
  }
  else if (props.action === 'apply-deposit') {
    loadDefaultsValues()
  }
  else if (props.action === 'deposit-transfer') {
    loadDefaultsValues()
  }
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
    // const decimalSchema = z.object(
    //   {
    //     amount: z
    //       .string()
    //       .regex(decimalRegex, { message: 'The amount must be greater than zero and lessor equal than Payment Balance' }),
    //   },
    // )
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
    updateFieldProperty(props.fields, 'remark', 'disabled', false)
    const decimalSchema = z.object(
      {
        remark: z
          .string()
      }
    )
    updateFieldProperty(props.fields, 'remark', 'validation', decimalSchema.shape.remark)
  }
  else if ($event.remarkRequired === true) {
    data.remark = $event.remarkRequired ? '' : $event.defaultRemark
    updateFieldProperty(props.fields, 'remark', 'disabled', false)
    const decimalSchema = z.object(
      {
        remark: z
          .string()
          .max($event.minNumberOfCharacter, { message: `The field "Remark" should have a maximum of ${data.transactionType.minNumberOfCharacter} characters.` })
      }
    )
    updateFieldProperty(props.fields, 'remark', 'validation', decimalSchema.shape.remark)
  }
  else {
    data.remark = $event.defaultRemark
    const decimalSchema = z.object(
      {
        remark: z
          .string()
      }
    )
    updateFieldProperty(props.fields, 'remark', 'validation', decimalSchema.shape.remark)
    updateFieldProperty(props.fields, 'remark', 'disabled', true)
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
        class: 'custom-dialog',
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
        :loading-save="props.loadingSaveAll"
        @cancel="closeDialog"
        @force-save="forceSave = $event"
        @submit="handleSaveForm($event)"
      >
        <template #field-transactionType="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!props.loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.transactionType"
            :disabled="props.action === 'deposit-transfer' || props.action === 'apply-deposit' || props.action === 'split-deposit'"
            :suggestions="[...transactionTypeList]"
            @change="($event) => {
              onUpdate('transactionType', $event)
              if ($event) {
                onUpdate('remark', '')
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
      </EditFormV2>
    </template>

    <template #footer>
      <Button v-if="action === 'new-detail' || action === 'apply-deposit'" v-tooltip.top="'Apply Payment'" link class="w-auto ml-1 sticky">
        <Button class="ml-1 w-3rem p-button-primary" icon="pi pi-cog" />
        <span class="ml-2 font-bold">
          Apply Payment
        </span>
      </Button>
      <Button v-tooltip.top="'Apply'" class="w-3rem ml-4 p-button" icon="pi pi-save" :loading="props.loadingSaveAll" @click="saveSubmit($event)" />
      <Button v-tooltip.top="'Cancel'" class="ml-1 w-3rem p-button-secondary" icon="pi pi-times" @click="closeDialog" />
      <!-- <Button v-tooltip.top="'Cancel'" class="w-3rem p-button-danger p-button-outlined" icon="pi pi-trash" @click="closeDialog" /> -->
    </template>
  </Dialog>
</template>

<style lang="scss">
.custom-dialog .p-dialog-content {
  background-color: #ffffff;
}
.custom-dialog .p-dialog-footer {
  background-color: #ffffff;
}
</style>

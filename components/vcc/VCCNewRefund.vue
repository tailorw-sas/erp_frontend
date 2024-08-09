<script setup lang="ts">
import { ref, watch } from 'vue'
import { usePrimeVue } from 'primevue/config'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import { z } from 'zod'
import Divider from 'primevue/divider'
import { GenericService } from '~/services/generic-services'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'

const props = defineProps({
  openDialog: {
    type: Boolean,
    required: true
  },
  parentTransaction: {
    type: Object,
    required: true,
  }
})
const emits = defineEmits<{
  (e: 'onCloseDialog', value: boolean): void
}>()

const $primevue = usePrimeVue()

defineExpose({
  $primevue
})
const formReload = ref(0)
const forceSave = ref(false)
let submitEvent = new Event('')
const confirm = useConfirm()
const dialogVisible = ref(props.openDialog)
const loadingSaveAll = ref(false)
const confApi = reactive({
  moduleApi: 'creditcard',
  uriApi: 'transactions/refund',
})
const toast = useToast()
const partialErrors = ref<string[]>([])

const fields: Array<FieldDefinitionType> = [
  {
    field: 'transactionId',
    header: 'Transaction Id',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-4',
    validation: z.string().trim().min(1, 'The transaction id field is required'),
  },
  {
    field: 'reference',
    header: 'Reference',
    dataType: 'text',
    disabled: true,
    class: 'field col-12',
    validation: z.string().trim().min(1, 'The reference field is required'),
  },
  {
    field: 'grossAmount',
    header: 'Gross Amount',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-4',
    validation: z.string().trim().min(1, 'The gross amount field is required')
      .regex(/^\d+(\.\d+)?$/, 'Only numeric characters allowed')
      .refine(val => Number.parseFloat(val) > 0, {
        message: 'The amount must be greater than zero',
      })
  },
  {
    field: 'transactionCommission',
    header: 'Commission',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-4',
  },
  {
    field: 'netAmount',
    header: 'Net Amount',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-4',
    validation: z.string().trim().min(1, 'The net amount field is required')
      .regex(/^\d+(\.\d+)?$/, 'Only numeric characters allowed')
      .refine(val => Number.parseFloat(val) > 0, {
        message: 'The amount must be greater than zero',
      })
  },
  {
    field: 'separator',
    header: '',
    dataType: 'text',
    class: 'field col-12',
  },
  {
    field: 'refundType',
    header: 'Refund Type',
    dataType: 'select',
    class: 'field col-12 sm:col-6 md:col-3',
    validation: z.object({
      id: z.string(),
      name: z.string(),
    }).nullable()
      .refine(value => value && value.id && value.name, { message: `The refund type field is required` })
  },
  {
    field: 'refundCommission',
    header: 'Commission',
    dataType: 'check',
    class: 'field col-12 sm:col-6 md:col-3 flex align-self-start justify-content-center pb-2 mt-4',
  },
  {
    field: 'selectedAmount',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-6',
  }
]

const validationPartialAmount = z.string().trim().min(1, 'The partial field is required')
  .regex(/^\d+(\.\d+)?$/, 'Only numeric characters allowed')
  .refine(val => Number.parseFloat(val) > 0, {
    message: 'The amount must be greater than zero',
  })

const item = ref<GenericObject>({
  transactionId: '',
  reference: '',
  grossAmount: '',
  transactionCommission: '',
  netAmount: '',
  refundType: null,
  refundCommission: true,
  reservationNumber: '',
  selectedAmount: {
    type: '',
    total: '0',
    partial: '0',
  },
})

const itemTemp = ref<GenericObject>({
  transactionId: '',
  reference: '',
  grossAmount: '',
  transactionCommission: '',
  netAmount: '',
  refundType: null,
  refundCommission: true,
  reservationNumber: '',
  selectedAmount: {
    type: '',
    total: '0',
    partial: '0',
  },
})

const ENUM_REFUND_TYPE = [
  { id: 'gross', name: 'Gross Amount' },
  { id: 'net', name: 'Net Amount' },
]

function validatePartialField(field: string) {
  if (item.value.selectedAmount.type === 'PARTIAL') {
    const result = validationPartialAmount.safeParse(field)
    if (!result.success) {
      partialErrors.value = result.error.issues.map(e => e.message)
      partialErrors.value = [partialErrors.value[0]]
    }
    else {
      partialErrors.value = []
    }
  }
  return partialErrors.value.length === 0
}

async function getObjectValues($event: any) {
  forceSave.value = false
  item.value = $event
}

function onClose(isCancel: boolean) {
  dialogVisible.value = false
  emits('onCloseDialog', isCancel)
}

function clearForm() {
  item.value = { ...itemTemp.value }
  item.value.transactionId = String(props.parentTransaction.id)
  item.value.reference = props.parentTransaction.referenceNumber
  item.value.grossAmount = String(props.parentTransaction.amount)
  item.value.transactionCommission = String(props.parentTransaction.commission)
  item.value.netAmount = String(props.parentTransaction.netAmount)
  item.value.selectedAmount = {
    type: '',
    total: '0',
    partial: '0',
  }
  formReload.value++
}

function requireConfirmationToSave(item: any) {
  if (!useRuntimeConfig().public.showSaveConfirm) {
    save(item)
  }
  else {
    confirm.require({
      target: submitEvent.currentTarget,
      group: 'headless',
      header: 'Save the record',
      message: 'Do you want to save the change?',
      rejectLabel: 'Cancel',
      acceptLabel: 'Accept',
      accept: async () => {
        await save(item)
      },
      reject: () => {}
    })
  }
}

async function save(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = {}
  try {
    payload.parentId = item.transactionId
    payload.hasCommission = item.refundCommission
    payload.amount = item.selectedAmount.type === 'TOTAL' ? item.selectedAmount.total : item.selectedAmount.partial
    const response: any = await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
    toast.add({ severity: 'info', summary: 'Confirmed', detail: `The transaction details id ${response.id} was created`, life: 10000 })
    onClose(false)
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    loadingSaveAll.value = false
  }
}

function saveSubmit(event: Event) {
  const isValidPartial = validatePartialField(item.value.selectedAmount.partial)
  if (isValidPartial) {
    forceSave.value = true
    submitEvent = event
  }
}

function handleRefundTypeChange(value: any) {
  if (value === 'gross') {
    item.value.selectedAmount.total = props.parentTransaction.amount
  }
  else {
    item.value.selectedAmount.total = props.parentTransaction.netAmount
  }
  if (!item.value.selectedAmount.type) {
    item.value.selectedAmount.type = 'TOTAL'
  }
}

watch(() => item.value, async (newValue) => {
  if (newValue && newValue.refundType && dialogVisible.value) {
    requireConfirmationToSave(newValue)
  }
})

watch(() => props.openDialog, (newValue) => {
  dialogVisible.value = newValue
  if (newValue) {
    clearForm()
    partialErrors.value = []
  }
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible"
    modal
    header="Refund Transaction Details"
    class="w-10 lg:w-6 card p-0"
    content-class="border-round-bottom border-top-1 surface-border pb-0"
    :pt="{
      root: {
        class: 'custom-dialog',
      },
      header: {
        style: 'padding-top: 0.5rem; padding-bottom: 0.5rem;',
      },
      mask: {
        style: 'backdrop-filter: blur(5px)',
      },
    }"
    @hide="onClose(true)"
  >
    <div class="mt-4 p-4">
      <EditFormV2
        :key="formReload"
        :fields="fields"
        :item="item"
        container-class="grid"
        :show-actions="false"
        :loading-save="loadingSaveAll"
        :force-save="forceSave"
        @force-save="forceSave = $event"
        @submit="getObjectValues($event)"
      >
        <template #field-refundType="{ item: data, onUpdate }">
          <Dropdown
            v-model="data.refundType"
            :options="[...ENUM_REFUND_TYPE]"
            option-label="name"
            return-object="false"
            class="align-items-center"
            @update:model-value="($event) => {
              onUpdate('refundType', $event)
              handleRefundTypeChange($event.id)
            }"
          />
        </template>
        <template #field-selectedAmount="{ item: data, onUpdate }">
          <div class="flex gap-4 mb-1 align-self-end">
            <div class="flex align-items-start">
              <RadioButton
                v-model="data.selectedAmount.type" :disabled="!data.refundType" style="margin-top: 18px"
                input-id="total" name="amountType" value="TOTAL" @change="($event) => {
                  data.selectedAmount.type = 'TOTAL'
                }"
              />
              <div class="ml-2">
                <strong for="total">Total</strong>
                <InputText
                  v-model="data.selectedAmount.total"
                  show-clear
                  disabled
                  @change="($event) => {
                    onUpdate('amountTotal', $event)
                  }"
                />
              </div>
            </div>
            <div class="flex align-items-start">
              <RadioButton
                v-model="data.selectedAmount.type" :disabled="!data.refundType" style="margin-top: 18px"
                input-id="partial" name="amountType" value="PARTIAL" @change="($event) => {
                  data.selectedAmount.type = 'PARTIAL'
                }"
              />
              <div class="ml-2">
                <strong for="partial">Partial</strong>
                <InputText
                  v-model="data.selectedAmount.partial"
                  show-clear
                  :disabled="!data.refundType || data.selectedAmount.type === 'TOTAL'"
                  @update:model-value="validatePartialField"
                />
                <div v-if="partialErrors.length > 0" class="w-full p-error text-xs">
                  <div v-for="error in partialErrors" :key="error" class="error-message">
                    {{ error }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
        <template #field-separator="{ item: data, onUpdate }">
          <div class="w-full">
            <Divider />
          </div>
        </template>
      </EditFormV2>
    </div>
    <template #footer>
      <div class="flex justify-content-end mr-4">
        <Button v-tooltip.top="'Save'" label="Save" icon="pi pi-save" :loading="loadingSaveAll" class="mr-2" @click="saveSubmit($event)" />
        <Button v-tooltip.top="'Cancel'" label="Cancel" severity="secondary" icon="pi pi-times" @click="onClose(true)" />
      </div>
    </template>
  </Dialog>
</template>

<style lang="scss">
.custom-dialog .p-dialog-content {
  background-color: #ffffff;
  padding-right: 0px;
  padding-left: 0px;
}
.custom-dialog .p-dialog-footer {
  background-color: #ffffff;
  padding-right: 0px;
  padding-left: 0px;
}
</style>

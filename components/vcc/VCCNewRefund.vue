<script setup lang="ts">
import { ref, watch } from 'vue'
import { usePrimeVue } from 'primevue/config'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import { z } from 'zod'
import dayjs from 'dayjs'
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
  uriApi: 'transactions/manual',
})
const toast = useToast()

const fields: Array<FieldDefinitionType> = [
  {
    field: 'transactionId',
    header: 'Transaction Id',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-6',
    validation: z.string().trim().min(1, 'The transaction id field is required'),
  },
  {
    field: 'reference',
    header: 'Reference',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-6',
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
    validation: z.string().trim().min(1, 'The commission field is required')
      .regex(/^\d+(\.\d+)?$/, 'Only numeric characters allowed')
      .refine(val => Number.parseFloat(val) > 0, {
        message: 'The amount must be greater than zero',
      })
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
    field: 'refundType',
    header: 'Refund Type',
    dataType: 'select',
    class: 'field col-12 sm:col-6 md:col-3',
  },
  {
    field: 'refundCommission',
    header: 'Commission',
    dataType: 'check',
    class: 'field col-12 sm:col-6 md:col-3 flex align-self-end justify-content-center pb-2 mt-2',
  },
  {
    field: 'selectedAmount',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-6',
  }
]

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
    total: '',
    partial: '',
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
    total: '',
    partial: '',
  },
})

const ENUM_REFUND_TYPE = [
  { id: 'gross', name: 'Gross Amount' },
  { id: 'net', name: 'Net Amount' },
]

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
  item.value.netAmount = String(props.parentTransaction.totalAmount)
  formReload.value++
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
      await save(item)
    },
    reject: () => {}
  })
}

async function save(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  console.log(payload)
  /* try {
    payload.merchant = typeof payload.merchant === 'object' ? payload.merchant.id : payload.merchant
    payload.amount = Number(payload.amount)
    payload.checkIn = payload.checkIn ? dayjs(payload.checkIn).format('YYYY-MM-DD') : ''
    payload.hotel = typeof payload.hotel === 'object' ? payload.hotel.id : payload.hotel
    payload.agency = typeof payload.agency === 'object' ? payload.agency.id : payload.agency
    payload.language = typeof payload.language === 'object' ? payload.language.id : payload.language
    payload.methodType = typeof payload.methodType === 'object' ? payload.methodType.id : payload.methodType
    delete payload.event
    const response: any = await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
    // TODO: El mensaje debe ser 'The transaction details id {id} was created'
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    loadingSaveAll.value = false
  } */
}

function saveSubmit(event: Event) {
  forceSave.value = true
  submitEvent = event
}

function handleRefundTypeChange(value: any) {
  if (value === 'gross') {
    item.value.selectedAmount.total = props.parentTransaction.amount
  }
  else {
    item.value.selectedAmount.total = props.parentTransaction.totalAmount
  }
  if (!item.value.selectedAmount.type) {
    item.value.selectedAmount.type = 'TOTAL'
  }
}

watch(() => item.value, async (newValue) => {
  if (newValue && dialogVisible.value) {
    requireConfirmationToSave(newValue)
  }
})

watch(() => props.openDialog, (newValue) => {
  dialogVisible.value = newValue
  if (newValue) {
    clearForm()
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
    @hide="onClose(false)"
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
            <div class="flex align-items-end">
              <RadioButton
                v-model="data.selectedAmount.type" :disabled="!data.refundType"
                class="mb-1" input-id="total" name="amountType" value="TOTAL" @change="($event) => {
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
            <div class="flex align-items-end">
              <RadioButton
                v-model="data.selectedAmount.type" :disabled="!data.refundType"
                class="mb-1" input-id="partial" name="amountType" value="PARTIAL" @change="($event) => {
                  data.selectedAmount.type = 'PARTIAL'
                }"
              />
              <div class="ml-2">
                <strong for="partial">Partial</strong>
                <InputText
                  v-model="data.selectedAmount.partial"
                  show-clear
                  :disabled="!data.refundType || data.amountType === 'TOTAL'"
                  @change="($event) => {
                  }"
                />
              </div>
            </div>
          </div>
        </template>
      </EditFormV2>
    </div>
    <template #footer>
      <div class="flex justify-content-end mr-4">
        <Button v-tooltip.top="'Cancel'" label="Cancel" severity="danger" icon="pi pi-times" class="mr-2" @click="onClose(true)" />
        <Button v-tooltip.top="'Save'" label="Save" icon="pi pi-save" :loading="loadingSaveAll" @click="saveSubmit($event)" />
      </div>
    </template>
  </Dialog>
</template>

<style scoped>
</style>

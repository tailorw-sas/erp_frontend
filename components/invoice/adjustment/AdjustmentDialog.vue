<script setup lang="ts">
import dayjs from 'dayjs'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import { InvoiceType } from '~/utils/Enums'

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
const dialogVisible = ref(props.openDialog)
const formFields = ref<FieldDefinitionType[]>([])
const route = useRoute()

const invoiceType = ref('')
const amountError = ref(false)

watch(() => props.invoiceObj, () => {
  if (props.invoiceObj?.invoiceType?.id) {
    invoiceType.value = props.invoiceObj?.invoiceType?.id
  }
  else {
    invoiceType.value = route.query.type as string
  }
})

onMounted(() => {
  props?.fields.forEach((container) => {
    formFields.value.push(...container.childs)
  })

  if (props.invoiceObj?.invoiceType?.id) {
    invoiceType.value = props.invoiceObj?.invoiceType?.id
  }
  else {
    invoiceType.value = route.query.type as string
  }
  props.getTransactionTypeList('', true)
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal :header="header" :class="props.class || 'p-4 h-fit'"
    :content-class="contentClass || 'border-round-bottom border-top-1 surface-border h-fit'" :block-scroll="true"
    @hide="closeDialog"
  >
    <div class="w-full h-full overflow-hidden mt-2">
      <EditFormV2WithContainer
        :key="formReload" :fields-with-containers="fields" :item="item" :show-actions="true"
        :loading-save="loadingSaveAll" :container-class="containerClass" @cancel="clearForm"
        @delete="requireConfirmationToDelete($event)" @submit="requireConfirmationToSave($event)"
      >
        <template #field-date="{ item: data, onUpdate }">
          <Calendar
            v-if="!loadingSaveAll" v-model="data.date" date-format="yy-mm-dd"
            :max-date="dayjs().endOf('day').toDate()" @update:model-value="($event) => {
              const selectedDate = Array.isArray($event) ? $event[0] : $event

              if (dayjs(selectedDate).isValid()) {
                onUpdate('date', dayjs(selectedDate).startOf('day').toDate())
              }
            }"
          />
        </template>

        <template #field-amount="{ item: data, onUpdate }">
          <InputNumber
            v-if="!loadingSaveAll"
            v-model="data.amount" :min-fraction-digits="0" :max-fraction-digits="2" @input="($event: any) => {
              $event = $event.value
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
          <Skeleton v-else height="2rem" class="mb-2" />
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

        <template #form-footer="slotProps">
          <div class="field flex justify-content-end mr-3 mb-2">
            <Button
              v-tooltip.top="'Save'" label="Save" class="w-6rem mx-1" icon="pi pi-save" @click="($event) => {
                if (!amountError) {
                  slotProps.item.submitForm($event)
                }
              }"
            />
            <Button
              v-tooltip.top="'Cancel'" label="Cancel" severity="secondary" class="w-6rem mx-1 " icon="pi pi-times" @click="() => {

                clearForm()
                closeDialog()
              }"
            />
          </div>
        </template>
      </EditFormV2WithContainer>
    </div>
  </Dialog>
</template>

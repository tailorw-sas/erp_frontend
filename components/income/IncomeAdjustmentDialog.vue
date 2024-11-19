<script setup lang="ts">
import { ref } from 'vue'
import type { FieldDefinitionType } from '../form/EditFormV2'
import type { FilterCriteria } from '~/composables/list'

const props = defineProps({
  visible: { type: Boolean, default: false },
  title: { type: String, default: '' },
  item: { type: Object, required: true },
  fields: { type: Array<FieldDefinitionType>, required: true },
  loadingSaveAll: { type: Boolean, default: false },
  isSplitAction: { type: Boolean, default: false },
  action: {
    type: String as PropType<'new-detail' | 'deposit-transfer' | 'split-deposit'>,
    default: 'new-detail'
  }
})
const emit = defineEmits(['update:visible', 'save', 'onClose'])
const confirm = useConfirm()
const onOffDialog = ref(props.visible)
const transactionTypeList = ref<any[]>([])
const formReload = ref(0)
const loadingDefaultTransactionType = ref(false)

const forceSave = ref(false)
let submitEvent: Event = new Event('')

const item = ref({ ...props.item })

const objApis = ref({
  transactionType: { moduleApi: 'settings', uriApi: 'manage-payment-transaction-type' },
})
function closeDialog() {
  onOffDialog.value = false
  emit('update:visible', false)
  emit('onClose', true)
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
  name: string
  code: string
  status: string
  defaults: boolean
  cash: boolean
}

interface ListItem {
  id: string
  name: string
  status: boolean | string
  default: boolean
  cash: boolean
}
function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status,
    default: data.defaults,
    cash: data.cash
  }
}

async function getTransactionTypeList(queryFilter: string, isDefault: boolean = false) {
  try {
    console.log(`listing default: ${isDefault}`)
    const objQueryToSearch = {
      query: queryFilter,
      keys: ['name', 'code'],
    }
    let filter: FilterCriteria[] = []
    const sortObj = {
      sortBy: '',
      // sortType: ENUM_SHORT_TYPE.DESC,
    }
    if (isDefault) {
      loadingDefaultTransactionType.value = true
      filter = [{
        key: 'incomeDefault',
        operator: 'EQUALS',
        value: true,
        logicalOperation: 'AND'
      }, {
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }]
    }

    // await getTransactionTypeList(objApis.transactionType.moduleApi, objApis.transactionType.uriApi, objQueryToSearch, filter, sortObj)
    transactionTypeList.value = await getDataList<DataListItem, ListItem>(objApis.value.transactionType.moduleApi, objApis.value.transactionType.uriApi, filter, objQueryToSearch, mapFunction, sortObj,)
    if (isDefault && transactionTypeList.value.length > 0) {
      item.value.transactionType = transactionTypeList.value[0]
      formReload.value += 1
    }
  }
  finally {
    if (isDefault) {
      loadingDefaultTransactionType.value = false
    }
  }
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

watch(() => props.visible, (newValue) => {
  onOffDialog.value = newValue
  emit('update:visible', newValue)
  if (newValue) {
    getTransactionTypeList('', true)
  }
})
// watch(() => item.value, async (newValue) => {
//   if (newValue) {
//     requireConfirmationToSave(newValue)
//   }
// })
watch(() => props.item, async (newValue) => {
  if (newValue) {
    // newValue.transactionType.status = 'ACTIVE'
    item.value = { ...newValue }
  }
})
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
        <template #field-amount="{ item: data, onUpdate }">
          <InputNumber
            v-if="!loadingSaveAll"
            v-model="data.amount"
            show-clear
            @update:model-value="($event: any) => {
              onUpdate('amount', $event)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-transactionType="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingDefaultTransactionType && !props.loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.transactionType"
            :suggestions="[...transactionTypeList]"
            @change="($event) => {
              onUpdate('transactionType', $event)
            }"
            @load="async($event) => {
              await getTransactionTypeList($event, false)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
        <template #field-date="{ item: data, onUpdate }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.date"
            date-format="yy-mm-dd"
            :max-date="new Date()"
            @update:model-value="($event) => {
              onUpdate('date', $event)
            }"
          />
        </template>
      </EditFormV2>
    </template>

    <template #footer>
      <Button v-tooltip.top="'Save'" label="Save" class="w-6rem p-button" icon="pi pi-save" :loading="props.loadingSaveAll" @click="saveSubmit($event)" />
      <Button v-tooltip.top="'Cancel'" label="Cancel" class=" w-6rem p-button-color color" icon="pi pi-times" @click="closeDialog" />

      <!-- <Button v-tooltip.top="'Cancel'" class="w-3rem p-button-danger p-button-outlined" icon="pi pi-trash" @click="closeDialog" /> -->
    </template>
  </Dialog>
</template>

<style lang="scss">
.custom-dialog .p-dialog-content {
  background-color: #ffffff;
}
.p-button-color{
  background-color:#f0f0f092;
  color:#6c757d;
  border: 1px solid #6c757d;
}
.custom-dialog .p-dialog-footer {
  background-color: #ffffff;
}
</style>

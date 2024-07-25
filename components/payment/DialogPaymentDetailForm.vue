<script setup lang="ts">
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
    type: String as PropType<'new-detail' | 'deposit-transfer' | 'split-deposit'>,
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
  const filter: FilterCriteria[] = [
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
  ]

  await getTransactionTypeList(objApis.value.transactionType.moduleApi, objApis.value.transactionType.uriApi, {
    query: '',
    keys: ['name', 'code'],
  }, filter)
  item.value.transactionType = transactionTypeList.value.length > 0 ? transactionTypeList.value[0] : null
  item.value.amount = props.selectedPayment.paymentBalance || 0
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
        <template #field-transactionType="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!props.loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.transactionType"
            :suggestions="[...transactionTypeList]"
            @change="($event) => {
              onUpdate('transactionType', $event)
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
                  // {
                  //   key: 'deposit',
                  //   logicalOperation: 'AND',
                  //   operator: 'EQUALS',
                  //   value: true,
                  // },
                  {
                    key: 'defaults',
                    logicalOperation: 'OR',
                    operator: 'EQUALS',
                    value: true,
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
      <Button v-tooltip.top="'Applay Payment'" :disabled="false" link class="w-auto ml-1 sticky">
        <Button class="ml-1 w-3rem p-button-primary" icon="pi pi-cog" />
        <span class="ml-2 font-bold">
          Applay Payment
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

<script setup lang="ts">
import type { FieldDefinitionType } from '../form/EditFormV2'

const props = defineProps({
  visible: { type: Boolean, default: false },
  item: { type: Object, required: true },
  fields: { type: Array<FieldDefinitionType>, required: true },
  loadingSaveAll: { type: Boolean, default: false },
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
}

interface DataListItem {
  id: string
  name: string
  code: string
  status: string
}

interface ListItem {
  id: string
  name: string
  status: boolean | string
}
function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status
  }
}

async function getTransactionTypeList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }) {
  transactionTypeList.value = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, [], queryObj, mapFunction)
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
})
watch(() => item.value, async (newValue) => {
  if (newValue) {
    requireConfirmationToSave(newValue)
  }
})
watch(() => props.item, async (newValue) => {
  if (newValue) {
    newValue.transactionType.status = 'ACTIVE'
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
      mask: {
        style: 'backdrop-filter: blur(2px)',
      },
    }"
  >
    <template #header>
      <div class="flex justify-content-between align-items-center w-full">
        <h5 class="m-0 py-2">
          Payment Details
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
        :loading-save="loadingSaveAll"
        @cancel="closeDialog"
        @force-save="forceSave = $event"
        @submit="handleSaveForm($event)"
      >
        <template #field-transactionType="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
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
              await getTransactionTypeList(objApis.transactionType.moduleApi, objApis.transactionType.uriApi, objQueryToSearch)
            }"
          />
          <Skeleton v-else height="2rem" class="mb-2" />
        </template>
      </EditFormV2>
    </template>

    <template #footer>
      <Button v-tooltip.top="'Save'" class="w-3rem p-button" icon="pi pi-save" @click="saveSubmit($event)" />
      <Button v-tooltip.top="'Cancel'" class="w-3rem p-button-danger p-button-outlined" icon="pi pi-trash" @click="closeDialog" />
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

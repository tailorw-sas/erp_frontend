<script setup lang="ts">
import { ref, watch } from 'vue'
import { usePrimeVue } from 'primevue/config'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import { z } from 'zod'
import { GenericService } from '~/services/generic-services'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'

const props = defineProps({
  openDialog: {
    type: Boolean,
    required: true
  },
  transactionId: {
    type: String,
    required: true
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
const idItem = ref('')
const confApi = reactive({
  moduleApi: 'creditcard',
  uriApi: 'transactions',
})
const toast = useToast()

const fields: Array<FieldDefinitionType> = [
  {
    field: 'guestName',
    header: 'Guest Name',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The guest name field is required')
  },
  {
    field: 'email',
    header: 'Email',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The email field is required').email('Invalid email')
  },
]

const item = ref<GenericObject>({
  guestName: '',
  email: '',
})

const itemTemp = ref<GenericObject>({
  guestName: '',
  email: '',
})

function onClose(isCancel: boolean) {
  dialogVisible.value = false
  emits('onCloseDialog', isCancel)
}

function clearForm() {
  item.value = { ...itemTemp.value }
  formReload.value++
}

async function requireConfirmationToSave(item: any) {
  if (!useRuntimeConfig().public.showSaveConfirm) {
    await save(item)
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

async function save(newItem: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...newItem }
  try {
    delete payload.event
    // Actualizar datos de la transaccion si cambian
    if (item.value.guestName !== payload.guestName || item.value.email !== payload.email) {
      await GenericService.update(confApi.moduleApi, confApi.uriApi, idItem.value, payload)
    }
    await GenericService.create('creditcard', 'transactions/resend-payment-link', { id: props.transactionId })
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    onClose(false)
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    loadingSaveAll.value = false
  }
}

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)
      if (response) {
        item.value.id = String(response.id)
        item.value.guestName = response.guestName
        item.value.email = response.email
      }
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Item could not be loaded', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}

function saveSubmit(event: Event) {
  forceSave.value = true
  submitEvent = event
}

watch(() => item.value, async (newValue) => {
  if (newValue && newValue.merchant && dialogVisible.value) {
    requireConfirmationToSave(newValue)
  }
})

watch(() => props.openDialog, async (newValue) => {
  dialogVisible.value = newValue
  if (newValue) {
    clearForm()
    getItemById(props.transactionId)
    // getHotelList('')
  }
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible"
    modal
    header="ReSend Link"
    :style="{ width: '40rem' }"
    :breakpoints="{ '1199px': '75vw', '575px': '90vw' }"
    :pt="{
      root: {
        class: 'custom-dialog',
      },
      header: {
        style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
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
        @submit="requireConfirmationToSave($event)"
      />
    </div>
    <template #footer>
      <div class="flex justify-content-end mr-4">
        <Button v-tooltip.top="'Save'" label="Save" icon="pi pi-save" :loading="loadingSaveAll" class="mr-2" @click="saveSubmit($event)" />
        <Button v-tooltip.top="'Cancel'" label="Cancel" severity="secondary" icon="pi pi-times" @click="onClose(true)" />
      </div>
    </template>
  </Dialog>
</template>

<style scoped>
</style>

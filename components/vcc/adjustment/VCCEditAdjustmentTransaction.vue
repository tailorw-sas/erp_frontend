<script setup lang="ts">
import { ref, watch } from 'vue'
import { usePrimeVue } from 'primevue/config'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import { z } from 'zod'
import { GenericService } from '~/services/generic-services'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import { validateEntityStatus } from '~/utils/schemaValidations'
import type { GenericObject } from '~/types'

const props = defineProps({
  openDialog: {
    type: Boolean,
    required: true
  },
  transactionId: {
    type: String,
    required: true
  },
  transaction: {
    type: Object,
    required: false
  }
})
const emits = defineEmits<{
  (e: 'onCloseDialog', value: boolean): void
  (e: 'onSaveLocal', value: any): void
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
    field: 'id',
    header: 'Id',
    dataType: 'text',
    class: 'field col-12 md:col-6 required',
    validation: z.string().trim().min(1, 'The id field is required'),
    disabled: true,
  },
  {
    field: 'agency',
    header: 'Agency',
    dataType: 'select',
    class: 'field col-12 md:col-6 required',
    validation: validateEntityStatus('agency'),
  },
  {
    field: 'netAmount',
    header: 'Amount',
    dataType: 'number',
    class: 'field col-12 md:col-6 required',
    minFractionDigits: 2,
    maxFractionDigits: 4,
    validation: z.number({
      invalid_type_error: 'The amount field must be a number',
      required_error: 'The amount field is required',
    })
      .refine(val => Number.parseFloat(String(val)) > 0, {
        message: 'The amount must be greater than zero',
      })
  },
  {
    field: 'reservationNumber',
    header: 'Reservation Number',
    dataType: 'text',
    class: 'field col-12 md:col-6',
    // validation: z.string().trim().min(1, 'The reservation number field is required'),
  },
  {
    field: 'referenceNumber',
    header: 'Reference Number',
    dataType: 'text',
    class: 'field col-12 md:col-6 required',
    validation: z.string().trim().min(1, 'The reference number field is required')
  },
]

const item = ref<GenericObject>({
  id: '',
  netAmount: 0,
  agency: null,
  reservationNumber: '',
  referenceNumber: '',
})

const itemTemp = ref<GenericObject>({
  id: '',
  netAmount: 0,
  agency: null,
  reservationNumber: '',
  referenceNumber: '',
})

const AgencyList = ref<any[]>([])

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

async function saveItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  try {
    payload.agency = typeof payload.agency === 'object' ? payload.agency.id : payload.agency
    payload.amount = payload.netAmount
    delete payload.event
    delete payload.netAmount
    const response: any = await GenericService.update(confApi.moduleApi, confApi.uriApi, idItem.value, payload)
    toast.add({ severity: 'info', summary: 'Confirmed', detail: `The transaction details id ${response.id} was updated`, life: 10000 })
    item.id = response.id
    onClose(false)
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    loadingSaveAll.value = false
  }
}

function saveItemLocal(item: { [key: string]: any }) {
  delete item.event
  emits('onSaveLocal', item)
  onClose(true)
}

async function save(item: { [key: string]: any }) {
  if (props.transaction) { // Si es local
    saveItemLocal(item)
  }
  else {
    saveItem(item)
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
        const objAgency = {
          id: response.agency.id,
          name: `${response.agency.code} ${response.agency.name ? `- ${response.agency.name}` : ''}`,
          status: response.agency.status || 'ACTIVE'
        }
        item.value.agency = objAgency
        AgencyList.value = [objAgency]
        item.value.reservationNumber = response.reservationNumber
        item.value.referenceNumber = response.referenceNumber
        item.value.netAmount = response.netAmount
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

async function getAgencyList(query: string) {
  try {
    const payload = {
      filter: [{
        key: 'code',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'OR'
      }, {
        key: 'name',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'OR'
      }, {
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }],
      query: '',
      sortBy: 'createdAt',
      sortType: 'ASC',
      pageSize: 20,
      page: 0,
    }
    const response = await GenericService.search('settings', 'manage-agency', payload)
    const { data: dataList } = response
    AgencyList.value = []

    for (const iterator of dataList) {
      AgencyList.value = [...AgencyList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading agency list:', error)
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
    if (props.transaction) {
      item.value = props.transaction
      fields[0].hidden = true
    }
    else {
      getItemById(props.transactionId)
    }
  }
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible"
    modal
    header="Edit Adjustment Transaction"
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
      >
        <template #field-agency="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.agency"
            :suggestions="AgencyList"
            @change="($event) => {
              onUpdate('agency', $event)
            }"
            @load="($event) => getAgencyList($event)"
          />
          <Skeleton v-else height="2rem" class="" />
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

<style scoped>
</style>

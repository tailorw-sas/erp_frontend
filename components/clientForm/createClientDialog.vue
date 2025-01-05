<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import { ENUM_STATUS } from '~/utils/Enums'
import type { IData } from '~/components/table/interfaces/IModelData'

const props = defineProps({
  message: {
    type: String,
    required: false,
    default: 'Are you sure you want to delete the selected record?'
  },
  openDialog: {
    type: Boolean,
    required: true
  },
  value: {
    type: String,
    required: true,
    default: ''
  }
})

const emits = defineEmits<{
  (e: 'submit', obj: any): void
  (e: 'close', value: boolean): void
}>()

const dialogVisible = ref(props.openDialog)

// VARIABLES -----------------------------------------------------------------------------------------
const toast = useToast()
const confirm = useConfirm()
const formReload = ref(0)
const resultObject = ref({})

const loadingSaveAll = ref(false)
const confApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-client',
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'code',
    header: 'Code',
    disabled: false,
    dataType: 'code',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'The code field is required').min(3, 'Minimum 3 characters').max(5, 'Maximum 5 characters').regex(/^[a-z]+$/i, 'Only text characters allowed')
  },
  {
    field: 'name',
    header: 'Name',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'The name field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12',
    headerClass: 'mb-1'
  },
  {
    field: 'status',
    header: 'Active',
    dataType: 'check',
    class: 'field col-12 required mb-3 mt-3',
  },
]

const item = ref<GenericObject>({
  name: '',
  code: '',
  description: '',
  status: true
})

function closeDialog() {
  emits('close', false)
}

// -------------------------------------------------------------------------------------------------------

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    payload.status = statusToString(payload.status)
    const response: any = await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
    // payload.id = response.data.data.id

    resultObject.value = { ...payload, id: response.id }
  }
}

async function saveItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  let successOperation = true

  try {
    await createItem(item)
  }
  catch (error: any) {
    successOperation = false
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }

  loadingSaveAll.value = false
  if (successOperation) {
    emits('submit', resultObject.value)
    dialogVisible.value = false
  }
}

function requireConfirmationToSave(item: any) {
  const { event } = item
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      saveItem(item)
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible"
    modal
    class="mx-3 sm:mx-0 sm:w-full md:w-3"
    content-class="border-round-bottom border-top-1 surface-border"
    @hide="closeDialog"
  >
    <template #header>
      <div class="flex justify-content-between">
        <h5 class="m-0">
          Manage Client
        </h5>
      </div>
    </template>

    <div class="grid p-fluid mt-3">
      <div class="col-12 order-1 md:order-0">
        <EditFormV2
          :key="formReload"
          :fields="fields"
          :item="item"
          :show-actions="true"
          :loading-save="loadingSaveAll"
          :hide-delete-button="true"
          @cancel="closeDialog"
          @submit="requireConfirmationToSave($event)"
        />
      </div>
    </div>
  </Dialog>
</template>

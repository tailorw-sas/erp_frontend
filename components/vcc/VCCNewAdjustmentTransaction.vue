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
  isLocal: {
    type: Boolean,
    required: true,
    default: false
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
const loadingDefaultAgency = ref(false)
const loadingDefaultCategory = ref(false)
const loadingDefaultSubCategory = ref(false)
const confApi = reactive({
  moduleApi: 'creditcard',
  uriApi: 'transactions/adjustment',
})
const toast = useToast()

const fields: Array<FieldDefinitionType> = [
  {
    field: 'agency',
    header: 'Agency',
    dataType: 'select',
    class: 'field col-12 required',
    validation: validateEntityStatus('agency'),
  },
  {
    field: 'transactionCategory',
    header: 'Transaction Category',
    dataType: 'select',
    class: 'field col-12 required',
    validation: validateEntityStatus('transaction category'),
  },
  {
    field: 'transactionSubCategory',
    header: 'Transaction Subcategory',
    dataType: 'select',
    class: 'field col-12 required',
    validation: validateEntityStatus('transaction subcategory'),
  },
  {
    field: 'amount',
    header: 'Amount',
    dataType: 'number',
    class: 'field col-12 required',
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
    class: 'field col-12',
    // validation: z.string().trim().refine(value => value === '' || /^([IG]) \d+ \d+$/i.test(value), {
    //   message: 'The reservation number field has an invalid format. Examples of valid formats are I 3432 15 , G 1134 44'
    // })
  },
  {
    field: 'referenceNumber',
    header: 'Reference Number',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The reference number field is required').regex(/^\d+$/, 'Only numeric characters allowed')
    // validation: z.string().trim().refine(value => value === '' || /^\d+$/.test(value), {
    //   message: 'Only numeric characters allowed'
    // })
  },
]

const item = ref<GenericObject>({
  agency: null,
  transactionCategory: null,
  subCategory: null,
  amount: 0,
  reservationNumber: '',
  referenceNumber: '',
})

const itemTemp = ref<GenericObject>({
  agency: null,
  transactionCategory: null,
  subCategory: null,
  amount: 0,
  reservationNumber: '',
  referenceNumber: '',
})

const AgencyList = ref<any[]>([])
const CategoryList = ref<any[]>([])
const SubCategoryList = ref<any[]>([])

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
  formReload.value++
}

function requireConfirmationToSave(item: any) {
  if (!useRuntimeConfig().public.showSaveConfirm) {
    if (props.isLocal) {
      saveLocal(item)
    }
    else {
      save(item)
    }
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
        if (props.isLocal) {
          saveLocal(item)
        }
        else {
          await save(item)
        }
      },
      reject: () => {}
    })
  }
}

async function save(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  try {
    payload.transactionCategory = typeof payload.transactionCategory === 'object' ? payload.transactionCategory.id : payload.transactionCategory
    payload.transactionSubCategory = typeof payload.transactionSubCategory === 'object' ? payload.transactionSubCategory.id : payload.transactionSubCategory
    payload.agency = typeof payload.agency === 'object' ? payload.agency.id : payload.agency
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

function saveLocal(item: { [key: string]: any }) {
  emits('onSaveLocal', item)
  onClose(false)
}

async function getCategoryList(query: string, isDefault: boolean = false) {
  try {
    if (isDefault) {
      loadingDefaultCategory.value = true
    }
    const payload = {
      filter: isDefault
        ? [{
            key: 'isDefault',
            operator: 'EQUALS',
            value: true,
            logicalOperation: 'AND'
          }, {
            key: 'subcategory',
            operator: 'LIKE',
            value: false,
            logicalOperation: 'AND'
          }, {
            key: 'status',
            operator: 'EQUALS',
            value: 'ACTIVE',
            logicalOperation: 'AND'
          }]
        : [{
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
            key: 'subcategory',
            operator: 'LIKE',
            value: false,
            logicalOperation: 'AND'
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
    const response = await GenericService.search('settings', 'manage-vcc-transaction-type', payload)
    const { data: dataList } = response
    CategoryList.value = []

    for (const iterator of dataList) {
      CategoryList.value = [...CategoryList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
    if (isDefault && CategoryList.value.length > 0) {
      item.value.transactionCategory = CategoryList.value[0]
      formReload.value += 1
    }
  }
  catch (error) {
    console.error('Error loading category list:', error)
  }
  finally {
    if (isDefault) {
      loadingDefaultCategory.value = false
    }
  }
}

async function getSubCategoryList(query: string, isDefault: boolean = false) {
  try {
    if (isDefault) {
      loadingDefaultSubCategory.value = true
    }
    const payload = {
      filter: isDefault
        ? [{
            key: 'isDefault',
            operator: 'EQUALS',
            value: true,
            logicalOperation: 'AND'
          }, {
            key: 'subcategory',
            operator: 'LIKE',
            value: true,
            logicalOperation: 'AND'
          }, {
            key: 'status',
            operator: 'EQUALS',
            value: 'ACTIVE',
            logicalOperation: 'AND'
          }]
        : [{
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
            key: 'subcategory',
            operator: 'LIKE',
            value: true,
            logicalOperation: 'AND'
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
    const response = await GenericService.search('settings', 'manage-vcc-transaction-type', payload)
    const { data: dataList } = response
    SubCategoryList.value = []

    for (const iterator of dataList) {
      SubCategoryList.value = [...SubCategoryList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
    if (isDefault && SubCategoryList.value.length > 0) {
      item.value.transactionSubCategory = SubCategoryList.value[0]
      formReload.value += 1
    }
  }
  catch (error) {
    console.error('Error loading subcategory list:', error)
  }
  finally {
    if (isDefault) {
      loadingDefaultSubCategory.value = false
    }
  }
}

async function getAgencyList(query: string, isDefault: boolean = false) {
  if (isDefault) {
    loadingDefaultAgency.value = true
  }
  try {
    const payload = {
      filter: isDefault
        ? [{
            key: 'isDefault',
            operator: 'EQUALS',
            value: true,
            logicalOperation: 'AND'
          }, {
            key: 'status',
            operator: 'EQUALS',
            value: 'ACTIVE',
            logicalOperation: 'AND'
          }]
        : [{
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
    if (isDefault && AgencyList.value.length > 0) {
      item.value.agency = AgencyList.value[0]
      formReload.value += 1
    }
  }
  catch (error) {
    console.error('Error loading agency list:', error)
  }
  finally {
    if (isDefault) {
      loadingDefaultAgency.value = false
    }
  }
}

function saveSubmit(event: Event) {
  forceSave.value = true
  submitEvent = event
}

watch(() => item.value, async (newValue) => {
  if (newValue && newValue.agency && dialogVisible.value) {
    requireConfirmationToSave(newValue)
  }
})

watch(() => props.openDialog, (newValue) => {
  dialogVisible.value = newValue
  if (newValue) {
    clearForm()
    getAgencyList('', true)
    getCategoryList('', true)
    getSubCategoryList('', true)
  }
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible"
    modal
    header="New Adjustment Transaction"
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
        @submit="getObjectValues($event)"
      >
        <template #field-transactionCategory="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingDefaultCategory && !loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.transactionCategory"
            :suggestions="CategoryList"
            @change="($event) => {
              onUpdate('transactionCategory', $event)
            }"
            @load="($event) => getCategoryList($event)"
          />
          <Skeleton v-else height="2rem" class="" />
        </template>
        <template #field-transactionSubCategory="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingDefaultSubCategory && !loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.transactionSubCategory"
            :suggestions="SubCategoryList"
            @change="($event) => {
              onUpdate('transactionSubCategory', $event)
            }"
            @load="($event) => getSubCategoryList($event)"
          />
          <Skeleton v-else height="2rem" class="" />
        </template>
        <template #field-agency="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingDefaultAgency && !loadingSaveAll"
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
        <Button v-tooltip.top="'Apply'" label="Apply" icon="pi pi-save" :loading="loadingSaveAll" class="mr-2" @click="saveSubmit($event)" />
        <Button v-tooltip.top="'Cancel'" label="Cancel" severity="secondary" icon="pi pi-times" @click="onClose(true)" />
      </div>
    </template>
  </Dialog>
</template>

<style scoped>
</style>

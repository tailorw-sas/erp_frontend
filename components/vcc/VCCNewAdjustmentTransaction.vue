<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
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
    field: 'category',
    header: 'Category',
    dataType: 'select',
    class: 'field col-12 required',
    validation: validateEntityStatus('category'),
  },
  {
    field: 'subcategory',
    header: 'Subcategory',
    dataType: 'select',
    class: 'field col-12 required',
    validation: validateEntityStatus('subcategory'),
  },
  {
    field: 'amount',
    header: 'Amount',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The amount field is required')
      .regex(/^\d+(\.\d+)?$/, 'Only numeric characters allowed')
      .refine(val => Number.parseFloat(val) > 0, {
        message: 'The amount must be greater than zero',
      })
  },
  {
    field: 'reservationNumber',
    header: 'Reservation Number',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The reservation number field is required').regex(/^([IG]) \d{1,8} \d{1,8}$/i, 'Invalid reservation number format')
  },
  {
    field: 'referenceNumber',
    header: 'Reference Number',
    dataType: 'text',
    class: 'field col-12 required',
    validation: z.string().trim().min(1, 'The reference number field is required').regex(/^\d+$/, 'Only numeric characters allowed')
  },
]

const item = ref<GenericObject>({
  agency: null,
  category: null,
  subCategory: null,
  amount: '0',
  reservationNumber: '',
  referenceNumber: '',
})

const itemTemp = ref<GenericObject>({
  agency: null,
  category: null,
  subCategory: null,
  amount: '0',
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
  try {
    payload.category = typeof payload.category === 'object' ? payload.category.id : payload.category
    payload.subCategory = typeof payload.subCategory === 'object' ? payload.subCategory.id : payload.subCategory
    payload.agency = typeof payload.agency === 'object' ? payload.agency.id : payload.agency
    const response: any = await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
    // TODO: El mensaje debe ser 'The transaction details id {id} was created'
    toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    loadingSaveAll.value = false
  }
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
            key: 'name',
            operator: 'LIKE',
            value: query,
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
      CategoryList.value = [...CategoryList.value, { id: iterator.id, name: iterator.name, status: iterator.status }]
    }
    if (isDefault && CategoryList.value.length > 0) {
      item.value.category = CategoryList.value[0]
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
            key: 'name',
            operator: 'LIKE',
            value: query,
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
      SubCategoryList.value = [...SubCategoryList.value, { id: iterator.id, name: iterator.name, status: iterator.status }]
    }
    if (isDefault && SubCategoryList.value.length > 0) {
      item.value.subcategory = SubCategoryList.value[0]
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

async function getAgencyList(query: string) {
  try {
    const payload = {
      filter: [{
        key: 'name',
        operator: 'LIKE',
        value: query,
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
    const response = await GenericService.search('settings', 'manage-agency', payload)
    const { data: dataList } = response
    AgencyList.value = []

    for (const iterator of dataList) {
      AgencyList.value = [...AgencyList.value, { id: iterator.id, name: iterator.name, status: iterator.status }]
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
  if (newValue && dialogVisible.value) {
    requireConfirmationToSave(newValue)
  }
})

watch(() => props.openDialog, (newValue) => {
  dialogVisible.value = newValue
  if (newValue) {
    getCategoryList('', true)
    getSubCategoryList('', true)
  }
})

onMounted(() => {
  clearForm()
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible"
    modal
    header="New Adjustment Transaction"
    class="w-8 md:w-6 lg:w-4 card p-0"
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
        <template #field-category="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingDefaultCategory"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.category"
            :suggestions="CategoryList"
            @change="($event) => {
              onUpdate('category', $event)
            }"
            @load="($event) => getCategoryList($event)"
          />
          <Skeleton v-else height="2rem" class="" />
        </template>
        <template #field-subcategory="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingDefaultSubCategory"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.subcategory"
            :suggestions="SubCategoryList"
            @change="($event) => {
              onUpdate('subcategory', $event)
            }"
            @load="($event) => getSubCategoryList($event)"
          />
          <Skeleton v-else height="2rem" class="" />
        </template>
        <template #field-agency="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
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

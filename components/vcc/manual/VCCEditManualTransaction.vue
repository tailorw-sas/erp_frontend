<script setup lang="ts">
import { ref, watch } from 'vue'
import { usePrimeVue } from 'primevue/config'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import { z } from 'zod'
import dayjs from 'dayjs'
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
  }
})
const emits = defineEmits<{
  (e: 'onCloseDialog', value: boolean): void
}>()

const $primevue = usePrimeVue()

defineExpose({
  $primevue
})
const { data: userData } = useAuth()
const formReload = ref(0)
const forceSave = ref(false)
let submitEvent = new Event('')
const confirm = useConfirm()
const dialogVisible = ref(props.openDialog)
const loadingSaveAll = ref(false)
const loadingDefaultLanguage = ref(false)
const idItem = ref('')
const confApi = reactive({
  moduleApi: 'creditcard',
  uriApi: 'transactions/manual',
})
const toast = useToast()

const fields: Array<FieldDefinitionType> = [
  {
    field: 'parentId',
    header: 'Parent Id',
    dataType: 'text',
    class: 'field col-12',
    disabled: true,
    hidden: true
  },
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
    field: 'language',
    header: 'Language',
    dataType: 'select',
    class: 'field col-12 md:col-6 required',
    validation: validateEntityStatus('language'),
  },
  {
    field: 'checkIn',
    header: 'Check In',
    dataType: 'date',
    class: 'field col-12 md:col-6 required',
    validation: z.date({
      required_error: 'The check in field is required',
      invalid_type_error: 'The check in field is required',
    })
  },
  {
    field: 'reservationNumber',
    header: 'Reservation Number',
    dataType: 'text',
    class: 'field col-12 md:col-6 required',
    tabIndex: 0,
    validation: z.string().trim().min(1, 'The reservation number field is required'),
    // .regex(/^([IG]) \d+ \d+$/i, 'The reservation number field has an invalid format. Examples of valid formats are I 3432 15 , G 1134 44')
  },
  {
    field: 'referenceNumber',
    header: 'Reference Number',
    dataType: 'text',
    class: 'field col-12 md:col-6 required',
    tabIndex: 0,
    validation: z.string().trim().min(1, 'The reference number field is required')
  },
  {
    field: 'hotelContactEmail',
    header: 'Hotel Email Contact',
    dataType: 'text',
    class: 'field col-12',
    tabIndex: 0,
    validation: z.array(
      z.string().trim().email('Invalid email') // Valida que cada elemento sea un email
    ).optional()
  }
]

const item = ref<GenericObject>({
  id: '',
  parentId: '',
  agency: null,
  language: null,
  checkIn: '',
  reservationNumber: '',
  referenceNumber: '',
  hotelContactEmail: []
})

const itemTemp = ref<GenericObject>({
  id: '',
  parentId: '',
  agency: null,
  language: null,
  checkIn: '',
  reservationNumber: '',
  referenceNumber: '',
  hotelContactEmail: []
})

const AgencyList = ref<any[]>([])
const LanguageList = ref<any[]>([])

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

async function save(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  try {
    payload.checkIn = payload.checkIn ? `${dayjs(payload.checkIn).format('YYYY-MM-DD')}T00:00:00` : ''
    payload.agency = typeof payload.agency === 'object' ? payload.agency.id : payload.agency
    payload.language = typeof payload.language === 'object' ? payload.language.id : payload.language
    payload.employee = userData?.value?.user?.name
    payload.employeeId = userData?.value?.user?.userId
    payload.hotelContactEmail = Array.isArray(item.hotelContactEmail) ? item.hotelContactEmail.join(';') : item.hotelContactEmail
    delete payload.event
    delete payload.parentId
    const response: any = await GenericService.update(confApi.moduleApi, 'transactions', idItem.value, payload)
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

async function getItemById(id: string) {
  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(confApi.moduleApi, 'transactions', id)
      if (response) {
        item.value.id = String(response.id)
        if (response.parent?.id) {
          updateFieldProperty(fields, 'parentId', 'hidden', false)
          item.value.parentId = String(response.parent.id)
        }
        const objAgency = {
          id: response.agency.id,
          name: `${response.agency.code} ${response.agency.name ? `- ${response.agency.name}` : ''}`,
          status: response.agency.status || 'ACTIVE'
        }
        item.value.agency = objAgency
        AgencyList.value = [objAgency]
        const objLanguage = {
          id: response.language.id,
          name: `${response.language.code} ${response.language.name ? `- ${response.language.name}` : ''}`,
          status: response.language.status || 'ACTIVE'
        }
        item.value.language = objLanguage
        LanguageList.value = [objLanguage]
        item.value.merchant = response.merchant
        const viewDate2 = dayjs(response.checkIn, 'YYYY-MM-DD')
        item.value.checkIn = viewDate2.toDate()
        item.value.reservationNumber = response.reservationNumber
        item.value.referenceNumber = response.referenceNumber
        item.value.hotelContactEmail = response.hotelContactEmail.split(';') // Separa el string por el delimitador `;`
          .map((email: any) => email.trim()) // Elimina espacios en blanco adicionales
          .filter((email: any) => email !== '')
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

async function getLanguageByMerchantList(query: string, isDefault: boolean = false) {
  try {
    if (!item.value.merchant) {
      return // No listar si no hay merchant seleccionado
    }
    if (isDefault) {
      loadingDefaultLanguage.value = true
    }
    const payload = {
      filter: isDefault
        ? [{
            key: 'manageLanguage.defaults',
            operator: 'EQUALS',
            value: true,
            logicalOperation: 'AND'
          }, {
            key: 'manageLanguage.code',
            operator: 'LIKE',
            value: query,
            logicalOperation: 'OR'
          }, {
            key: 'manageLanguage.name',
            operator: 'LIKE',
            value: query,
            logicalOperation: 'OR'
          }, {
            key: 'manageLanguage.status',
            operator: 'EQUALS',
            value: 'ACTIVE',
            logicalOperation: 'AND'
          }, {
            key: 'manageMerchant.id',
            operator: 'EQUALS',
            value: item.value.merchant.id,
            logicalOperation: 'AND'
          }]
        : [
            {
              key: 'manageLanguage.name',
              operator: 'LIKE',
              value: query,
              logicalOperation: 'OR'
            },
            {
              key: 'manageLanguage.code',
              operator: 'LIKE',
              value: query,
              logicalOperation: 'OR'
            },
            {
              key: 'manageLanguage.status',
              operator: 'EQUALS',
              value: 'ACTIVE',
              logicalOperation: 'AND'
            },
            {
              key: 'manageMerchant.id',
              operator: 'EQUALS',
              value: item.value.merchant.id,
              logicalOperation: 'AND'
            }
          ],
      query: '',
      sortBy: 'createdAt',
      sortType: 'ASC',
      pageSize: 20,
      page: 0,
    }
    const response = await GenericService.create('creditcard', 'merchant-language-code/languages', payload)
    const { data: dataList } = response
    LanguageList.value = []

    for (const iterator of dataList) {
      LanguageList.value = [...LanguageList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status || 'ACTIVE' }]
    }

    if (isDefault && LanguageList.value.length > 0) {
      item.value.language = LanguageList.value[0]
      formReload.value += 1
    }
  }
  catch (error) {
    console.error('Error loading language list:', error)
  }
  finally {
    if (isDefault) {
      loadingDefaultLanguage.value = false
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
  updateFieldProperty(fields, 'parentId', 'hidden', true)
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
    header="Edit Manual Transaction"
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
        <template #field-language="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingDefaultLanguage && !loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.language"
            :suggestions="LanguageList"
            @change="($event) => {
              onUpdate('language', $event)
            }"
            @load="($event) => getLanguageByMerchantList($event)"
          />
          <Skeleton v-else height="2rem" class="" />
        </template>
        <template #field-checkIn="{ item: data, onUpdate }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.checkIn"
            date-format="yy-mm-dd"
            :max-date="new Date()"
            :manual-input="false"
            @update:model-value="($event) => {
              onUpdate('checkIn', $event)
            }"
          />
          <Skeleton v-else height="2rem" class="" />
        </template>
        <template #field-hotelContactEmail="{ item: data, onUpdate }">
          <Chips
            v-if="!loadingSaveAll"
            v-model="data.hotelContactEmail"
            separator=";"
            class="custom-chips"
            @update:model-value="($event) => {
              onUpdate('hotelContactEmail', $event)
              item.hotelContactEmail = $event
            }"
            @blur="($event) => {
              const input = ($event.target as HTMLInputElement)?.value || '';
              if (input && input.trim() !== '') {
                // Agregar el valor al modelo de chips
                const newChip = input.trim();
                if (!item.hotelContactEmail.includes(newChip)) {
                  item.hotelContactEmail.push(newChip); // Añade el nuevo chip
                  onUpdate('hotelContactEmail', item.hotelContactEmail)
                }

                // Limpiar el input manualmente
                $event.target.value = '';
              }
            }"
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

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
const loadingDefaultMerchant = ref(false)
const loadingDefaultLanguage = ref(false)
const confApi = reactive({
  moduleApi: 'creditcard',
  uriApi: 'transactions/manual',
})
const toast = useToast()

const fields: Array<FieldDefinitionType> = [
  {
    field: 'merchant',
    header: 'Merchant',
    dataType: 'select',
    class: 'field col-12 md:col-6 required',
    validation: validateEntityStatus('merchant'),
  },
  {
    field: 'methodType',
    header: 'Method Type',
    dataType: 'select',
    class: 'field col-12 md:col-6 required',
    validation: z.object({
      id: z.string(),
      name: z.string(),
    }).nullable()
      .refine(value => value && value.id && value.name, { message: `The method type field is required` })
  },
  {
    field: 'hotel',
    header: 'Hotel',
    dataType: 'select',
    class: 'field col-12 md:col-6 required',
    validation: z.object({
      id: z.string(),
      name: z.string(),
    }).nullable()
      .refine(value => value && value.id && value.name, { message: `The hotel field is required` }),
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
    field: 'amount',
    header: 'Amount',
    dataType: 'text',
    class: 'field col-12 md:col-6 required',
    validation: z.string().trim().min(1, 'The amount field is required')
      .regex(/^\d+(\.\d+)?$/, 'Only numeric characters allowed')
      .refine(val => Number.parseFloat(val) > 0, {
        message: 'The amount must be greater than zero',
      })
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
    validation: z.string().trim().min(1, 'The reservation number field is required'),
    // .regex(/^([IG]) \d+ \d+$/i, 'The reservation number field has an invalid format. Examples of valid formats are I 3432 15 , G 1134 44')
  },
  {
    field: 'referenceNumber',
    header: 'Reference Number',
    dataType: 'text',
    class: 'field col-12 md:col-6',
    validation: z.string().trim().refine((value) => {
      if (value === '') {
        return true
      }
      return /^\d+$/.test(value)
    }, {
      message: 'Only numeric characters allowed'
    })
  },
  {
    field: 'hotelContactEmail',
    header: 'Hotel Email Contact',
    dataType: 'text',
    class: 'field col-12 md:col-6',
    validation: z.string().trim().email('Invalid email').or(z.string().length(0))
  },
  {
    field: 'guestName',
    header: 'Guest Name',
    dataType: 'text',
    class: 'field col-12 md:col-6',
  },
  {
    field: 'email',
    header: 'Email',
    dataType: 'text',
    class: 'field col-12 md:col-6',
    validation: z.string().trim().email('Invalid email').or(z.string().length(0))
  },
]

const item = ref<GenericObject>({
  merchant: null,
  methodType: null,
  hotel: null,
  agency: null,
  language: null,
  amount: '0',
  checkIn: '',
  reservationNumber: '',
  referenceNumber: '',
  hotelContactEmail: '',
  guestName: '',
  email: '',
})

const itemTemp = ref<GenericObject>({
  merchant: null,
  methodType: null,
  hotel: null,
  agency: null,
  language: null,
  amount: '0',
  checkIn: new Date(),
  reservationNumber: '',
  referenceNumber: '',
  hotelContactEmail: '',
  guestName: '',
  email: '',
})

const MerchantList = ref<any[]>([])
const HotelList = ref<any[]>([])
const AgencyList = ref<any[]>([])
const LanguageList = ref<any[]>([])

const ENUM_METHOD_TYPE = [
  { id: 'LINK', name: 'Link' },
  { id: 'POST', name: 'Post' },
]

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

async function handleMerchantRedirect(item: any) {
  const data = {
    orderNumber: '002',
    amount: `${item.amount}00`,
    approvedUrl: 'https://freebieflux.com/freebies-by-category/figma-app-designs',
    declinedUrl: 'http://localhost/payment.php',
    cancelUrl: 'http://localhost/payment.php',
    merchantId: '39038540035',
    merchantName: item.merchant.name ?? '',
    merchantType: 'ECommerce',
    currencyCode: '$',
    itbis: '000'
  }
  const response = await fetch('/api/redirect-to-merchant', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  })

  if (response.ok) {
    const newTab = window.open('', '_blank')
    newTab.document.write(await response.text())
    newTab.document.close()
  }
  else {
    console.error('Error al redirigir al merchant')
  }
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
    payload.merchant = typeof payload.merchant === 'object' ? payload.merchant.id : payload.merchant
    payload.amount = Number(payload.amount)
    payload.checkIn = payload.checkIn ? dayjs(payload.checkIn).format('YYYY-MM-DD') : ''
    payload.hotel = typeof payload.hotel === 'object' ? payload.hotel.id : payload.hotel
    payload.agency = typeof payload.agency === 'object' ? payload.agency.id : payload.agency
    payload.language = typeof payload.language === 'object' ? payload.language.id : payload.language
    payload.methodType = typeof payload.methodType === 'object' ? payload.methodType.id : payload.methodType
    delete payload.event
    const response: any = await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
    toast.add({ severity: 'info', summary: 'Confirmed', detail: `The transaction details id ${response.id} was created`, life: 10000 })
    handleMerchantRedirect(item)
    onClose(false)
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    loadingSaveAll.value = false
  }
}

async function getMerchantList(query: string, isDefault: boolean = false) {
  try {
    if (isDefault) {
      loadingDefaultMerchant.value = true
    }
    const payload = {
      filter: isDefault
        ? [{
            key: 'defaultm',
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
            key: 'description',
            operator: 'LIKE',
            value: query,
            logicalOperation: 'OR'
          }, {
            key: 'code',
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
      pageSize: 20,
      page: 0,
    }
    const response = await GenericService.search('settings', 'manage-merchant', payload)
    const { data: dataList } = response
    MerchantList.value = []

    for (const iterator of dataList) {
      MerchantList.value = [...MerchantList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.description}`, status: iterator.status }]
    }
    if (isDefault && MerchantList.value.length > 0) {
      item.value.merchant = MerchantList.value[0]
      formReload.value += 1
    }
  }
  catch (error) {
    console.error('Error loading merchant list:', error)
  }
  finally {
    if (isDefault) {
      loadingDefaultMerchant.value = false
    }
  }
}

async function getHotelList(query: string) {
  if (!item.value.merchant) {
    return
  }
  try {
    const payload = {
      filter: [{
        key: 'manageHotel.name',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'OR'
      }, {
        key: 'manageHotel.code',
        operator: 'LIKE',
        value: query,
        logicalOperation: 'OR'
      }, {
        key: 'manageHotel.status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }, {
        key: 'manageMerchant.id',
        operator: 'EQUALS',
        value: item.value.merchant.id,
        logicalOperation: 'AND'
      }],
      query: '',
      sortBy: 'createdAt',
      sortType: 'ASC',
      pageSize: 20,
      page: 0,
    }
    const response: any = await GenericService.create('creditcard', 'nomenclators/hotels-by-merchant', payload)
    const { data: dataList } = response
    HotelList.value = []

    for (const iterator of dataList) {
      HotelList.value = [...HotelList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
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

async function getLanguageList(query: string, isDefault: boolean = false) {
  try {
    if (isDefault) {
      loadingDefaultLanguage.value = true
    }
    const payload = {
      filter: isDefault
        ? [{
            key: 'defaults',
            operator: 'EQUALS',
            value: true,
            logicalOperation: 'AND'
          }, {
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
          }]
        : [
            {
              key: 'name',
              operator: 'LIKE',
              value: query,
              logicalOperation: 'OR'
            },
            {
              key: 'code',
              operator: 'LIKE',
              value: query,
              logicalOperation: 'OR'
            },
            {
              key: 'status',
              operator: 'EQUALS',
              value: 'ACTIVE',
              logicalOperation: 'AND'
            }
          ],
      query: '',
      sortBy: 'createdAt',
      sortType: 'ASC',
      pageSize: 20,
      page: 0,
    }
    const response = await GenericService.search('settings', 'manage-language', payload)
    const { data: dataList } = response
    LanguageList.value = []

    for (const iterator of dataList) {
      LanguageList.value = [...LanguageList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
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

function handleMethodTypeChange(value: any) {
  if (value === 'LINK') {
    updateFieldProperty(fields, 'email', 'validation', z.string().trim()
      .min(1, 'The email field is required').email('Invalid email'))
    updateFieldProperty(fields, 'guestName', 'validation', z.string().trim()
      .min(1, 'The guest name field is required'))
    // required
    updateFieldProperty(fields, 'email', 'class', 'field col-12 md:col-6 required')
    updateFieldProperty(fields, 'guestName', 'class', 'field col-12 md:col-6 required')
  }
  else {
    updateFieldProperty(fields, 'email', 'validation', z.string().trim()
      .email('Invalid email').or(z.string().length(0)))
    updateFieldProperty(fields, 'guestName', 'validation', z.string())
    updateFieldProperty(fields, 'email', 'class', 'field col-12 md:col-6')
    updateFieldProperty(fields, 'guestName', 'class', 'field col-12 md:col-6')
  }
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
    handleMethodTypeChange('')
    getLanguageList('', true)
    await getMerchantList('', true)
    // getHotelList('')
  }
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible"
    modal
    header="New Manual Transaction"
    class="w-10 lg:w-6 card p-0"
    content-class="border-round-bottom border-top-1 surface-border pb-0"
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
        <template #field-merchant="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingDefaultMerchant && !loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.merchant"
            :suggestions="MerchantList"
            @change="($event) => {
              onUpdate('merchant', $event)
              item.merchant = $event
            }"
            @load="($event) => getMerchantList($event)"
          />
          <Skeleton v-else height="2rem" class="" />
        </template>
        <template #field-methodType="{ item: data, onUpdate }">
          <Dropdown
            v-if="!loadingSaveAll"
            v-model="data.methodType"
            :options="[...ENUM_METHOD_TYPE]"
            option-label="name"
            return-object="false"
            class="align-items-center"
            show-clear
            @update:model-value="($event) => {
              onUpdate('methodType', $event)
              handleMethodTypeChange($event.id)
            }"
          />
          <Skeleton v-else height="2rem" class="" />
        </template>
        <template #field-hotel="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll"
            id="autocomplete"
            field="name"
            item-value="id"
            :model="data.hotel"
            :suggestions="HotelList"
            :disabled="!data.merchant"
            @change="($event) => {
              onUpdate('hotel', $event)
            }"
            @load="($event) => getHotelList($event)"
          />
          <Skeleton v-else height="2rem" class="" />
        </template>
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
            @load="($event) => getLanguageList($event)"
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

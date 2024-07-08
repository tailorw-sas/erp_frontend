<script setup lang="ts">
import { ref, watch } from 'vue'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import getUrlByImage from '~/composables/files'
import { ModulesService } from '~/services/modules-services'
import { GenericService } from '~/services/generic-services'
import type { PageState } from 'primevue/paginator'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType, Container } from '~/components/form/EditFormV2WithContainer'
import type { GenericObject } from '~/types'
import type { IData } from '~/components/table/interfaces/IModelData'
import dayjs from 'dayjs'

const toast = useToast()

const forceSave = ref(false)
const forceUpdate = ref(false)
const active = ref(0)

const route = useRoute()

//@ts-ignore
const selectedInvoice = <string>ref(route.params.id.toString())
const selectedBooking = ref<string>('')
const selectedRoomRate = ref<string>('')


const loadingSaveAll = ref(false)
const loadingDelete = ref(false)

const bookingDialogOpen = ref<boolean>(false)
const roomRateDialogOpen = ref<boolean>(false)
const adjustmentDialogOpen = ref<boolean>(false)



const errorInTab = ref({
  tabGeneralData: false,
  tabMedicalInfo: false,
  tabServices: false,
  tabPermissions: false
})

const invoiceAgency = ref<any>(null)
const invoiceHotel = ref<any>(null)



const fields: Array<Container> = [
  {
    childs: [
      {
        field: 'invoice_id',
        header: 'Id',
        dataType: 'text',
        class: 'w-full',
        disabled: true,
        containerFieldClass: 'ml-10'

      },
      {
        field: 'invoiceNumber',
        header: 'Invoice Number',
        dataType: 'text',
        class: 'w-full',
        disabled: true,
      },

    ],
    containerClass: 'flex flex-column justify-content-evenly w-full'
  },

  {
    childs: [

      {
        field: 'hotel',
        header: 'Hotel',
        dataType: 'select',
        class: 'w-full px-4 required',
        

      },
      {
        field: 'agency',
        header: 'Agency',
        dataType: 'select',
        class: 'w-full px-4 required',
        
      },

    ],

    containerClass: 'flex flex-column justify-content-evenly w-full '
  },
  {
    childs: [
      {
        field: 'invoiceDate',
        header: 'Invoice Date',
        dataType: 'date',
        class: 'w-full required',
        validation: z.date({ required_error: 'The Invoice Date field is required' }).max(dayjs().endOf('day').toDate(), 'The Invoice Date field cannot be greater than current date')
      },
      {
        field: 'isManual',
        header: 'Manual',
        dataType: 'check',
        class: 'w-full ',
        disabled: true
      },

    ],
    containerClass: 'flex flex-column justify-content-evenly w-full'
  },
  {
    childs: [
      {
        field: 'invoiceAmount',
        header: 'Invoice Amount',
        dataType: 'text',
        class: 'w-full px-4 required',
        disabled: true
      },
      {
        field: 'invoiceType',
        header: 'Type',
        dataType: 'select',
        class: 'w-full px-4 ',
        containerFieldClass: '',
        validation: validateEntityStatus('Invoice Type')
      },

    ],
    containerClass: 'flex flex-column justify-content-evenly w-full'
  },

]

const hotelList = ref<any[]>([])
const agencyList = ref<any[]>([])
const invoiceTypeList = ref<any[]>([])

const confhotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})


const confagencyListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})


const confinvoiceTypeListtApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-invoice-type',
})




// VARIABLES -----------------------------------------------------------------------------------------


//
const confirm = useConfirm()
const formReload = ref(0)

const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})



const item = ref<GenericObject>({
  invoice_id: '',
  invoiceNumber: '',
  invoiceDate: new Date(),
  isManual: true,
  invoiceAmount: '0.00',
  hotel: null,
  agency: null,
  invoiceType: null,
})

const itemTemp = ref<GenericObject>({
  invoice_id: '',
  invoiceNumber: '',
  invoiceDate: new Date(),
  isManual: true,
  invoiceAmount: '0.00',
  hotel: null,
  agency: null,
  invoiceType: null,
})



// -------------------------------------------------------------------------------------------------------

// -------------------------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'code', name: 'Code' },
  { id: 'name', name: 'Name' },
  { id: 'description', name: 'Description' },
]
// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})



// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------


function handleDialogOpen() {
  console.log(active);
  switch (active.value) {
    case 0:
      bookingDialogOpen.value = true
      break;

    case 1:
      roomRateDialogOpen.value = true
      break;

    case 2:
      adjustmentDialogOpen.value = true
      break;

    default:
      break;
  }

  console.log(bookingDialogOpen);
}

async function getHotelList() {
  try {
    const payload
      = {
        filter: [{
            key: 'status',
            operator: 'EQUALS',
            value: 'ACTIVE',
            logicalOperation: 'AND'
          }],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: 'DES'
      }

    hotelList.value = []
    const response = await GenericService.search(confhotelListApi.moduleApi, confhotelListApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function getAgencyList() {
  try {
    const payload
      = {
        filter: [{
            key: 'status',
            operator: 'EQUALS',
            value: 'ACTIVE',
            logicalOperation: 'AND'
          }],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: 'DES'
      }

    agencyList.value = []
    const response = await GenericService.search(confagencyListApi.moduleApi, confagencyListApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      agencyList.value = [...agencyList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading agency list:', error)
  }
}

async function getInvoiceTypeList() {
  try {
    const payload
      = {
        filter: [],
        query: '',
        pageSize: 200,
        page: 0,
        sortBy: 'createdAt',
        sortType: 'DES'
      }

    invoiceTypeList.value = []
    const response = await GenericService.search(confinvoiceTypeListtApi.moduleApi, confinvoiceTypeListtApi.uriApi, payload)
    const { data: dataList } = response
    for (const iterator of dataList) {
      invoiceTypeList.value = [...invoiceTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}


function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''

  formReload.value++
}

async function getItemById(id: string) {

  if (id) {
    idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(options.value.moduleApi, options.value.uriApi, id)

      if (response) {
        item.value.id = response.id
        item.value.invoice_id = response.invoice_id
        item.value.invoiceNumber = response.invoiceNumber
        item.value.invoiceDate = new Date(response.invoiceDate)
        item.value.isManual = response.isManual
        item.value.invoiceAmount = response.invoiceAmount
        item.value.hotel = response.hotel
        item.value.agency = response.agency
        item.value.invoiceType = response.invoiceType ? ENUM_INVOICE_TYPE.find((element => element.id === response?.invoiceType)) : ENUM_INVOICE_TYPE[0]


  

        await getInvoiceAgency(response.agency?.id)
        await getInvoiceHotel(response.hotel?.id)

      }

      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Invoice methods could not be loaded', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}


async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }

    payload.invoice_id = item.invoice_id
    payload.invoiceNumber = item.invoiceNumber
    payload.invoiceDate = item.invoiceDate
    payload.isManual = item.isManual
    payload.invoiceAmount = 0.00
    payload.hotel = item.hotel.id
    payload.invoiceType = item?.invoiceType?.id
    payload.agency = item.agency.id
    
    await GenericService.create(options.value.moduleApi, options.value.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = {  }
  payload.invoiceDate = item.invoiceDate
  payload.isManual = item.isManual
  payload.hotel = item.hotel.id
  payload.agency = item.agency.id
  
  await GenericService.update(options.value.moduleApi, options.value.uriApi, idItem.value || '', payload)
  navigateTo(
    '/invoice'
  )
}

async function deleteItem(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
    clearForm()
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Could not delete invoice', life: 3000 })
    loadingDelete.value = false
  }
  finally {
    loadingDelete.value = false
  }
}

async function saveItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  let successOperation = true
  if (idItem.value) {
    try {
      await updateItem(item)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
    idItem.value = ''
  }
  else {
    try {
      await createItem(item)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
    catch (error: any) {
      successOperation = false
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
  if (successOperation) {
    clearForm()
  }
}
const goToList = async () => await navigateTo('/invoice')

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
function requireConfirmationToDelete(event: any) {
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    acceptClass: 'p-button-danger',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: () => {
      deleteItem(idItem.value)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}


function toggleForceUpdate() {
  forceUpdate.value = !forceUpdate.value
}

function update() {
  forceUpdate.value = true

  setTimeout(() => {

    forceUpdate.value = false
  }, 100);




}

function openRoomRateDialog(booking?: any) {
  active.value = 1

  if (booking?.id) {

    selectedBooking.value = booking?.id
  }



  roomRateDialogOpen.value = true


}

function openAdjustmentDialog(roomRate?: any) {
  active.value = 2

  if (roomRate?.id) {
    selectedRoomRate.value = roomRate?.id
  }

  adjustmentDialogOpen.value = true
}


async function getInvoiceHotel(id) {
  try {
    const hotel = await GenericService.getById(confhotelListApi.moduleApi, confhotelListApi.uriApi, id)

    if (hotel) {
      invoiceHotel.value = { ...hotel }
    }
  }
  catch (err) {

  }
}
async function getInvoiceAgency(id) {
  try {
    const agency = await GenericService.getById(confagencyListApi.moduleApi, confagencyListApi.uriApi, id)

    if (agency) {
      invoiceAgency.value = { ...agency }
    }
  }
  catch (err) {

  }
}


watch(() => idItemToLoadFirstTime.value, async (newValue) => {
  if (!newValue) {
    clearForm()
  }
  else {
    await getItemById(newValue)
  }
})




onMounted(async () => {
  filterToSearch.value.criterial = ENUM_FILTER[0]
  //@ts-ignore
  await getItemById(route.params.id.toString())
  
  
})
</script>

<template>
  <div class="justify-content-center align-center ">
     <div class=" flex justify-content-start p-3 mb-4  align-items-center" style="background-color: #0F8BFD; height: 45px; border-radius: 10px;">
      <span style="font-weight: 700; color: white; font-size: large;">{{ OBJ_UPDATE_INVOICE_TITLE[String(item?.invoiceType)] || "Edit Invoice" }}</span>
    </div>

    <EditFormV2WithContainer :key="formReload" :fields-with-containers="fields" :item="item" :show-actions="true"
      :loading-save="loadingSaveAll" :loading-delete="loadingDelete" @cancel="clearForm"
      @delete="requireConfirmationToDelete($event)" container-class="flex flex-row justify-content-evenly card w-full">

      <template #field-hotel="{ item: data, onUpdate }">
        <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.hotel"
                :suggestions="hotelList"
                @change="($event) => {
                  onUpdate('hotel', $event)
                }"
                @load="($event) => getHotelList($event)"
                >
                <template #option="props">
      
                  <span>{{ props.item.code }} - {{ props.item.name }}</span>
      
      
                </template>
              </DebouncedAutoCompleteComponent>
      </template>
      <template #field-agency="{ item: data, onUpdate }">
        <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.agency"
                :suggestions="agencyList"
                @change="($event) => {
                  onUpdate('agency', $event)
                }"
                @load="($event) => getAgencyList($event)"
                >
                <template #option="props">
      
                  <span>{{ props.item.code }} - {{ props.item.name }}</span>
      
      
                </template>
              </DebouncedAutoCompleteComponent>
      </template>
     <template #field-invoiceType="{ item: data, onUpdate }">
        <Dropdown
          v-if="!loadingSaveAll"
          v-model="data.invoiceType"
          :options="[...ENUM_INVOICE_TYPE]"
          option-label="name"
          return-object="false"
          show-clear
          disabled
          @update:model-value="($event) => {
            onUpdate('invoiceType', $event)
          }"
        />
        <Skeleton v-else height="2rem" class="mb-2" />
      </template>

      <template #form-footer="props">
        <div class="px-4" style="width: 100%; height: 100%;">
          <div class="flex justify-content-end mb-3">
              
              <Button v-if="active === 0" v-tooltip.top="'Add Booking'" class="w-fit mx-1" icon="pi pi-plus"
                :loading="loadingSaveAll" @click="handleDialogOpen()" label="New" />
              <Button v-tooltip.top="'Update'" class="w-3rem mx-1" icon="pi pi-replay" :loading="loadingSaveAll"
                @click="update" />
             

            </div>
          <TabView v-model:activeIndex="active" class="tabview-custom">
            <TabPanel  >
              <template #header >
                <div class="flex align-items-center gap-2 p-2 " :style="`${active === 0 && 'background-color: white; color: #0F8BFD;'} border-radius: 5px 5px 0 0;  width: 130px`"  >
                  <i class="pi pi-calendar" style="font-size: 1.5rem" />
                  <span class="font-bold white-space-nowrap" >
                    Bookings
                    <i v-if="errorInTab.tabGeneralData" class="pi p-error pi-question-circle"
                      style="font-size: 1.2rem" />
                  </span>
                </div>
              </template>
              <BookingTab :is-dialog-open="bookingDialogOpen" :close-dialog="() => { bookingDialogOpen = false }"
                :open-dialog="handleDialogOpen" :open-room-rate-dialog="openRoomRateDialog" :force-update="forceUpdate"
                :toggle-force-update="() => { }" :selected-invoice="selectedInvoice" :is-creation-dialog="false" :show-totals="true" :invoice-obj="item" :invoice-agency="invoiceAgency" :invoice-hotel="invoiceHotel"></BookingTab>
              
            </TabPanel>
            <TabPanel>
              <template #header>
                <div class="flex align-items-center gap-2 p-2" :style="`${active === 1 && 'background-color: white; color: #0F8BFD;'} border-radius: 5px 5px 0 0;  width: 130px`">
                  <i class="pi pi-receipt" style="font-size: 1.5rem" />
                  <span class="font-bold white-space-nowrap">
                    Room Rates
                    <i v-if="errorInTab.tabGeneralData" class="pi p-error pi-question-circle"
                      style="font-size: 1.2rem" />
                  </span>
                </div>
              </template>
              <RoomRateTab :is-dialog-open="roomRateDialogOpen" :close-dialog="() => { roomRateDialogOpen = false }"
                :open-dialog="handleDialogOpen" :selected-booking="selectedBooking"
                :open-adjustment-dialog="openAdjustmentDialog" :selected-invoice="selectedInvoice"
                :force-update="forceUpdate" :is-creation-dialog="false" :toggle-force-update="() => { }" :show-totals="true">
              </RoomRateTab>
            </TabPanel>
            <TabPanel>
              <template #header>
                <div class="flex align-items-center gap-2 p-2" :style="`${active === 2 && 'background-color: white; color: #0F8BFD;'} border-radius: 5px 5px 0 0;  width: 130px`">
                  <i class="pi pi-sliders-v"  style="font-size: 1.5rem" />
                  <span class="font-bold white-space-nowrap">
                    Adjustments
                    <i v-if="errorInTab.tabGeneralData" class="pi p-error pi-question-circle"
                      style="font-size: 1.2rem" />
                  </span>
                </div>
              </template>
              <AdjustmentTab :is-dialog-open="adjustmentDialogOpen"
                :close-dialog="() => { adjustmentDialogOpen = false }" :open-dialog="handleDialogOpen"
                :selected-room-rate="selectedRoomRate" :is-creation-dialog="false" :selected-invoice="selectedInvoice"
                :force-update="forceUpdate" :toggle-force-update="() => { }" :show-totals="true">
              </AdjustmentTab>
            </TabPanel>

          </TabView>

          <div >
            <div class="flex justify-content-end">
              
              
              <Button severity="danger" v-tooltip.top="'Cancel'" class="w-fit mx-1" icon="pi pi-times" @click="goToList" label="Cancel" />
              <Button v-tooltip.top="'Save'" class="w-fit mx-1" icon="pi pi-save" :loading="loadingSaveAll" @click="() => {
      saveItem(props.item.fieldValues)
    }" label="Save"/>
            </div>
          </div>
        </div>
      </template>
    </EditFormV2WithContainer>



    
  </div>
</template>

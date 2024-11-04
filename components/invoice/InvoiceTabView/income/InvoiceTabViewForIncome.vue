<script setup lang="ts">
import { useToast } from 'primevue/usetoast'

import BookingTabIncome from '../../booking/income/bookingTabIncome.vue'
import RoomRateTabForIncome from '../../roomRate/income/roomRateTabForIncome.vue'
import AdjustmentTabForIncome from '../../adjustment/income/adjustmentTabForIncome.vue'
import { GenericService } from '~/services/generic-services'

import type { GenericObject } from '~/types'
import type { IData } from '~/components/table/interfaces/IModelData'

const props = defineProps({
  selectedInvoice: {
    type: String,
    default: '',
    required: true
  },
  forceUpdate: {
    type: Boolean,
    required: false
  },
  toggleForceUpdate: {
    type: Function as any,
    required: false,

  },

  closeDialog: {
    type: Function as any,
    required: false
  },
  openDialog: {
    type: Function as any,
    required: false
  },
  isDialogOpen: {
    type: Boolean,
    required: true
  },
  isCreationDialog: {
    type: Boolean,
    required: false,
    default: false,
  },
  listItems: {
    type: Array,
    required: false
  },
  addBooking: {
    type: Function as any,
    required: false
  },
  updateBooking: {
    type: Function as any,
    required: false
  },
  addRoomRate: {
    type: Function as any,
    required: false
  },
  updateRoomRate: {
    type: Function as any,
    required: false
  },
  addAdjustment: {
    type: Function as any,
    required: false
  },
  updateAdjustment: {
    type: Function as any,
    required: false
  },
  showTotals: {
    type: Boolean,
    required: false,
    default: false
  },
  isTabView: {
    type: Boolean,
    default: false
  },
  invoiceObj: {
    type: Object as any,
    required: false,
    default: () => {},
  },

  invoiceAgency: {
    type: Object,
    required: false,
    default: () => {},
  },
  invoiceHotel: {
    type: Object,
    required: false,
    default: () => {},
  },
  isDetailView: {
    type: Boolean,
    required: false,
    default: false,
  },
  refetchInvoice: { type: Function, default: () => {} },
  getInvoiceAgency: { type: Function, default: () => {} },
  getInvoiceHotel: { type: Function, default: () => {} },
  setActive: { type: Function, required: true },
  active: { type: Number, required: true },
  bookingList: Array<any>,
  roomRateList: Array<any>,
  adjustmentList: Array<any>,
  sortRoomRate: Function as any,
  sortAdjustment: Function as any,
  sortBooking: Function as any,
  invoiceObjAmount: { type: Number, required: true },
  nightTypeRequired: Boolean,
  requiresFlatRate: Boolean,
  openAdjustmentDialogFirstTime: {
    type: Boolean,
    required: false,
    default: false,
  },
  invoiceType: {
    type: String,
    required: false
  }
})

const activeTab = ref(props.active)

const route = useRoute()

const showTabs = ref<boolean>(true)

const toast = useToast()

const selectedBooking = ref<string>('')
const bookingObj = ref<any>(null)

const selectedRoomRate = ref<string>('')

const loadingSaveAll = ref(false)

const roomRateDialogOpen = ref<boolean>(false)
const adjustmentDialogOpen = ref<boolean>(false)

const errorInTab = ref({
  tabGeneralData: false,
  tabMedicalInfo: false,
  tabServices: false,
  tabPermissions: false
})

// VARIABLES -----------------------------------------------------------------------------------------

//

const formReload = ref(0)

const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})

const item = ref<GenericObject>({
  invoiceId: '',
  invoiceNumber: '',
  invoiceDate: new Date(),
  isManual: true,
  invoiceAmount: '0.00',
  hotel: null,
  agency: null,
  invoiceType: null,
})

const itemTemp = ref<GenericObject>({
  invoiceId: '',
  invoiceNumber: '',
  invoiceDate: new Date(),
  isManual: true,
  invoiceAmount: '0.00',
  hotel: null,
  agency: null,
  invoiceType: null,
})

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
  switch (props.active) {
    case 0:
      props.openDialog()
      break

    case 1:
      activeTab.value = 1
      roomRateDialogOpen.value = true
      break

    case 2:
      activeTab.value = 2
      adjustmentDialogOpen.value = true
      break

    default:
      break
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
        item.value.invoiceId = response.invoiceId
        item.value.invoiceNumber = response.invoiceNumber
        item.value.invoiceDate = new Date(response.invoiceDate)
        item.value.isManual = response.isManual
        item.value.invoiceAmount = response.invoiceAmount
        item.value.hotel = response.hotel
        item.value.agency = response.agency
        item.value.invoiceType = response.invoiceType
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

function openRoomRateDialog(booking?: any) {
  activeTab.value = 1

  if (booking?.id) {
    selectedBooking.value = booking?.id
    bookingObj.value = booking
  }

  roomRateDialogOpen.value = true
}

function openAdjustmentDialog(roomRate?: any) {
  activeTab.value = 2

  if (roomRate?.id) {
    selectedRoomRate.value = roomRate?.id
  }

  adjustmentDialogOpen.value = true
}

watch(activeTab, () => {
  props.setActive(activeTab.value)
})

watch(() => props.openAdjustmentDialogFirstTime, (newValue) => {
  if (newValue && props.roomRateList && props.roomRateList.length > 0) {
    openAdjustmentDialog(props.roomRateList[0])
  }
})

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
  if (props.isCreationDialog) {
    showTabs.value = route.query.type !== InvoiceType.CREDIT
  }
})
</script>

<template>
  <div class="justify-content-center align-center ">
    <div style="width: 100%; height: 100%;">
      <TabView id="tabView" v-model:activeIndex="activeTab" class="no-global-style">
        <TabPanel v-if="route.name !== 'invoice-edit-booking-id'">
          <template #header>
            <div class="flex align-items-center gap-2 p-2" :style="`${active === 0 && 'color: #0F8BFD;'} border-radius: 5px 5px 0 0;  width: 130px`">
              <i class="pi pi-calendar" style="font-size: 1.5rem" />
              <span class="font-bold white-space-nowrap">
                Bookings
                <i
                  v-if="errorInTab.tabGeneralData" class="pi p-error pi-question-circle"
                  style="font-size: 1.2rem"
                />
              </span>
            </div>
          </template>
          <BookingTabIncome
            :refetch-invoice="refetchInvoice"
            :get-invoice-agency="getInvoiceAgency"
            :is-dialog-open="isDialogOpen"
            :close-dialog="() => closeDialog()"
            :open-dialog="openDialog"
            :open-room-rate-dialog="openRoomRateDialog"
            :force-update="forceUpdate"
            :toggle-force-update="toggleForceUpdate"
            :sort-booking="sortBooking"
            :selected-invoice="selectedInvoice"
            :add-item="addBooking"
            :update-item="updateBooking"
            :list-items="bookingList"
            :night-type-required="nightTypeRequired"
            :is-creation-dialog="isCreationDialog"
            :invoice-obj="invoiceObj"
            :invoice-agency="invoiceAgency"
            :invoice-hotel="invoiceHotel"
            :is-detail-view="isDetailView"
            :show-totals="showTotals"
            :invoice-type="props.invoiceType"
          />
        </TabPanel>
        <TabPanel v-if="showTabs">
          <template #header>
            <div class="flex align-items-center gap-2 p-2" :style="`${active === 1 && 'color: #0F8BFD;'} border-radius: 5px 5px 0 0;  width: 130px`">
              <i class="pi pi-receipt" style="font-size: 1.5rem" />
              <span class="font-bold">
                Room Rates
                <i
                  v-if="errorInTab.tabGeneralData" class="pi p-error pi-question-circle"
                  style="font-size: 1.2rem"
                />
              </span>
            </div>
          </template>

          <!-- RoomRateTab -> components/invoice/roomRate/roomRateTab.vue  -->
          <RoomRateTabForIncome
            :booking-obj="bookingObj"
            :refetch-invoice="refetchInvoice"
            :requires-flat-rate="requiresFlatRate"
            :get-invoice-hotel="getInvoiceHotel"
            :is-dialog-open="roomRateDialogOpen"
            :close-dialog="() => { roomRateDialogOpen = false }"
            :open-dialog="handleDialogOpen"
            :selected-booking="selectedBooking"
            :open-adjustment-dialog="openAdjustmentDialog"
            :sort-room-rate="sortRoomRate"
            :force-update="forceUpdate"
            :toggle-force-update="toggleForceUpdate"
            :list-items="roomRateList"
            :add-item="addRoomRate"
            :invoice-obj="invoiceObj"
            :update-item="updateRoomRate"
            :is-creation-dialog="isCreationDialog"
            :is-detail-view="isDetailView"
            :selected-invoice="selectedInvoice as any"
            :show-totals="showTotals"
          />
        </TabPanel>
        <TabPanel v-if="showTabs">
          <template #header>
            <div class="flex align-items-center gap-2 p-2" :style="`${active === 2 && 'color: #0F8BFD;'} border-radius: 5px 5px 0 0;  width: 130px`">
              <i class="pi pi-sliders-v" style="font-size: 1.5rem" />
              <span class="font-bold white-space-nowrap">
                Adjustments
                <i
                  v-if="errorInTab.tabGeneralData" class="pi p-error pi-question-circle"
                  style="font-size: 1.2rem"
                />
              </span>
            </div>
          </template>

          <!-- AdjustmentTab -> components/invoice/adjustment/adjustmentTab.vue  -->
          <AdjustmentTabForIncome
            :invoice-obj="invoiceObj"
            :invoice-obj-amount="invoiceObjAmount"
            :is-dialog-open="adjustmentDialogOpen"
            :close-dialog="() => {

              adjustmentDialogOpen = false;

            }"
            :open-dialog="handleDialogOpen"
            :refetch-invoice="refetchInvoice"
            :selected-room-rate="selectedRoomRate"
            :sort-adjustment="sortAdjustment"
            :force-update="forceUpdate"
            :toggle-force-update="toggleForceUpdate"
            :list-items="adjustmentList"
            :add-item="addAdjustment"
            :update-item="updateAdjustment"
            :is-creation-dialog="isCreationDialog"
            :selected-invoice="selectedInvoice"
            :is-detail-view="isDetailView"
            :show-totals="showTotals"
          />
        </TabPanel>
      </TabView>
    </div>
  </div>
</template>

<style lang="scss">
.no-global-style .p-tabview-nav-container {
  padding-left: 0 !important;
  background-color: initial !important;
  border-top-left-radius: 0 !important;
  border-top-right-radius: 0 !important;
}
#tabView {
  .p-tabview-nav-container {
    .p-tabview-nav-link {
      color: var(--secondary-color) !important;
    }
    .p-tabview-nav-link:hover{
      border-bottom-color: transparent !important;
    }
  }

  .p-tabview-panels {
    padding: 0 !important;
  }
}

.no-global-style .p-tabview-nav li.p-highlight .p-tabview-nav-link {
    background: #d8f2ff;
    border-color: #0F8BFD;
    color: #0F8BFD;
}
</style>

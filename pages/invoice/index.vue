<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { any, string, z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import dayjs from 'dayjs'
import Checkbox from 'primevue/checkbox'
import ContextMenu from 'primevue/contextmenu'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import { statusToString } from '~/utils/helpers'
import type { IData } from '~/components/table/interfaces/IModelData'
import getUrlByImage from '~/composables/files'
import { v4 } from 'uuid'
import { ENUM_INVOICE_STATUS } from '~/utils/Enums'
import AttachmentDialog from '~/components/invoice/attachment/AttachmentDialog.vue'
import AttachmentHistoryDialog from '~/components/invoice/attachment/AttachmentHistoryDialog.vue'
import type { UndoImportInvoiceResponse } from './undo-import.vue'

// VARIABLES -----------------------------------------------------------------------------------------
const authStore = useAuthStore()
const route = useRoute()
const { data: userData } = useAuth()

const { status, data } = useAuth()
const isAdmin = (data.value?.user as any)?.isAdmin === true
const menu = ref()
let selectedInvoice = ref('')
let selectedInvoiceObj = ref<any>()
const menu_import = ref()
const menu_send = ref()
const menu_reconcile = ref()
const toast = useToast()
const entryCode = ref('')
const randomCode = ref(generateRandomCode());
const confirm = useConfirm()
const listItems = ref<any[]>([])
const listPrintItems = ref<any[]>([])
const formReload = ref(0)
const invoiceTypeList = ref<any[]>([])
const openDialog = ref(false)

const totalInvoiceAmount = ref(0)
const totalDueAmount = ref(0)
const totalAmount = ref(0)
const totalHotelAmount = ref(0)
const bookingDialogOpen = ref<boolean>(false)
const idItemDetail = ref('')
const loadingSaveAll = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const loadingSearch = ref(false)
const hotelError = ref(false)
const attachmentHistoryDialogOpen = ref<boolean>(false)
const attachmentDialogOpen = ref<boolean>(false)
const doubleFactorOpen = ref<boolean>(false)
const doubleFactorTotalOpen = ref<boolean>(false)
const changeAgencyDialogOpen = ref<boolean>(false)
const paymentsDialogOpen = ref<boolean>(false)
const attachmentInvoice = <any>ref(null)

const active = ref(0)


const itemSend = ref<GenericObject>({
  employee:userData?.value?.user?.userId,
  invoice:null,
})

// const allDefaultItem = { id: 'All', name: 'All', status: 'ACTIVE' }
const loadingDelete = ref(false)
const statusListForFilter = ref<any[]>([])
const filterToSearch = ref<IData>({
  criteria: ENUM_INVOICE_CRITERIA.find((i) => i.id === 'invoiceId'),
  search: '',
  client: [],
  agency: [],
  hotel: [],
  status: [],
  invoiceType: [],
  from: dayjs(new Date()).startOf('month').toDate(),
  to: dayjs(new Date()).endOf('month').toDate(),
  includeInvoicePaid: null
})

const confApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
})

const objApis = ref({
  client: { moduleApi: 'settings', uriApi: 'manage-client' },
  agency: { moduleApi: 'settings', uriApi: 'manage-agency' },
  hotel: { moduleApi: 'settings', uriApi: 'manage-hotel' },
  status: { moduleApi: 'invoicing', uriApi: 'manage-invoice-status' },
  invoiceType: { moduleApi: 'settings', uriApi: 'manage-invoice-type' },
})

const confAttachmentApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-attachment',
})

const confAdjustmentsApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-adjustment',
})
const confSendApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice/send',
})


const disableClient = ref<boolean>(false)
const disableDates = ref<boolean>(false)


const expandedInvoice = ref('')

const hotelList = ref<any[]>([])
const statusList = ref<any[]>([])
const clientList = ref<any[]>([])
const agencyList = ref<any[]>([])

const confclientListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-client',
})

const confClonationApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice/total-clone-invoice',
})

const confhotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const confagencyListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})

const confinvoiceTypeListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-invoice-type',
})

const confApiApplyUndo = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice/undo',
})

function generateRandomCode() {
  return Math.floor(100000 + Math.random() * 900000).toString();
}

const computedShowMenuItemNewCedit = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:CREDIT-CREATE'])))
})
const computedShowMenuItemShowHistory = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:ATTACHMENT-SHOW-HISTORY'])))
})

const computedShowMenuItemAttachment = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:ATTACHMENT-CREATE']) || authStore.can(['INVOICE-MANAGEMENT:ATTACHMENT-EDIT'])))
})

const computedShowMenuItemPrint = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:PRINT'])))
})

const computedShowClone = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:CLONE'])))
})
const computedShowCloneTotal = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:CLONE-TOTAL'])))
})

const invoiceContextMenu = ref()
const invoiceAllContextMenuItems = ref([
    {
      label: 'Selected Invoice:',
      icon: 'pi pi-folder',
      width: '24px',
      height: '24px',
      iconSvg:'',
      command: () => {},
      default: true,
      disabled: true,
      showItem: true,
    },    
    {
      label: 'Change Agency',
      icon: 'pi pi-arrow-right-arrow-left',
      width: '24px',
      height: '24px',
      iconSvg:'',
      command: () => {
        changeAgencyDialogOpen.value = true
      },
      default: true,
      showItem: false,
    },
    {
      label: 'New Credit',
      icon: 'pi pi-credit-card',
      width: '24px',
      height: '24px',
      iconSvg:'',
      command: () => {
        navigateTo(`invoice/credit/create?selected=${attachmentInvoice.value.id}`, { open: { target: '_blank' } })
      },
      default: true,
      disabled: computedShowMenuItemNewCedit,
      showItem: true,
    },
    {
      label: 'Status History',
      iconSvg: 'M320-240h320v-80H320v80Zm0-160h320v-80H320v80ZM240-80q-33 0-56.5-23.5T160-160v-640q0-33 23.5-56.5T240-880h320l240 240v480q0 33-23.5 56.5T720-80H240Zm280-520v-200H240v640h480v-440H520ZM240-800v200-200 640-640Z',
      viewBox: '0 -960 960 960',
      width: '19px',
      height: '19px',
      icon:'',
      command: handleAttachmentHistoryDialogOpen,
      default: true,
      disabled: computedShowMenuItemShowHistory,
      showItem: true,
    },
    {
      label: 'Document',
      icon: 'pi pi-paperclip',
      width: '24px',
      height: '24px',
      iconSvg:'',
      viewBox:'',
      command: () => {
        attachmentDialogOpen.value = true
      },
      default: true,
      disabled: computedShowMenuItemAttachment,
      showItem: true,
    },
    {
      label: 'Print',
      icon: 'pi pi-print',
      width: '24px',
      height: '24px',
      iconSvg:'',
      command: () => {
        exportAttachments()
        // if (attachmentInvoice.value?.hasAttachments) {
        // }
      },
      default: true,
      disabled: computedShowMenuItemPrint,
      showItem: true,
    },
    {
      label: 'Clone',
      icon: 'pi pi-copy',
      width: '24px',
      height: '24px',
      iconSvg:'',
      command: () => doubleFactor(),
      default: true,
     // disabled: computedShowClone,
      showItem: false,
    },
    {
      label: 'Adjustment',
      icon: 'pi pi-wrench',
      width: '24px',
      height: '24px',
      iconSvg:'',
      command: () => {
      },
      default: true,
      showItem: false,
    },
    {
      label: 'Undo Import',
      icon: 'pi pi-file-import',
      width: '24px',
      height: '24px',
      iconSvg:'',
      command: () => { 
        doubleFactorForUndo()
       },
      default: true,
      showItem: false,
    },
    {
      label: 'Payments',
      icon: 'pi pi-money-bill',
      width: '24px',
      height: '24px',
      iconSvg:'',
      command: () => {
        paymentsDialogOpen.value = true
      },
      default: true,
      showItem: false,
    },
    {
      label: 'Clone Complete',
      icon: 'pi pi-clone',
      width: '24px',
      height: '24px',
      iconSvg:'',
      command: () => {
        doubleFactorTotal()
      },
      default: true,
     
    },
    {
      label: 'Re-Send',
      icon: 'pi pi-send',
      width: '24px',
      height: '24px',
      iconSvg:'',
      command: () => { SendInvoiceByType() },
      default: true,
      showItem: false,
    },
    {
      label: 'Send',
      icon: 'pi pi-send',
      width: '24px',
      height: '24px',
      iconSvg:'',
      command: () => { SendInvoiceByType() },
      default: true,
      showItem: false,
    },
    {
      label: 'From Invoice',
      icon: 'pi pi-receipt',
      width: '24px',
      height: '24px',
      iconSvg:'',
      command: () => { onFromInvoice() },
      default: true,
      showItem: false,
    },
    {
      label: 'Clone Total',
      icon: 'pi pi-clone',
      width: '24px',
      height: '24px',
      iconSvg:'',
      command: () => {
        doubleFactorTotal()
      },
      default: true,
      disabled: computedShowCloneTotal
    },
])

const invoiceContextMenuItems = ref<any[]>([])

const exportDialogOpen = ref(false)
const exportPdfDialogOpen = ref(false)
const exportAttachmentsDialogOpen = ref(false)
const exportBlob = ref<any>(null)

// PRINT
const messageForEmptyTable = ref('The data does not correspond to the selected criteria.')
const loadingSavePrint = ref(false)
const openDialogPrint = ref(false)
const groupByClient = ref(false)
const invoiceSupport = ref(false)
const invoiceAndBookings = ref(true)
const invoiceIdsListToPrint = ref<string[]>([])

function savePrint() {
  loadingSavePrint.value = true
  setTimeout(() => {
    loadingSavePrint.value = false
  }, 3000)
}



async function invoicePrint() {
  try {
    loadingSavePrint.value = true
    let nameOfPdf = ''
    const arrayInvoiceType: string[] = []
    if (invoiceSupport.value) { arrayInvoiceType.push('INVOICE_SUPPORT') }
    if (invoiceAndBookings.value) { arrayInvoiceType.push('INVOICE_AND_BOOKING') }

    const payloadTemp = {
      invoiceId: [...invoiceIdsListToPrint.value],
      invoiceType: arrayInvoiceType
    }
    // En caso de que solo este marcado el paymentAndDetails

    nameOfPdf = invoiceSupport.value ? `invoice-support-${dayjs().format('YYYY-MM-DD')}.pdf` : `invoice-and-bookings-${dayjs().format('YYYY-MM-DD')}.pdf`

    const response: any = await GenericService.create('invoicing', 'manage-invoice/report', payloadTemp)

    const url = window.URL.createObjectURL(new Blob([response]))
    const a = document.createElement('a')
    a.href = url
    a.download = nameOfPdf // Nombre del archivo que se descargará
    document.body.appendChild(a)
    a.click()
    window.URL.revokeObjectURL(url)
    document.body.removeChild(a)
    loadingSavePrint.value = false
  }
  catch (error) {
    loadingSavePrint.value = false
  }
  finally {
    loadingSavePrint.value = false
    openDialogPrint.value = false
  }

  // generateStyledPDF()
}

function closeModalPrint() {
  openDialogPrint.value = false
}

function openDialogToPrint() {
  openDialogPrint.value = true
  getPrintList()
}
function Print() {
  navigateTo('/invoice/print',{ open: { target: '_blank' } })
}

////

async function SendInvoiceByType() {
  loadingSaveAll.value = true
  options.value.loading = true
  let completed = false
  try {
    if (!selectedInvoice) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Please select at least one item', life: 10000 })
      return
    }
    const invoiceItem = []
    invoiceItem.push(selectedInvoice)
    const payload = {
      invoice: invoiceItem,
      employee: userData?.value?.user?.userId
    }

    await GenericService.create(confSendApi.moduleApi, confSendApi.uriApi, payload)
    completed = true
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'Could not send invoice', life: 10000 })
  }
  finally {
    options.value.loading = false
    if (completed) {
        toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Invoice sent successfully', life: 10000 })
        await getList()
      }
  }

  loadingSaveAll.value = false
}

async function onFromInvoice() {
  const id = selectedInvoiceObj.value.parent
  if (id) {
    await getItemById({ id: id, type: 'INVOICE', status: '' })
  }
}

async function createSend() {
   // Opcional: Puedes manejar el estado de carga aquí
}

const computedShowMenuItemInvoice = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:CREATE'])))
})

const computedShowMenuItemCredit = computed(() => {
  if (!authStore.can(['INVOICE-MANAGEMENT:CREATE'])) {
    return true
  } else {
    return expandedInvoice.value === ''
  }
})

const computedShowMenuItemOldCredit = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:OLD-CREDIT-CREATE'])))
})
function handleDialogClose() {
    doubleFactorOpen.value = false;
    entryCode.value = '';
    randomCode.value = generateRandomCode();
}
function handleDialogCloseTotal() {
 
  doubleFactorTotalOpen.value = false;
  entryCode.value = '';
  randomCode.value = generateRandomCode();
}


async function handleApplyClick() {

    // Captura el invoice ID desde props
    const invoiceId = selectedInvoice;
    entryCode.value = '';
    // Redirecciona a la nueva interfaz
    navigateTo(`invoice/clone-partial?type=${InvoiceType.INVOICE}&selected=${selectedInvoice}`, { open: { target: '_blank' } });

   
   
    // Cierra el diálogo
    handleDialogClose();
  
}
async function createClonation() {
  try {
    loadingSaveAll.value = true;

    // Crear el payload con solo los datos necesarios
    const payload = {
      invoiceToClone: selectedInvoice, // ID del invoice seleccionado
      employeeName: userData?.value?.user?.name // ID del empleado autenticado
    };

    // Realizar la llamada al servicio
    return await GenericService.create(confClonationApi.moduleApi, confClonationApi.uriApi, payload);
  } catch (error: any) {
    console.error('Error en createClonation:', error?.data?.data?.error?.errorMessage);
    toast.add({ 
      severity: 'error', 
      summary: 'Error', 
      detail: error?.data?.data?.error?.errorMessage || error?.message, 
      life: 10000 
    });
  } finally {
    loadingSaveAll.value = false;
  }
}
async function handleTotalApplyClick() {
  entryCode.value = '';

  /*try {
    const response: any = await createClonation();

    if (response && response.clonedInvoice) {
      const clonedInvoiceId = response.clonedInvoice; // Asegúrate de que el ID esté en esta propiedad
      const clonedInvoiceNo=response.clonedInvoiceNo;
      toast.add({
        severity: 'info',
        summary: 'Confirmed',
        detail: `The clonation invoice ${clonedInvoiceNo} was created successfully`,
        life: 10000
      })
      // Redirigir a la página de edición con el ID del invoice clonado
      navigateTo({ path: `/invoice/edit/${clonedInvoiceId}` });
    }
  } catch (error: any) {
    // Mostrar un toast con el mensaje de error
    toast.add({ 
      severity: 'error', 
      summary: 'Error in Clonation', 
      detail: error?.data?.data?.error?.errorMessage || error?.message, 
      life: 10000 
    });
  } finally {
    handleDialogCloseTotal();
  }

  */
  navigateTo(`invoice/clone-total?type=${InvoiceType.INVOICE}&selected=${selectedInvoice}`, { open: { target: '_blank' } });

  handleDialogCloseTotal();
}


const createItems = ref([
  {
    label: 'Invoice',
    command: () => navigateTo(`invoice/create?type=${InvoiceType.INVOICE}`, { open: { target: '_blank' } }),
    disabled: computedShowMenuItemInvoice
  },
  // {
  //   label: 'Credit',
  //   command: () => navigateTo(`invoice/create?type=${InvoiceType.CREDIT}&selected=${expandedInvoice.value}`),
  //   disabled: computedShowMenuItemCredit
  // },
  // {
  //   label: 'Old Credit',
  //   command: () => navigateTo(`invoice/create?type=${InvoiceType.OLD_CREDIT}`, { open: { target: '_blank' } }),
  //   disabled: computedShowMenuItemOldCredit
  // },
  {
    label: 'Old Credit',
    command: () => navigateTo(`invoice/old-credit/create`, { open: { target: '_blank' } }),
    disabled: computedShowMenuItemOldCredit
  },
])

const createReconcile = ref([
  {
    label: 'Automatic',
    command: () => navigateTo('invoice/reconcile-automatic', { open: { target: '_blank' } }),
   // disabled: computedShowMenuItemReconcile
  },
   {
    label: 'Manual',
     command: () => navigateTo('invoice/reconcile-manual',{open:{target:'blank'}}),
  //   disabled: computedShowMenuItemCredit
   },
  {
    label: 'Reconcile from Files',
    command: () => navigateTo('invoice/reconcile-from-files', { open: { target: '_blank' } }),
    
  },
])


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
    class: 'field col-12  mb-3 mt-3',
  },
]

const item = ref<GenericObject>({
  image: '',
  name: '',
  code: '',
  description: '',
  status: true
})

const itemTemp = ref<GenericObject>({
  image: '',
  name: '',
  code: '',
  description: '',
  status: true
})

// IMPORTS ---------------------------------------------------------------------------------------------
const computedShowMenuItemImportBookingFromFile = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:IMPORT-BOOKING-FROM-FILE'])))
})

const computedShowMenuItemImportBookingFromVirtual = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:IMPORT-BOOKING-FROM-VIRTUAL'])))
})
const computedShowMenuItemUndoImport = computed(() => {
  return !(status.value === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:UNDO-IMPORT'])))
})
const itemsMenuImport = ref([
  {
    label: 'Booking From File',
    command: () => navigateTo('invoice/import', { open: { target: '_blank' } }),
    disabled: computedShowMenuItemImportBookingFromFile
  },
  {
    label: 'Booking From File (Virtual Hotels)',
    command: () => navigateTo('invoice/import-virtual'),
    disabled: computedShowMenuItemImportBookingFromVirtual
  },
  {
    label: 'Undo Import',
    command: () => navigateTo('invoice/undo-import',{ open: { target: '_blank' } }),
    //disabled: computedShowMenuItemUndoImport
  }
])

const itemsMenuSend = ref([
  {
    label: 'By FTP',
    command: () => navigateTo(`invoice/sendInvoice-ftp?type=${ENUM_INVOICE_SEND_TYPE.FTP}`, { open: { target: '_blank' } }),
    disabled: computedShowMenuItemImportBookingFromFile
  },
  {
    label: 'By Email',
    command: () => navigateTo(`invoice/sendInvoice-email?type=${ENUM_INVOICE_SEND_TYPE.EMAIL}`, { open: { target: '_blank' } }),
    disabled: computedShowMenuItemImportBookingFromVirtual
  },
  {
    label: 'By Bavel',
    command: () => navigateTo(`invoice/sendInvoice-bavel?type=${ENUM_INVOICE_SEND_TYPE.BAVEL}`, { open: { target: '_blank' } }),
    disabled: computedShowMenuItemImportBookingFromVirtual
  }
])
// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------

const columnstoPrint: IColumn[] = [
  { field: 'invoiceId', header: 'Id', type: 'text' },
  { field: 'invoiceType', header: 'Type', type:'text' },
  { field: 'hotel', header: 'Hotel', type: 'select', objApi: confhotelListApi },
  { field: 'agencyCd', header: 'Agency CD', type: 'text' },
  { field: 'agency', header: 'Agency', type: 'select', objApi: confagencyListApi },
  { field: 'invoiceNumber', header: 'Inv. No', type: 'text' },
  { field: 'invoiceDate', header: 'Gen. Date', type: 'date' },
  { field: 'isManual', header: 'Manual', type: 'bool', tooltip: 'Manual' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text' },
  { field: 'dueAmount', header: 'Invoice Balance', type: 'text' },
  { field: 'hasAttachments', header: 'Attachment', type: 'bool' },
  { field: 'aging', header: 'Aging', type: 'text' },
   { field: 'status', header: 'Status', width: '100px', frozen: true, type: 'slot-select', localItems: ENUM_INVOICE_STATUS, sortable: true },
]
const columns: IColumn[] = [
  { field: 'invoiceId', header: 'Id', type: 'text' },
   //{ field: 'invoiceType', header: 'Type', type: 'select' },
  { field: 'hotel', header: 'Hotel', type: 'select', objApi: confhotelListApi },
  { field: 'agencyCd', header: 'Agency CD', type: 'text' },
  { field: 'agency', header: 'Agency', type: 'select', objApi: confagencyListApi },
  { field: 'invoiceNumber', header: 'Inv. No', type: 'text' },
  { field: 'invoiceDate', header: 'Gen. Date', type: 'date' },
  { field: 'isManual', header: 'Manual', type: 'bool', tooltip: 'Manual' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'number' },
  { field: 'dueAmount', header: 'Invoice Balance', type: 'number' },
  // { field: 'autoRec', header: 'Auto Rec', type: 'bool' },
  // { field: 'status', header: 'Status', type: 'local-select', localItems: ENUM_INVOICE_STATUS },
  // { field: 'status', header: 'Status', width: '100px', frozen: true, type: 'select', objApi: objApis.value.status, sortable: true },
  { field: 'invoiceStatus', header: 'Status', width: '100px', frozen: true, type: 'select', objApi: objApis.value.status, sortable: true },
]
// -------------------------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'invoiceNumber', name: 'Invoice Number' },
  { id: 'invoiceDate', name: 'Invoice Date' },
  { id: 'invoiceAmount', name: 'Invoice Amount' },
]

// const sClassMap: IStatusClass[] = [
//   { status: 'Transit', class: 'text-transit' },
//   { status: 'Cancelled', class: 'text-cancelled' },
//   { status: 'Confirmed', class: 'text-confirmed' },
//   { status: 'Applied', class: 'text-applied' },
// ]
// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  showEdit: false,
  showAcctions: false,
  messageToDelete: 'Do you want to save the change?',
  showTitleBar: false
})
const optionsToPrint = ref({
  tableName: 'Invoice to Print',
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  showEdit: false,
  showAcctions: false,
  selectionMode: 'multiple' as 'multiple' | 'single',
  messageToDelete: 'Do you want to save the change?',
  showTitleBar: false
})
const payloadOnChangePage = ref<PageState>()
const payloadPrintOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})

const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const paginationPrint = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

const payloadForStatus = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})
// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------
function clearForm() {
  item.value = { ...itemTemp.value }
  idItem.value = ''
  fields[0].disabled = false
  formReload.value++
}

function handleAttachmentHistoryDialogOpen() {
  attachmentHistoryDialogOpen.value = true
}
function doubleFactor() {
   // Limpiar el campo entryCode


  doubleFactorOpen.value = true;
  entryCode.value = '';
  randomCode.value = generateRandomCode();
}


function doubleFactorTotal() {
  doubleFactorTotalOpen.value = true
  entryCode.value = '';
  randomCode.value = generateRandomCode();
}

function doubleFactorForUndo() {  
  openDialog.value = true
  entryCode.value = '';
  randomCode.value = generateRandomCode();
}


async function exportToPdf() {
  exportPdfDialogOpen.value = true
}

async function exportAttachments() {
  exportAttachmentsDialogOpen.value = true
}

async function exportList() {
  try {
    //   const response = await GenericService.export(options.value.moduleApi, options.value.uriApi, payload.value)
    // exportBlob.value = response

    exportDialogOpen.value = true




    // const url = window.URL.createObjectURL(response);
    //       const link = document.createElement('a');
    //       link.href = url;
    //       link.setAttribute('download', 'invoice-list.xlsx'); 
    //       document.body.appendChild(link);
    //       link.click();
    //       document.body.removeChild(link);
    //       window.URL.revokeObjectURL(url);

  } catch (error) {
    console.log(error);
  }

}


async function getList() {
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    listItems.value = []
    const newListItems = []

    totalInvoiceAmount.value = 0
    totalDueAmount.value = 0

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)
    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        let invoiceNumber
        if (iterator?.invoiceNumber?.split('-')?.length === 3) {
          invoiceNumber = `${iterator?.invoiceNumber?.split('-')[0]}-${iterator?.invoiceNumber?.split('-')[2]}`
        } else {
          invoiceNumber = iterator?.invoiceNumber
        }
        newListItems.push({
          ...iterator, 
          loadingEdit: false, 
          loadingDelete: false, 
          invoiceDate: new Date(iterator?.invoiceDate), 
          agencyCd: iterator?.agency?.code, 
          dueAmount: iterator?.dueAmount || 0, 
          invoiceAmount: iterator?.invoiceAmount || 0,
          invoiceNumber: invoiceNumber ?  invoiceNumber.replace("OLD", "CRE") : '',
          hotel: { ...iterator?.hotel, name: `${iterator?.hotel?.code || ""}-${iterator?.hotel?.name || ""}` }
        })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }

      totalInvoiceAmount.value += iterator.invoiceAmount
      totalDueAmount.value += iterator.dueAmount ? Number(iterator?.dueAmount) : 0
    }

    listItems.value = [...listItems.value, ...newListItems]
    return listItems
  }
  
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
    isFirstTimeInOnMounted.value = false
  }
}

async function getPrintList() {
  try {
   const payloadPrint = {
    filter: [{
        key: 'invoiceStatus',
        operator: 'IN', // Cambia a 'IN' para incluir varios valores
        value: ['RECONCILED', 'SENT'] // Lista de estados
      }],
      query: '',
      pageSize: 10,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    };

    idItemToLoadFirstTime.value = '';
    optionsToPrint.value.loading = true;
    listPrintItems.value = [];
    const newListItems = [];

    totalInvoiceAmount.value = 0;
    totalDueAmount.value = 0;

    const response = await GenericService.search(optionsToPrint.value.moduleApi, optionsToPrint.value.uriApi, payloadPrint);

    const { data: dataList, page, size, totalElements, totalPages } = response;
    
    paginationPrint.value.page = page;
    paginationPrint.value.limit = size;
    paginationPrint.value.totalElements = totalElements;
    paginationPrint.value.totalPages = totalPages;

    const existingIds = new Set(listPrintItems.value.map(item => item.id));

    for (const iterator of dataList) {
      let invoiceNumber;
      if (iterator?.invoiceNumber?.split('-')?.length === 3) {
        invoiceNumber = `${iterator?.invoiceNumber?.split('-')[0]}-${iterator?.invoiceNumber?.split('-')[2]}`;
      } else {
        invoiceNumber = iterator?.invoiceNumber;
      }
      newListItems.push({
        ...iterator,
        loadingEdit: false,
        loadingDelete: false,
        invoiceDate: new Date(iterator?.invoiceDate),
        agencyCd: iterator?.agency?.code,
        dueAmount: iterator?.dueAmount || 0,
        invoiceNumber: invoiceNumber.replace("OLD", "CRE"),
        hotel: { ...iterator?.hotel, name: `${iterator?.hotel?.code || ""}-${iterator?.hotel?.name || ""}` }
      });
      existingIds.add(iterator.id);
    
      totalInvoiceAmount.value += iterator.invoiceAmount;
      totalDueAmount.value += iterator.dueAmount ? Number(iterator.dueAmount) : 0;
    }

    listPrintItems.value = newListItems;


    console.log(paginationPrint.value.totalElements, 'Total de elementos');

    // Actualizar los totales en la interfaz de usuario
    // Puedes hacer esto si los elementos se actualizan en tiempo real en la interfaz

    // Aquí puedes manejar la lógica de la paginación, como actualizar los botones de página, etc.
    // Por ejemplo, puedes llamar a una función para renderizar los botones de paginación

  } catch (error) {
    console.error(error);
  } finally {
    optionsToPrint.value.loading = false;
  }
}

async function openEditDialog (item: any, type: string) {  
  switch (type) {
    case InvoiceType.INVOICE:
      await navigateTo({ path: `invoice/edit/${item}` }, { open: { target: '_blank' } })
      break
    case InvoiceType.CREDIT:
      await navigateTo({ path: `invoice/credit/edit/${item}` }, { open: { target: '_blank' } })
      break
    case InvoiceType.OLD_CREDIT:
      await navigateTo({ path: `invoice/credit/edit/${item}` }, { open: { target: '_blank' } })
      break
    case InvoiceType.INCOME:
      await navigateTo({ path: `invoice/income/edit/${item}` }, { open: { target: '_blank' } })
      break
    default:
      await navigateTo({ path: `invoice/edit/${item}` }, { open: { target: '_blank' } })
      break
  }
}

async function resetListItems() {
  payload.value.page = 0
  getList()
}

const isFirstTimeInOnMounted = ref(false)

function searchAndFilter() {
  payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.DESC
  }

  if (!filterToSearch.value.search) {
    // if (isFirstTimeInOnMounted.value === false) {}
      
    const filterObjIncludeInvoicePaid = payload.value.filter.find((item: any) => item?.key === 'dueAmount');

    switch (filterToSearch.value.includeInvoicePaid) {
      case true:
        // Elimina el filtro de 'dueAmount' si existe, para que muestre todos
        if (filterObjIncludeInvoicePaid) {
          payload.value.filter = payload.value.filter.filter((item: any) => item?.key !== 'dueAmount');
        }
        break;

      case false:
        // Solo muestra los que tienen dueAmount igual a 0
        if (filterObjIncludeInvoicePaid) {
          filterObjIncludeInvoicePaid.operator = 'EQUALS';
          filterObjIncludeInvoicePaid.value = 0;
        } else {
          payload.value.filter.push({
            key: 'dueAmount',
            operator: 'EQUALS',
            value: 0,
            logicalOperation: 'AND'
          });
        }
        break;

      default:
        // Muestra los elementos que no son 0 (por defecto)
        if (filterObjIncludeInvoicePaid) {
          filterObjIncludeInvoicePaid.operator = 'NOT_EQUALS';
          filterObjIncludeInvoicePaid.value = 0;
        } else {
          payload.value.filter.push({
            key: 'dueAmount',
            operator: 'NOT_EQUALS',
            value: 0,
            logicalOperation: 'AND'
          });
        }
        break;
    }

      // const filterObjIncludeInvoicePaid = payload.value.filter.find((item: any) => item?.key === 'dueAmount');

      // if (filterToSearch.value.includeInvoicePaid !== undefined && filterToSearch.value.includeInvoicePaid !== null) {
      //   const operator = filterToSearch.value.includeInvoicePaid ? 'EQUALS' : 'NOT_EQUALS';
      //   const filterValue = 0;

      //   if (filterObjIncludeInvoicePaid) {
      //     filterObjIncludeInvoicePaid.operator = operator;
      //     filterObjIncludeInvoicePaid.value = filterValue;
      //   } else {
      //     payload.value.filter.push({
      //       key: 'dueAmount',
      //       operator,
      //       value: filterValue,
      //       logicalOperation: 'AND'
      //     });
      //   }
      // } else if (filterObjIncludeInvoicePaid) {
      //   payload.value.filter = payload.value.filter.filter((item: any) => item?.key !== 'dueAmount');
      // }
    if (filterToSearch.value.client?.length > 0 && !filterToSearch.value.client.find(item => item.id === 'All')) {
      const filteredItems = filterToSearch.value.client.filter((item: any) => item?.id !== 'All')
      const itemIds = filteredItems?.map((item: any) => item?.id)

      payload.value.filter = [...payload.value.filter, {
        key: 'agency.client.id',
        operator: 'IN',
        value: itemIds,
        logicalOperation: 'AND'
      }]
    }
    if (filterToSearch.value.agency?.length > 0 && !filterToSearch.value.agency.find(item => item.id === 'All')) {
      const filteredItems = filterToSearch.value.agency.filter((item: any) => item?.id !== 'All')
      const itemIds = filteredItems?.map((item: any) => item?.id)
      payload.value.filter = [...payload.value.filter, {
        key: 'agency.id',
        operator: 'IN',
        value: itemIds,
        logicalOperation: 'AND'
      }]
    }
    if (filterToSearch.value.status?.length > 0) {
      const filteredItems = filterToSearch.value.status.filter((item: any) => item?.id !== 'All')      
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)        
        payload.value.filter = [...payload.value.filter, {
          key: 'manageInvoiceStatus.id',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND'
        }]
      }
    }
    if (filterToSearch.value.invoiceType?.length > 0) {
      const filteredItems = filterToSearch.value.invoiceType.filter((item: any) => item?.id !== 'All')
      if (filteredItems.length > 0) {
        const itemIds = filteredItems?.map((item: any) => item?.id)
        payload.value.filter = [...payload.value.filter, {
          key: 'manageInvoiceType.id',
          operator: 'IN',
          value: itemIds,
          logicalOperation: 'AND'
        }]
      }
    }
    if (filterToSearch.value.from && !disableDates.value) {
      payload.value.filter = [...payload.value.filter, {
        key: 'invoiceDate',
        operator: 'GREATER_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.from).startOf('day').format('YYYY-MM-DD'),
        logicalOperation: 'AND'
      }]
    }
    if (filterToSearch.value.to && !disableDates.value) {
      payload.value.filter = [...payload.value.filter, {
        key: 'invoiceDate',
        operator: 'LESS_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.to).endOf('day').format('YYYY-MM-DD'),
        logicalOperation: 'AND'
      }]
    }
  }

  if (filterToSearch.value.criteria && filterToSearch.value.search) {
    payload.value.filter = [...payload.value.filter, {
      key: filterToSearch.value.criteria ? filterToSearch.value.criteria.id : '',
      operator: 'EQUALS',
      value: filterToSearch.value.search,
      logicalOperation: 'AND'
    }]
  }


  if (filterToSearch.value.hotel?.length > 0) {
    const filteredItems = filterToSearch.value.hotel.filter((item: any) => item?.id !== 'All')
    if (filteredItems.length > 0) {
      const itemIds = filteredItems?.map((item: any) => item?.id)
      payload.value.filter = [...payload.value.filter, {
        key: 'hotel.id',
        operator: 'IN',
        value: itemIds,
        logicalOperation: 'AND'
      }]
    }
  }
  // else {
  //   const invoiceIdTemp = ENUM_INVOICE_CRITERIA.find((item: any) => item?.id === 'invoiceId')?.id
  //   const invoiceNumberTemp = ENUM_INVOICE_CRITERIA.find((item: any) => item?.id === 'invoiceNumberPrefix')?.id
  //   if (filterToSearch.value.criteria?.id !== invoiceIdTemp && filterToSearch.value.criteria?.id !== invoiceNumberTemp) {
  //     return hotelError.value = true
  //   }
  // }
  getList()
}

async function clearFilterToSearch() {  
  payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.DESC
  }
  filterToSearch.value = {
    criteria: ENUM_INVOICE_CRITERIA.find((item: any) => item?.id === 'invoiceId'),
    search: '',
    client: [],
    agency: [],
    hotel: [],
    invoiceType: [],
    from: dayjs(new Date()).startOf('month').toDate(),
    to: dayjs(new Date()).endOf('month').toDate(),
    includeInvoicePaid: true
  }
  await getStatusListTemp()
  getList()
}
async function getItemById(data: { id: string, type: string, status: any }) {
  await openEditDialog(data?.id, data?.type)
}

function handleDialogOpen() {
  bookingDialogOpen.value = true
}

async function createItem(item: { [key: string]: any }) {
  if (item) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    payload.status = statusToString(payload.status)
    payload.image = typeof payload.image === 'object' ? await getUrlByImage(payload.image) : payload.image
    await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
  }
}

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.status = statusToString(payload.status)
  payload.image = typeof payload.image === 'object' ? await getUrlByImage(payload.image) : payload.image
  await GenericService.update(confApi.moduleApi, confApi.uriApi, idItem.value || '', payload)
}

async function deleteItem(id: string) {
  try {
    loadingDelete.value = true
    await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
    clearForm()
    getList()
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
    getList()
  }
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

// async function getHotelList(query = '') {
//   try {
//     const payload
//       = {
//       filter: [
//         {
//           key: 'name',
//           operator: 'LIKE',
//           value: query,
//           logicalOperation: 'OR'
//         },
//         {
//           key: 'code',
//           operator: 'LIKE',
//           value: query,
//           logicalOperation: 'OR'
//         },
//         {
//           key: 'status',
//           operator: 'EQUALS',
//           value: 'ACTIVE',
//           logicalOperation: 'AND'
//         }
//       ],
//       query: '',
//       pageSize: 200,
//       page: 0,
//       sortBy: 'name',
//       sortType: ENUM_SHORT_TYPE.ASC
//     }

//     hotelList.value = [{ id: 'All', name: 'All', code: 'All' }]
//     const response = await GenericService.search(confhotelListApi.moduleApi, confhotelListApi.uriApi, payload)
//     const { data: dataList } = response
//     for (const iterator of dataList) {
//       hotelList.value = [...hotelList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
//     }
//   }
//   catch (error) {
//     console.error('Error loading hotel list:', error)
//   }
// }

// async function getClientList(query = '') {
//   try {
//     debugger
//     const payload
//       = {
//       filter: [
//         {
//           key: 'name',
//           operator: 'LIKE',
//           value: query,
//           logicalOperation: 'OR'
//         },
//         {
//           key: 'status',
//           operator: 'EQUALS',
//           value: 'ACTIVE',
//           logicalOperation: 'AND'
//         }
//       ],
//       query: '',
//       pageSize: 200,
//       page: 0,
//       sortBy: 'name',
//       sortType: ENUM_SHORT_TYPE.ASC
//     }
//     let clientTemp: any[] = []
//     clientList.value = []
//     const response = await GenericService.search(confclientListApi.moduleApi, confclientListApi.uriApi, payload)
//      const { data: dataList } = response
//     for (const iterator of dataList) {
//        clientList.value = [...clientList.value, { id: iterator.id, name: iterator.name, status: iterator.status }]
//      }
//   }
//   catch (error) {
//     console.error('Error loading client list:', error)
//   }
// }

interface DataListItem {
  id: string
  name: string
  code: string
  status: string
  description?: string
}

interface ListItem {
  id: string
  name: string
  status: boolean | string
  code?: string
  description?: string
}

function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status,
    code: data.code,
    description: data.description
  }
}

interface DataListItemForStatus {
  id: string
  name: string
  code: string
  status: string
  description?: string
  invoiceStatus: {
    sentStatus: boolean
    reconciledStatus: boolean
    canceledStatus: boolean
    processStatus: boolean
  }
}

interface ListItemForStatus {
  id: string
  name: string
  status: boolean | string
  code?: string
  description?: string
  sentStatus?: boolean
  reconciledStatus?: boolean
  canceledStatus?: boolean
  processStatus?: boolean
}

function mapFunctionStatus(data: DataListItemForStatus): ListItemForStatus {  
  return {
    id: data.id,
    name: `${data.name}`,
    status: data.status,
    code: data.code,
    description: data.description,
    sentStatus: data.invoiceStatus.sentStatus,
    reconciledStatus: data.invoiceStatus.reconciledStatus,
    canceledStatus: data.invoiceStatus.canceledStatus,
    processStatus: data.invoiceStatus.processStatus
  }
}

function mapFunctionForType(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status,
    code: data.code,
    description: data.description
  }
}

async function getClientList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[],) {
  let clientTemp: any[] = []
  clientList.value = []
  clientTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
  clientList.value = [...clientList.value, ...clientTemp]
}
async function getAgencyList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  let agencyTemp: any[] = []
  agencyList.value = []
  agencyTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
  agencyList.value = [...agencyList.value, ...agencyTemp]
}
async function getAgencyListTemp(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  return await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
}
async function getHotelList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  let hotelTemp: any[] = []
  hotelList.value = []
  hotelTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
  hotelList.value = [...hotelList.value, ...hotelTemp]
}
async function getHotelListTemp(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  return await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
}
async function getStatusList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  let statusTemp: any[] = []
  statusList.value = []
  statusTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
  statusList.value = [...statusList.value, ...statusTemp]
}

// async function getStatusListLoadValuesByDefaults(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
//   return await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
// }

async function getStatusListTemp() {
  try {
    const filter: FilterCriteria[] = [
      {
        key: 'status',
        logicalOperation: 'AND',
        operator: 'EQUALS',
        value: 'ACTIVE',
      },
      {
        key: 'processStatus',
        logicalOperation: 'OR',
        operator: 'EQUALS',
        value: true
      },
      {
        key: 'sentStatus',
        logicalOperation: 'OR',
        operator: 'EQUALS',
        value: true
      },
      {
        key: 'reconciledStatus',
        logicalOperation: 'OR',
        operator: 'EQUALS',
        value: true
      }
    ]
    payloadForStatus.value.filter = filter
    const response = await GenericService.search(objApis.value.status.moduleApi, objApis.value.status.uriApi, payloadForStatus.value)
    if (response) {
      
      filterToSearch.value.status = [
        ...response.data.map((item: any) => (
          { 
            id: item.id, 
            name: `${item.code} - ${item.name}`, 
            code: item.code,
            description: item.description,
            status: item.status 
          }
        )
        ),
      ]
      statusList.value = [
        ...response.data.map((item: any) => (
          { 
            id: item.id, 
            name: `${item.code} - ${item.name}`, 
            code: item.code,
            description: item.description,
            status: item.status 
          }
        )),
      ]
    }    
  }
  catch (error) {
    console.error('Error loading status list:', error)
  }
}

// {
//   "id": "a2befe8a-d335-4be0-94be-a8ed38a6d4f2",
//   "name": "Sent",
//   "status": "ACTIVE",
//   "code": "SENT",
//   "description": ""
// }

// async function getStatusList(query = '') {
//   try {
//     statusList.value = [{ id: 'All', name: 'All', code: 'All' }, ...ENUM_INVOICE_STATUS]

//     if (query) {
//       statusList.value = statusList.value.filter(inv => String(inv?.name).toLowerCase().includes(query.toLowerCase()))
//     }
//   }
//   catch (error) {
//     console.error('Error loading hotel list:', error)
//   }
// }

async function getInvoiceTypeList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[]) {
  let invoiceTypeListTemp: any[] = []
  invoiceTypeList.value = []
  invoiceTypeListTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunctionForType, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })    
  invoiceTypeList.value = [...invoiceTypeList.value, ...invoiceTypeListTemp]
}

// async function getInvoiceTypeList(query = '') {
//   try {

//     const payload
//       = {
//       filter: [
//         {
//           key: 'name',
//           operator: 'LIKE',
//           value: query,
//           logicalOperation: 'OR'
//         },
//         {
//           key: 'code',
//           operator: 'LIKE',
//           value: query,
//           logicalOperation: 'OR'
//         },
//         {
//           key: 'status',
//           operator: 'EQUALS',
//           value: 'ACTIVE',
//           logicalOperation: 'AND'
//         }
//       ],
//       query: '',
//       pageSize: 200,
//       page: 0,
//       sortBy: 'name',
//       sortType: ENUM_SHORT_TYPE.ASC
//     }

//     invoiceTypeList.value = [{ id: 'All', name: 'All', code: 'All' }]
//     const response = await GenericService.search(confinvoiceTypeListApi.moduleApi, confinvoiceTypeListApi.uriApi, payload)
//     const { data: dataList } = response
//     for (const iterator of dataList) {
//       invoiceTypeList.value = [...invoiceTypeList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
//     }

//     // invoiceTypeList.value = [{ id: 'All', name: 'All', code: 'All' }, ...ENUM_INVOICE_TYPE]

//     // if (query) {
//     //   invoiceTypeList.value = invoiceTypeList.value.filter(inv => String(inv?.name).toLowerCase().includes(query.toLowerCase()))
//     // }


//   }
//   catch (error) {
//     console.error('Error loading invoice type list:', error)
//   }
// }

// async function getAgencyList(query = '') {
//   try {
//     const payload
//       = {
//       filter: [
//         {
//           key: 'name',
//           operator: 'LIKE',
//           value: query,
//           logicalOperation: 'OR'
//         },
//         {
//           key: 'code',
//           operator: 'LIKE',
//           value: query,
//           logicalOperation: 'OR'
//         },
//         {
//           key: 'status',
//           operator: 'EQUALS',
//           value: 'ACTIVE',
//           logicalOperation: 'AND'
//         }
//       ] as any,
//       query: '',
//       pageSize: 200,
//       page: 0,
//       sortBy: 'name',
//       sortType: ENUM_SHORT_TYPE.ASC
//     }

//     agencyList.value = [{ id: 'All', name: 'All', code: 'All' }]

//     if (filterToSearch.value.client?.length === 0) {
//       return agencyList.value = []
//     }
//     const clientIds: any[] = []

//     filterToSearch.value?.client?.forEach((client: any) => clientIds.push(client?.id))

//     payload.filter.push({
//       key: 'client.id',
//       operator: 'IN',
//       value: clientIds,
//       logicalOperation: 'AND'
//     })

//     const response = await GenericService.search(confagencyListApi.moduleApi, confagencyListApi.uriApi, payload)
//     const { data: dataList } = response
//     for (const iterator of dataList) {
//       agencyList.value = [...agencyList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
//     }
//   }
//   catch (error) {
//     console.error('Error loading agency list:', error)
//   }
// }

async function parseDataTableFilter(payloadFilter: any) {
  console.log(payloadFilter);

  // if(payloadFilter?.agencyCd){
  //   payloadFilter['agency.code'] = payloadFilter.agencyCd
  //   delete payloadFilter.agencyCd
  // }

  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)

  if (parseFilter && parseFilter?.length > 0) {
    for (let i = 0; i < parseFilter?.length; i++) {

      if (parseFilter[i]?.key === 'agencyCd') {
        parseFilter[i].key = 'agency.code'
      }

      if (parseFilter[i]?.key === 'status') {
        parseFilter[i].key = 'invoiceStatus'
      }

      if (parseFilter[i]?.key === 'invoiceNumber') {
        parseFilter[i].key = 'invoiceNumberPrefix'
      }

    }
  }

  payload.value.filter = [...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    if (event.sortField === 'hotel') {
      event.sortField = 'hotel.name'
    }
    if (event.sortField === 'agencyCd') {
      event.sortField = 'agency.code'
    }
    if (event.sortField === 'agency') {
      event.sortField = 'agency.name'
    }
    if(event.sortField === 'status') {
      event.sortField = 'invoiceStatus'
    }
    if (event.sortField === 'invoiceNumber') {
      event.sortField = 'invoiceNumberPrefix'
    }
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    getList()
  }
}

const checkboxLabel = computed(() => {
  switch (filterToSearch.value.includeInvoicePaid) {
    case true:
      return "Include all invoices";
    case false:
      return "Include paid invoices only";
    case null:
      return "Exclude paid invoices";
    default:
      return "Exclude paid invoices";
  }
});


function getStatusBadgeBackgroundColor(code: string) {  
  switch (code) {
    case 'PROCESSED': return '#FF8D00'
    case 'RECONCILED': return '#005FB7'
    case 'SENT': return '#006400'
    case 'CANCELLED': return '#888888'
    case 'PENDING': return '#686868'

    default:
      return '#686868'
  }
}

function getStatusName(code: string) {
  switch (code) {
    case 'PROCESSED': return 'Processed'
    case 'RECONCILED': return 'Reconciled'
    case 'SENT': return 'Sent'
    case 'CANCELLED': return 'Cancelled'
    case 'PENDING': return 'Pending'

    default:
      return ''
  }
}

interface InvoiceStatus {
  id: string;
  code: string;
  name: string;
  showClone: boolean;
  enabledToApply: boolean;
  sentStatus: boolean;
  reconciledStatus: boolean;
  canceledStatus: boolean;
  processStatus: boolean;
}


function getStatusBadgeBackgroundColorByItem(item: InvoiceStatus) { 
  if (!item) return
  if (item.processStatus) return '#FF8D00'
  if (item.sentStatus) return '#006400'
  if (item.reconciledStatus) return '#005FB7'
  if (item.canceledStatus) return '#888888'
}

function getStatusNameByItem(item: InvoiceStatus) {


  switch (code) {
    case 'PROCESSED': return 'Processed'
    case 'RECONCILED': return 'Reconciled'
    case 'SENT': return 'Sent'
    case 'CANCELLED': return 'Cancelled'
    case 'PENDING': return 'Pending'

    default:
      return ''
  }
}


const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criterial && filterToSearch.value.search)
  return false
})

const disabledClearSearch = computed(() => {
  return !(filterToSearch.value.criterial && filterToSearch.value.search)
})

function toggle(event) {
  menu.value.toggle(event)
}

function toggleReconcile(event) {
  menu_reconcile.value.toggle(event)
}
function toggleImport(event) {
  menu_import.value.toggle(event)
}

function toggleSend(event) {
  menu_send.value.toggle(event)
}

function setMenuOptions() {
  invoiceContextMenuItems.value = [...invoiceAllContextMenuItems.value.filter((item: any) => item?.default).map((item) => ({ ...item }))]
}

function findMenuItemByLabelSetShow(label: string, list: any[], showItem: boolean) {
  let menuItem = list.find((item: any) => item.label === label);
  if (menuItem) {
    menuItem.showItem = showItem
  }
}

async function onCloseChangeAgencyDialog(isCancel: boolean) {
  changeAgencyDialogOpen.value = false
  if (!isCancel) {
    getList()
  }
}

function onRowRightClick(event: any) {

  selectedInvoice = event.data.id
  selectedInvoiceObj.value = event.data
  setMenuOptions()
  // Mostrar New Credit, solo para los invoice, en estados SENT y RECONCILED, cuyo balance sea distinto de cero
  if (event.data?.invoiceType !== InvoiceType.INVOICE || ![InvoiceStatus.SENT, InvoiceStatus.RECONCILED].includes(event?.data?.status)
    || (event.data?.dueAmount === 0 && event.data?.invoiceAmount === 0)) {
    invoiceContextMenuItems.value = [...invoiceContextMenuItems.value.filter((item: any) => item?.label !== 'New Credit')]
  }

  if (event.data?.invoiceType === InvoiceType.INVOICE) {
    // Mostrar Clone solo si es de tipo Invoice y esta como showClone el status en el nomenclador Invoice Status
    if ([InvoiceStatus.SENT, InvoiceStatus.RECONCILED, InvoiceStatus.PROCECSED].includes(event?.data?.status) &&
      event.data?.invoiceStatus?.showClone && !event.data?.hotel?.virtual) {
      findMenuItemByLabelSetShow('Clone', invoiceContextMenuItems.value, true)
    }

    // Mostrar undo import solo para Processed y no sea manual (Solo para invoice)
    if (event?.data?.status === InvoiceStatus.PROCECSED && !event.data.isManual) {
      findMenuItemByLabelSetShow('Undo Import', invoiceContextMenuItems.value, true)
    }
    
    // Mostrar Clone Complete solo para Reconciled,Sent y e iguales amounts. Debe estar en close operation el invoice date
    if ([InvoiceStatus.SENT, InvoiceStatus.RECONCILED].includes(event?.data?.status)
      && event?.data?.dueAmount === event?.data?.invoiceAmount && event.data?.isInCloseOperation) {  
      if (!event.data?.hotel?.virtual && (typeof event?.data?.dueAmount === 'number' && Number(event?.data?.dueAmount) > 0) || (typeof event?.data?.dueAmount === 'string' && Number(event?.data?.dueAmount.replace(/,/g, '')) > 0)) {
        findMenuItemByLabelSetShow('Clone Complete', invoiceContextMenuItems.value, true)
      }      
    }
  }

  //Change Agency
  if ([InvoiceStatus.SENT, InvoiceStatus.RECONCILED, InvoiceStatus.PROCECSED].includes(event?.data?.status)
    && event?.data.dueAmount === event?.data.invoiceAmount) {
    if (event.data.status === InvoiceStatus.PROCECSED) {
      if (event.data.isInCloseOperation) {
        let changeAgencyItem = invoiceContextMenuItems.value.find((item: any) => item.label === 'Change Agency')
        changeAgencyItem.showItem = true
      }
    } else {
      if (event?.data.hotel?.virtual) {
        let changeAgencyItem = invoiceContextMenuItems.value.find((item: any) => item.label === 'Change Agency')
        changeAgencyItem.showItem = true
      }
    }
  }

  
  const changeSelectedInvoiceTitleItem = invoiceContextMenuItems.value.find((item: any) => item.label === 'Selected Invoice:')
  if (changeSelectedInvoiceTitleItem) {
    changeSelectedInvoiceTitleItem.label = `Selected Invoice: ${event.data.invoiceId}`
  }

  // Payments
  if ([InvoiceStatus.SENT, InvoiceStatus.PROCECSED, InvoiceStatus.RECONCILED].includes(event?.data?.status) && event?.data.dueAmount !== event?.data.invoiceAmount) {
    findMenuItemByLabelSetShow('Payments', invoiceContextMenuItems.value, true)
  }

  // Resend
  if (event?.data?.status === InvoiceStatus.SENT) {
    findMenuItemByLabelSetShow('Re-Send', invoiceContextMenuItems.value, true)
  }

  // Resend
  if (event?.data?.status === InvoiceStatus.RECONCILED) {
    findMenuItemByLabelSetShow('Send', invoiceContextMenuItems.value, true)
  }

  // From Invoice // Solo se debe mostrar la opcion si el parentId no es null, o sea, si es un Credit
  if ([InvoiceType.CREDIT].includes(event.data?.invoiceType)
      && selectedInvoiceObj.value.parent) { //indica si es de tipo credit, ya que los old-credit no tienen padre
    findMenuItemByLabelSetShow('From Invoice', invoiceContextMenuItems.value, true)
  }

  // Adjustment (Se comenta temporalmente, no borrar)
  // if (event?.data?.status === InvoiceStatus.PROCECSED) {
  //   findMenuItemByLabelSetShow('Adjustment', invoiceContextMenuItems.value, true)
  // }




  invoiceContextMenuItems.value = [...invoiceContextMenuItems.value.filter((item: any) => item?.showItem)]
  // Mostrar solo si es para estos estados
  attachmentInvoice.value = event.data
  invoiceContextMenu.value.show(event.originalEvent)
}

async function applyUndo() {
  try {
    loadingSaveAll.value = true
    if (selectedInvoiceObj.value && selectedInvoiceObj.value.id) {
      const payload = {
        ids: [selectedInvoiceObj.value.id]
      }
      const response = await GenericService.create(confApiApplyUndo.moduleApi, confApiApplyUndo.uriApi, payload) as UndoImportInvoiceResponse
      loadingSaveAll.value = false
      openDialog.value = false
      if (response.errors.length === 0) {
        toast.add({
          severity: 'success',
          summary: 'Success',
          detail: `Undo successfully applied to ${response.satisfactoryQuantity} records.`,
          life: 6000
        })
        await getList()
      }
      else {
        for (const element of response.errors) {
          const objInvoiceTemp = listItems.value.find((item: any) => item.id === element?.invoiceId)
          if (objInvoiceTemp) {
            objInvoiceTemp.reverseStatus = `Error undoing the import of invoice ${element?.invoiceNo}. Please try again.`
          }
        }
      }
    }
  }
  catch (error) {
    loadingSaveAll.value = false
    console.error('Error applying undo:', error)
  }
}

function handleClose() {
  openDialog.value = false
  entryCode.value = ''
  randomCode.value = generateRandomCode()
}
// -------------------------------------------------------------------------------------------------------

// WATCH FUNCTIONS -------------------------------------------------------------------------------------
watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10

  getList()
})
watch(() => idItemToLoadFirstTime.value, async (newValue) => {
  if (!newValue) {
    clearForm()
  }
  else {
    await getItemById(newValue)
  }
})

watch(disableClient, () => {
  if (disableClient.value) {
    filterToSearch.value.client = []
    filterToSearch.value.agency = []
  }
})
watch(filterToSearch, () => {
  if (filterToSearch.value.criteria?.id === ENUM_INVOICE_CRITERIA[3]?.id || filterToSearch.value.criteria?.id === ENUM_INVOICE_CRITERIA[1]?.id) {
    hotelError.value = false
  }
}, { deep: true })

// -------------------------------------------------------------------------------------------------------

// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
onMounted(async () => {
  isFirstTimeInOnMounted.value = true
  filterToSearch.value.criterial = ENUM_FILTER[0]
  await getStatusListTemp()  
  searchAndFilter()
})

const legend = ref(
  [
    {
      name: 'Processed',
      color: '#FF8D00',
      colClass: 'pr-4',
    },
    {
      name: 'Cancelled',
      color: '#686868',
      colClass: 'pr-4',
    },
    {
      name: 'Waiting',
      color: '#F90303',
      colClass: 'pr-4',
    },
    {
      name: 'Reconciled',
      color: '#005FB7',
      colClass: 'pr-4',
    },
    {
      name: 'Sent',
      color: '#006400',
      colClass: '',
    },
  ]
)

// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class=" col-12 align-items-center grid w-full">
    <div class="flex align-items-center justify-content-between w-full">
      <h5 class="mb-0 w-6">
        Invoice Management
      </h5>
      <div class="flex flex-row w-full place-content-center justify-center justify-content-end">
        <Button v-if="status === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:SHOW-BTN-NEW']))"
          v-tooltip.left="'New'" label="New" icon="pi pi-plus" severity="primary" aria-haspopup="true"
          aria-controls="overlay_menu" @click="toggle" />
        <Menu id="overlay_menu" ref="menu" :model="createItems" :popup="true" />

        <PopupNavigationMenu v-if="false" :items="createItems" icon="pi pi-plus" label="New">
          <template #item="props">
            <button :disabled="props.props.label === 'Credit' && expandedInvoice === ''"
              style="border: none; width: 100%;">
              {{ props.props.label }}
            </button>
          </template>
        </PopupNavigationMenu>

        <Button v-if="status === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:SHOW-BTN-IMPORT']))"
          v-tooltip.left="'Import'" class="ml-2" label="Import" icon="pi pi-file-import" severity="primary"
          aria-haspopup="true" aria-controls="overlay_menu_import" @click="toggleImport">

        </Button>
        <Menu id="overlay_menu_import" ref="menu_import" class="ml-2" :model="itemsMenuImport" :popup="true" />
        <PopupNavigationMenu v-if="false" :items="itemsMenuImport" icon="pi pi-plus" label="Import">
          <template #item="props">
            <button style="border: none; width: 100%;">
              {{ props.props.label }}
            </button>
          </template>
        </PopupNavigationMenu>
        <!-- <SplitButton class="ml-2" icon="pi pi-download" label="Import" :model="itemsMenuImport" /> -->
        <!---<Button class="ml-2" icon="pi pi-copy" label="Rec Inv" />.-->
        <Button
         
          v-tooltip.left="'Reconcile Invoice'" class="ml-2" label="Rec Inv" icon="pi pi-copy" severity="primary"
          aria-haspopup="true" aria-controls="overlay_menu_reconcile" @click="toggleReconcile" />

        <Menu id="overlay_menu_reconcile" ref="menu_reconcile" :model="createReconcile" :popup="true" />

        <PopupNavigationMenu v-if="false" :items="createReconcile" icon="pi pi-copy" label="Rec Inv">
          <template #item="props">
            <button style="border: none; width: 100%;">
              {{ props.props.label }}
            </button>
          </template>
        </PopupNavigationMenu>


        <!-- <Button class="ml-2" icon="pi pi-envelope" label="Send"  @click="() => navigateTo(`invoice/sendInvoice`, { open: { target: '_blank' } })" /> -->
          <Button v-if="status === 'authenticated' && (isAdmin || authStore.can(['INVOICE-MANAGEMENT:SHOW-BTN-IMPORT']))"
          v-tooltip.left="'Send'" class="ml-2" label="Send" icon="pi pi-envelope" severity="primary"
          aria-haspopup="true" aria-controls="overlay_menu_send" @click="toggleSend">

        </Button>
        <Menu id="overlay_menu_send" ref="menu_send" class="ml-2" :model="itemsMenuSend" :popup="true" />
        <PopupNavigationMenu v-if="false" :items="itemsMenuSend" icon="pi pi-plus" label="Send">
          <template #item="props">
            <button style="border: none; width: 100%;">
              {{ props.props.label }}
            </button>
          </template>
        </PopupNavigationMenu>
          <!-- <Button
          class="ml-2" icon="pi pi-paperclip" :disabled="!attachmentInvoice" label="Document" @click="() => {
            attachmentDialogOpen = true
          }"
        /> -->
        <!-- <Button class="ml-2" icon="pi pi-file-plus" label="Process" /> -->
        <!--  <Button class="ml-2" icon="pi pi-cog" label="Adjustment" disabled /> -->
        <Button class="ml-2" icon="pi pi-print" label="Print" :disabled="listItems.length === 0" @click="Print()" />
        <Button class="ml-2" icon="pi pi-download" label="Export" :disabled="listItems.length === 0"
          @click="() => exportList()" />
        <!-- <Button class="ml-2" icon="pi pi-times" label="Exit" @click="() => { navigateTo('/') }" /> -->
      </div>
    </div>
  </div>
  <div class="grid w-full">
    <div class="col-12 order-0 w-full md:order-1 md:col-6 xl:col-9">
      <div class=" p-0">
        <Accordion :active-index="0" class="mb-2">
          <AccordionTab>
            <template #header>
              <div
                class="text-white font-bold custom-accordion-header flex justify-content-between w-full align-items-center">
                <div>
                  Search Fields
                </div>
                <div>
                  <PaymentLegend :legend="legend" />
                </div>
              </div>
            </template>
            <div class="flex gap-4 flex-column lg:flex-row">
              <div class="flex flex-row h-fit">
                <div class="flex flex-column justify-content-around align-content-end align-items-end mr-1">
                  <label for="" class="font-bold">Client</label>
                  <label for="" class="font-bold">Agency</label>
                </div>

                <div class="flex flex-column gap-2 ">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <div class="w-full lg:w-auto" style=" z-index:5 ">
                      <DebouncedAutoCompleteComponent
                        id="autocomplete"
                        field="name"
                        item-value="id"
                        class="w-full"
                        :multiple="true"
                        :model="filterToSearch.client"
                        :suggestions="[...clientList]"
                        @change="async ($event) => {
                          if (!filterToSearch.client.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                            filterToSearch.client = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {
                            filterToSearch.client = $event.filter((element: any) => element?.id !== 'All')
                          }
                          if (filterToSearch.client.length === 0) {
                            filterToSearch.agency = []
                          }
                        }"
                        @load="async($event) => {
                          const objQueryToSearch = {
                            query: $event,
                            keys: ['name', 'code'],
                          }
                          const filter: FilterCriteria[] = [{
                            key: 'status',
                            logicalOperation: 'AND',
                            operator: 'EQUALS',
                            value: 'ACTIVE',
                          }]
                          await getClientList(objApis.client.moduleApi, objApis.client.uriApi, objQueryToSearch, filter)
                        }"
                      />
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <div class="w-full lg:w-auto">
                      <DebouncedAutoCompleteComponent
                        id="autocomplete"
                        field="name"
                        item-value="id"
                        class="w-full"
                        :multiple="true"
                        :model="filterToSearch.agency"
                        :suggestions="[...agencyList]"
                        @change="($event) => {
                          if (!filterToSearch.agency.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                            filterToSearch.agency = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {
                            filterToSearch.agency = $event.filter((element: any) => element?.id !== 'All')
                          }
                        }"
                        @load="async($event) => {
                          let ids = []
                          if (filterToSearch.client.length > 0) {
                            ids = filterToSearch.client.map((element: any) => element?.id)
                          }

                          const filter: FilterCriteria[] = [
                            {
                              key: 'client.id',
                              logicalOperation: 'AND',
                              operator: 'IN',
                              value: ids,
                            },
                            {
                              key: 'status',
                              logicalOperation: 'AND',
                              operator: 'EQUALS',
                              value: 'ACTIVE',
                            },
                          ]
                          await getAgencyList(objApis.agency.moduleApi, objApis.agency.uriApi, {
                            query: $event,
                            keys: ['name', 'code'],
                          }, filter)
                        }"
                      />
                    </div>
                  </div>
                </div>
              </div>

              <div class="flex flex-row h-fit">
                <div class="flex flex-column justify-content-around align-content-end align-items-end mr-1">
                  <label for="" class="font-bold">Hotel</label>
                  <label for="" class="font-bold">Status</label>
                </div>

                <div class="flex flex-column gap-2 ">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <div class="w-full" style=" z-index:5">
                      <div class="flex gap-2 w-full">
                        <DebouncedAutoCompleteComponent
                        id="autocomplete"
                        class="w-full"
                        field="name"
                        item-value="id"
                        :multiple="true"
                        :model="filterToSearch.hotel"
                        :suggestions="[...hotelList]"
                        @change="($event) => {
                          if (!filterToSearch.hotel.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                            filterToSearch.hotel = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {
                            filterToSearch.hotel = $event.filter((element: any) => element?.id !== 'All')
                          }
                        }"
                        @load="async($event) => {
                          const filter: FilterCriteria[] = [
                            {
                              key: 'status',
                              logicalOperation: 'AND',
                              operator: 'EQUALS',
                              value: 'ACTIVE',
                            },
                          ]
                          const objQueryToSearch = {
                            query: $event,
                            keys: ['name', 'code'],
                          }
                          await getHotelList(objApis.hotel.moduleApi, objApis.hotel.uriApi, objQueryToSearch, filter)
                        }"
                      />
                        <div v-if="hotelError" class="flex align-items-center text-sm">
                          <span style="color: red; margin-right: 3px; margin-left: 3px;">You must select the "Hotel"
                            field as required</span>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <div class="w-full lg:w-auto">
                      <DebouncedAutoCompleteComponent
                        id="autocomplete"
                        class="w-full"
                        field="name"
                        item-value="id"
                        :multiple="true"
                        :model="filterToSearch.status"
                        :suggestions="[...statusList]"
                        @change="($event) => {
                          if (!filterToSearch.status.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                            filterToSearch.status = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {
                            filterToSearch.status = $event.filter((element: any) => element?.id !== 'All')
                          }
                        }"
                        @load="async($event) => {
                          const filter: FilterCriteria[] = [
                            {
                              key: 'status',
                              logicalOperation: 'AND',
                              operator: 'EQUALS',
                              value: 'ACTIVE',
                            },
                          ]
                          const objQueryToSearch = {
                            query: $event,
                            keys: ['name', 'code'],
                          }
                          await getStatusList(objApis.status.moduleApi, objApis.status.uriApi, objQueryToSearch, filter)
                        }"
                      />
                    </div>
                  </div>
                </div>
              </div>
              <div class="flex flex-row h-fit">
                <div class="flex flex-column justify-content-around align-content-end align-items-end mr-1">
                  <label for="" class="font-bold">From</label>
                  <label for="" class="font-bold">To</label>
                </div>

                <div class="flex flex-column gap-2 ">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <div class="w-full lg:w-auto" style=" z-index:5 ">
                      <Calendar v-model="filterToSearch.from" date-format="yy-mm-dd" icon="pi pi-calendar-plus"
                        show-icon icon-display="input" style="max-width: 130px; max-height: 32px"
                        :disabled="disableDates" :max-date="dayjs(new Date()).endOf('month').toDate()" />
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <div class="w-full lg:w-auto">
                      <Calendar v-model="filterToSearch.to" date-format="yy-mm-dd" icon="pi pi-calendar-plus" show-icon
                        icon-display="input" style="max-width: 130px; max-height: 32px; overflow: auto;"
                        :invalid="filterToSearch.to < filterToSearch.from" :disabled="disableDates"
                        :max-date="dayjs(new Date()).endOf('month').toDate()" :min-date="filterToSearch.from" />
                      <span v-if="filterToSearch.to < filterToSearch.from" style="color: red; margin-left: 2px;">Check
                        date range</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="flex align-items-center gap-2 ">
                <Checkbox id="all-check-1" v-model="disableDates" :binary="true" style="z-index: 999;" />
                <label for="all-check-1" class="font-bold">All</label>
              </div>
              <div class="flex flex-row h-fit">
                <div class="flex flex-column justify-content-around align-content-end align-items-end mr-1">
                  <label for="" class="font-bold">Criteria:</label>
                  <label for="" class="font-bold">Search:</label>
                </div>

                <div class="flex flex-column gap-2 ">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <div class="w-full lg:w-auto" style=" z-index:5 ">
                      <Dropdown v-model="filterToSearch.criteria" show-clear option-label="name"
                        style="min-width: 140px;" :options="[...ENUM_INVOICE_CRITERIA]">
                        <template #option="slotProps">
                          <div class="flex align-items-center gap-2">
                            <span>{{ slotProps.option.name }}</span>
                          </div>
                        </template>
                      </Dropdown>
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <div class="w-full lg:w-auto mr-3">

                      <InputText v-model="filterToSearch.search" type="text" style="width: 140px;" />


                    </div>
                  </div>
                </div>
              </div>
              <div class="flex flex-row h-fit">
                <div class="flex flex-column justify-content-around align-content-start align-items-end mr-1">
                  <label for="" class="font-bold">Type: </label>
                  <div class="w-full lg:w-auto" />
                </div>

                <div class="flex flex-column gap-2 ">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <div class="w-full lg:w-auto" style=" z-index:5 ">
                      <DebouncedAutoCompleteComponent
                        id="autocomplete"
                        class="w-full"
                        field="name"
                        item-value="id"
                        :multiple="true"
                        :model="filterToSearch.invoiceType"
                        :suggestions="[...invoiceTypeList]"
                        @change="($event) => {
                          if (!filterToSearch.invoiceType.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                            filterToSearch.invoiceType = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {
                            filterToSearch.invoiceType = $event.filter((element: any) => element?.id !== 'All')
                          }
                        }"
                        @load="async($event) => {
                          const filter: FilterCriteria[] = [
                            {
                              key: 'status',
                              logicalOperation: 'AND',
                              operator: 'EQUALS',
                              value: 'ACTIVE',
                            },
                          ]
                          const objQueryToSearch = {
                            query: $event,
                            keys: ['name', 'code'],
                          }
                          await getInvoiceTypeList(objApis.invoiceType.moduleApi, objApis.invoiceType.uriApi, objQueryToSearch, filter)
                        }"
                      />
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2 w-full">
                    <TriStateCheckbox 
                      id="all-check-2" 
                      v-model="filterToSearch.includeInvoicePaid"
                    />
                    <!-- <label for="all-check-2" class="font-bold">Include Invoice Paid</label> -->
                    <label for="all-check-2" class="font-bold">{{checkboxLabel}}</label>
                  </div>
                  <div class="flex align-items-center gap-2" />
                </div>
              </div>
              <div class="flex align-items-center gap-2">
                <Button v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :disabled="disabledSearch"
                  :loading="loadingSearch" @click="searchAndFilter" />
                <Button v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash"
                  :loading="loadingSearch" @click="clearFilterToSearch" />
              </div>
              <!-- <div class="col-12 md:col-3 sm:mb-2 flex align-items-center">
            </div> -->
              <!-- <div class="col-12 md:col-5 flex justify-content-end">
            </div> -->
            </div>
          </AccordionTab>
        </Accordion>
      </div>
      <ExpandableTable 
        :data="listItems" 
        :columns="columns" 
        :options="options" 
        :pagination="pagination"
        @on-confirm-create="clearForm" 
        @open-edit-dialog="openEditDialog($event)"
        @on-change-pagination="payloadOnChangePage = $event" 
        @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems" 
        @on-sort-field="onSortField" 
        @update:double-clicked="getItemById"
        @on-expand-field="($event) => { expandedInvoice = $event }"
        @on-select-field="($event) => { attachmentInvoice = $event }" 
        @on-row-right-click="onRowRightClick">
        <template #column-invoiceDate="{ item: data }">
          {{ dayjs(data).format('DD-MM-YYYY') }}
        </template>
        <template #row-selector-body="props">
          <button v-if="props.item?.hasAttachments" class="pi pi-paperclip"
            style="background-color: white; border: 0; padding: 5px; border-radius: 100%;" @click="() => {
              attachmentInvoice = props.item
              attachmentDialogOpen = true

            }" />
        </template>

        <template #expanded-item="props">
          <InvoiceTabView 
            :invoice-obj-amount="0" 
            :selected-invoice="props.itemId" 
            :is-dialog-open="bookingDialogOpen"
            :close-dialog="() => { bookingDialogOpen = false }" 
            :open-dialog="handleDialogOpen" 
            :active="active"
            :set-active="($event) => { active = $event }" 
            :is-detail-view="true"  
          />
        </template>

        <!-- <template #column-status="props">
          <Badge :value="getStatusName(props.item)"
            :style="`background-color: ${getStatusBadgeBackgroundColor(props?.item)}`" />
        </template> -->

        <!-- Asi estaba antes -->
        <!-- <template #column-invoiceStatus="props">
          <Badge v-if="props.item" :value="getStatusName(props.item?.name?.toUpperCase())"
            :style="`background-color: ${getStatusBadgeBackgroundColor(props?.item?.name?.toUpperCase())}`" />
        </template> -->

        <template #column-invoiceStatus="props">
          <Badge v-if="props.item" :value="props.item?.name"
            :style="`background-color: ${getStatusBadgeBackgroundColorByItem(props?.item)}`" />
        </template>

        <template #datatable-footer>
          <ColumnGroup type="footer" class="flex align-items-center font-bold font-500" style="font-weight: 700">
            <Row>
              <Column footer="Totals:" :colspan="9" footer-style="text-align:right; font-weight: 700" />
              <Column :footer="formatNumber(Math.round((totalInvoiceAmount + Number.EPSILON) * 100) / 100)" footer-style="font-weight: 700" />
              <Column :footer="formatNumber(Math.round((totalDueAmount + Number.EPSILON) * 100) / 100)" footer-style="font-weight: 700" />
              <Column :colspan="9" />
            </Row>
          </ColumnGroup>
        </template>


      </ExpandableTable>
    </div>
    <ContextMenu ref="invoiceContextMenu" :model="invoiceContextMenuItems" >
      <template #itemicon="{ item }">
      <div v-if="item.iconSvg !== ''" class="w-2rem flex justify-content-center align-items-center">
        <svg xmlns="http://www.w3.org/2000/svg" :height="item.height" :viewBox="item.viewBox" :width="item.width" fill="#8d8faa">
          <path :d="item.iconSvg" />
        </svg>
      </div>
      <div v-else class="w-2rem flex justify-content-center align-items-center">
        <i v-if="item.icon" :class="item.icon" />
      </div>
    </template>
  </ContextMenu>
  </div>
  <div v-if="attachmentDialogOpen">
    <AttachmentDialogForManagerInvoice 
      :close-dialog="() => { attachmentDialogOpen = false, getList() }" 
      :is-creation-dialog="false"
      header="Manage Invoice Attachment" 
      :open-dialog="attachmentDialogOpen" 
      :selected-invoice="attachmentInvoice?.id"
      :selected-invoice-obj="attachmentInvoice"
      :disableDeleteBtn="true"
      :document-option-has-been-used="true"
    />
  </div>
  <div v-if="doubleFactorOpen">
    <Dialog v-model:visible="doubleFactorOpen" modal class="mx-3 sm:mx-0"
      content-class="border-round-bottom border-top-1 surface-border" :style="{ width: '25%' }" :pt="{
        root: {
          class: 'custom-dialog',
        },
        header: {
          style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
        },
     
      }"  @hide="doubleFactorOpen = false">
      <template #header>
        <div class="flex justify-content-between">
          <h5 v-if="selectedInvoiceObj.invoiceId"  class="m-0">
            Do you want to clone the invoice {{selectedInvoiceObj.invoiceId}}?
          </h5>
          <h5 v-else class="m-0">
            Do you want to clone this invoice?
          </h5>
        </div>
      </template>
      <template #default>
        <div class="p-2 pb-0">
          <div class="flex align-items-center justify-content-between mt-3 mb-2">
            <span class="font-bold text-2xl ml-5">{{ randomCode }}</span>
            <InputText v-model="entryCode" type="text" placeholder="Enter code" class="w-15rem h-3rem mr-2 ml-2" />
          </div>
          <div class="flex justify-content-end mb-0">
            <Button :disabled="entryCode !== randomCode" class="mr-2 p-button-primary h-2rem  w-3rem mt-3 "
              icon="pi pi-save" @click="handleApplyClick" />
            <Button class="mr-2  p-button-text p-button-gray h-2rem w-3rem mt-3 px-2" icon="pi pi-times ml-1 mr-1 "
            @click="doubleFactorOpen = false"  />

          </div>
        </div>
      </template>

    </Dialog>

  </div>
  <div v-if="doubleFactorTotalOpen">
    <Dialog v-model:visible="doubleFactorTotalOpen" :id-item="idItem" modal class="mx-3 sm:mx-0"
      content-class="border-round-bottom border-top-1 surface-border" :style="{ width: '30%' }" :pt="{
        root: {
          class: 'custom-dialog',
        },
        header: {
          style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
        },
      
      }" @hide="doubleFactorTotalOpen = false">
      <template #header>
        <div class="flex justify-content-between">
          <h5 v-if="selectedInvoiceObj.invoiceId"  class="m-0">
            Do you want to clone complete the invoice {{selectedInvoiceObj.invoiceId}}?
          </h5>
          <h5 v-else class="m-0">
            Do you want to clone complete this invoice?
          </h5>
        </div>
      </template>
      <template #default>
        <div class="p-2 pb-2">
          <div class="flex align-items-center justify-content-between mt-4 mb-0">
            <span class="font-bold text-2xl ml-5">{{ randomCode }}</span>
            <InputText v-model="entryCode" type="text" placeholder="Enter code" class="w-15rem h-3rem mr-2 ml-2" />
          </div>
          <div class="flex justify-content-end mb-0">
            <Button :disabled="entryCode !== randomCode" class="mr-2 p-button-primary h-2rem  w-3rem mt-3 "
              icon="pi pi-save" @click="handleTotalApplyClick" />
            <Button  class="mr-2  p-button-text p-button-gray h-2rem w-3rem mt-3 px-2"
              icon="pi pi-times ml-1 mr-1 "   @click="doubleFactorTotalOpen = false" />

          </div>
        </div>
      </template>

    </Dialog>

    
  </div>
  <div v-if="openDialog">
    <Dialog
      v-model:visible="openDialog" modal class="mx-3 sm:mx-0"
      content-class="border-round-bottom border-top-1 surface-border" :style="{ width: '26%' }" :pt="{
        root: {
          class: 'custom-dialog',
        },
        header: {
          style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
        },
  
      }" @hide="openDialog = false"
    >
      <template #header>
        <div class="flex justify-content-between">
          <h5 class="m-0">
            Do you want to undo import this booking: {{ selectedInvoiceObj.invoiceId }} ?
          </h5>
        </div>
      </template>
      <template #default>
        <div class="p-2 pb-0">
          <div class="flex align-items-center mt-3 mb-2">
            <div class="mr-2 px-3">
              <span class="font-bold text-2xl">{{ randomCode }}</span>
            </div>
            <div>
              <InputText
                id="entry-code" v-model="entryCode" type="text"
                placeholder="Enter code" class="w-15rem h-3rem"
              />
            </div>
          </div>
          <div class="flex justify-content-end mb-0">
            <Button
              :disabled="entryCode !== randomCode"
              class="mr-2 p-button-primary h-2rem  w-3rem mt-3 "
              icon="pi pi-save"
              :loading="loadingSaveAll"
              @click="applyUndo"
            />
  
            <Button
              v-tooltip.top="'Cancel'" severity="secondary" class="h-2rem w-3rem p-button mt-3 px-2" icon="pi pi-times"
              @click="handleClose"
            />
          </div>
        </div>
      </template>
    </Dialog>
  </div>
  <div v-if="attachmentHistoryDialogOpen">
    <InvoiceHistoryDialog 
      :selected-attachment="''" 
      :close-dialog="() => { attachmentHistoryDialogOpen = false }"
      :header="`Invoice Status History - Invoice: ${attachmentInvoice?.invoiceId}`" 
      :open-dialog="attachmentHistoryDialogOpen"
      :selected-invoice="attachmentInvoice?.id" 
      :selected-invoice-obj="attachmentInvoice" 
      :is-creation-dialog="false" 
      />
  </div>

  <div v-if="exportDialogOpen">
    <ExportDialog 
      :total="pagination.totalElements" 
      :close-dialog="() => { exportDialogOpen = false }"
      :open-dialog="exportDialogOpen" 
      :payload="payload"
    />
  </div>
  <div v-if="exportPdfDialogOpen">
    <ExportToPdfDialog 
      :close-dialog="() => { exportPdfDialogOpen = false }" 
      :open-dialog="exportPdfDialogOpen"
      :payload="payload" 
      :invoices="listItems" 
      :total-amount="totalInvoiceAmount"
      :total-due-amount="totalInvoiceAmount" />
  </div>
  <div v-if="exportAttachmentsDialogOpen">
    <ExportAttachmentsDialog 
      :close-dialog="() => { exportAttachmentsDialogOpen = false }"
      :open-dialog="exportAttachmentsDialogOpen" 
      :payload="payload" 
      :invoice="attachmentInvoice"
    />
  </div>
  <div v-if="changeAgencyDialogOpen">
    <InvoiceChangeAgencyDialog
      :open-dialog="changeAgencyDialogOpen"
      :selected-invoice="selectedInvoiceObj"
      @on-close-dialog="onCloseChangeAgencyDialog($event)"
    />
  </div>
  <div v-if="paymentsDialogOpen">
    <InvoicePaymentDetailsDialog
        :open-dialog="paymentsDialogOpen"
        :selected-invoice="selectedInvoiceObj"
        @on-close-dialog="() => { paymentsDialogOpen = false }"
    />
  </div>

  <Dialog
      v-model:visible="openDialogPrint"
      modal
      class="mx-3 sm:mx-0"
      content-class="border-round-bottom border-top-1 surface-border"
      :style="{ width: '60%' }"
      :pt="{
        root: {
          class: 'custom-dialog-history',
        },
        header: {
          style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
        },
        // mask: {
        //   style: 'backdrop-filter: blur(5px)',
        // },
      }"
      @hide="closeModalPrint()"
    >
      <template #header>
        <div class="flex justify-content-between">
          <h5 class="m-0">
            Invoice to Print
          </h5>
        </div>
      </template>

        <div class="p-fluid pt-3">
          <DynamicTable
            class="card p-0"
            :data="listPrintItems"
            :columns="columnstoPrint"
            :options="optionsToPrint"
            :pagination="paginationPrint"
            @on-change-pagination="payloadPrintOnChangePage = $event"
            @update:clicked-item="invoiceIdsListToPrint=$event"
            >
            <!-- @update:clicked-item="invoiceSelectedListForApplyPayment = $event" -->
            <template #column-status="{ data: item }">
              <Badge
                :value="getStatusName(item?.status)"
                :style="`background-color: ${getStatusBadgeBackgroundColor(item.status)}`"
              />
            </template>
          </DynamicTable>
        </div>
        <div class="flex justify-content-between">
          <div class="flex align-items-center">
            <div>
              <Checkbox
                id="invoiceAndBookings"
                v-model="invoiceAndBookings"
                :binary="true"
                disabled
                @update:model-value="($event) => {
                  // changeValueByCheckApplyPaymentBalance($event);
                }"
              />
              <label for="invoiceAndBookings" class="ml-2 font-bold">
                Invoice And Bookings
              </label>
            </div>
            <div class="mx-4">
              <Checkbox
                id="invoiceSupport"
                v-model="invoiceSupport"
                :binary="true"
                @update:model-value="($event) => {
                  // changeValueByCheckApplyPaymentBalance($event);
                }"
              />
              <label for="invoiceSupport" class="ml-2 font-bold">
                Invoice Supports
              </label>
            </div>
            <div>
              <Checkbox
                id="groupByClient"
                v-model="groupByClient"
                :binary="true"
                @update:model-value="($event) => {
                  // changeValueByCheckApplyPaymentBalance($event);
                }"
              />
              <label for="groupbyClient" class="ml-2 font-bold">
                Group By Client
              </label>
            </div>
          </div>
          <div>
            <Button
              v-tooltip.top="'Print'"
              class="w-3rem mx-1"
              icon="pi pi-print"
              :disabled="false"
              :loading="loadingSavePrint"
              @click="invoicePrint()"
            />
            <Button v-tooltip.top="'Cancel'" class="w-3rem" icon="pi pi-times" severity="secondary" @click="closeModalPrint()" />
          </div>
        </div>
    
    </Dialog>
</template>


<style scoped lang="sass">
.p-button-gray
  background-color: #f5f5f5 !important
  color: #666 !important
  border-color: #ddd !important

  &:hover
    background-color: #e6e6e6 !important
    border-color: #ccc !important
    color: #333 !important
</style>

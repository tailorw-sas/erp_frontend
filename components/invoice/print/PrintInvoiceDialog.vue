<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { jsPDF } from 'jspdf'
import autoTable from 'jspdf-autotable'
import dayjs from 'dayjs'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import type { GenericObject } from '~/types'
import type { InvoiceParams } from '@/utils/templates/printInvoice'
import { getPrintInvoiceTemplate } from '@/utils/templates/printInvoice'

const props = defineProps({

  openDialog: {
    type: Boolean,
    required: true
  },
  closeDialog: {
    type: Function as any,
    required: true
  },
  invoice: {
    type: Object as any,
    required: true
  },

})

const exportSummary = ref(false)
const invoiceAndBookings = ref(true)
const invoiceSupport = ref(true)

const loading = ref(false)

const filename = ref<string>()

const dialogVisible = ref(props.openDialog)

const options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
  loading: false,
  showDelete: false,
  showFilters: false,
  actionsAsMenu: false,
  showEdit: false,
  showAcctions: false,
  messageToDelete: 'Do you want to save the change?',
  showTitleBar: false
})

const confAgencyApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})

const confHotelApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

async function getAgencyById(id: string) {
  try {
    const response = await GenericService.getById(confAgencyApi.moduleApi, confAgencyApi.uriApi, id)
    return response
  }
  catch (error) {

  }
}
async function getHotelById(id: string) {
  try {
    const response = await GenericService.getById(confHotelApi.moduleApi, confHotelApi.uriApi, id)
    return response
  }
  catch (error) {

  }
}

async function getBookingList() {
  try {
    const Options = {
      tableName: 'Invoice',
      moduleApi: 'invoicing',
      uriApi: 'manage-booking',
    }
    const Payload = {
      filter: [{
        key: 'invoice.id',
        operator: 'EQUALS',
        value: props.invoice?.id,
        logicalOperation: 'AND'
      }],
      query: '',
      pageSize: 10,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.ASC
    }
    const Pagination = {
      page: 0,
      limit: 50,
      totalElements: 0,
      totalPages: 0,
      search: ''
    }

    let list: any[] = []

    const response = await GenericService.search(Options.moduleApi, Options.uriApi, Payload)

    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.page = page
    Pagination.limit = size
    Pagination.totalElements = totalElements
    Pagination.totalPages = totalPages

    for (const iterator of dataList) {
      list = [...list, {
        ...iterator,
        loadingEdit: false,
        loadingDelete: false,
        agency: iterator?.invoice?.agency,
        nights: dayjs(iterator?.checkOut).endOf('day').diff(dayjs(iterator?.checkIn).startOf('day'), 'day', false),
        fullName: `${iterator.firstName ? iterator.firstName : ''} ${iterator.lastName ? iterator.lastName : ''}`
      }]
    }

    return list
  }
  catch (error) {
    console.error(error)
  }
}

async function getPrintObj() {
  const obj: InvoiceParams = {
    bookings: [],
    companyName: '',
    invoiceId: '',
    companyAddress: '',
    companyCif: '',
    hotelName: '',
    hotelCode: '',
    invoiceNumber: props?.invoice?.invoiceNumber,
    agencyCode: '',
    agencyName: '',
    agencyAddress: '',
    agencyCif: '',
    agencyCityState: '',
    agencyCountry: '',
    invoiceDate: dayjs(props?.invoice?.invoiceDate).format('DD/MM/YYYY'),
  }

  if (props?.invoice?.invoiceNumber?.split('-')?.length === 3) {
    obj.invoiceNumber = `${props?.invoice?.invoiceNumber?.split('-')[0]}-${props?.invoice?.invoiceNumber?.split('-')[2]}`
  }
  else {
    obj.invoiceNumber = props?.invoice?.invoiceNumber
  }

  obj.invoiceId = props?.invoice?.invoiceId

  const agency = await getAgencyById(props?.invoice?.agency?.id)
  const hotel = await getHotelById(props?.invoice?.hotel?.id)
  const bookingList = await getBookingList()

  if (agency) {
    obj.agencyAddress = agency?.address || ''
    obj.agencyCif = agency?.cif || ''
    obj.agencyCityState = agency?.cityState?.name || ''
    obj.agencyCode = agency?.code || ''
    obj.agencyCountry = agency?.country?.name || ''
    obj.agencyName = agency?.name
  }

  if (hotel) {
    obj.companyName = hotel?.manageTradingCompanies?.company || ''
    obj.companyAddress = hotel?.manageTradingCompanies?.address || ''
    obj.companyCif = hotel?.manageTradingCompanies?.cif || ''
    obj.hotelName = hotel?.name || ''
    obj.hotelCode = hotel?.code
  }

  if (bookingList && bookingList?.length > 0) {
    obj.bookings = bookingList?.map((booking) => {
      return {
        fullName: booking?.fullName || '',
        description: booking?.description || '',
        voucher: booking?.couponNumber || '',
        adults: booking?.adults || '',
        children: booking?.children || '',
        from: dayjs(booking?.checkIn).format('DD/MM/YYYY') || '',
        to: dayjs(booking?.checkOut).format('DD/MM/YYYY') || '',
        quantity: booking?.nights || '',
        plan: booking?.ratePlan?.name || '',
        price: '',
        total: booking?.invoiceAmount
      }
    })
  }

  return obj
}

async function invoicePrint() {
  try {
    loading.value = true
    let nameOfPdf = ''
    const arrayInvoiceType: string[] = []
    if (invoiceSupport.value) { arrayInvoiceType.push('INVOICE_SUPPORT') }
    if (invoiceAndBookings.value) { arrayInvoiceType.push('INVOICE_AND_BOOKING') }

    const payloadTemp = {
      invoiceId: [props.invoice.id],
      invoiceType: arrayInvoiceType
    }
    // En caso de que solo este marcado el paymentAndDetails
    nameOfPdf = `${props.invoice?.hotel?.code}-${props.invoice?.invoiceId}.pdf`

    const response: any = await GenericService.create('invoicing', 'manage-invoice/report', payloadTemp)

    const url = window.URL.createObjectURL(new Blob([response]))
    const a = document.createElement('a')
    a.href = url
    a.download = nameOfPdf // Nombre del archivo que se descargará
    document.body.appendChild(a)
    a.click()
    window.URL.revokeObjectURL(url)
    document.body.removeChild(a)
    loading.value = false
  }
  catch (error) {
    loading.value = false
  }
  finally {
    loading.value = false
    dialogVisible.value = false
  }
}

async function handleDownload() {
  loading.value = true

  try {
    console.log(props?.invoice)

    const obj = await getPrintObj()

    const template = getPrintInvoiceTemplate(obj)

    const elementHTML = document.createElement('div')
    elementHTML.innerHTML = template

    const doc = new jsPDF('p', 'pt', 'a2')

    doc.html(elementHTML, {
      callback(doc) {
        // Guardamos el PDF
        doc.save(`${obj?.hotelCode}-${obj?.invoiceId}.pdf`)
      },
      x: 10,
      y: 10,
      autoPaging: 'text'

    })

    // Genera el PDF y lo descarga automáticamente

    filename.value = ''
    props.closeDialog()
  }
  catch (error) {
    console.log(error)
  }
  finally {
    loading.value = false
  }
}
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal header="Invoice to print" class="p-4 h-fit w-fit"
    content-class="border-round-bottom border-top-1 surface-border h-fit" :block-scroll="true" style="width: 800px;"
    @hide="closeDialog"
  >
    <div class=" h-fit overflow-hidden mt-4">
      <div class="flex gap-2 flex-column align-items-start h-fit ">
        <div class="flex flex-column gap-5" />
        <div class="flex  h-fit flex-column gap-4 align-items-start mb-4">
          <div class="flex flex-row gap-4">
            <div class="flex align-items-center gap-2">
              <Checkbox id="all-check-1" v-model="invoiceAndBookings" disabled :binary="true" style="z-index: 999;" />
              <span>Invoice And Bookings</span>
            </div>

            <div class="flex align-items-center gap-2">
              <Checkbox id="all-check-1" v-model="invoiceSupport" :binary="true" style="z-index: 999;" />
              <span>Invoice Support</span>
            </div>
          </div>
        </div>
      </div>
      <div class=" flex w-full justify-content-end ">
        <Button
          v-tooltip.top="'Save'" class="w-3rem mx-1" icon="pi pi-save" :loading="loading"
          @click="() => {
            invoicePrint()
            // handleDownload()
          }"
        />
        <Button
          v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem mx-1" icon="pi pi-times" @click="() => {

            closeDialog()
          }"
        />
      </div>
    </div>
  </Dialog>
</template>

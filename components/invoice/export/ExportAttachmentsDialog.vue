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
    type: Object,
    required: true
  }

})

const Pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const PayloadOnChangePage = ref<PageState>()

const Payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})

const ListItems = ref<any[]>([])

const exportSummary = ref(false)
const invoiceAndBookings = ref(true)
const invoiceSupport = ref(true)

const loading = ref(false)

const filename = ref<string>()

const dialogVisible = ref(props.openDialog)

const options = ref({
  tableName: 'Invoice',
  moduleApi: 'invoicing',
  uriApi: 'manage-attachment',
  loading: false,
  showDelete: false,
  showFilters: false,
  actionsAsMenu: false,
  showEdit: false,
  showAcctions: false,
  messageToDelete: 'Do you want to save the change?',
  showTitleBar: false
})

async function paymentPrint() {
  try {
    loading.value = true
    let nameOfPdf = ''
    const payloadTemp = {
      invoiceId: props.invoice.id || '',
      invoiceType: invoiceSupport.value ? 'INVOICE_SUPPORT' : 'INVOICE_AND_BOOKING',
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
    loading.value = false
  }
  catch (error) {
    loading.value = false
  }
  finally {
    loading.value = false
    dialogVisible.value = false
  }

  // generateStyledPDF()
}

async function getList() {
  try {
    options.value.loading = true
    ListItems.value = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, Payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    Pagination.value.page = page
    Pagination.value.limit = size
    Pagination.value.totalElements = totalElements
    Pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      ListItems.value = [...ListItems.value, { ...iterator, loadingEdit: false, loadingDelete: false }]
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}

async function handleDownload() {
  loading.value = true

  try {
    await getList()

    const files = ListItems.value.map(attachment => [attachment.filename, attachment.file])

    downloadFiles(files)
  }
  catch (error) {
    console.log(error)
  }
  finally {
    loading.value = false
  }
}
function downloadFiles(files: any[]) {
  for (let i = 0; i < files?.length; i++) {
    const file = files[i]
    const link = document.createElement('a')
    link.href = file[1]
    link.download = file[0]
    link.target = '_blank'

    document.body.appendChild(link)

    link.click()
    link.remove()
  }
}

onMounted(() => {
  if (props.invoice) {
    Payload.value.filter = [{
      key: 'invoice.id',
      operator: 'EQUALS',
      value: props.invoice.id,
      logicalOperation: 'AND'
    }]
  }
})
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
            paymentPrint()
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

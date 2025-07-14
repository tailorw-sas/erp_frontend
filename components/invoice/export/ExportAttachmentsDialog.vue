<script setup lang="ts">
import type { IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import { GenericService } from '~/services/generic-services'

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

const Payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'createdAt',
  sortType: ENUM_SHORT_TYPE.DESC
})

const invoiceAndBookings = ref(true)
const invoiceSupport = ref(true)
const loading = ref(false)
const dialogVisible = ref(props.openDialog)

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

    // nameOfPdf = invoiceSupport.value ? `invoice-support-${dayjs().format('YYYY-MM-DD')}.pdf` : `invoice-and-bookings-${dayjs().format('YYYY-MM-DD')}.pdf`
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
  catch {
    loading.value = false
  }
  finally {
    loading.value = false
    dialogVisible.value = false
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
    v-model:visible="dialogVisible" modal header="Invoice To Print" class="p-4"
    content-class="border-round-bottom border-top-1 surface-border h-fit" :block-scroll="true" style="width: 500px;"
    @hide="closeDialog"
  >
    <template #header>
      <div class="flex align-items-center justify-content-between w-full">
        <div class="flex align-items-center">
          <h5 class="m-0">
            Invoice To Print
          </h5>
        </div>
        <div class="flex align-items-center">
          <h5 class="m-0 mr-4">
            Invoice: {{ props.invoice.invoiceId }}
          </h5>
        </div>
      </div>
    </template>
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
          v-if="false"
          v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem mx-1" icon="pi pi-times" @click="() => {

            closeDialog()
          }"
        />
      </div>
    </div>
  </Dialog>
</template>

<script setup lang="ts">
import { useToast } from 'primevue/usetoast'
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
  invoices: {
    type: Array as any,
    required: true
  },
  totalAmount: {
    type: Number,
    required: true
  },
  totalDueAmount: {
    type: Number,
    required: true
  },

  payload: Object as any

})

const invoiceAndBookings = ref(true)
const invoiceSupport = ref(true)

const loading = ref(false)
const filename = ref<string>()
const dialogVisible = ref(props.openDialog)

async function invoicePrint() {
  try {
    loading.value = true
    let nameOfPdf = ''
    const arrayInvoiceType: string[] = []
    if (invoiceSupport.value) { arrayInvoiceType.push('INVOICE_SUPPORT') }
    if (invoiceAndBookings.value) { arrayInvoiceType.push('INVOICE_AND_BOOKING') }

    const payloadTemp = {
      invoiceId: props.invoices.map((item: any) => item.id),
      invoiceType: arrayInvoiceType
    }
    // En caso de que solo este marcado el paymentAndDetails

    // nameOfPdf = invoiceSupport.value ? `invoice-support-${dayjs().format('YYYY-MM-DD')}.pdf` : `invoice-and-bookings-${dayjs().format('YYYY-MM-DD')}.pdf`
    nameOfPdf = `${filename.value || 'Invoice'}.pdf`
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

          <div class="flex flex-column gap-2 align-items-start w-full">
            <span>Filename</span>
            <IconField icon-position="right" style="width: 100%;">
              <InputText v-model="filename" type="text" class="w-full" />
              <InputIcon class="pi pi-file" />
            </IconField>
          </div>
        </div>
      </div>
      <div class=" flex w-full justify-content-end ">
        <Button
          v-tooltip.top="'Save'"
          class="w-3rem mx-1" icon="pi pi-save"
          :loading="loading"
          :disabled="!filename"
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

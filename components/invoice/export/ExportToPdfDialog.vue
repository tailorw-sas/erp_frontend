<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'
import {jsPDF } from 'jspdf';
import autoTable from 'jspdf-autotable';
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
  invoices: {
    type: Array as any,
    required: true
  } ,
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


const exportSummary = ref(false)
const invoiceAndBookings = ref(true)
const invoiceSupport = ref(false)

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



async function handleDownload() {
  loading.value = true

  try {
    
  
 // Crea una nueva instancia de jsPDF
 const doc = new jsPDF('landscape');


// Configura las opciones para autoTable
const options: any = {
  head: [['ID', 'Hotel', 'Agency', 'Inv. No', 'Gen. Date', 'Manual', 'Amount', 'Invoice Balance', 'Status']],
  body: props.invoices.map((item: any) => [item.invoiceId, item.hotel.code + "-" + item.hotel.name,`${item.agency.code}-${item.agency.name}`, item.invoiceNumber , item.invoiceDate, String(item.isManual), item?.invoiceAmount,  item?.invoiceAmount, item?.status]),
  foot: [["","","","","","Totals:", props.totalAmount, props.totalDueAmount, ""]],
  styles: {
    cellWidth: 30
  },
  

  didDrawPage: function(data: any) {
    // Agrega encabezados al documento
    doc.setFontSize(10);
    doc.text('Invoice Management', data.settings.margin.left, 8);
  },
};

// Usa autoTable para agregar la tabla al documento
autoTable(doc, {...options});

// Genera el PDF y lo descarga automáticamente
doc.save(`${filename.value || "Invoice"}.pdf`);
  filename.value = ""
  props.closeDialog()

} catch (error) {
  console.log(error);
    
  }finally{
    loading.value = false
  }
   
}


</script>

<template>
  <Dialog v-model:visible="dialogVisible" modal header="Invoice to print" class="p-4 h-fit w-fit"
    content-class="border-round-bottom border-top-1 surface-border h-fit" :block-scroll="true" style="width: 800px;"
    @hide="closeDialog">
    <div class=" h-fit overflow-hidden mt-4">

      
      
        <div class="flex gap-2 flex-column align-items-start h-fit ">

          <div class="flex flex-column gap-5">

            
          </div>
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
                <InputText type="text" v-model="filename" class="w-full" />
                <InputIcon class="pi pi-file" />
              </IconField>
            </div>

          </div>

         
        </div>
        <div class=" flex w-full justify-content-end ">

          <Button v-tooltip.top="'Save'" class="w-3rem mx-1" icon="pi pi-save" :loading="loading" :disabled="!filename" @click="() => { handleDownload() }" />
          <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem mx-1" icon="pi pi-times" @click="() => {


  closeDialog()
}" />
        </div>
      </div>
    
  </Dialog>
</template>

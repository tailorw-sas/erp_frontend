<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
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

  total: {
    type: Number,
    required: true
  },

  payload: Object as any

})

const exportSummary = ref(true)

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
    const response = await GenericService.export(options.value.moduleApi, options.value.uriApi, { ...props.payload, pageSize: props.total || 50 })

    const url = window.URL.createObjectURL(response)

    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${filename.value}.xlsx`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)

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
    v-model:visible="dialogVisible" modal header="Excel setting" class="p-4 h-fit w-fit"
    content-class="border-round-bottom border-top-1 surface-border h-fit" :block-scroll="true" style="width: 800px;"
    @hide="closeDialog"
  >
    <div class=" h-fit overflow-hidden mt-4">
      <div class="flex gap-2 flex-row align-items-start h-fit ">
        <div class="flex flex-column gap-5">
          <span>Export Summary</span>
          <span>Filename</span>
        </div>
        <div class="flex  h-fit flex-column gap-4 align-items-start mb-4">
          <div class="flex align-items-center gap-2 ">
            <Checkbox id="all-check-1" v-model="exportSummary" :binary="true" style="z-index: 999;" />
          </div>

          <div class="flex gap-2 align-items-center">
            <IconField icon-position="right" style="width: 100%;">
              <InputText v-model="filename" type="text" />
              <InputIcon class="pi pi-file" />
            </IconField>
          </div>
        </div>
      </div>
      <div class=" flex w-full justify-content-end ">
        <Button v-tooltip.top="'Save'" class="w-3rem mx-1" icon="pi pi-save" :loading="loading" :disabled="!filename" @click="() => { handleDownload() }" />
        <Button
          v-if="false" v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem mx-1" icon="pi pi-times" @click="() => {

            closeDialog()
          }"
        />
      </div>
    </div>
  </Dialog>
</template>

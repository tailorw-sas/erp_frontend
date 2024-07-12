<script setup lang="ts">
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'

const props = defineProps({
  fields: {
    type: Array<Container>,
    required: true,

  },
  header: {
    type: String,
    required: true
  },
  class: {
    type: String,
    required: false,
  },
  contentClass: {
    type: String,
    required: false,
  },
  openDialog: {
    type: Boolean,
    required: true
  },
  formReload: {
    type: Number,
    required: true
  },
  item: {
    type: Object
  },
  loadingSaveAll: { type: Boolean, required: true },
  clearForm: {
    required: true,
    type: Function as any
  },
  requireConfirmationToDelete: {
    required: true,
    type: Function as any
  },
  requireConfirmationToSave: {
    required: true,
    type: Function as any
  },
  closeDialog: {
    type: Function as any,
    required: true
  },
  containerClass: {
    type: String,
    required: false
  },
  bookingList: {
    type: Array,
    required: true
  }
})
const dialogVisible = ref(props.openDialog)
const formFields = ref<FieldDefinitionType[]>([])

onMounted(() => {
  props?.fields.forEach((container) => {
    formFields.value.push(...container.childs)
  })
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal :header="header" :class="props.class || 'p-4 h-fit'"
    :content-class="contentClass || 'border-round-bottom border-top-1 surface-border h-fit'" :block-scroll="true"
    @hide="closeDialog"
  >
    <div class="w-full h-full overflow-hidden p-2">
      <EditFormV2WithContainer
        :key="formReload" :fields-with-containers="fields" :item="item" :show-actions="true"
        :loading-save="loadingSaveAll" :container-class="containerClass" class="w-full h-fit m-4"
        @cancel="clearForm" @delete="requireConfirmationToDelete($event)" @submit="requireConfirmationToSave($event)"
      >
        <template #field-invoiceAmount="{ onUpdate, item: data }">
          <InputText
            v-model="data.invoiceAmount"
            show-clear :disabled="!!item?.id"
            @update:model-value="onUpdate('invoiceAmount', $event)"
          />
        </template>
        <template #field-hotelAmount="{ onUpdate, item: data }">
          <InputText
            v-model="data.hotelAmount"
            show-clear :disabled="!!item?.id"
            @update:model-value="onUpdate('hotelAmount', $event)"
          />
        </template>
        <template #form-footer="props">
          <Button
            v-tooltip.top="'Save'" class="w-3rem mx-2 sticky" icon="pi pi-save"
            @click="props.item.submitForm($event)"
          />
          <Button v-tooltip.top="'Cancel'" severity="danger" class="w-3rem mx-1" icon="pi pi-times" @click="closeDialog" />
        </template>
      </EditFormV2WithContainer>
    </div>
  </Dialog>
</template>

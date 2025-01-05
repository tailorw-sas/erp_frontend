<script setup lang="ts">
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'
import dayjs from 'dayjs'

const props = defineProps({
  fields: {
    type: Array<FieldDefinitionType>,
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



</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal :header="header" :class="props.class || 'p-4 h-fit'"
    :content-class="contentClass || 'border-round-bottom border-top-1 surface-border h-fit'" :block-scroll="true"
    @hide="closeDialog" style="width: 600px;"
  > 
    <div class="w-full h-full overflow-hidden p-2">
      <EditFormV2
        :key="formReload" :fields="fields" :item="item" :show-actions="true"
        :loading-save="loadingSaveAll" :container-class="containerClass" 
        @cancel="clearForm" @delete="requireConfirmationToDelete($event)" @submit="requireConfirmationToSave($event)"
      >
        <template #field-invoiceAmount="{ onUpdate, item: data }">
          <InputText
            v-model="data.invoiceAmount"
            show-clear :disabled="!!item?.id"
            @update:model-value="onUpdate('invoiceAmount', $event)"
          />
        </template>

        <template #field-checkIn="{ item: data, onUpdate }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.checkIn"
            date-format="yy-mm-dd"
            :max-date="data.checkOut ? new Date(data.checkOut) : undefined"
            @update:model-value="($event) => {

              onUpdate('checkIn', dayjs($event).startOf('day').toDate())
            }"
          />
        </template>
        <template #field-checkOut="{ item: data, onUpdate }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.checkOut"
            date-format="yy-mm-dd"
            :min-date="data?.checkIn ? new Date(data?.checkIn) : new Date()"
            @update:model-value="($event) => {
              
              onUpdate('checkOut',   dayjs($event).startOf('day').toDate())
            }"
          />
        </template>
        <template #field-hotelAmount="{ onUpdate, item: data }">
          <InputText
            v-model="data.hotelAmount"
            show-clear 
            @update:model-value="onUpdate('hotelAmount', $event)"
          />
        </template>
        <template #form-footer="props">
          <Button
            v-tooltip.top="'Save'" label="Save" class="w-6rem mx-2 sticky" icon="pi pi-save"
            @click="props.item.submitForm($event)"
          />
          <Button v-tooltip.top="'Cancel'" label="Save" severity="secondary" class="w-6rem mx-1" icon="pi pi-times" @click="closeDialog" />
        </template>
      </EditFormV2>
    </div>
  </Dialog>
</template>

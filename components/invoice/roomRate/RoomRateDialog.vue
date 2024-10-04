<script setup lang="ts">
import dayjs from 'dayjs'
import { z } from 'zod'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'

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

const validationSchema = z.object({
  invoiceAmount: z.string()
    .min(1, 'The Rate amount field is required')
    .refine(val => +val > +0, 'The Rate amount field must be greater than 0')
    .refine(val => +val > +5, 'The Rate amount field must be greater than 5'),

  hotelAmount: z.string()
    .refine((val, ctx) => {
      const invoiceAmount = +ctx.parent.invoiceAmount
      return invoiceAmount > 0 ? +val >= 0 : true
    }, 'The Hotel Amount field cannot be negative when the Rate amount is greater than 0')
    .refine((val, ctx) => {
      const adults = ctx.parent.adults
      return adults > 2 ? +val >= 100 : true
    }, 'The Hotel Amount must be at least 100 when there are more than 2 adults')
    .nullable(),

  adults: z.number().min(1, 'The Adults field must be greater than 0'),
  checkIn: z.date({ required_error: 'The Check In field is required' }),
  checkOut: z.date({ required_error: 'The Check Out field is required' })
})
const errorsListParent = reactive<{ [key: string]: string[] }>({})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal :header="header" :class="props.class || 'p-4 h-fit'"
    :content-class="contentClass || 'border-round-bottom border-top-1 surface-border h-fit'" :block-scroll="true"
    style="width: 600px;" @hide="closeDialog"
  >
    <div class="w-full h-full overflow-hidden p-2 mt-3">
      <EditFormV2
        :key="formReload"
        :fields="fields"
        :item="item"
        :error-list="errorsListParent"
        :show-actions="true"
        :loading-save="loadingSaveAll"
        :container-class="containerClass"
        @cancel="clearForm"
        @delete="requireConfirmationToDelete($event)"
        @submit="requireConfirmationToSave($event)"
        @update:errors-list="errorsListParent = $event"
      >
        <template #field-invoiceAmount="{ onUpdate, item: data }">
          <InputText
            v-model="data.invoiceAmount"
            show-clear :disabled="!!item?.id"
            @update:model-value="($event) => {
              onUpdate('invoiceAmount', $event)
            }"
          />
        </template>

        <template #field-adults="{ onUpdate, item: data }">
          <InputText
            v-model="data.adults"
            show-clear
            @update:model-value="($event) => {
              onUpdate('adults', $event)
              if ($event && +$event > 0) {
                delete errorsListParent.children
              }

              if (data?.children && data?.children > 0) {
                const decimalSchema = z.object(
                  {
                    adults: z
                      .string()
                      .refine(value => !Number.isNaN(value) && +value >= 0, 'The adults field must be greater than 0').nullable(),
                  },
                )
                updateFieldProperty(props.fields, 'adults', 'validation', decimalSchema.shape.adults)
              }
              else {
                const decimalSchema = z.object(
                  {
                    adults: z
                      .string()
                      .refine(value => !Number.isNaN(value) && +value > 0, 'The adults field must be greater than 0').nullable(),
                  },
                )
                updateFieldProperty(props.fields, 'adults', 'validation', decimalSchema.shape.adults)
              }
            }"
          />
        </template>

        <template #field-children="{ onUpdate, item: data }">
          <InputText
            v-model="data.children"
            show-clear
            @update:model-value="($event) => {
              onUpdate('children', $event)

              if ($event && +$event > 0) {
                delete errorsListParent.adults
              }

              if (data?.adults && data?.adults > 0) {
                const decimalSchema = z.object(
                  {
                    children: z
                      .string()
                      .refine(value => +value >= 0, 'The children field must be greater than 0').nullable(),
                  },
                )
                updateFieldProperty(props.fields, 'children', 'validation', decimalSchema.shape.children)
              }
              else {
                const decimalSchema = z.object(
                  {
                    children: z
                      .string()
                      .refine(value => +value > 0, 'The children field must be greater than 0').nullable(),
                  },
                )
                updateFieldProperty(props.fields, 'children', 'validation', decimalSchema.shape.children)
              }
            }"
          />
        </template>

        <template #field-hotelAmount="{ onUpdate, item: data }">
          <InputText
            v-model="data.hotelAmount"
            show-clear
            @update:model-value="($event) => {
              onUpdate('hotelAmount', $event)

              if (data?.invoiceAmount && data?.invoiceAmount > 0) {
                const decimalSchema = z.object(
                  {
                    hotelAmount: z
                      .string()
                      .refine(value => +value >= 0, 'The Hotel Amount field cannot be negative').nullable(),
                  },
                )
                updateFieldProperty(props.fields, 'hotelAmount', 'validation', decimalSchema.shape.hotelAmount)
              }
              else {
                const decimalSchema = z.object(
                  {
                    hotelAmount: z
                      .string()
                      .refine(value => +value > 0, 'The Hotel Amount field must be greater than 0').nullable(),
                  },
                )
                updateFieldProperty(props.fields, 'hotelAmount', 'validation', decimalSchema.shape.hotelAmount)
              }
            }"
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

              onUpdate('checkOut', dayjs($event).startOf('day').toDate())
            }"
          />
        </template>
        <template #form-footer="props">
          <Button
            v-tooltip.top="'Save'" label="Save" class="w-6rem mx-2 sticky" icon="pi pi-save"
            @click="props.item.submitForm($event)"
          />
          <Button v-tooltip.top="'Cancel'" label="Cancel" severity="secondary" class="w-6rem mx-1" icon="pi pi-times" @click="closeDialog" />
        </template>
      </EditFormV2>
    </div>
  </Dialog>
</template>

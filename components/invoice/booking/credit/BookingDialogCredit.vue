<script setup lang="ts">
import dayjs from 'dayjs'
import type { Container, FieldDefinitionType } from '~/components/form/EditFormV2WithContainer'

const props = defineProps({
  fields: {
    type: Array<Container | FieldDefinitionType>,
    required: true,

  },
  fieldsv2: {
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
  roomCategoryList: {
    type: Array,
    required: true
  },
  roomTypeList: {
    type: Array,
    required: true
  },
  nightTypeList: {
    type: Array,
    required: true
  },
  ratePlanList: {
    type: Array,
    required: true
  },
  getRoomCategoryList: {
    type: Function,
    required: true
  },
  getNightTypeList: {
    type: Function,
    required: true
  },
  getRoomTypeList: {
    type: Function,
    required: true
  },
  getratePlanList: {
    type: Function,
    required: true
  },
  couponNumberValidation: {
    type: String,
    required: false
  },
  isNightTypeRequired: {
    type: Boolean,
    required: false
  },
  invoiceObj: Object as any

})

const route = useRoute()

const dialogVisible = ref(props.openDialog)
const formFields = ref<FieldDefinitionType[]>([])

const couponValidation = ref<string>('')

const couponValid = ref(true)

watch(() => props.couponNumberValidation, () => {
  if (props.couponNumberValidation) {
    couponValidation.value = props.couponNumberValidation
  }
})

onMounted(() => {
  props?.fields.forEach((container) => {
    formFields.value.push(...container.childs)
  })

  if (props.couponNumberValidation) {
    couponValidation.value = props.couponNumberValidation
  }
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible" modal :header="header" :class="props.class || 'p-4 h-fit'"
    :content-class="contentClass || 'border-round-bottom border-top-1 surface-border h-fit'" :block-scroll="true"
    style="width: 900px;" @hide="closeDialog"
  >
    <div class=" h-full overflow-hidden p-2">
      <EditFormV2
        v-if="true"
        :key="formReload"
        :fields="fieldsv2"
        :item="item"
        :show-actions="true"
        :loading-save="loadingSaveAll"
        container-class="grid pt-5"
        @cancel="clearForm"
        @delete="requireConfirmationToDelete($event)"
        @submit="requireConfirmationToSave($event)"
      >
        <template #field-hotelCreationDate="{ item: data, onUpdate }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.hotelCreationDate"
            date-format="yy-mm-dd"
            :max-date="new Date()"
            @update:model-value="($event) => {
              onUpdate('hotelCreationDate', $event)
            }"
          />
        </template>
        <template #field-invoiceAmount="{ onUpdate, item: data }">
          <InputText
            v-model="data.invoiceAmount"
            show-clear
            @update:model-value="($event) => {
              let value: any = $event
              value = toNegative(value)
              onUpdate('invoiceAmount', String(value))
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
        <template #field-checkIn="{ item: data, onUpdate }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.checkIn"
            date-format="yy-mm-dd"
            :max-date="data.checkOut ? new Date(data.checkOut) : undefined"
            @update:model-value="($event) => {
              onUpdate('checkIn', $event)
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
              onUpdate('checkOut', $event)
            }"
          />
        </template>
        <template #field-ratePlan="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll" id="autocomplete" field="name" item-value="id"
            :model="data.ratePlan" :suggestions="ratePlanList" @change="($event) => {
              onUpdate('ratePlan', $event)
            }" @load="($event) => getratePlanList($event)"
          >
            <template #option="props">
              <span>{{ props.item.code }} - {{ props.item.name }}</span>
            </template>
          </DebouncedAutoCompleteComponent>
        </template>
        <template #field-roomCategory="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll" id="autocomplete" field="name" item-value="id"
            :model="data.roomCategory" :suggestions="roomCategoryList" @change="($event) => {
              onUpdate('roomCategory', $event)
            }" @load="($event) => getRoomCategoryList($event)"
          >
            <template #option="props">
              <span>{{ props.item.code }} - {{ props.item.name }}</span>
            </template>
          </DebouncedAutoCompleteComponent>
        </template>
        <template #field-bookingDate="{ item: data, onUpdate }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.bookingDate"
            date-format="yy-mm-dd"
            :max-date="new Date()"
            @update:model-value="($event) => {
              onUpdate('bookingDate', $event)
            }"
          />
        </template>
        <template #field-roomType="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll" id="autocomplete" field="name" item-value="id"
            :model="data.roomType" :suggestions="roomTypeList" @change="($event) => {
              onUpdate('roomType', $event)
            }" @load="($event) => getRoomTypeList($event)"
          >
            <template #option="props">
              <span>{{ props.item.code }} - {{ props.item.name }}</span>
            </template>
          </DebouncedAutoCompleteComponent>
        </template>
        <template #header-nightType="{ field }">
          <strong>
            {{ typeof field.header === 'function' ? field.header() : field.header }}
          </strong>
          <span v-if="isNightTypeRequired" class="p-error">*</span>
        </template>
        <template #field-nightType="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll" id="autocomplete" field="name" item-value="id"
            :model="data.nightType" :suggestions="nightTypeList" @change="($event) => {
              onUpdate('nightType', $event)
            }" @load="($event) => getNightTypeList($event)"
          >
            <template #option="props">
              <span>{{ props.item.code }} - {{ props.item.name }}</span>
            </template>
          </DebouncedAutoCompleteComponent>
        </template>

        <template #form-footer="props">
          <Button
            v-tooltip.top="'Save'" class="w-3rem mx-2 sticky" icon="pi pi-save"
            @click="props.item.submitForm($event)"
          />
          <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem mx-1" icon="pi pi-times" @click="closeDialog" />
        </template>
      </EditFormV2>

      <EditFormV2WithContainer
        v-if="false"
        :key="formReload"
        :fields-with-containers="fields"
        :item="item"
        :show-actions="true"
        :loading-save="loadingSaveAll"
        :container-class="containerClass"
        class="w-full h-fit m-4"
        @cancel="clearForm"
        @delete="requireConfirmationToDelete($event)"
        @submit="requireConfirmationToSave($event)"
      >
        <template #field-couponNumber="{ item: data, errors, onUpdate }">
          <InputText
            v-model="data.couponNumber"
            show-clear
            @update:model-value="($event) => {

              // if (couponValidation && $event) {
              //   const regex = new RegExp(couponValidation)

              //   console.log('REGEX', regex)

              //   couponValid = regex.test($event)

              //   console.log('couponValid', couponValid)

              // }

              onUpdate('couponNumber', $event)
            }"
          />
        </template>
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
        <template #field-bookingDate="{ item: data, onUpdate }">
          <Calendar
            v-if="!loadingSaveAll"
            v-model="data.bookingDate"
            date-format="yy-mm-dd"
            :max-date="new Date()"
            @update:model-value="($event) => {
              onUpdate('bookingDate', $event)
            }"
          />
        </template>
        <template #field-ratePlan="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll" id="autocomplete" field="name" item-value="id"
            :model="data.ratePlan" :suggestions="ratePlanList" @change="($event) => {
              onUpdate('ratePlan', $event)
            }" @load="($event) => getratePlanList($event)"
          >
            <template #option="props">
              <span>{{ props.item.code }} - {{ props.item.name }}</span>
            </template>
          </DebouncedAutoCompleteComponent>
        </template>

        <template #field-roomCategory="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll" id="autocomplete" field="name" item-value="id"
            :model="data.roomCategory" :suggestions="roomCategoryList" @change="($event) => {
              onUpdate('roomCategory', $event)
            }" @load="($event) => getRoomCategoryList($event)"
          >
            <template #option="props">
              <span>{{ props.item.code }} - {{ props.item.name }}</span>
            </template>
          </DebouncedAutoCompleteComponent>
        </template>

        <template #field-roomType="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll" id="autocomplete" field="name" item-value="id"
            :model="data.roomType" :suggestions="roomTypeList" @change="($event) => {
              onUpdate('roomType', $event)
            }" @load="($event) => getRoomTypeList($event)"
          >
            <template #option="props">
              <span>{{ props.item.code }} - {{ props.item.name }}</span>
            </template>
          </DebouncedAutoCompleteComponent>
        </template>

        <template #header-nightType="{ field }">
          <strong>
            {{ typeof field.header === 'function' ? field.header() : field.header }}
          </strong>
          <span v-if="isNightTypeRequired" class="p-error">*</span>
        </template>

        <template #field-nightType="{ item: data, onUpdate }">
          <DebouncedAutoCompleteComponent
            v-if="!loadingSaveAll" id="autocomplete" field="name" item-value="id"
            :model="data.nightType" :suggestions="nightTypeList" @change="($event) => {
              onUpdate('nightType', $event)
            }" @load="($event) => getNightTypeList($event)"
          >
            <template #option="props">
              <span>{{ props.item.code }} - {{ props.item.name }}</span>
            </template>
          </DebouncedAutoCompleteComponent>
        </template>

        <template #form-footer="props">
          <Button
            v-tooltip.top="'Save'" class="w-3rem mx-2 sticky" icon="pi pi-save"
            @click="props.item.submitForm($event)"
          />
          <Button
            v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem mx-1" icon="pi pi-times" @click="() => {

              clearForm()
              closeDialog()
            }"
          />
        </template>
      </EditFormV2WithContainer>
    </div>
  </Dialog>
</template>

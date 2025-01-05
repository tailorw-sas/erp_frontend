<script setup lang="ts">
import { ref } from 'vue'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import { z } from 'zod'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import getUrlByImage from '~/composables/files'

const toast = useToast()
const { data } = useAuth()
const userId = (data.value as any).user.userId
const loadingSaveAll = ref(false)

const confirm = useConfirm()
const formReload = ref(0)

const confApi = reactive({
  moduleApi: 'identity',
  uriApi: 'users',
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'image',
    header: '',
    dataType: 'image',
    disabled: false,
    class: 'field col-12',
    headerClass: 'mb-1',
  },
  {
    field: 'name',
    header: 'Name',
    dataType: 'text',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'The name field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'lastName',
    header: 'Last Name',
    dataType: 'text',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'The last name field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'email',
    header: 'Email',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'The email field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'userName',
    header: 'User Name',
    dataType: 'text',
    disabled: true,
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'The username field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'userType',
    header: 'User Type',
    dataType: 'select',
    class: 'field col-12 md:col-6 required',
    disabled: true,
    hidden: false,
    headerClass: 'mb-1',
    validation: z.object({
      id: z.string().min(1, 'The user type field is required'),
      name: z.string().min(1, 'The user type field is required'),
    }).nullable().refine(value => value && value.id && value.name, { message: 'The user type field is required' }),
  },
  {
    field: 'status',
    header: 'Status',
    dataType: 'select',
    class: 'field col-12 md:col-6 required',
    disabled: true,
    hidden: false,
    headerClass: 'mb-1',
    validation: z.object({
      id: z.string().min(1, 'The status field is required'),
      name: z.string().min(1, 'The status field is required'),
    }).nullable().refine(value => value && value.id && value.name, { message: 'The status field is required' }),
  },
]

const item = ref<GenericObject>({
  id: '',
  image: '',
  lastName: '',
  email: '',
  userName: '',
  name: '',
  userType: null,
  password: '',
  passwordConfirm: '',
  status: null,
})

const itemTemp = ref<GenericObject>({
  id: '',
  image: '',
  lastName: '',
  email: '',
  userName: '',
  name: '',
  userType: null,
  password: '',
  passwordConfirm: '',
  status: null,
})

async function updateItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.userType = 'INTERNAL'
  // payload.userType = typeof payload.userType === 'object' ? payload.userType.id : payload.userType
  payload.status = typeof payload.status === 'object' ? payload.status.id : payload.status
  if (payload.image && typeof payload.image === 'object') {
    const imageURL = await getUrlByImage(payload.image)
    if (imageURL) {
      payload.image = imageURL
    }
  }
  await GenericService.update(confApi.moduleApi, confApi.uriApi, userId || '', payload)
  await getItemById(userId)
}

async function saveItem(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  if (userId) {
    try {
      await updateItem(item)
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
    }
    catch (error: any) {
      toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
    }
  }
  loadingSaveAll.value = false
}

function requireConfirmationToSave(item: any) {
  if (!useRuntimeConfig().public.showSaveConfirm) {
    saveItem(item)
  }
  else {
    const { event } = item
    confirm.require({
      target: event.currentTarget,
      group: 'headless',
      header: 'Save the record',
      message: 'Do you want to save the change?',
      rejectLabel: 'Cancel',
      acceptLabel: 'Accept',
      accept: () => {
        saveItem(item)
      },
      reject: () => {
        // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
      }
    })
  }
}

async function getItemById(id: string) {
  if (id) {
    // idItem.value = id
    loadingSaveAll.value = true
    try {
      const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)
      if (response) {
        item.value.id = response.id
        item.value.name = response.name
        item.value.lastName = response.lastName
        item.value.email = response.email
        item.value.userName = response.userName
        item.value.image = response.image

        item.value.userType = ENUM_USER_TYPE.find(x => x.id === response.userType)
        item.value.status = ENUM_STATUS.find(x => x.id === response.status)
      }
      fields[0].disabled = true
      updateFieldProperty(fields, 'status', 'disabled', false)
      formReload.value += 1
    }
    catch (error) {
      if (error) {
        toast.add({ severity: 'error', summary: 'Error', detail: 'Account type methods could not be loaded', life: 3000 })
      }
    }
    finally {
      loadingSaveAll.value = false
    }
  }
}

onMounted(() => {
  getItemById(userId)
})
</script>

<template>
  <div class="flex justify-content-center align-center">
    <div class="p-4" style="width: 70%;">
      <div class="card">
        <EditFormV2
          :key="formReload"
          :fields="fields"
          :item="item"
          :show-actions="true"
          container-class="grid pt-3"
          :loading-save="loadingSaveAll"
          @submit="requireConfirmationToSave($event)"
        >
          <template #field-userType="{ item: data, onUpdate, fields: listFields, field }">
            <Dropdown
              v-if="!loadingSaveAll"
              v-model="data.userType"
              :options="[...ENUM_USER_TYPE]"
              option-label="name"
              return-object="false"
              filter
              :disabled="listFields.find(x => x.field === field)?.disabled"
              show-clear
              @update:model-value="($event) => {
                onUpdate('userType', $event)
              }"
            />
            <Skeleton v-else height="2rem" class="mb-2" />
          </template>
          <template #field-status="{ item: data, onUpdate }">
            <Dropdown
              v-if="!loadingSaveAll"
              v-model="data.status"
              :options="[...ENUM_STATUS]"
              option-label="name"
              return-object="false"
              filter
              @update:model-value="($event) => {
                onUpdate('status', $event)
              }"
            />
            <Skeleton v-else height="2rem" class="mb-2" />
          </template>
          <template #form-footer="props">
            <div class="flex justify-content-end">
              <IfCan :perms="userId ? ['ACCOUNT-TYPE:EDIT'] : ['ACCOUNT-TYPE:CREATE']">
                <Button v-tooltip.top="'Save'" class="w-3rem mx-2" icon="pi pi-save" :loading="loadingSaveAll" @click="props.item.submitForm($event)" />
              </IfCan>
            </div>
          </template>
        </EditFormV2>
      </div>

      <!-- <div class="card">
        <div class="flex justify-content-end">
          <Button class="w-6rem mr-3" icon="pi pi-save" label="Save" :loading="loadingSaveAll" :disabled="errorInfForm()" @click="saveAll" />
          <Button class="w-6rem" severity="secondary" icon="pi pi-times" label="Cancel" @click="cancelEdit()" />
        </div>
      </div> -->
    </div>
  </div>
</template>

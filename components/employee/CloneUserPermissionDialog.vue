<script setup lang="ts">
import { ref } from 'vue'
import { usePrimeVue } from 'primevue/config'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import { GenericService } from '~/services/generic-services'
import { ENUM_SHORT_TYPE } from '~/utils/Enums'

const props = defineProps({
  openDialog: {
    type: Boolean,
    required: true
  },
  user: {
    type: Object,
    required: true
  }
})
const emits = defineEmits<{
  (e: 'onCloseDialog', value: boolean): void
}>()

const $primevue = usePrimeVue()

defineExpose({
  $primevue
})

const confirm = useConfirm()
const dialogVisible = ref(props.openDialog)
const UserList = ref<any>([[], []])
const loadingSaveAll = ref(false)
const filter = ref('')
const loadingUsers = ref(false)
const confApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-employee',
})
const toast = useToast()

watch(() => props.openDialog, (newValue) => {
  dialogVisible.value = newValue
  if (newValue) {
    UserList.value[0] = []
    UserList.value[1] = []
    filter.value = ''
    getUserList()
  }
})

function onClose(isCancel: boolean) {
  dialogVisible.value = false
  emits('onCloseDialog', isCancel)
}

async function getUserList() {
  try {
    loadingUsers.value = true
    const existingIds = UserList.value[1].map((user: any) => user.id)
    const payload = {
      filter: [{
        key: 'id',
        operator: 'NOT_IN',
        value: [props.user.id, ...existingIds],
        logicalOperation: 'AND'
      }, {
        key: 'userType',
        operator: 'EQUALS',
        value: props.user.userType,
        logicalOperation: 'AND'
      }, {
        key: 'status',
        operator: 'EQUALS',
        value: 'ACTIVE',
        logicalOperation: 'AND'
      }],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }
    if (filter.value) {
      payload.filter = [...payload.filter, {
        key: 'firstName',
        operator: 'LIKE',
        value: filter.value,
        logicalOperation: 'OR'
      }, {
        key: 'lastName',
        operator: 'LIKE',
        value: filter.value,
        logicalOperation: 'OR'
      }]
    }
    const response = await GenericService.search('settings', 'manage-employee', payload)
    const { data: dataList } = response
    const newUserList = dataList.map(iterator => ({
      id: iterator.id,
      name: `${iterator.firstName} ${iterator.lastName}`,
      status: iterator.status
    }))
    UserList.value[0] = [...newUserList]
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error on loading user list', detail: '', life: 10000 })
  }
  finally {
    loadingUsers.value = false
  }
}

function requireConfirmationToSave(event: any) {
  confirm.require({
    target: event.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: async () => {
      await clonePermissions()
    },
    reject: () => {
      // toast.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 })
    }
  })
}

async function clonePermissions() {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = {
    targetEmployees: UserList.value[1].map((user: any) => user.id)
  }
  try {
    const response: any = await GenericService.update(confApi.moduleApi, confApi.uriApi, `${props.user.id}/clone`, payload)
    if (response) {
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
      onClose(false)
    }
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    loadingSaveAll.value = false
  }
}
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible"
    modal
    :header="`Clone Permissions From: ${props.user?.firstName} ${props.user?.lastName}`"
    class="w-10 lg:w-6 card p-0"
    content-class="border-round-bottom border-top-1 surface-border"
    @hide="onClose(false)"
  >
    <BlockUI :blocked="loadingUsers">
      <div class="flex align-items-center justify-content-center mt-4 mb-4">
        <PickList v-model="UserList" list-style="height:250px" :show-target-controls="false" :show-source-controls="false" class="card m-0" style="width: 100%">
          <template #sourceheader>
            <div class="flex align-items-center justify-between">
              <span class="pr-4">Users</span>
              <InputGroup>
                <InputText v-model="filter" type="text" placeholder="Search" />
                <Button icon="pi pi-search" severity="warn" @click="getUserList()" />
              </InputGroup>
            </div>
          </template>
          <template #targetheader>
            <div style="min-height: 27.14px" class="flex align-items-center">
              Selected Users
            </div>
          </template>
          <template #item="{ item }">
            {{ item.name }}
          </template>
        </PickList>
        <span v-if="loadingUsers" class="absolute">
          <i class="pi pi-spin pi-spinner" style="font-size: 2.6rem" />
        </span>
      </div>
    </BlockUI>
    <template #footer>
      <div class="flex justify-content-end">
        <Button v-tooltip.top="'Cancel'" class="w-3rem" severity="secondary" icon="pi pi-arrow-left" @click="onClose(true)" />
        <Button v-tooltip.top="'Save'" class="w-3rem mx-2" icon="pi pi-save" :loading="loadingSaveAll" :disabled="UserList[1].length === 0" @click="($event) => requireConfirmationToSave($event)" />
      </div>
    </template>
  </Dialog>
</template>

<style scoped>
</style>

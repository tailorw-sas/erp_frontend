<script setup lang="ts">
import { useRoute } from 'vue-router'
import { ref } from 'vue'
import { useToast } from 'primevue/usetoast'
import { z } from 'zod'
import getImage from '~/composables/files'
import type { ITreeNode } from '~/interfaces/IInterfaces'
import type { GenericObject } from '~/types'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import { GenericService } from '~/services/generic-services'

const route = useRoute()
const toast = useToast()
const confirm = useConfirm()

const treeTableValue = ref<ITreeNode[]>([])
const dialogReload = ref(0)
const active = ref(0)
const loadingPermissions = ref(false)
const loadingSavePermissions = ref(false)
const selectedTreeTableValue = ref({})
const treeViewPermissionRef = ref()
const loadingSaveAll = ref(false)
const currentBussiness = ref<any>({})
const businessStore = useBusinessStore()
const forceSave = ref(false)
currentBussiness.value = businessStore.current

const confApi = reactive({
  moduleApi: 'identity',
  uriApi: 'users',
})

const item = ref<GenericObject>({
  image: '',
  name: '',
  lastName: '',
  email: '',
  userName: '',
  userType: null,
})

const fieldsWithImage = ['image', 'name', 'lastName', 'email', 'userName', 'userType', 'password']
const fields: Array<FieldDefinitionType> = [
  {
    field: 'image',
    dataType: 'image',
    class: 'field col-12 md:col-3',
    headerClass: 'mb-1',
  },
  {
    field: 'name',
    header: 'Name',
    dataType: 'text',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'This is a required field').max(50, 'Maximum 50 characters')
  },
  {
    field: 'lastName',
    header: 'Last Name',
    dataType: 'text',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'This is a required field').max(50, 'Maximum 50 characters')
  },
  {
    field: 'email',
    header: 'Email',
    dataType: 'text',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'This is a required field').max(30, 'Maximum 30 characters').email('Invalid email')
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
      id: z.string().min(1, 'This is a required field'),
      name: z.string().min(1, 'This is a required field'),
    }).nullable().refine(value => value && value.id && value.name, { message: 'This is a required field' }),
  },
  {
    field: 'userName',
    header: 'User Name',
    dataType: 'text',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'This is a required field').max(20, 'Maximum 20 characters')
  },
]
const errorInTab = ref({
  tabGeneralData: false,
  tabMedicalInfo: false,
  tabServices: false,
  tabPermissions: false
})

async function getItemById() {
  loadingSaveAll.value = true
  try {
    const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, route.params ? route.params.id.toString() : '')
    if (response) {
      item.value.image = response.image
      item.value.name = response.name
      item.value.lastName = response.lastName
      item.value.email = response.email
      item.value.userName = response.userName
      item.value.userType = response.userType ? ENUM_USER_TYPE.find(x => x.id === response.userType) : null
    }
    dialogReload.value += 1
  }
  catch (error) {
    if (error) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Could not load user data.', life: 3000 })
    }
  }
  finally {
    loadingSaveAll.value = false
  }
}

async function getObjectValues($event: any) {
  forceSave.value = false
  item.value = $event
}

async function getListPermissionsByBusiness(idBusiness: string) {
  if (idBusiness) {
    await getListModulesAndPermissions(idBusiness)
    getSelectedPermissions(idBusiness)
  }
}

function transformData(data: any) {
  return data.map((item: any) => {
    return {
      key: item.id,
      label: item.name ? item.name.trim().toUpperCase() : '',
      data: item.description ? item.description.trim().toUpperCase() : '',
      icon: '',
      children: item.permissions.length > 0
        ? item.permissions.map((permission: any, pIndex: number) => ({
          key: item.permissions[pIndex].id,
          label: permission.action ? permission.action.trim().toUpperCase() : '',
          data: permission.description ? permission.description.trim().toUpperCase() : '',
          icon: '',
          parentName: item.name ? item.name.trim().toUpperCase() : '',
          isHighRisk: permission?.isHighRisk || false,
          isIT: permission?.isIT || false,
          status: permission?.status || ''
        }))
        : []
    }
  })
}

function unCheckItemSelected(key: string | string[], selected: any, ref: any) {
  if (Array.isArray(key)) {
    key.forEach((k) => {
      if (Object.prototype.hasOwnProperty.call(selected.value, k)) {
        delete selected.value[k]
      }
    })
  }
  else {
    if (Object.prototype.hasOwnProperty.call(selected.value, key)) {
      delete selected.value[key]
    }
  }
  if (ref.value) {
    ref.value.updateParentSelection()
  }
}

async function rejectInactiveItem(item: any) {
  if (item) {
    await nextTick()
    unCheckItemSelected(item, selectedTreeTableValue, treeViewPermissionRef)
  }
}

async function requireConfirmationToSelectItem(items: any[]) {
  const confirmItemSelection = (item: any) => {
    return new Promise((resolve) => {
      confirm.require({
        header: 'High risk item',
        message: `Are you sure you want to assign ${item.parentName} - ${item.label} high-risk permission?`,
        rejectLabel: 'Cancel',
        acceptLabel: 'Accept',
        icon: 'pi pi-exclamation-triangle',
        rejectClass: 'p-button-secondary',
        acceptClass: 'p-button-danger',
        accept: () => {
          if (treeViewPermissionRef.value) {
            treeViewPermissionRef.value.updateParentSelection()
          }
          resolve(true)
        },
        reject: () => {
          unCheckItemSelected(item.key, selectedTreeTableValue, treeViewPermissionRef)
          resolve(false)
        }
      })
    })
  }
  for (const i in items) {
    await confirmItemSelection(items[i])
  }
}

async function getListModulesAndPermissions(idBusiness: string) {
  loadingPermissions.value = true
  treeTableValue.value = []

  const response = await GenericService.getById('identity', 'module', idBusiness, 'build')

  const transformedData = transformData(response.data)

  treeTableValue.value = [...transformedData]

  loadingPermissions.value = false
}

async function getSelectedPermissions(idBusiness: string) {
  try {
    const response = await GenericService.getById('identity', 'user-permission-business', route.params ? route.params.id.toString() : '', 'business', idBusiness)
    if (response && response.permissions.length > 0) {
      for (const iterator of response.permissions) {
        selectedTreeTableValue.value[iterator.id] = {
          checked: true,
          partialChecked: false
        }
      }

      await nextTick(() => {
        if (treeViewPermissionRef.value) {
          treeViewPermissionRef.value.updateParentSelection()
        }
      })
    }
  }
  catch (error) {

  }
}

const goToList = async () => await navigateTo('/user')

async function assignPermissions() {
  loadingSavePermissions.value = true
  const hasProperties = Object.keys(selectedTreeTableValue.value).length > 0
  if (hasProperties) {
    const keys = Object.keys(selectedTreeTableValue.value)
    const payload = {
      filter: [
        {
          key: 'id',
          operator: 'IN',
          value: keys,
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 1000,
      page: 0
    }
    const response = await GenericService.search('identity', 'permission', payload)
    if (response.data.length > 0) {
      const listIds = response.data.map((item: any) => item.id)
      const payload = {
        userId: route.params ? route.params.id.toString() : '',
        businessId: currentBussiness.value?.businessId || '41833c25-5ca2-41cd-b22f-8488723097da',
        permissionIds: listIds
      }
      await GenericService.updateWithOutId('identity', 'user-permission-business', { ...payload })
    }
  }
  else {
    const payload = {
      userId: route.params ? route.params.id.toString() : '',
      businessId: currentBussiness.value?.businessId || '41833c25-5ca2-41cd-b22f-8488723097da',
      permissionIds: []
    }
    await GenericService.updateWithOutId('identity', 'user-permission-business', { ...payload })
  }
  loadingSavePermissions.value = false
}

function errorInfForm() {
  if (errorInTab.value.tabGeneralData || errorInTab.value.tabMedicalInfo || errorInTab.value.tabServices || errorInTab.value.tabPermissions) {
    return true
  }
  else {
    return false
  }
}

async function save(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.userType = typeof payload.userType === 'object' ? payload.userType.id : payload.userType
  payload.image = typeof payload.image === 'object' ? await getImage(payload.image) : payload.image
  try {
    const response = await GenericService.update(confApi.moduleApi, confApi.uriApi, route.params ? route.params.id.toString() : '', payload)
    if (response) {
      await assignPermissions()
      goToList()
    }
  }
  catch (error) {
    if (error) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'Could not create user', life: 3000 })
    }
  }
  finally {
    loadingSaveAll.value = false
  }
}

onMounted(async () => {
  await getItemById()
  await getListPermissionsByBusiness(currentBussiness.value?.businessId || '41833c25-5ca2-41cd-b22f-8488723097da')
})

watch(() => item.value, async (newValue) => {
  if (newValue) {
    save(newValue)
  }
})

getListPermissionsByBusiness(currentBussiness.value?.businessId || '41833c25-5ca2-41cd-b22f-8488723097da')
</script>

<template>
  <div class="flex justify-content-center align-center">
    <div style="width: 100%;">
      <TabView v-model:activeIndex="active">
        <TabPanel>
          <template #header>
            <div class="flex align-items-center gap-2">
              <i class="pi" :class="active === 0 ? 'pi-folder-open' : 'pi-folder'" style="font-size: 1.5rem" />
              <span class="font-bold white-space-nowrap">
                General Information
                <i v-if="errorInTab.tabGeneralData" class="pi p-error pi-question-circle" style="font-size: 1.2rem" />
              </span>
            </div>
          </template>
          <div class="card">
            <EditFormV2WithImage
              :key="dialogReload"
              :field-with-image="[...fieldsWithImage]"
              :fields="fields"
              :item="item"
              class-first-grid="col-12 md:col-3"
              class-second-grid="col-12 md:col-9"
              :force-save="forceSave"
              :show-actions="false"
              :loading-save="loadingSaveAll"
              @force-save="forceSave = $event"
              @submit="getObjectValues($event)"
            >
              <template #field-userType="{ item: data, onUpdate }">
                <Dropdown
                  v-if="loadingSaveAll === false"
                  v-model="data.userType"
                  :options="[...ENUM_USER_TYPE]"
                  option-label="name"
                  return-object="false"
                  class="h-3rem align-items-center"
                  show-clear
                  @update:model-value="($event) => {
                    onUpdate('userType', $event)
                  }"
                />
                <Skeleton v-else height="2rem" class="mb-2" />
              </template>
            </EditFormV2WithImage>
          </div>
        </TabPanel>
        <!-- :disabled="active === 0" -->
        <TabPanel>
          <template #header>
            <div class="flex align-items-center gap-2">
              <i class="pi" :class="active === 1 ? 'pi-folder-open' : 'pi-folder'" style="font-size: 1.5rem" />
              <span class="font-bold white-space-nowrap">Assign Permissions</span>
            </div>
          </template>
          <div class="card">
            <div class="grid p-fluid formgrid">
              <div class="col-12 field">
                <TreeView
                  ref="treeViewPermissionRef"
                  :items="treeTableValue"
                  :selected-key="selectedTreeTableValue"
                  @update:selected-key="($event) => selectedTreeTableValue = $event"
                  @on-selected-node-highrisk="requireConfirmationToSelectItem($event)"
                  @on-select-inactive-node="rejectInactiveItem($event)"
                />
              </div>
            </div>
          </div>
        </TabPanel>
      </TabView>

      <div class="card">
        <div class="flex justify-content-end">
          <Button v-tooltip.top="'Cancel'" class="w-3rem" severity="secondary" icon="pi pi-arrow-left" @click="goToList" />
          <Button v-tooltip.top="'Save'" class="w-3rem mx-2" icon="pi pi-save" :loading="loadingSaveAll" :disabled="errorInfForm()" @click="forceSave = true" />
        </div>
      </div>
    </div>
  </div>
</template>import { GenericService } from '~/services/generic-services'

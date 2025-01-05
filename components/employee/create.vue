<script setup lang="ts">
import { ref, watch } from 'vue'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import type { ITreeNode } from '~/interfaces/IInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import { GenericService } from '~/services/generic-services'
import { validateEntityStatus } from '~/utils/schemaValidations'
import { statusToString } from '~/utils/helpers'
import { ENUM_SHORT_TYPE } from '~/utils/Enums'
import type { IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'

const emits = defineEmits<{
  (e: 'onSuccessCreate', value: boolean): void
}>()
const treeTableValue = ref<ITreeNode[]>([])
const treeTableAgencyValue = ref<ITreeNode[]>([])
const treeTableHotelValue = ref<ITreeNode[]>([])
const treeTableReportValue = ref<ITreeNode[]>([])
const treeTableTradingCompanyValue = ref<ITreeNode[]>([])
const confirm = useConfirm()
const toast = useToast()
const active = ref(0)
const userId = ref('')
const loadingPermissions = ref(false)
const loadingAgencies = ref(false)
const loadingSaveAll = ref(false)
const DepartmentGroupList = ref<any[]>([])
const forceSave = ref(false)
let submitEvent = new Event('')
const currentBussiness = ref<any>({})
const businessStore = useBusinessStore()
const selectedKey = ref({})
const selectedAgencyKey = ref({})
const selectedHotelKey = ref({})
const selectedReportKey = ref({})
const selectedTradingCompanyKey = ref({})
const treeViewPermissionRef = ref()
const treeViewAgencyRef = ref()
const treeViewHotelRef = ref()
const treeViewReportRef = ref()
const treeViewCompanyRef = ref()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 500,
  page: 0,
  sortBy: 'name',
  sortType: ENUM_SHORT_TYPE.ASC
})

const confApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-employee',
})
currentBussiness.value = businessStore.current

const errorInTab = ref({
  tabGeneralData: false,
  tabPermissions: false
})

const fields: Array<FieldDefinitionType> = [
  {
    field: 'userType',
    dataType: 'text',
    class: 'field col-12 pt-0 pb-2 flex',
    validation: z.string().trim().min(1, 'The user type field is required')
  },
  {
    field: 'firstName',
    header: 'First Name',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'The first name field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'lastName',
    header: 'Last Name',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'The last name field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'loginName',
    header: 'Login Name',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'The login name field is required').max(50, 'Maximum 50 characters')
  },
  {
    field: 'email',
    header: 'Email',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'The email field is required').email('Invalid email')
  },
  {
    field: 'departmentGroup',
    header: 'Department Group',
    dataType: 'select',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: validateEntityStatus('department group'),
  },
  {
    field: 'phoneExtension',
    header: 'Phone Extension',
    headerClass: 'mb-1',
    dataType: 'text',
    class: 'field col-12',
    validation: z.string().trim().refine((value) => {
      if (value === '') {
        return true
      }
      return /^\d+$/.test(value)
    }, {
      message: 'Only numeric characters allowed'
    })
  },
  {
    field: 'innsistCode',
    header: 'Innsist Code',
    headerClass: 'mb-1',
    dataType: 'text',
    class: 'field col-12',
  },
  {
    field: 'status',
    header: 'Active',
    disabled: true,
    dataType: 'check',
    class: 'field col-12 mt-3 mb-3',
  },
]

const item = ref({
  userType: 'INTERNAL',
  firstName: '',
  lastName: '',
  loginName: '',
  email: '',
  departmentGroup: null,
  phoneExtension: '0',
  innsistCode: '',
  isLock: false,
  status: true,
  managePermissionList: [],
  manageAgencyList: [],
  manageHotelList: [],
  manageTradingCompaniesList: [],
})

async function getObjectValues($event: any) {
  forceSave.value = false
  item.value = $event
}

function transformData(data: any) {
  return data.map((item: any, index: number) => {
    return {
      key: index,
      label: item.name ? item.name.trim().toUpperCase() : '',
      data: item.description ? item.description.trim().toUpperCase() : '',
      icon: '',
      children: item.permissions.length > 0
        ? item.permissions.map((permission: any) => ({
          key: permission.id,
          label: permission.code ? permission.code.trim().toUpperCase() : '',
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

function transformAgenciesData(data: any) {
  return data.map((item: any) => {
    return {
      key: `country-${item.countryName}`,
      label: item.countryName ? item.countryName.trim().toUpperCase() : '',
      data: item.description ? item.description.trim().toUpperCase() : '',
      icon: '',
      children: item.clients.length > 0
        ? item.clients.map((client: any) => ({
          key: `client-${item.countryName}-${client.clientName}`,
          label: client.clientName ? client.clientName.trim().toUpperCase() : '',
          data: client.description ? client.description.trim().toUpperCase() : '',
          icon: '',
          children: client.agencies.map((agency: any) => ({
            key: agency.id,
            label: agency.name ? agency.name.trim().toUpperCase() : '',
            data: agency.description ? agency.description.trim().toUpperCase() : '',
            icon: '',
            status: agency?.status || ''
          }))
        }))
        : []
    }
  })
}

function transformHotelsData(data: any) {
  return data.map((item: any, index: number) => {
    return {
      key: index,
      label: item.country ? item.country.trim().toUpperCase() : '',
      data: item.description ? item.description.trim().toUpperCase() : '',
      icon: '',
      children: item.hotels.length > 0
        ? item.hotels.map((hotel: any) => ({
          key: hotel.id,
          label: hotel.name ? hotel.name.trim().toUpperCase() : '',
          data: hotel.description ? hotel.description.trim().toUpperCase() : '',
          icon: '',
          status: hotel?.status || ''
        }))
        : []
    }
  })
}

function transformReportsData(data: any) {
  return data.map((item: any, index: number) => {
    return {
      key: index,
      label: item.module ? item.module.trim().toUpperCase() : '',
      data: item.description ? item.description.trim().toUpperCase() : '',
      icon: '',
      children: item.reports.length > 0
        ? item.reports.map((report: any) => ({
          key: report.id,
          label: report.name ? report.name.trim().toUpperCase() : '',
          data: report.description ? report.description.trim().toUpperCase() : '',
          icon: '',
          status: report?.status || ''
        }))
        : []
    }
  })
}

function transformTradingCompaniesData(data: any) {
  return data.map((item: any, index: number) => {
    return {
      key: index,
      label: item.country ? item.country.trim().toUpperCase() : '',
      data: item.description ? item.description.trim().toUpperCase() : '',
      icon: '',
      children: item.tradingCompanies.length > 0
        ? item.tradingCompanies.map((tradingCompany: any) => ({
          key: tradingCompany.id,
          label: tradingCompany.company ? tradingCompany.company.trim().toUpperCase() : '',
          data: tradingCompany.description ? tradingCompany.description.trim().toUpperCase() : '',
          icon: '',
          status: tradingCompany?.status || ''
        }))
        : []
    }
  })
}

async function getListEmployeePermissionsTree(idBusiness: string) {
  loadingPermissions.value = true
  treeTableValue.value = []

  const response = await GenericService.search('settings', 'module', payload.value)

  const transformedData = transformData(response.data)

  treeTableValue.value = [...transformedData]

  loadingPermissions.value = false
}

async function getListEmployeeAgenciesTree() {
  loadingAgencies.value = true
  treeTableAgencyValue.value = []

  const response = await GenericService.getById('settings', 'manage-agency', 'all-grouped')
  const transformedData = transformAgenciesData(response.response)

  treeTableAgencyValue.value = [...transformedData]

  loadingAgencies.value = false
}

async function getListEmployeeHotelsTree() {
  loadingAgencies.value = true
  treeTableHotelValue.value = []

  const response = await GenericService.getById('settings', 'manage-hotel', 'all-grouped')
  const transformedData = transformHotelsData(response.response)

  treeTableHotelValue.value = [...transformedData]

  loadingAgencies.value = false
}

async function getListEmployeeReportsTree() {
  loadingAgencies.value = true
  treeTableReportValue.value = []

  const response = await GenericService.getById('settings', 'manage-report', 'all-grouped')
  const transformedData = transformReportsData(response.response)

  treeTableReportValue.value = [...transformedData]

  loadingAgencies.value = false
}

async function getListEmployeeTradingCompaniesTree() {
  loadingAgencies.value = true
  treeTableTradingCompanyValue.value = []

  const response = await GenericService.getById('settings', 'manage-trading-companies', 'all-grouped')
  const transformedData = transformTradingCompaniesData(response.response)

  treeTableTradingCompanyValue.value = [...transformedData]

  loadingAgencies.value = false
}

const goToList = async () => await navigateTo('/settings/employee')

async function getDepartmentGroupList(query: string) {
  try {
    const payload = {
      filter: [
        {
          key: 'name',
          operator: 'LIKE',
          value: query,
          logicalOperation: 'OR'
        },
        {
          key: 'code',
          operator: 'LIKE',
          value: query,
          logicalOperation: 'OR'
        },
        {
          key: 'status',
          operator: 'EQUALS',
          value: 'ACTIVE',
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'name',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search('settings', 'manage-department-group', payload)
    const { data: dataList } = response
    DepartmentGroupList.value = []
    for (const iterator of dataList) {
      DepartmentGroupList.value = [...DepartmentGroupList.value, { id: iterator.id, name: `${iterator.code} - ${iterator.name}`, status: iterator.status }]
    }
  }
  catch (error) {
    console.error('Error on loading department group list:', error)
  }
}

function errorInfForm() {
  if (errorInTab.value.tabGeneralData || errorInTab.value.tabPermissions) {
    return true
  }
  else {
    return false
  }
}

async function createUser(item: any) {
  const payload = { userName: item.loginName, email: item.email, password: generatePassword(12, true), name: item.firstName, lastName: item.lastName, userType: item.userType, }
  const response: any = await GenericService.create('identity', 'users', payload)
  if (response) {
    return response.id
  }
}

async function save(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.status = statusToString(payload.status)
  payload.departmentGroup = typeof payload.departmentGroup === 'object' ? payload.departmentGroup.id : payload.departmentGroup
  try {
    const createdUserId: string = await createUser(item)
    if (createdUserId) {
      payload.id = createdUserId
      payload.phoneExtension = (item.phoneExtension) ? item.phoneExtension : '0'
      payload.managePermissionList = filterSelectedIds(selectedKey.value)
      payload.manageAgencyList = filterAgencySelectedIds(selectedAgencyKey.value)
      payload.manageHotelList = filterSelectedIds(selectedHotelKey.value)
      payload.manageReportList = filterSelectedIds(selectedReportKey.value)
      payload.manageTradingCompaniesList = filterSelectedIds(selectedTradingCompanyKey.value)
    }
    const response: any = await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
    if (response) {
      userId.value = response.id
      toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
      emits('onSuccessCreate', true)
    }
  }
  catch (error: any) {
    toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
  }
  finally {
    loadingSaveAll.value = false
  }
}

function filterSelectedIds(data: any) {
  const uuidList: string[] = []

  for (const key in data) {
    if (Number.isNaN(Number(key))) {
      uuidList.push(key)
    }
  }
  return uuidList
}

function filterAgencySelectedIds(data: any) {
  const uuidList: string[] = []

  for (const key in data) {
    if (!key.includes('country') && !key.includes('client')) {
      uuidList.push(key)
    }
  }
  return uuidList
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
          unCheckItemSelected(item.key, selectedKey, treeViewPermissionRef)
          resolve(false)
        }
      })
    })
  }
  for (const i in items) {
    await confirmItemSelection(items[i])
  }
}

async function rejectInactiveItem(item: any) {
  if (item) {
    await nextTick()
    unCheckItemSelected(item, selectedKey, treeViewPermissionRef)
  }
}

async function rejectInactiveAgencyItem(item: any) {
  if (item) {
    await nextTick()
    unCheckItemSelected(item, selectedAgencyKey, treeViewAgencyRef)
  }
}

async function rejectInactiveHotelItem(item: any) {
  if (item) {
    await nextTick()
    unCheckItemSelected(item, selectedHotelKey, treeViewHotelRef)
  }
}

async function rejectInactiveReportItem(item: any) {
  if (item) {
    await nextTick()
    unCheckItemSelected(item, selectedReportKey, treeViewReportRef)
  }
}

async function rejectInactiveTradingCompanyItem(item: any) {
  if (item) {
    await nextTick()
    unCheckItemSelected(item, selectedTradingCompanyKey, treeViewCompanyRef)
  }
}

function requireConfirmationToSave(item: any) {
  confirm.require({
    target: submitEvent.currentTarget,
    group: 'headless',
    header: 'Save the record',
    message: 'Do you want to save the change?',
    rejectLabel: 'Cancel',
    acceptLabel: 'Accept',
    accept: async () => {
      await save(item)
    },
    reject: () => {}
  })
}

function saveSubmit(event: Event) {
  forceSave.value = true
  submitEvent = event
}

watch(() => item.value, async (newValue) => {
  if (newValue) {
    requireConfirmationToSave(newValue)
  }
})

onMounted(async () => {
  getListEmployeePermissionsTree(currentBussiness.value?.businessId || '41833c25-5ca2-41cd-b22f-8488723097da')
  getListEmployeeAgenciesTree()
  getListEmployeeHotelsTree()
  getListEmployeeReportsTree()
  getListEmployeeTradingCompaniesTree()
})
</script>

<template>
  <div>
    <TabView v-model:activeIndex="active" class="tabview-employee p-0" :scrollable="true">
      <TabPanel>
        <template #header>
          <div class="flex align-items-center gap-2">
            <i class="pi" :class="active === 0 ? 'pi-folder-open' : 'pi-folder'" style="font-size: 1.5rem" />
            <span class="font-bold white-space-nowrap">
              General
              <i v-if="errorInTab.tabGeneralData" class="pi p-error pi-question-circle" style="font-size: 1.2rem" />
            </span>
          </div>
        </template>
        <div class="card p-4 m-0 pb-0 content-max-height">
          <EditFormV2
            :fields="fields"
            :item="item"
            :force-save="forceSave"
            :show-actions="false"
            :loading-save="loadingSaveAll"
            :un-restricted-content-height="true"
            @force-save="forceSave = $event"
            @submit="getObjectValues($event)"
          >
            <template #field-departmentGroup="{ item: data, onUpdate }">
              <DebouncedAutoCompleteComponent
                v-if="!loadingSaveAll"
                id="autocomplete"
                field="name"
                item-value="id"
                :model="data.departmentGroup"
                :suggestions="DepartmentGroupList"
                @change="($event) => {
                  onUpdate('departmentGroup', $event)
                }"
                @load="($event) => getDepartmentGroupList($event)"
              />
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
            <template #field-userType="{ item: data, onUpdate }">
              <div v-if="!loadingSaveAll" class="flex gap-4 mb-1">
                <div class="flex align-items-center">
                  <RadioButton
                    v-model="data.userType" input-id="internal" name="userType" value="INTERNAL" @change="($event) => {
                      onUpdate('userType', 'INTERNAL')
                    }"
                  />
                  <label for="internal" class="ml-2">Internal</label>
                </div>
                <div class="flex align-items-center">
                  <RadioButton
                    v-model="data.userType" input-id="external" name="userType" value="EXTERNAL" @change="($event) => {
                      onUpdate('userType', 'EXTERNAL')
                    }"
                  />
                  <label for="external" class="ml-2">External</label>
                </div>
              </div>
              <Skeleton v-else height="2rem" class="mb-2" />
            </template>
          </EditFormV2>
        </div>
      </TabPanel>
      <!-- :disabled="active === 0" -->
      <TabPanel>
        <template #header>
          <div class="flex align-items-center gap-2">
            <i class="pi" :class="active === 1 ? 'pi-folder-open' : 'pi-folder'" style="font-size: 1.5rem" />
            <span class="font-bold white-space-nowrap"> Permission </span>
          </div>
        </template>
        <div class="card content-max-height m-0 p-4">
          <div class="grid p-fluid formgrid">
            <div class="col-12 field">
              <TreeView
                ref="treeViewPermissionRef"
                :items="treeTableValue"
                :selected-key="selectedKey"
                @update:selected-key="($event) => selectedKey = $event"
                @on-selected-node-highrisk="requireConfirmationToSelectItem($event)"
                @on-select-inactive-node="rejectInactiveItem($event)"
              />
            </div>
          </div>
        </div>
      </TabPanel>
      <!-- Agencies -->
      <TabPanel>
        <template #header>
          <div class="flex align-items-center gap-2">
            <i class="pi" :class="active === 2 ? 'pi-folder-open' : 'pi-folder'" style="font-size: 1.5rem" />
            <span class="font-bold white-space-nowrap"> Agency </span>
          </div>
        </template>
        <div class="card content-max-height m-0 p-4">
          <div class="grid p-fluid formgrid">
            <div class="col-12 field">
              <TreeView
                ref="treeViewAgencyRef"
                :items="treeTableAgencyValue"
                :selected-key="selectedAgencyKey"
                @update:selected-key="($event) => selectedAgencyKey = $event"
                @on-select-inactive-node="rejectInactiveAgencyItem($event)"
              />
            </div>
          </div>
        </div>
      </TabPanel>
      <!-- Hotels -->
      <TabPanel>
        <template #header>
          <div class="flex align-items-center gap-2">
            <i class="pi" :class="active === 3 ? 'pi-folder-open' : 'pi-folder'" style="font-size: 1.5rem" />
            <span class="font-bold white-space-nowrap"> Hotel </span>
          </div>
        </template>
        <div class="card content-max-height m-0 p-4">
          <div class="grid p-fluid formgrid">
            <div class="col-12 field">
              <TreeView
                ref="treeViewHotelRef"
                :items="treeTableHotelValue"
                :selected-key="selectedHotelKey"
                @update:selected-key="($event) => selectedHotelKey = $event"
                @on-select-inactive-node="rejectInactiveHotelItem($event)"
              />
            </div>
          </div>
        </div>
      </TabPanel>
      <!-- Reports -->
      <TabPanel>
        <template #header>
          <div class="flex align-items-center gap-2">
            <i class="pi" :class="active === 4 ? 'pi-folder-open' : 'pi-folder'" style="font-size: 1.5rem" />
            <span class="font-bold white-space-nowrap"> Report </span>
          </div>
        </template>
        <div class="card content-max-height m-0 p-4">
          <div class="grid p-fluid formgrid">
            <div class="col-12 field">
              <TreeView
                ref="treeViewReportRef"
                :items="treeTableReportValue"
                :selected-key="selectedReportKey"
                @update:selected-key="($event) => selectedReportKey = $event"
                @on-select-inactive-node="rejectInactiveReportItem($event)"
              />
            </div>
          </div>
        </div>
      </TabPanel>
      <!-- Trading Company -->
      <TabPanel>
        <template #header>
          <div class="flex align-items-center gap-2">
            <i class="pi" :class="active === 5 ? 'pi-folder-open' : 'pi-folder'" style="font-size: 1.5rem" />
            <span class="font-bold white-space-nowrap"> Trading Company </span>
          </div>
        </template>
        <div class="card content-max-height m-0 p-4">
          <div class="grid p-fluid formgrid">
            <div class="col-12 field">
              <TreeView
                ref="treeViewCompanyRef"
                :items="treeTableTradingCompanyValue"
                :selected-key="selectedTradingCompanyKey"
                @update:selected-key="($event) => selectedTradingCompanyKey = $event"
                @on-select-inactive-node="rejectInactiveTradingCompanyItem($event)"
              />
            </div>
          </div>
        </div>
      </TabPanel>
    </TabView>
    <div class="card">
      <div class="flex justify-content-end">
        <!--        <Button v-tooltip.top="'Cancel'" class="w-3rem" severity="secondary" icon="pi pi-arrow-left" @click="goToList" /> -->
        <IfCan :perms="['EMPLOYEE:CREATE']">
          <Button v-tooltip.top="'Save'" class="w-3rem mx-2" icon="pi pi-save" :loading="loadingSaveAll" :disabled="errorInfForm()" @click="saveSubmit($event)" />
        </IfCan>
      </div>
    </div>
  </div>
</template>

<style scoped>
  .content-max-height {
    max-height: 60vh;
    overflow-y: auto;
    width: 100%;
    min-height: 60vh;
  }
</style>

<style lang="scss">
 .tabview-employee {
   .p-tabview-panels {
     padding-top: 0 !important;
     padding-bottom: 0 !important;
   }
   .p-tabview-nav-container {
     border-top-left-radius: 0 !important;
     border-top-right-radius: 0 !important;
     .p-tabview-nav-content .p-tabview-nav {
       li {
         padding: 0 4px;
       }
       li[data-p-active="true"] {
         background-color: #045BFC;
         border-radius: 5px 5px 0px 0px;
       }
     }
   }
 }
 .p-tabview .p-tabview-nav-btn.p-link {
   background-color: var(--primary-color);
   color: white !important;
 }
 .p-tabview-nav-content {
   margin-right: 30px !important;
 }
</style>

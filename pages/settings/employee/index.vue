<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import type { IData } from '~/components/table/interfaces/IModelData'
import { GenericService } from '~/services/generic-services'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { statusToBoolean } from '~/utils/helpers'
import CreateEmployeePage from '~/components/employee/create.vue'
import EditEmployeePage from '~/components/employee/edit.vue'
import CloneEmployeePage from '~/components/employee/clone.vue'

enum E_OPERATION {
  CREATE = 'Create',
  EDIT = 'Edit',
  CLONE = 'Clone',
}

// VARIABLES -----------------------------------------------------------------------------------------
const listItems = ref<any[]>([])
const selectedOperation = ref<E_OPERATION>(E_OPERATION.CREATE)
const idItemToLoadFirstTime = ref('')
const loadingSearch = ref(false)
const userToClone = ref()
const editDialogVisible = ref(false)
const editReload = ref(0)
const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})

// FORM CONFIG -------------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'loginName', name: 'Login Name' },
  { id: 'firstName', name: 'First Name' },
  { id: 'lastName', name: 'Last Name' },
]
// -------------------------------------------------------------------------------------------------------

// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns = ref<IColumn[]>([
  { field: 'loginName', header: 'Login Name', type: 'text' },
  { field: 'firstName', header: 'First Name', type: 'text' },
  { field: 'lastName', header: 'Last Name', type: 'text' },
  { field: 'departmentGroup', header: 'Department Group', type: 'select', objApi: { moduleApi: 'settings', uriApi: 'manage-department-group' }, sortable: true },
  { field: 'userType', header: 'User Type', type: 'text' },
  { field: 'status', header: 'Active', type: 'bool' },
])
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Employee',
  moduleApi: 'settings',
  uriApi: 'manage-employee',
  loading: false,
  showToolBar: false,
  showAcctions: false,
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0,
  sortBy: 'createdAt',
  sortType: 'DES'
})
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------

function searchAndFilter() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    payload.value.filter = [...payload.value.filter, {
      key: filterToSearch.value.criterial ? filterToSearch.value.criterial.id : '',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'AND',
      type: 'filterSearch',
    }]
  }
  getList()
}

function clearFilterToSearch() {
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')]
  filterToSearch.value.criterial = ENUM_FILTER[0]
  filterToSearch.value.search = ''
  getList()
}

async function getList() {
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    idItemToLoadFirstTime.value = ''
    options.value.loading = true
    listItems.value = []
    const newListItems = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = statusToBoolean(iterator.status)
      }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({ ...iterator, loadingEdit: false, loadingDelete: false })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }
    listItems.value = [...listItems.value, ...newListItems]

    if (listItems.value.length > 0) {
      idItemToLoadFirstTime.value = listItems.value[0].id
      userToClone.value = listItems.value[0]
    }
  }
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}

async function resetListItems() {
  payload.value.page = 0
  getList()
}

async function openClonePermissionsDialog() {
  editDialogVisible.value = true
}

async function onCloseDialog(result: any) {
  editDialogVisible.value = false
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns.value)
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payload.value.filter = [...payload.value.filter, ...parseFilter || []]
  getList()
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    parseDataTableFilter(event.filter)
  }
}

// Form Operations
function setOperation(operation: E_OPERATION) {
  selectedOperation.value = operation
}

function reloadEditForm(id: string) {
  idItemToLoadFirstTime.value = id
}
// -------------------------------------------------------------------------------------------------------

// WATCH FUNCTIONS -------------------------------------------------------------------------------------
watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10

  getList()
})

watch(() => idItemToLoadFirstTime.value, async (newValue) => {
  if (!newValue) {
    selectedOperation.value = E_OPERATION.CREATE
  }
  else {
    selectedOperation.value = E_OPERATION.EDIT
    editReload.value++
    userToClone.value = listItems.value.find((e: any) => e.id === newValue)
  }
})
// -------------------------------------------------------------------------------------------------------

// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
onMounted(() => {
  filterToSearch.value.criterial = ENUM_FILTER[0]
  if (useRuntimeConfig().public.loadTableData) {
    getList()
  }
})

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criterial && filterToSearch.value.search)
  return false
})

const disabledClearSearch = computed(() => {
  return !(filterToSearch.value.criterial && filterToSearch.value.search)
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="flex justify-content-between align-items-center">
    <h3 class="mb-0">
      Manage Employee
    </h3>
    <div class="my-2 flex justify-content-end px-0">
      <IfCan :perms="['EMPLOYEE:CREATE']">
        <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true">
          <Button v-tooltip.left="'Add'" label="Add" icon="pi pi-plus" severity="primary" @click="setOperation(E_OPERATION.CREATE)" />
        </div>
      </IfCan>
      <IfCan :perms="['EMPLOYEE:CLONE']">
        <Button
          v-tooltip.left="'Clone Employee'" label="Clone" icon="pi pi-users" severity="primary" class="ml-2 mr-2"
          :disabled="!idItemToLoadFirstTime || options.loading" @click="setOperation(E_OPERATION.CLONE)"
        />
      </IfCan>
      <IfCan :perms="['EMPLOYEE:ASSIGN']">
        <Button
          v-tooltip.left="'Clone Permissions'" label="Clone Permissions" icon="pi pi-clone" severity="primary"
          :disabled="!userToClone || options.loading" @click="openClonePermissionsDialog"
        />
      </IfCan>
    </div>
  </div>
  <div class="grid">
    <div class="col-12 md:order-1 md:col-6 xl:col-7">
      <div class="card p-0">
        <Accordion :active-index="0" class="mb-2">
          <AccordionTab>
            <template #header>
              <div class="text-white font-bold custom-accordion-header">
                Filters
              </div>
            </template>
            <div class="flex gap-4 flex-column lg:flex-row">
              <div class="flex align-items-center gap-2">
                <label for="email3">Criteria</label>
                <div class="w-full lg:w-auto">
                  <Dropdown
                    v-model="filterToSearch.criterial"
                    :options="[...ENUM_FILTER]"
                    option-label="name"
                    placeholder="Criteria"
                    return-object="false"
                    class="align-items-center w-full"
                    show-clear
                  />
                </div>
              </div>
              <div class="flex align-items-center gap-2">
                <label for="email">Search</label>
                <div class="w-full lg:w-auto">
                  <IconField icon-position="left" class="w-full">
                    <InputText v-model="filterToSearch.search" type="text" placeholder="Search" class="w-full" />
                    <InputIcon class="pi pi-search" />
                  </IconField>
                </div>
              </div>
              <div class="flex align-items-center">
                <Button v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search" :disabled="disabledSearch" :loading="loadingSearch" @click="searchAndFilter" />
                <Button v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash" :disabled="disabledClearSearch" :loading="loadingSearch" @click="clearFilterToSearch" />
              </div>
            </div>
          </AccordionTab>
        </Accordion>
      </div>
      <DynamicTable
        :data="listItems"
        :columns="columns"
        :options="options"
        :pagination="pagination"
        @on-confirm-create="setOperation(E_OPERATION.CREATE)"
        @update:clicked-item="reloadEditForm($event)"
        @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems"
        @on-sort-field="onSortField"
      />
    </div>
    <div class="col-12 md:order-0 md:col-6 xl:col-5">
      <div>
        <div class="font-bold text-lg px-4 bg-primary custom-card-header">
          {{ selectedOperation }}
        </div>
        <div class="card p-0">
          <CreateEmployeePage v-if="selectedOperation === E_OPERATION.CREATE" @on-success-create="getList" />
          <EditEmployeePage v-if="selectedOperation === E_OPERATION.EDIT" :key="editReload" :employee-id="idItemToLoadFirstTime" @on-success-edit="getList" />
          <CloneEmployeePage v-if="selectedOperation === E_OPERATION.CLONE" :key="editReload" :employee-id="idItemToLoadFirstTime" @on-success-clone="getList" />
        </div>
      </div>
    </div>
    <CloneUserPermissionDialog :open-dialog="editDialogVisible" :user="userToClone" @on-close-dialog="onCloseDialog" />
  </div>
</template>

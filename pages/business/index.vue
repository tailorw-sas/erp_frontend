<script setup lang="ts">
import type { PageState } from 'primevue/paginator'
import { onMounted, ref, watch } from 'vue'
import type { IFilter, IFormField, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IBusiness, IData } from '~/components/table/interfaces/IModelData'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { GenericService } from '~/services/generic-services'

// VARIABLES -----------------------------------------------------------------------------------------
const listItems = ref<IBusiness[]>([])
const loadingSearch = ref(false)

const loadingForm = ref(false)
const dialogReload = ref(0)
const isEdit = ref(false)
const dialogConfig = ref({
  visible: false,
  header: ''
})

const filterToSearch = ref<IData>({
  criterial: null,
  search: '',
})

// FORM CONFIG -------------------------------------------------------------------------------------------
const formConfig = ref<IFormField>(
  {
    logo: {
      value: '',
      label: 'Logo',
      type: 'image',
      isRequired: false,
      showRequiredLabel: true,
      placeholder: '',
      errorMessage: [],
      haveError: false,
      maxLength: 5,
      showCounter: false,
      validationKeywords: [],
      col: '12'
    },
    name: {
      value: '',
      label: 'Name',
      type: 'text',
      isRequired: true,
      showRequiredLabel: true,
      placeholder: '',
      errorMessage: [],
      haveError: false,
      maxLength: 5,
      showCounter: true,
      validationKeywords: [],
      col: '6'
    },
    ruc: {
      value: '',
      label: 'RUC',
      type: 'text',
      isRequired: true,
      showRequiredLabel: true,
      placeholder: '',
      errorMessage: [],
      haveError: false,
      maxLength: 5,
      showCounter: true,
      validationKeywords: [],
      col: '6'
    },
    description: {
      value: '',
      label: 'Description',
      type: 'textarea',
      isRequired: true,
      showRequiredLabel: true,
      placeholder: '',
      errorMessage: [],
      haveError: false,
      validationKeywords: [],
      col: '12'
    },
    latitude: {
      value: '',
      label: 'Latitude',
      type: 'text',
      isRequired: true,
      showRequiredLabel: true,
      placeholder: '',
      errorMessage: [],
      haveError: false,
      maxLength: 5,
      showCounter: true,
      validationKeywords: [],
      col: '6'
    },
    longitude: {
      value: '',
      label: 'Longitude',
      type: 'text',
      isRequired: true,
      showRequiredLabel: true,
      placeholder: '',
      errorMessage: [],
      haveError: false,
      maxLength: 5,
      showCounter: true,
      validationKeywords: [],
      col: '6'
    },
    provinceDto: {
      value: '',
      label: 'Province',
      type: 'select',
      isRequired: false,
      moduleApi: 'patients',
      uriApi: 'locations',
      filter: [
        {
          key: 'type',
          operator: 'EQUALS',
          value: 'PROVINCE',
          logicalOperation: 'AND'
        },
      ],
      showRequiredLabel: false,
      placeholder: 'Select a province',
      errorMessage: [],
      haveError: false,
      col: '6'
    },
    cantonDto: {
      keyRef: 0,
      keyValue: 'provinceDto',
      value: '',
      label: 'Canton',
      type: 'dependent-select',
      isRequired: false,
      moduleApi: 'patients',
      uriApi: 'locations',
      filter: [
        {
          key: 'type',
          operator: 'EQUALS',
          value: 'CANTON',
          logicalOperation: 'AND'
        },
      ],
      showRequiredLabel: false,
      placeholder: 'Select a canton',
      errorMessage: [],
      haveError: false,
      col: '6'
    },
    parroquiaDto: {
      keyRef: 0,
      keyValue: 'cantonDto',
      value: '',
      label: 'Parroquia',
      type: 'dependent-select',
      isRequired: false,
      moduleApi: 'patients',
      uriApi: 'locations',
      filter: [
        {
          key: 'type',
          operator: 'EQUALS',
          value: 'PARROQUIA',
          logicalOperation: 'AND'
        },
      ],
      showRequiredLabel: false,
      placeholder: 'Select a parish',
      errorMessage: [],
      haveError: false,
      col: '12'
    },
  }
)

const formOptions = ref({
  moduleApi: 'identity',
  uriApi: 'business',
})

const formOptionsToEdit = ref({
  moduleApi: 'identity',
  uriApi: 'business',
  itemId: ''
})

// -------------------------------------------------------------------------------------------------------
const ENUM_FILTER = [
  { id: 'ruc', name: 'RUC' },
  { id: 'name', name: 'Name' },
]
// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------
const columns: IColumn[] = [
  { field: 'image', header: 'Image', type: 'image' },
  { field: 'ruc', header: 'RUC', type: 'text' },
  { field: 'name', header: 'Name', type: 'text' },
  { field: 'description', header: 'Description', type: 'text' },
  { field: 'createdAt', header: 'Created At', type: 'date' },
]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Companies',
  moduleApi: 'identity',
  uriApi: 'business',
  loading: false,
  showAcctions: true,
  messageToDelete: 'Are you sure you want to delete: {{name}}?'
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 50,
  page: 0
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
async function getList() {
  try {
    options.value.loading = true
    listItems.value = []

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    for (const iterator of dataList) {
      listItems.value = [...listItems.value, { ...iterator, loadingEdit: false, loadingDelete: false }]
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

function isSelectField(field: any) {
  const column = columns.find(column => column.field === field)
  return column && (column.type === 'select' || column.type === 'local-select')
}
function isLocalItemsField(field: any) {
  const column = columns.find(column => column.field === field)
  return column && (column.localItems && column.localItems?.length > 0)
}

function getTransformedKey(keyName: string) {
  let keyResult = ''
  if (keyName === 'moduleName') {
    keyResult = 'module'
  }
  if (!isLocalItemsField(keyName) && keyResult !== '') {
    keyResult = `${keyResult}.id`
  }
  else if (!isLocalItemsField(keyName) && keyResult === '') {
    keyResult = `${keyName}.id`
  }
  else if (isLocalItemsField(keyName) && keyResult === '') {
    keyResult = `${keyName}`
  }
  return keyResult
}

function existsInPayloadFilter(key: string, array: IFilter[]): boolean {
  return array.some(filter => filter.key === key)
}

async function getEventFromTable(payloadFilter: any) {
  if (typeof payloadFilter === 'object') {
    let arrayFilter: IFilter[] = []
    for (const key in payloadFilter) {
      if (Object.prototype.hasOwnProperty.call(payloadFilter, key) && key !== 'search') {
        // Para los tipos Select
        if (isSelectField(key)) {
          const element = payloadFilter[key]

          if (element && Array.isArray(element.value) && element.value.length > 0) {
            for (const iterator of element.value) {
              const ketTemp = getTransformedKey(key)
              if (!existsInPayloadFilter(ketTemp, arrayFilter)) {
                const objFilter: IFilter = {
                  key: ketTemp,
                  operator: element.matchMode.toUpperCase(),
                  value: [iterator.id],
                  logicalOperation: 'AND'
                }
                arrayFilter = [...arrayFilter, objFilter]
              }
              else {
                const filterKey = ketTemp
                const index = arrayFilter.findIndex(item => item.key === filterKey)
                if (index !== -1) {
                  arrayFilter[index].value = [...arrayFilter[index].value, iterator.id]
                }
              }
            }
          }
        }
        else { // En caso de que no sea un filtro de tipo SELECT
          const element = payloadFilter[key]
          if (element.value || element.value === false) {
            const objFilter: IFilter = {
              key,
              operator: element.matchMode.toUpperCase() === 'CONTAINS' ? 'LIKE' : element.matchMode.toUpperCase(),
              value: element.value,
              logicalOperation: 'AND'
            }
            arrayFilter = [...arrayFilter, objFilter]
          }
        }
      }
    }
    // Eliminando elmentos duplicados
    arrayFilter = arrayFilter.filter((value, index, self) => {
      return self.indexOf(value) === index
    })
    payload.value.filter = [...arrayFilter]
    getList()
  }
}

const openCreateDialog = async () => await navigateTo({ path: 'business/create' })

const openEditDialog = async (item: any) => await navigateTo({ path: `business/edit/${item}` })

function onCloseDialog($event: Event) {
  dialogReload.value += 1
  dialogConfig.value.visible = false
}

function onSortField(event: any) {
  if (event) {
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    getEventFromTable(event.filter)
  }
}

function clearFilterToSearch() {
  payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'name',
    sortType: 'DES'
  }
  filterToSearch.value.criterial = ENUM_FILTER[0]
  filterToSearch.value.search = ''
  getList()
}

function searchAndFilter() {
  payload.value = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    // sortBy: 'code',
    // sortType: 'DES'
  }
  if (filterToSearch.value.criterial && filterToSearch.value.search) {
    payload.value.filter = [...payload.value.filter, {
      key: filterToSearch.value.criterial ? filterToSearch.value.criterial.id : '',
      operator: 'LIKE',
      value: filterToSearch.value.search,
      logicalOperation: 'AND'
    }]
  }
  getList()
}

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criterial && filterToSearch.value.search)
  return false
})

const disabledClearSearch = computed(() => {
  return !(filterToSearch.value.criterial && filterToSearch.value.search)
})

// -------------------------------------------------------------------------------------------------------

// WATCH FUNCTIONS -------------------------------------------------------------------------------------
watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10

  getList()
})
// -------------------------------------------------------------------------------------------------------

// TRIGGER FUNCTIONS -------------------------------------------------------------------------------------
onMounted(() => {
  filterToSearch.value.criterial = ENUM_FILTER[0]
  if (useRuntimeConfig().public.loadTableData) {
    getList()
  }
})
// -------------------------------------------------------------------------------------------------------
</script>

<template>
  <div class="grid">
    <div class="col-12">
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
                    @update:model-value="filterToSearch.search = ''"
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
        @on-confirm-create="openCreateDialog"
        @open-edit-dialog="openEditDialog($event)"
        @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="getEventFromTable"
        @on-list-item="resetListItems" @on-sort-field="onSortField"
      />
    </div>

    <div class="12">
      <Dialog
        v-model:visible="dialogConfig.visible"
        modal
        :header="dialogConfig.header || ''"
        class="mx-3 sm:mx-0 sm:w-full md:w-6 lg:w-5"
        content-class="border-round-bottom border-top-1 surface-border p-0"
        @hide="dialogConfig.visible = false"
      >
        <CreateForm
          v-if="isEdit === false"
          :key="dialogReload"
          :list-fields="formConfig"
          :form-options="formOptions"
          @close="onCloseDialog($event)"
          @save="getList"
        />
        <EditForm
          v-if="isEdit === true"
          :key="dialogReload"
          :list-fields="formConfig"
          :form-options="formOptionsToEdit"
          @close="onCloseDialog($event)"
          @save="getList"
        />
      </Dialog>
    </div>
  </div>
</template>

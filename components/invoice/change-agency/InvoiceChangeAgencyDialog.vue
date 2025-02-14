<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { usePrimeVue } from 'primevue/config'
import type { PageState } from 'primevue/paginator'
import { GenericService } from '~/services/generic-services'
import { statusToBoolean } from '~/utils/helpers'
import type { FilterCriteria } from '~/composables/list'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { GenericObject } from '~/types'

const props = defineProps({
  openDialog: {
    type: Boolean,
    required: true
  },
  selectedInvoice: {
    type: Object,
    required: true
  }
})
const emit = defineEmits(['onCloseDialog'])

const $primevue = usePrimeVue()

defineExpose({
  $primevue
})
const toast = useToast()
const dialogVisible = ref(props.openDialog)
const listAgencyByClient = ref<any[]>([])
const payloadOnChangePageChangeAgency = ref<PageState>()
const objLoading = ref({
  loadingAgency: false,
  loadingClient: false,
  loadingHotel: false,
  loadingStatus: false
})

const columnsChangeAgency = ref<IColumn[]>([
  { field: 'code', header: 'Code', type: 'text', width: '90px', sortable: true, showFilter: true },
  { field: 'name', header: 'Name', type: 'text', width: '90px', sortable: true, showFilter: true },
  { field: 'description', header: 'Description', type: 'text', width: '90px', sortable: true, showFilter: true },
])

const optionsOfTableChangeAgency = ref({
  tableName: 'Change Agency',
  moduleApi: 'settings',
  uriApi: 'manage-agency',
  expandableRows: false,
  loading: false,
  showDelete: false,
  showFilters: true,
  actionsAsMenu: false,
  messageToDelete: 'Do you want to save the change?'
})

const payloadChangeAgency = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
  sortBy: 'name',
  sortType: ENUM_SHORT_TYPE.ASC
})

const paginationChangeAgency = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})

function onClose(isCancel: boolean = true) {
  dialogVisible.value = false
  emit('onCloseDialog', isCancel)
}

async function getAgencyByClient(clientId: string) {
  if (optionsOfTableChangeAgency.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return
  }
  try {
    optionsOfTableChangeAgency.value.loading = true
    listAgencyByClient.value = []
    const newListItems = []

    // // Si es RECONCILED o SENT se deben listar solo las agencias del cliente
    // if ([InvoiceStatus.SENT, InvoiceStatus.RECONCILED].includes(props.selectedInvoice.status) && props.selectedInvoice.agency.client) {
    //   payloadChangeAgency.value.filter = [
    //     ...payloadChangeAgency.value.filter,
    //     {
    //       key: 'client.id',
    //       logicalOperation: 'AND',
    //       operator: 'EQUALS',
    //       value: clientId, // props.selectedInvoice.agency.client.id,
    //     }
    //   ]
    // }

    const objFilterByClient = payloadChangeAgency.value.filter.find((item: FilterCriteria) => item.key === 'client.id')
    if (objFilterByClient) {
      objFilterByClient.value = clientId
    }
    else {
      payloadChangeAgency.value.filter.push({
        key: 'client.id',
        operator: 'EQUALS',
        value: clientId,
        logicalOperation: 'AND',
      })
    }

    const response = await GenericService.search(optionsOfTableChangeAgency.value.moduleApi, optionsOfTableChangeAgency.value.uriApi, payloadChangeAgency.value)

    const { data: dataList, page, size, totalElements, totalPages } = response

    paginationChangeAgency.value.page = page
    paginationChangeAgency.value.limit = size
    paginationChangeAgency.value.totalElements = totalElements
    paginationChangeAgency.value.totalPages = totalPages

    const existingIds = new Set(listAgencyByClient.value.map(item => item.id))

    for (const iterator of dataList) {
      if (Object.prototype.hasOwnProperty.call(iterator, 'status')) {
        iterator.status = statusToBoolean(iterator.status)
      }

      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        newListItems.push({
          id: iterator.id,
          name: `${iterator.name}`,
          code: `${iterator.code}`,
          description: `${iterator.description}`,
          status: statusToBoolean(iterator.status),
          client: iterator.client?.id
        })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }
    }

    listAgencyByClient.value = [...listAgencyByClient.value, ...newListItems]
  }
  catch (error) {
    optionsOfTableChangeAgency.value.loading = false
    console.error(error)
  }
  finally {
    optionsOfTableChangeAgency.value.loading = false
  }
}

// TODO: Implement change agency api integration
async function onRowDoubleClickInDataTableForChangeAgency(event: any) {
  if (optionsOfTableChangeAgency.value.loading) {
    return
  }
  try {
    optionsOfTableChangeAgency.value.loading = true
    const payloadChangeAgency: any = {
      agency: event?.id,
    }

    await GenericService.update('invoicing', 'manage-invoice', props.selectedInvoice.id || '', payloadChangeAgency)
    toast.add({ severity: 'info', summary: 'Successful', detail: 'The agency has been changed successfully', life: 3000 })
    onClose(false)
  }
  catch (error) {
    toast.add({ severity: 'error', summary: 'Error', detail: 'The agency could not be changed', life: 3000 })
  }
  finally {
    optionsOfTableChangeAgency.value.loading = false
  }
}

async function parseDataTableFilterForChangeAgency(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columnsChangeAgency.value)
  payloadChangeAgency.value.filter = [...payloadChangeAgency.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
  payloadChangeAgency.value.filter = [...payloadChangeAgency.value.filter, ...parseFilter || []]
  await getAgencyByClient(objClientFormChangeAgency.value.id)
}

function onSortFieldForChangeAgency(event: any) {
  if (event) {
    payloadChangeAgency.value.sortBy = event.sortField
    payloadChangeAgency.value.sortType = event.sortOrder
    parseDataTableFilterForChangeAgency(event.filter)
  }
}

const listClientFormChangeAgency = ref<any[]>([])
const objClientFormChangeAgency = ref<GenericObject>({})
const objApis = ref({
  client: { moduleApi: 'settings', uriApi: 'manage-client' },
  agency: { moduleApi: 'settings', uriApi: 'manage-agency' },
  hotel: { moduleApi: 'settings', uriApi: 'manage-hotel' },
  status: { moduleApi: 'settings', uriApi: 'manage-payment-status' },
  transactionType: { moduleApi: 'settings', uriApi: 'manage-payment-transaction-type' },
})
interface DataListItem {
  id: string
  name: string
  code: string
  status: string
  description?: string
}

interface ListItem {
  id: string
  name: string
  status: boolean | string
  code?: string
  description?: string
}

function mapFunction(data: DataListItem): ListItem {
  return {
    id: data.id,
    name: `${data.code} - ${data.name}`,
    status: data.status,
    code: data.code,
    description: data.description
  }
}

async function getClientList(moduleApi: string, uriApi: string, queryObj: { query: string, keys: string[] }, filter?: FilterCriteria[],) {
  try {
    objLoading.value.loadingClient = true
    let clientTemp: any[] = []
    listClientFormChangeAgency.value = []
    clientTemp = await getDataList<DataListItem, ListItem>(moduleApi, uriApi, filter, queryObj, mapFunction, { sortBy: 'name', sortType: ENUM_SHORT_TYPE.ASC })
    listClientFormChangeAgency.value = [...listClientFormChangeAgency.value, ...clientTemp]
  }
  catch (error) {
    objLoading.value.loadingClient = false
  }
  finally {
    objLoading.value.loadingClient = false
  }
}

watch(payloadOnChangePageChangeAgency, (newValue) => {
  payloadChangeAgency.value.page = newValue?.page ? newValue?.page : 0
  payloadChangeAgency.value.pageSize = newValue?.rows ? newValue.rows : 10
  getAgencyByClient(objClientFormChangeAgency.value.id)
})

watch(() => props.openDialog, (newValue) => {
  dialogVisible.value = newValue
  // if (newValue) {}
})

onMounted(async () => {
  objClientFormChangeAgency.value = props.selectedInvoice.agency.client
  payloadChangeAgency.value.filter = [
    { // no listar la actual
      key: 'id',
      logicalOperation: 'AND',
      operator: 'NOT_EQUALS',
      value: props.selectedInvoice.agency.id,
      type: 'filterSearch'
    },
    {
      key: 'status',
      logicalOperation: 'AND',
      operator: 'EQUALS',
      value: 'ACTIVE',
      type: 'filterSearch'
    },
  ]
  await getAgencyByClient(objClientFormChangeAgency.value.id)
})
</script>

<template>
  <Dialog
    v-model:visible="dialogVisible"
    modal
    class="mx-3 sm:mx-0"
    content-class="border-round-bottom border-top-1 surface-border"
    :style="{ width: '60%' }"
    :pt="{
      root: {
        class: 'custom-dialog-history',
      },
      header: {
        style: 'padding-top: 0.5rem; padding-bottom: 0.5rem',
      },
    }"
    @hide="onClose(true)"
  >
    <template #header>
      <div class="flex align-items-center justify-content-between w-full">
        <div class="flex align-items-center">
          <h5 class="m-0">
            Change Agency
          </h5>
        </div>
        <div class="flex align-items-center">
          <h5 class="m-0 mr-4">
            Invoice: {{ props.selectedInvoice.invoiceId }}
          </h5>
        </div>
      </div>
    </template>
    <template #default>
      <div class="p-fluid pt-3">
        <!-- // Label -->
        <!--        <pre>{{ props.selectedInvoice }}</pre> -->
        <div class="flex justify-content-between mb-2">
          <div v-if="true" class="flex align-items-center mb-3">
            <div class="mr-2">
              <label for="autocomplete" class="font-semibold"> Client: </label>
            </div>
            <div class="mr-4">
              <!-- <pre>{{ objClientFormChangeAgency }}</pre> -->
              <DebouncedAutoCompleteComponent
                id="autocomplete"
                class="w-29rem"
                field="name"
                item-value="id"
                :model="objClientFormChangeAgency"
                :suggestions="[...listClientFormChangeAgency]"
                @change="async ($event) => {
                  objClientFormChangeAgency = $event
                  if ($event && $event.id) {
                    await getAgencyByClient($event.id)
                  }
                }"
                @load="async($event) => {
                  const objQueryToSearch = {
                    query: $event,
                    keys: ['name', 'code'],
                  }
                  const filter: FilterCriteria[] = [{
                    key: 'status',
                    logicalOperation: 'AND',
                    operator: 'EQUALS',
                    value: 'ACTIVE',
                  }]
                  await getClientList(objApis.client.moduleApi, objApis.client.uriApi, objQueryToSearch, filter)
                }"
              />
            </div>
          </div>
          <div v-if="false" class="bg-primary w-auto h-2rem flex align-items-center px-2" style="border-radius: 5px">
            <strong class="mr-2 w-auto">Client:</strong>
            <span class="w-auto text-white font-semibold">{{ props.selectedInvoice.agency?.client.code ?? '' }} - {{ props.selectedInvoice.agency?.client.name ?? '' }}</span>
          </div>
          <div class="bg-primary w-auto h-2rem flex align-items-center px-2" style="border-radius: 5px">
            <strong class="mr-2 w-auto">Current Agency:</strong>
            <span class="w-auto text-white font-semibold">{{ props.selectedInvoice.agency.code }} - {{ props.selectedInvoice.agency.name }}</span>
          </div>
        </div>

        <DynamicTable
          class="card p-0"
          :data="listAgencyByClient"
          :columns="columnsChangeAgency"
          :options="optionsOfTableChangeAgency"
          :pagination="paginationChangeAgency"
          @on-sort-field="onSortFieldForChangeAgency"
          @on-change-filter="parseDataTableFilterForChangeAgency"
          @on-change-pagination="payloadOnChangePageChangeAgency = $event"
          @on-row-double-click="onRowDoubleClickInDataTableForChangeAgency"
        />
      </div>
      <div v-if="false" class="flex justify-content-end">
        <div>
          <!-- idInvoicesSelectedToApplyPaymentForOtherDeduction.length === 0 -->
          <Button v-tooltip.top="'Cancel'" class="w-3rem" icon="pi pi-times" severity="secondary" @click="onClose()" />
        </div>
      </div>
    </template>
  </Dialog>
</template>

<style scoped>
</style>

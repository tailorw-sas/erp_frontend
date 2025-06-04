<script setup lang="ts">
import { ref, computed , watch } from 'vue'
import { useToast } from 'primevue/usetoast'
import { GenericService } from '~/services/generic-services'
import DynamicTable from '@/components/table/DynamicTable.vue'
import type { PageState } from 'primevue/paginator'


const listAgencyByClient = ref<any[]>([]);
const currentContext = ref<'payment' | 'invoice'>('payment');
const currentClient = ref<any>(null);
const currentAgency = ref<any>(null);
const currentTarget = ref<any>(null);
const agencies = ref<any[]>([]);
const loading = ref(false);

const selectedItem = ref<any>(null) 

interface Props {
  visible: boolean;
  context: 'payment' | 'invoice';
  targetObject: any;
  clientInfo: any;
  agenciesList: any[];
  columnsChangeAgency: any[];
  paginationChangeAgency: IPagination;
  optionsOfTableChangeAgency: any;
  clientId: string;
  currentAgency?: any;
  respectClient: boolean;
}

interface IPagination {
  page: number
  limit: number
  totalElements: number
  totalPages: number
  search: string
}

const props = defineProps<Props>();
const emit = defineEmits([
  'update:visible',
  'close',
  'agency-changed',
  'sort-field',
  'change-filter',
  'change-pagination',
  'update:payloadOnChangePageChangeAgency',
  'agencies-loaded',
])

const toast = useToast()

// Configuración de la tabla
const columnsChangeAgency = ref<IColumn[]>([
  { field: 'code', header: 'Code', type: 'text', width: '90px', sortable: true, showFilter: true },
  { field: 'name', header: 'Agency Name', type: 'text', width: '90px', sortable: true, showFilter: true },
  { field: 'description', header: 'Description', type: 'text', width: '90px', sortable: true, showFilter: true },
  { field: 'clientName', header: 'Client Name', type: 'text', width: '90px', sortable: true, showFilter: true },
  { field: 'clientId', header: 'Client ID', type: 'text', width: '90px', sortable: true, showFilter: true },
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

const pagination = computed({
  get: () => props.paginationChangeAgency,
  set: (val) => emit('update:paginationChangeAgency', val)
})

const currentIdLabel = computed(() =>
  props.context === 'payment' ? 'Payment ID' : 'Invoice ID'
)

const currentFormattedId = computed(() => {
  const item = props.targetObject

  if (!item) return 'N/A'

  if (props.context === 'payment') {
    return item.paymentInternalId ||
           item.paymentId ||
           item.id?.replace(/\D/g, '').slice(-4) ||
           'N/A'
  } else {
    return item.invoiceNumber || `INV-${item.id?.slice(0, 5)}` || 'N/A'
  }
})

const closeModal = () => {
  emit('close')
}

const onRowDoubleClickInDataTableForChangeAgency = async (event: any) => {
  if (optionsOfTableChangeAgency.value.loading) return
  
  try {
    optionsOfTableChangeAgency.value.loading = true
    
    const payload = {
      agencyId: event.id,
      ...props.targetObject,
      // Campos específicos para payment o invoice
      ...(props.context === 'payment' ? {
        payment: props.targetObject.id,
        paymentSource: props.targetObject.paymentSource?.id,
        paymentStatus: props.targetObject.paymentStatus?.id
      } : {
        invoice: props.targetObject.id,
        invoiceStatus: props.targetObject.invoiceStatus?.id
      })
    }

    // Llamada al servicio genérico
    await GenericService.update(
      props.context === 'payment' ? 'payment' : 'invoice',
      'change-agency',
      props.targetObject.id,
      payload
    )

    toast.add({
      severity: 'success',
      summary: 'Success',
      detail: 'Agency changed successfully',
      life: 3000
    })

    emit('agency-changed', {
      newAgency: {
        id: event.id,
        name: event.name,
        code: event.code
      },
      targetId: props.targetObject.id,
      context: props.context
    })
    
    closeModal()
    
  } catch (error: any) {
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: error?.data?.data?.error?.errors[0]?.message || 
             'Failed to change agency',
      life: 3000
    })
  } finally {
    optionsOfTableChangeAgency.value.loading = false
  }
}

const handlePaginationChange = (event: PageState) => {
  emit('update:payloadOnChangePageChangeAgency', {
    page: event.page,
    pageSize: event.rows // <- Enviar tamaño de página correctamente
  })
    getAgencyByClient(); // Llama de inmediato

}

// Funciones de ordenamiento y filtrado
const onSortFieldForChangeAgency = (sortField: string) => {
  optionsOfTableChangeAgency.value.sortField = sortField
  optionsOfTableChangeAgency.value.sortOrder *= -1
}


const getAgencyByClient = async () => {
  if (optionsOfTableChangeAgency.value.loading) return;

  try {
    optionsOfTableChangeAgency.value.loading = true;

    const filters = [
      { key: 'status', operator: 'EQUALS', value: 'ACTIVE', logicalOperation: 'AND' },
    ];

    if (props.respectClient && props.clientId) {
      filters.push({
        key: 'client.id',
        operator: 'EQUALS',
        value: props.clientId,
        logicalOperation: 'AND',
      });
    }

    // Evitar mostrar la agencia actual
    if (props.currentAgency?.id) {
      filters.push({
        key: 'id',
        operator: 'NOT_EQUALS',
        value: props.currentAgency.id,
        logicalOperation: 'AND',
      });
    }

    const payload = {
      filter: filters,
      query: '',
      pageSize: props.paginationChangeAgency.limit,
      page: props.paginationChangeAgency.page,
      sortBy: 'createdAt',
      sortType: 'DESC',
    };

    const response = await GenericService.search(
      optionsOfTableChangeAgency.value.moduleApi,
      optionsOfTableChangeAgency.value.uriApi,
      payload
    );

    const dataList = response.data;
    const newListItems = dataList.map((iterator: any) => ({
      id: iterator.id,
      name: iterator.name,
      code: iterator.code,
      description: iterator.description,
      status: statusToBoolean(iterator.status),
      clientId: iterator.client?.internalId || iterator.client?.id?.replace(/\D/g, '').slice(-5) || 'N/A',
      clientName: iterator.client?.name ?? 'N/A',
    }));

    agencies.value = newListItems;
    emit('agencies-loaded', newListItems)

    // Actualizar paginación
    pagination.value.totalElements = response.totalElements
    pagination.value.totalPages = response.totalPages

  } catch (error) {
    console.error('Error loading agencies:', error);
    toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudieron cargar las agencias.', life: 3000 });
  } finally {
    optionsOfTableChangeAgency.value.loading = false;
  }
}


defineExpose({
  getAgencyByClient,
  agencies,
   selectedItem
})

watch(
  () => props.visible,
  (visible) => {
    if (visible) {
      getAgencyByClient()
    }
  }
)
</script>

<template>
  <Dialog
    :visible="visible"
    :style="{ width: '60%' }"
    :modal="true"
    @update:visible="$emit('update:visible', $event)"
    @hide="$emit('close')"
  >
    <template #header>
      <div class="flex align-items-center justify-content-between w-full">
        <h5 class="m-0">Change Agency</h5>
        <div>
          <span class="font-bold">{{ currentIdLabel }}:</span>
          {{ currentFormattedId }}
        </div>
      </div>
    </template>

    <div class="p-fluid pt-3">
      <div class="flex justify-content-between mb-2">
        <div class="bg-primary w-auto h-2rem flex align-items-center px-2" style="border-radius: 5px">
          <strong class="mr-2 w-auto">Client:</strong>
          <span class="w-auto text-white font-semibold">{{ clientInfo?.name }}</span>
        </div>
        <div class="bg-primary w-auto h-2rem flex align-items-center px-2" style="border-radius: 5px">
          <strong class="mr-2 w-auto">Current Agency:</strong>
          <span class="w-auto text-white font-semibold">{{ currentAgency?.name }}</span>
        </div>
      </div>
      
      <div class="card p-0">
        <DynamicTable
          :data="agencies"
          :columns="columnsChangeAgency"
          :options="optionsOfTableChangeAgency"
          :pagination="pagination"
          @on-sort-field="onSortFieldForChangeAgency"
          @on-change-pagination="handlePaginationChange"
          @on-row-double-click="onRowDoubleClickInDataTableForChangeAgency"
        />
      </div>
    </div>

    <template #footer>
      <Button
        label="Cancel"
        icon="pi pi-times"
        class="p-button-text"
        @click="$emit('close')"
      />
    </template>
  </Dialog>
</template>
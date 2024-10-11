<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useToast } from 'primevue/usetoast'

import dayjs from 'dayjs'
import type { PageState } from 'primevue/paginator'
import { GenericService } from '~/services/generic-services'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'

import type { IData } from '~/components/table/interfaces/IModelData'

const idItemToLoadFirstTime = ref('')
const toast = useToast()
const listItems = ref<any[]>([])
const selectedElements = ref<string[]>([])
const startOfMonth = ref<any>(null)
const endOfMonth = ref<any>(null)
const filterAllDateRange = ref(false)
const loadingSearch = ref(false)

const loadingSaveAll = ref(false)

const allDefaultItem = { id: 'All', name: 'All', code: 'All' }

const filterToSearch = ref<IData>({
  criteria: null,
  search: '',
  allFromAndTo: false,
  agency: [allDefaultItem],
  hotel: [allDefaultItem],
  from: dayjs(new Date()).startOf('month').toDate(),
  to: dayjs(new Date()).endOf('month').toDate(),
})

const hotelList = ref<any[]>([])
const attachList = ref<any[]>([])
const resourceList = ref<any[]>([])
const agencyList = ref<any[]>([])

const { data: userData } = useAuth()
const confApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-booking/import',
})

const confHotelApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})
const confAgencyApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})

// VARIABLES -----------------------------------------------------------------------------------------

//
const idItem = ref('')
const ENUM_FILTER = [
  { id: 'invoiceId', name: 'Invoice Id' },
]

const confhotelListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-hotel',
})

const confagencyListApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-agency',
})

const confAttachApi = reactive({
  moduleApi: 'settings',
  uriApi: 'manage-attachment-type',
})
const confResourceApi = reactive({
  moduleApi: 'payment',
  uriApi: 'resource-type',
})
const confReconcileApi = reactive({
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice/reconcile-manual',
})
// -------------------------------------------------------------------------------------------------------
/*const columns: IColumn[] = [
  { field: 'invoiceId', header: 'Invoice Id', type: 'text' },
  { field: 'hotel', header: 'Hotel', type: 'select', objApi: confhotelListApi },
  { field: 'invoiceNumber', header: 'Invoice No', type: 'text' },
  { field: 'agency', header: 'Agency', type: 'select', objApi: confagencyListApi },
  { field: 'invoiceDate', header: 'Generation Date', type: 'date' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text' },
  { field: 'reconcilestatus', header: 'Rec Status', type: 'slot-text' },
  { field: 'status', header: 'Status', width: '100px', frozen: true, type: 'slot-select', localItems: ENUM_INVOICE_STATUS, sortable: true },
]
*/
const columns: IColumn[] = [
  { field: 'invoiceId', header: 'Id', type: 'text', width: '6%' },
  { field: 'hotel', header: 'Hotel', type: 'select', objApi: confhotelListApi, width: '15%' },
  { field: 'invoiceNumber', header: 'Inv. No', type: 'text', width: '8%' },
  { field: 'agency', header: 'Agency', type: 'select', objApi: confagencyListApi, width: '15%' },
  { field: 'invoiceDate', header: 'Gen.  Date', type: 'date', width: '12%' },
  { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text', width: '14%' },
  { field: 'message', header: 'Rec Status', type: 'slot-text', width: '15%' },
  { field: 'status', header: 'Status', width: '100px', frozen: true,showFilter: false, type: 'slot-select', localItems: ENUM_INVOICE_STATUS, sortable: true },
]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
  tableName: 'Reconcile Manual',
  moduleApi: 'invoicing',
  uriApi: 'manage-invoice',
  loading: false,
  showDelete: false,
  selectionMode: 'multiple' as 'multiple' | 'single',
  selectAllItemByDefault: false,
  showFilters: true,
  expandableRows: false,
  messageToDelete: 'Do you want to save the change?'
})

const payload = ref<IQueryRequest>({
  filter: [],
  query: '',
  pageSize: 10,
  page: 0,
   sortBy: 'invoiceId',
   sortType: ENUM_SHORT_TYPE.ASC
})

const payloadOnChangePage = ref<PageState>()
const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
// -------------------------------------------------------------------------------------------------------
async function onMultipleSelect(data: any) {
  selectedElements.value = data
  console.log(selectedElements.value,'que hay aqui')
}
// FUNCTIONS ---------------------------------------------------------------------------------------------
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
    payload.value.filter = [...payload.value.filter, {
      key: 'invoiceStatus',
      operator: 'IN',
      value: ['PROCECSED'],
      logicalOperation: 'AND'
    },
    /*{
      key: 'invoiceDate',
      operator: 'GREATER_THAN_OR_EQUAL_TO',
      value: dayjs(filterToSearch.value.from).startOf('day').format('YYYY-MM-DD'),
      logicalOperation: 'AND',
      type: 'filterSearch'
    },{
      key: 'invoiceDate',
      operator: 'LESS_THAN_OR_EQUAL_TO',
      value:  dayjs(filterToSearch.value.to).endOf('day').format('YYYY-MM-DD'),
      logicalOperation: 'AND',
      type: 'filterSearch'
    }*/
   ]

    // Agregar filtros de fecha solo si se especifican
    if (filterToSearch.value.from) {
      payload.value.filter.push({
        key: 'invoiceDate',
        operator: 'GREATER_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.from).startOf('day').format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      });
    }

    if (filterToSearch.value.to) {
      payload.value.filter.push({
        key: 'invoiceDate',
        operator: 'LESS_THAN_OR_EQUAL_TO',
        value: dayjs(filterToSearch.value.to).endOf('day').format('YYYY-MM-DD'),
        logicalOperation: 'AND',
        type: 'filterSearch'
      });
    }
  

    const response = await GenericService.search(options.value.moduleApi, options.value.uriApi, payload.value)
    console.log(response.data);
    

    const { data: dataList, page, size, totalElements, totalPages } = response

    pagination.value.page = page
    pagination.value.limit = size
    pagination.value.totalElements = totalElements
    pagination.value.totalPages = totalPages

    const existingIds = new Set(listItems.value.map(item => item.id))

    for (const iterator of dataList) {
      // Verificar si el ID ya existe en la lista
      if (!existingIds.has(iterator.id)) {
        let invoiceNumber
        if (iterator?.invoiceNumber?.split('-')?.length === 3) {
          invoiceNumber = `${iterator?.invoiceNumber?.split('-')[0]}-${iterator?.invoiceNumber?.split('-')[2]}`
        } else {
          invoiceNumber = iterator?.invoiceNumber
        }
        newListItems.push({
          ...iterator, 
          loadingEdit: false, 
          loadingDelete: false, 
         // invoiceDate: new Date(iterator?.invoiceDate), 
          agencyCd: iterator?.agency?.code, 
          dueAmount: iterator?.dueAmount || 0, 
          invoiceNumber: invoiceNumber ?  invoiceNumber.replace("OLD", "CRE") : '',


          hotel: { ...iterator?.hotel, name: `${iterator?.hotel?.code || ""}-${iterator?.hotel?.name || ""}` },
    
        })
        existingIds.add(iterator.id) // Añadir el nuevo ID al conjunto
      }

      
     
    }

    listItems.value = [...listItems.value, ...newListItems]
    return listItems
  }
  
  catch (error) {
    console.error(error)
  }
  finally {
    options.value.loading = false
  }
}


async function getAttach() {
  try {
    const payload = {
      filter: [
        {
          key: 'attachInvDefault',
          operator: 'EQUALS',
          value: true,
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    };

    const response= await GenericService.search(confAttachApi.moduleApi, confAttachApi.uriApi, payload);
    const { data: dataList } = response;

    // Inicializar la lista de adjuntos
    

    if (dataList.length > 0) {
      const attachId = dataList[0].id; // Tomar solo el primer ID
      console.log(attachId, 'resource id');
      return attachId; // Devolver solo el ID
    } else {
      return null; // Devolver null si no hay recursos
    }
  }
     catch (error) {
    console.error('Error loading attachments:', error);
    return null; // Devolver un arreglo vacío en caso de error
  }
}

async function getResource() {
  try {
    const payload = {
      filter: [
        {
          key: 'invoice',
          operator: 'EQUALS',
          value: true,
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    };

    const response = await GenericService.search(confResourceApi.moduleApi, confResourceApi.uriApi, payload);
    const { data: dataList } = response;

    // Verificar si hay resultados
    if (dataList.length > 0) {
      const resourceId = dataList[0].id; // Tomar solo el primer ID
      console.log(resourceId, 'resource id');
      return resourceId; // Devolver solo el ID
    } else {
      return null; // Devolver null si no hay recursos
    }
  } catch (error) {
    console.error('Error loading resource:', error);
    return null; // Devolver null en caso de error
  }
}


async function getHotelList(query: string = '') {
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
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confhotelListApi.moduleApi, confhotelListApi.uriApi, payload)
    const { data: dataList } = response
    hotelList.value = [allDefaultItem]
    for (const iterator of dataList) {
      hotelList.value = [...hotelList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function reconcileManual() {
  try {
    loadingSaveAll.value = true;
 
    const attachmentIds = await getAttach(); // Obtener arreglo de IDs
    const resource  = await getResource();
   
    // Asegúrate de que attachInvDefault tenga solo un ID o esté vacío
   // const attachInvDefault = attachmentIds.length > 0 ? attachmentIds[0] : null; // Solo tomar un ID
    //const resourceType = resource.length > 0 ? resource[0] : null; // Solo tomar un ID
 
    // Extraer el ID del attachment y asegurarte de que esté en un arreglo

    // Crear el payload inicial
    const payload: any = {
      invoices: [], // Inicializar el array de facturas
      employeeName: userData?.value?.user?.name || 'Sin nombre',
      employeeId: userData?.value?.user?.userId || 'Sin ID',
      attachInvDefault: attachmentIds,
      resourceType: resource
    };

    // Utilizar selectedElements.value como el array de facturas
    const invoicesFromState = selectedElements.value;

    // Llenar el array de facturas
    if (invoicesFromState && Array.isArray(invoicesFromState)) {
      payload.invoices = invoicesFromState; // Asignar directamente
    }

    // Enviar el payload a la API
    return await GenericService.create(confReconcileApi.moduleApi, confReconcileApi.uriApi, payload);
  } catch (error: any) {
    console.error('Error en reconcileManual:', error?.data?.data?.error?.errorMessage);
    toast.add({
      severity: 'error',
      summary: 'Error',
      detail: error?.data?.data?.error?.errorMessage || error?.message,
      life: 10000,
    });
  } finally {
    loadingSaveAll.value = false;
  }
}

/*
async function saveItem() {
  loadingSaveAll.value = true;

  // Llamar a reconcileManual y guardar la respuesta
  let response: any;
  try {
    response = await reconcileManual();
  } catch (error) {
    toast.add({ 
      severity: 'error', 
      summary: 'Error', 
      detail: error?.data?.data?.error?.errorMessage || 'An error occurred while reconciling invoices.', 
      life: 10000 
    });
  }

  // Comprobar si la respuesta contiene errorsResponse
  const { errorsResponse, totalInvoicesRec} = response; // Asumiendo que la respuesta tiene una propiedad 'errors'

  // Si no hay errores, navegar y mostrar mensaje de éxito
  if (errorsResponse && errorsResponse.length === 0) {
     // Navegar a la página de invoice
     navigateTo('/invoice'); // Define la función para navegar a la página deseada
    toast.add({ 
      severity: 'info', 
      summary: 'Confirmed', 
      detail: `The invoices have been reconciled successfully. Total invoices reconciled: ${totalInvoicesRec}`, 
      life: 0 
    });

   
  } else if (errorsResponse && errorsResponse.length > 0) {
    // Si hay errores, llamar a la función getErrors para mostrar los errores
    getErrors(errorsResponse);
 
  }

  loadingSaveAll.value = false;
}
*/
async function saveItem() {
  loadingSaveAll.value = true;

  // Llamar a reconcileManual y guardar la respuesta
  let response: any;
  try {
    response = await reconcileManual();
  } catch (error) {
    toast.add({ 
      severity: 'error', 
      summary: 'Error', 
      detail: error?.data?.data?.error?.errorMessage || 'An error occurred while reconciling invoices.', 
      life: 10000 
    });
    loadingSaveAll.value = false; // Detener el loading en caso de error
    return; // Salir de la función si hay un error
  }

  // Comprobar si la respuesta contiene errorsResponse y totalInvoicesRec
  const { errorsResponse, totalInvoicesRec } = response;

  // Validaciones según tus requisitos
  if (errorsResponse && errorsResponse.length === 0) {
    // Si no hay errores y totalInvoicesRec es 0
    if (totalInvoicesRec === 0) {
      // No mostrar mensaje ni redirección, solo llamar a getErrors
      getErrors(errorsResponse);
    } else {
      // Si totalInvoicesRec es mayor que 0
      navigateTo('/invoice'); // Navegar a la página de invoice
      toast.add({ 
        severity: 'info', 
        summary: 'Confirmed', 
        detail: `The invoices have been reconciled successfully. Total invoices reconciled: ${totalInvoicesRec}`, 
        life: 0 
      });
     
    }
  } else if (errorsResponse && errorsResponse.length > 0) {
    // Si hay errores y totalInvoicesRec es mayor que 0
    if (totalInvoicesRec > 0) {
      toast.add({ 
        severity: 'info', 
        summary: 'Confirmed', 
        detail: `The invoices have been reconciled successfully. Total invoices reconciled: ${totalInvoicesRec}`, 
        life: 0 
      });
      // No redirigir, solo mostrar errores
    }
    // Llamar a getErrors para mostrar los errores
    getErrors(errorsResponse);
  }

  loadingSaveAll.value = false; // Detener el loading al final de la función
}

async function getErrors(errorsResponse:any) {
  if (options.value.loading) {
    // Si ya hay una solicitud en proceso, no hacer nada.
    return;
  }
  
  try {
    idItemToLoadFirstTime.value = '';
    options.value.loading = true;
    listItems.value = []; // Limpiar la lista antes de agregar nuevos elementos

    const newListItems = errorsResponse.map((error: { invoiceId: any; message: any ,agency:any,hotel:any,invoiceDate:any,status:any,invoiceNo:any,invoiceAmount:any}) => ({
      invoiceId: error.invoiceId, // Asignar invoiceId
      hotel:error.hotel,
      agency:error.agency,
      invoiceDate:error.invoiceDate,
      status:error.status,
      invoiceAmount: error.invoiceAmount.toString(),
      invoiceNumber:error.invoiceNo,
      message: error.message, // Asignar message 
      loadingEdit: false,
      loadingDelete: false,
      
    }));

    // Llenar listItems con los nuevos errores
    listItems.value = newListItems;

    return listItems;
  } catch (error) {
    console.error(error);
  } finally {
    options.value.loading = false;
  }
}



async function getAgencyList(query: string = '') {
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
        },
        {
          key: 'autoReconcile',
          operator: 'EQUALS',
          value: true,
          logicalOperation: 'AND'
        }
      ],
      query: '',
      pageSize: 20,
      page: 0,
      sortBy: 'createdAt',
      sortType: ENUM_SHORT_TYPE.DESC
    }

    const response = await GenericService.search(confagencyListApi.moduleApi, confagencyListApi.uriApi, payload)
    const { data: dataList } = response
    agencyList.value = [allDefaultItem]
    for (const iterator of dataList) {
      agencyList.value = [...agencyList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
    }
  }
  catch (error) {
    console.error('Error loading hotel list:', error)
  }
}

async function clearForm() {
  navigateTo('/invoice')
}

async function resetListItems() {
  payload.value.page = 0
  getList()
}
function getStatusName(code: string) {
  switch (code) {
    case 'PROCECSED': return 'Processed'

    case 'RECONCILED': return 'Reconciled'
    case 'SENT': return 'Sent'
    case 'CANCELED': return 'Canceled'
    case 'PENDING': return 'Pending'

    default:
      return ''
  }
}
function getStatusBadgeBackgroundColor(code: string) {
  switch (code) {
    case 'PROCECSED': return '#FF8D00'
    case 'RECONCILED': return '#005FB7'
    case 'SENT': return '#006400'
    case 'CANCELED': return '#F90303'
    case 'PENDING': return '#686868'

    default:
      return '#686868'
  }
}

async function parseDataTableFilter(payloadFilter: any) {
  const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
  if (parseFilter && parseFilter?.length > 0) {
    for (let i = 0; i < parseFilter?.length; i++) {

   
   /*   if (parseFilter[i]?.key === 'status') {
        parseFilter[i].key = 'invoiceStatus'
      }
*/

      if (parseFilter[i]?.key === 'invoiceNumber') {
        parseFilter[i].key = 'invoiceNumberPrefix'
      }

    }
  }

  payload.value.filter = [...parseFilter || []]
  getList()
  
}

function onSortField(event: any) {
  if (event) {
    if (event.sortField === 'hotel') {
      event.sortField = 'hotel.name'
    }
    if (event.sortField === 'agency') {
      event.sortField = 'agency.name'
    }
    if (event.sortField === 'invoiceNumber') {
      event.sortField = 'invoiceNumberPrefix'
    }
    payload.value.sortBy = event.sortField
    payload.value.sortType = event.sortOrder
    getList()
  }
}

async function searchAndFilter() {
  const newPayload: IQueryRequest = {
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.ASC
  };
  // Mantener los filtros existentes
  newPayload.filter = [
    ...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')
  ];
// Filtro por el ID de la factura basado en el criterio seleccionado
if (filterToSearch.value.criterial && filterToSearch.value.search) {
    newPayload.filter.push({
      key: filterToSearch.value.criterial.id, // Utiliza el id del criterio seleccionado
      operator: 'LIKE', // Cambia a 'LIKE' si es necesario para tu búsqueda
      value: filterToSearch.value.search,
      logicalOperation: 'AND',
      type: 'filterSearch',
    });
  }


  // Filtros de rango de fechas usando 'from' y 'to'
  if (filterToSearch.value.from) {
    newPayload.filter.push({
      key: 'invoiceDate',
      operator: 'GREATER_THAN_OR_EQUAL_TO',
      value: dayjs(filterToSearch.value.from).startOf('day').format('YYYY-MM-DD'),
      logicalOperation: 'AND',
      type: 'filterSearch'
    });
  }
  if (filterToSearch.value.to) {
    newPayload.filter.push({
      key: 'invoiceDate',
      operator: 'LESS_THAN_OR_EQUAL_TO',
      value:  dayjs(filterToSearch.value.to).endOf('day').format('YYYY-MM-DD'),
      logicalOperation: 'AND',
      type: 'filterSearch'
    });
  }
  // Filtro por invoiceType con valor INVOICE
  newPayload.filter.push({
    key: 'invoiceType',
    operator: 'EQUALS',
    value: 'INVOICE', // Valor específico para el filtro
    logicalOperation: 'AND',
    type: 'filterSearch',
  });

  // Filtrar agencias que tienen autoReconcile en true
  if (filterToSearch.value.agency?.length > 0) {
    const selectedAgencyIds = filterToSearch.value.agency
      .filter((item: any) => item?.id !== 'All')
      .map((item: any) => item?.id);
    
    if (selectedAgencyIds.length > 0) {
      newPayload.filter.push({
        key: 'agency.id',
        operator: 'IN',
        value: selectedAgencyIds,
        logicalOperation: 'AND',
        type: 'filterSearch'
      });
    }
  }

  // Siempre agregar el filtro para autoReconcile en true
 newPayload.filter.push({
    key: 'agency.autoReconcile',
    operator: 'EQUALS',
    value: true,
    logicalOperation: 'AND',
    type: 'filterSearch'
  });


  // Filtros de hoteles
  if (filterToSearch.value.hotel?.length > 0) {
    const selectedHotelIds = filterToSearch.value.hotel
      .filter((item: any) => item?.id !== 'All')
      .map((item: any) => item?.id);
    
    if (selectedHotelIds.length > 0) {
      newPayload.filter.push({
        key: 'hotel.id',
        operator: 'IN',
        value: selectedHotelIds,
        logicalOperation: 'AND',
        type: 'filterSearch'
      });
    }
  }

  // Incluir el filtro para el estado de la factura
  newPayload.filter.push({
    key: 'invoiceStatus',
    operator: 'IN',
    value: ['PROCECSED'], // Asegúrate de que esté correctamente escrito
    logicalOperation: 'AND'
  });

  payload.value = newPayload;
   // Obtener la lista de facturas
   options.value.selectAllItemByDefault = true
   const dataList = await getList();


   // Seleccionar automáticamente todos los elementos retornados
  if (dataList && dataList.value.length > 0) {
    selectedElements.value = dataList.value; // Llenar selectedElements con los elementos obtenidos
  } else {
    selectedElements.value = []; // Asegurarse de que esté vacío si no hay resultados
  }

// Verificar si no hay resultados
if (!dataList || dataList.value.length === 0) {
  toast.add({
      severity:'info',
      summary: 'Confirmed',
      detail: `No invoices available in processed status `,
      life: 0 // Duración del toast en milisegundos
    });
}

}

function clearFilterToSearch() {
  // Limpiar los filtros existentes
  payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type !== 'filterSearch')];

  // Reiniciar los valores de búsqueda a sus estados iniciales
  filterToSearch.value = {
    criterial: ENUM_FILTER[0], // Mantener el primer elemento del enum como valor predeterminado
    search: '', // Dejar el campo de búsqueda en blanco
    agency: [allDefaultItem], // Restablecer a valor predeterminado
    hotel: [allDefaultItem], // Restablecer a valor predeterminado
    from: dayjs(new Date()).startOf('month').toDate(),
    to: dayjs(new Date()).endOf('month').toDate(),
  };
selectedElements.value = [];
 listItems.value = [];
 pagination.value.totalElements=0
 
}

const disabledSearch = computed(() => {
  // return !(filterToSearch.value.criteria && filterToSearch.value.search)
  return false
})

watch(payloadOnChangePage, (newValue) => {
  payload.value.page = newValue?.page ? newValue?.page : 0
  payload.value.pageSize = newValue?.rows ? newValue.rows : 10

  getList()
})

onMounted(async () => {
  filterToSearch.value.criterial = ENUM_FILTER[0]

  // getList()
})
</script>

<template>
  <div class="grid">
    <div class="col-12 order-0 w-full md:order-1 md:col-6 xl:col-9">
      <div class=" p-0">
        <Accordion :active-index="0" class="mb-2">
          <AccordionTab>
            <template #header>
              <div class="text-white font-bold custom-accordion-header flex justify-content-between w-full align-items-center">
                <div>
                  Invoice to Reconcile Manual
                </div>
              </div>
            </template>

            <div class="grid">
              <div class="col-12 md:col-6 lg:col-3 flex pb-0">
                <div class="flex flex-column gap-2 w-full">
                  <div class="flex align-items-center gap-2 w-full" style=" z-index:5 ">
                    <label class="filter-label font-bold" for="">Agency:</label>
                    <div class="w-full" style=" z-index:5 ">
                      <DebouncedAutoCompleteComponent
                        v-if="!loadingSaveAll" id="autocomplete"
                        :multiple="true" class="w-full" field="name"
                        item-value="id" :model="filterToSearch.agency" :suggestions="agencyList"
                        @load="($event) => getAgencyList($event)" @change="($event) => {
                          if (!filterToSearch.agency.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                            filterToSearch.agency = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {
                            filterToSearch.agency = $event.filter((element: any) => element?.id !== 'All')
                          }
                        }"
                      >
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2">
                    <label class="filter-label font-bold ml-3" for="">Hotel:</label>
                    <div class="w-full">
                      <DebouncedAutoCompleteComponent
                        v-if="!loadingSaveAll" id="autocomplete"
                        :multiple="true" class="w-full" field="name"
                        item-value="id" :model="filterToSearch.hotel" :suggestions="hotelList"
                        @load="($event) => getHotelList($event)" @change="($event) => {
                          if (!filterToSearch.hotel.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                            filterToSearch.hotel = $event.filter((element: any) => element?.id === 'All')
                          }
                          else {
                            filterToSearch.hotel = $event.filter((element: any) => element?.id !== 'All')
                          }
                        }"
                      >
                        <template #option="props">
                          <span>{{ props.item.code }} - {{ props.item.name }}</span>
                        </template>
                      </DebouncedAutoCompleteComponent>
                    </div>
                  </div>
                </div>
              </div>

              <div class="col-12 md:col-6 lg:col-2 flex pb-0">
                <div class="flex flex-column gap-2 w-full">
                  <div class="flex align-items-center gap-2" style=" z-index:5 ">
                    <label class="filter-label font-bold" for="">From:</label>
                    <div class="w-full" style=" z-index:5 ">
                      <Calendar
                        v-model="filterToSearch.from"  date-format="yy-mm-dd" icon="pi pi-calendar-plus"
                        show-icon icon-display="input" class="w-full"  :min-date="new Date(startOfMonth)"   :max-date="filterToSearch.to ? new Date(filterToSearch.to) : new Date(endOfMonth)"
                      />
                    </div>
                  </div>
                  <div class="flex align-items-center gap-2 ml-4">
                    <label class="filter-label font-bold" for="">To:</label>
                    <div class="w-full">
                      <Calendar
                        v-model="filterToSearch.to"  date-format="yy-mm-dd" icon="pi pi-calendar-plus" show-icon
                        icon-display="input" class="w-full"   :min-date="filterToSearch.from ? new Date(filterToSearch.from) : new Date(startOfMonth)"
                       
                   
                      />
                    </div>
                  </div>
                </div>

              
              </div>
              <div class="col-12 md:col-6 lg:col-2 flex pb-0">
                <div class="flex w-full">
                  <div class="flex flex-row w-full">
                    <div class="flex flex-column gap-2 w-full">
                      <div class="flex align-items-center gap-2" style=" z-index:5 ">
                        <label class="filter-label font-bold" for="">Criteria:</label>
                        <div class="w-full">
                          <Dropdown
                            v-model="filterToSearch.criterial" :options="[...ENUM_FILTER]" option-label="name"
                            placeholder="Criteria" return-object="false" class="align-items-center w-full" show-clear
                          />
                        </div>
                      </div>
                      <div class="flex align-items-center gap-2">
                        <label class="filter-label font-bold ml-1" for="">Search:</label>
                        <div class="w-full">
                          <IconField icon-position="left">
                            <InputText v-model="filterToSearch.search" type="text" style="width: 100% !important;" />
                            <InputIcon class="pi pi-search"  />
                          </IconField>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="flex align-items-center ">
                <Button
                  v-tooltip.top="'Search'" class="w-3rem mx-2 " icon="pi pi-search" :disabled="disabledSearch"
                  :loading="loadingSearch" @click="searchAndFilter"
                />
                <Button
                  v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash"
                  :loading="loadingSearch" @click="clearFilterToSearch"
                />
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
        @on-confirm-create="clearForm"
        @on-change-pagination="payloadOnChangePage = $event"
        @on-change-filter="parseDataTableFilter"
        @on-list-item="resetListItems"
        @on-sort-field="onSortField"
        @update:clicked-item="onMultipleSelect($event)"
      >
      <template #column-message="{ data }">
        <div id="fieldError">
            <span v-tooltip.bottom="data.message" style="color: red;">{{ data.message }}</span>
          </div>
        </template>

        <template #column-status="{ data: item }">
            <Badge
              :value="getStatusName(item?.status)"
              :style="`background-color: ${getStatusBadgeBackgroundColor(item.status)}`"
            />
          </template>

        <!-- <template #datatable-footer>
          <ColumnGroup type="footer" class="flex align-items-center font-bold font-500" style="font-weight: 700">
            <Row>
              <Column footer="Totals:" :colspan="6" footer-style="text-align:right; font-weight: 700" />

              <Column :colspan="8" />
            </Row>
          </ColumnGroup>
        </template> -->
      </DynamicTable>

      <div class="flex align-items-end justify-content-end">
        <Button v-tooltip.top="'Apply'" class="w-3rem mx-2" icon="pi pi-check" @click="saveItem()"
        :disabled="selectedElements.length === 0" />
        <Button v-tooltip.top="'Cancel'" severity="secondary" class="w-3rem p-button" icon="pi pi-times" @click="clearForm" />
      </div>
    </div>
  </div>
</template>

<style lang="scss">
.custom-file-upload {
  background-color: transparent !important;
  border: none !important;
  text-align: left !important;
}
.custom-width {
    width: 300px; /* Ajusta el ancho como desees, por ejemplo 300px, 50%, etc. */
}
.ellipsis-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  display: block;
  max-width: 150px; /* Ajusta el ancho máximo según tus necesidades */
}
</style>

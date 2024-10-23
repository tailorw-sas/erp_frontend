<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import type { PageState } from 'primevue/paginator'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { useConfirm } from 'primevue/useconfirm'
import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { GenericObject } from '~/types'
import { GenericService } from '~/services/generic-services'
import { statusToBoolean, statusToString } from '~/utils/helpers'
import type { IData } from '~/components/table/interfaces/IModelData'

// VARIABLES -----------------------------------------------------------------------------------------
const allDefaultItem = { id: 'All', name: 'All', code: 'All' }
const allDefault = { id: 'All', name: 'All' }
const filterToSearch = ref<IData>({
    criteria: null,
    search: '',
    allFromAndTo: false,
    agency: [allDefaultItem],
    hotel: [allDefaultItem],
    client: [allDefaultItem],

})

const confhotelListApi = reactive({
    moduleApi: 'settings',
    uriApi: 'manage-hotel',
})
const confClientApi = reactive({
    moduleApi: 'settings',
    uriApi: 'manage-client',
})

const confagencyListApi = reactive({
    moduleApi: 'settings',
    uriApi: 'manage-agency',
})

const toast = useToast()
const confirm = useConfirm()
const listItems = ref<any[]>([])
const listItemsInvoice = ref<any[]>([])
const loadingSearch = ref(false)
const formReload = ref(0)
const agencyList = ref<any[]>([])
const loadingSaveAll = ref(false)
const loadingDelete = ref(false)
const idItem = ref('')
const idItemToLoadFirstTime = ref('')
const hotelList = ref<any[]>([])
const clientList = ref<any[]>([])

const confApi = reactive({
    moduleApi: 'settings',
    uriApi: 'manage-agency-type',
})

const fields: Array<FieldDefinitionType> = [
    {
        field: 'code',
        header: 'Code',
        disabled: false,
        dataType: 'code',
        class: 'field col-12 required',
        validation: z.string().trim().min(1, 'The code field is required').min(3, 'Minimum 3 characters').max(5, 'Maximum 5 characters').regex(/^[a-z]+$/i, 'Only text characters allowed')
    },
    {
        field: 'name',
        header: 'Name',
        dataType: 'text',
        class: 'field col-12 required',
        validation: z.string()
            .trim()
            .min(1, 'The name field is required')
            .max(50, 'Maximum 50 characters')
    },
    {
        field: 'description',
        header: 'Description',
        dataType: 'textarea',
        class: 'field col-12',
    },
    {
        field: 'status',
        header: 'Active',
        dataType: 'check',
        disabled: true,
        class: 'field col-12 required mb-3 mt-3',
    },
]

const item = ref<GenericObject>({
    name: '',
    code: '',
    status: true,
})

const itemTemp = ref<GenericObject>({
    name: '',
    code: '',
    status: true,
})

const formTitle = computed(() => {
    return idItem.value ? 'Edit' : 'Create'
})

// -------------------------------------------------------------------------------------------------------

// TABLE COLUMNS -----------------------------------------------------------------------------------------

const ENUM_FILTER = [
    { id: 'code', name: 'Code' },
    { id: 'name', name: 'Name' },
]

const columns: IColumn[] = [
    { field: 'code', header: '', type: 'text', showFilter: false, sortable: false },
    { field: 'paymentId', header: 'Id', type: 'text' },
    { field: 'transDate', header: 'Trans. Date', type: 'text' },
    { field: 'hotel', header: 'Hotel', type: 'text' },
    { field: 'agency', header: 'Agencia', type: 'text' },
    { field: 'paymentAmount', header: 'P.Amount', type: 'text' },
    { field: 'deposit', header: 'Deposit Balance', type: 'text' },
    { field: 'applyd', header: 'Applied', type: 'text' },
    { field: 'notapply', header: 'Not Applied', type: 'text' },

]
const columnsInvoice: IColumn[] = [
    { field: 'code', header: '', type: 'text', showFilter: false, sortable: false },
    { field: 'hotel', header: 'Hotel', type: 'text' },
    { field: 'agency', header: 'Agency', type: 'text' },
    { field: 'invoiceNo', header: 'Invoice No', type: 'text' },
    { field: 'genDate', header: 'Generation Date', type: 'text' },
    { field: 'invoiceAmount', header: 'Invoice Amount', type: 'text' },
    { field: 'paymentAmount', header: 'P.Amount', type: 'text' },
    { field: 'invoiceBalance', header: 'Invoice Balance', type: 'text' },
    { field: 'aging', header: 'Aging', type: 'text' },
    { field: 'status', header: 'Status', type: 'bool' },

]
// -------------------------------------------------------------------------------------------------------

// TABLE OPTIONS -----------------------------------------------------------------------------------------
const options = ref({
    tableName: 'Payment',
    moduleApi: 'payment',
    uriApi: 'manage-payment',
    loading: false,
    selectionMode: 'multiple' as 'multiple' | 'single',
    selectAllItemByDefault: false,
    actionsAsMenu: false,
    messageToDelete: 'Do you want to save the change?'
})
const optionsInvoice = ref({
    tableName: 'Invoice',
    moduleApi: 'invoicing',
    uriApi: 'manage-invoice',
    loading: false,
    selectionMode: 'multiple' as 'multiple' | 'single',
    selectAllItemByDefault: false,
    actionsAsMenu: false,
    messageToDelete: 'Do you want to save the change?'
})
const payloadOnChangePage = ref<PageState>()
const payload = ref<IQueryRequest>({
    filter: [],
    query: '',
    pageSize: 50,
    page: 0,
    sortBy: 'createdAt',
    sortType: ENUM_SHORT_TYPE.DESC
})
const pagination = ref<IPagination>({
    page: 0,
    limit: 50,
    totalElements: 0,
    totalPages: 0,
    search: ''
})
const paginationInvoice = ref<IPagination>({
    page: 0,
    limit: 50,
    totalElements: 0,
    totalPages: 0,
    search: ''
})
// -------------------------------------------------------------------------------------------------------

// FUNCTIONS ---------------------------------------------------------------------------------------------
function clearForm() {
    item.value = { ...itemTemp.value }
    idItem.value = ''
    fields[0].disabled = false
    updateFieldProperty(fields, 'status', 'disabled', true)
    formReload.value++
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
        }
    }
    catch (error) {
        console.error(error)
    }
    finally {
        options.value.loading = false
    }
}

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

async function resetListItems() {
    payload.value.page = 0
    getList()
}

async function getItemById(id: string) {
    if (id) {
        idItem.value = id
        loadingSaveAll.value = true
        try {
            const response = await GenericService.getById(confApi.moduleApi, confApi.uriApi, id)
            if (response) {
                item.value.id = response.id
                item.value.name = response.name
                item.value.status = statusToBoolean(response.status)
                item.value.description = response.description
                item.value.code = response.code
            }
            fields[0].disabled = true
            updateFieldProperty(fields, 'status', 'disabled', false)
            formReload.value += 1
        }
        catch (error) {
            if (error) {
                toast.add({ severity: 'error', summary: 'Error', detail: 'Item could not be loaded', life: 3000 })
            }
        }
        finally {
            loadingSaveAll.value = false
        }
    }
}

async function createItem(item: { [key: string]: any }) {
    if (item) {
        loadingSaveAll.value = true
        const payload: { [key: string]: any } = { ...item }
        payload.status = statusToString(payload.status)
        await GenericService.create(confApi.moduleApi, confApi.uriApi, payload)
    }
}

async function updateItem(item: { [key: string]: any }) {
    loadingSaveAll.value = true
    const payload: { [key: string]: any } = { ...item }
    payload.status = statusToString(payload.status)
    await GenericService.update(confApi.moduleApi, confApi.uriApi, idItem.value || '', payload)
}

async function deleteItem(id: string) {
    try {
        loadingDelete.value = true
        await GenericService.deleteItem(options.value.moduleApi, options.value.uriApi, id)
        toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 3000 })
        clearForm()
        getList()
    }
    catch (error: any) {
        toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 3000 })
        loadingDelete.value = false
    }
    finally {
        loadingDelete.value = false
    }
}

async function saveItem(item: { [key: string]: any }) {
    loadingSaveAll.value = true
    let successOperation = true
    if (idItem.value) {
        try {
            await updateItem(item)
            idItem.value = ''
            toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
        }
        catch (error: any) {
            successOperation = false
            toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
        }
    }
    else {
        try {
            await createItem(item)
            toast.add({ severity: 'info', summary: 'Confirmed', detail: 'Transaction was successful', life: 10000 })
        }
        catch (error: any) {
            successOperation = false
            toast.add({ severity: 'error', summary: 'Error', detail: error.data.data.error.errorMessage, life: 10000 })
        }
    }
    loadingSaveAll.value = false
    if (successOperation) {
        clearForm()
        getList()
    }
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
function requireConfirmationToDelete(event: any) {
    if (!useRuntimeConfig().public.showDeleteConfirm) {
        deleteItem(idItem.value)
    }
    else {
        confirm.require({
            target: event.currentTarget,
            group: 'headless',
            header: 'Save the record',
            message: 'Do you want to save the change?',
            acceptClass: 'p-button-danger',
            rejectLabel: 'Cancel',
            acceptLabel: 'Accept',
            accept: () => {
                deleteItem(idItem.value)
            },
            reject: () => { }
        })
    }
}

async function parseDataTableFilter(payloadFilter: any) {
    const parseFilter: IFilter[] | undefined = await getEventFromTable(payloadFilter, columns)
    payload.value.filter = [...payload.value.filter.filter((item: IFilter) => item?.type === 'filterSearch')]
    payload.value.filter = [...payload.value.filter, ...parseFilter || []]
    getList()
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
async function getClientList(query = '') {
    try {
        const payload
            = {
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
            pageSize: 200,
            page: 0,
            sortBy: 'name',
            sortType: ENUM_SHORT_TYPE.ASC
        }

        clientList.value = [{ id: 'All', name: 'All', code: 'All' }]
        const response = await GenericService.search(confClientApi.moduleApi, confClientApi.uriApi, payload)
        const { data: dataList } = response
        for (const iterator of dataList) {
            clientList.value = [...clientList.value, { id: iterator.id, name: iterator.name, code: iterator.code }]
        }
    }
    catch (error) {
        console.error('Error loading client list:', error)
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

function onSortField(event: any) {
    if (event) {
        payload.value.sortBy = event.sortField
        payload.value.sortType = event.sortOrder
        parseDataTableFilter(event.filter)
    }
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

watch(() => idItemToLoadFirstTime.value, async (newValue) => {
    if (!newValue) {
        clearForm()
    }
    else {
        await getItemById(newValue)
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
// -------------------------------------------------------------------------------------------------------
</script>
<template>

    <div class="grid">

        <div class="col-12 md:order-1 md:col-6 xl:col-6">
            <div class="flex justify-content-between align-items-center">
                <!-- Título a la derecha -->
                <div class="font-bold">
                    <h3 class="mb-0 ">
                        Collection Management
                    </h3>
                </div>
                <div class="flex  align-items-center">
                    <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
                        class="my-2 flex justify-content-end px-0">
                        <Button v-tooltip.left="'New'" label="New" icon="pi pi-plus" class="h-2.5rem w-6rem"
                            severity="primary" @click="clearForm" />
                    </div>
                    <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
                        class="my-2 ml-2 flex justify-content-end px-0">
                        <Button v-tooltip.left="'Share File'" label="Share File" icon="pi pi-share-alt"
                            class="h-2.5rem w-8rem" severity="primary" @click="clearForm" />
                    </div>
                    <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
                        class="my-2 ml-2 flex justify-content-end px-0">
                        <Button v-tooltip.left="'Export'" label="Export" icon="pi pi-download" class="h-2.5rem w-6rem"
                            severity="primary" @click="clearForm" />
                    </div>
                </div>
            </div>
            <div class="card p-0">
                <Accordion :active-index="0" class="mb-0">
                    <AccordionTab>
                        <template #header>
                            <div class="text-white font-bold custom-accordion-header">
                                Client View
                            </div>
                        </template>
                        <div class="grid">
                            <div class="col-12 md:col-6 lg:col-6 flex pb-0">

                                <div class="flex flex-column gap-2 w-full">
                                    <div class="flex align-items-center gap-2">
                                        <label class="filter-label font-bold ml-3" for="">Client:</label>
                                        <div class="w-full">
                                            <DebouncedAutoCompleteComponent v-if="!loadingSaveAll" id="autocomplete"
                                                multiple field="name" item-value="id" class="w-full"
                                                :model="filterToSearch.client" :suggestions="clientList" placeholder=""
                                                @load="($event) => getClientList($event)" @change="($event) => {

                                                    if (!filterToSearch.client.find(element => element?.id === 'All') && $event.find(element => element?.id === 'All')) {
                                                        filterToSearch.client = $event.filter((element: any) => element?.id === 'All')
                                                    }
                                                    else {

                                                        filterToSearch.client = $event.filter((element: any) => element?.id !== 'All')
                                                    }

                                                    filterToSearch.agency = filterToSearch.client.length > 0 ? [{ id: 'All', name: 'All', code: 'All' }] : []
                                                }">
                                                <template #option="props">
                                                    <span>{{ props.item.code }} - {{ props.item.name }}</span>
                                                </template>
                                                <template #chip="{ value }">
                                                    <div>
                                                        {{ value?.code }}
                                                    </div>
                                                </template>
                                            </DebouncedAutoCompleteComponent>
                                        </div>
                                    </div>

                                    <div class="flex align-items-center gap-2 w-full" style=" z-index:5 ">
                                        <label class="filter-label font-bold" for="">Agency:</label>
                                        <div class="w-full" style=" z-index:5 ">
                                            <DebouncedAutoCompleteComponent v-if="!loadingSaveAll" id="autocomplete"
                                                :multiple="true" class="w-full" field="name" item-value="id"
                                                :model="filterToSearch.agency" :suggestions="agencyList"
                                                @load="($event) => getAgencyList($event)" @change="($event) => {
                                                    if (!filterToSearch.agency.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                                                        filterToSearch.agency = $event.filter((element: any) => element?.id === 'All')
                                                    }
                                                    else {
                                                        filterToSearch.agency = $event.filter((element: any) => element?.id !== 'All')
                                                    }
                                                }">
                                                <template #option="props">
                                                    <span>{{ props.item.code }} - {{ props.item.name }}</span>
                                                </template>
                                            </DebouncedAutoCompleteComponent>

                                        </div>


                                    </div>
                                    <div class="flex align-items-center gap-2">
                                        <label class="filter-label font-bold ml-3" for="">Hotel:</label>
                                        <div class="w-full">
                                            <DebouncedAutoCompleteComponent v-if="!loadingSaveAll" id="autocomplete"
                                                :multiple="true" class="w-full" field="name" item-value="id"
                                                :model="filterToSearch.hotel" :suggestions="hotelList"
                                                @load="($event) => getHotelList($event)" @change="($event) => {
                                                    if (!filterToSearch.hotel.find((element: any) => element?.id === 'All') && $event.find((element: any) => element?.id === 'All')) {
                                                        filterToSearch.hotel = $event.filter((element: any) => element?.id === 'All')
                                                    }
                                                    else {
                                                        filterToSearch.hotel = $event.filter((element: any) => element?.id !== 'All')
                                                    }
                                                }">
                                                <template #option="props">
                                                    <span>{{ props.item.code }} - {{ props.item.name }}</span>
                                                </template>
                                            </DebouncedAutoCompleteComponent>
                                        </div>
                                    </div>

                                    <div class="flex align-items-center ">
                                        <Button v-tooltip.top="'Search'" class="w-3rem mx-2 " icon="pi pi-search"
                                            :disabled="disabledSearch" :loading="loadingSearch"
                                            @click="searchAndFilter" />
                                        <Button v-tooltip.top="'Clear'" outlined class="w-3rem"
                                            icon="pi pi-filter-slash" :loading="loadingSearch"
                                            @click="clearFilterToSearch" />
                                    </div>

                                </div>
                            </div>


                            <div class="col-12 xl:col-6 mb-3"> <!-- Segunda mitad para otros campos -->
                                <!-- Aquí puedes agregar los otros campos que deseas mostrar -->
                                <div class="flex flex-col mt-0">
                                    <label for="otherField1" class="font-bold mt-2 ml-5">Client Name:</label>

                                    <div class="w-full lg:w-full">
                                        <InputText v-model="filterToSearch.otherField1" class="w-full" />
                                    </div>
                                </div>

                                <div class="flex flex-col mt-2">
                                    <label for="otherField2" class="font-bold mt-2 ml-5">Client Status:</label>
                                    <div class="w-full lg:w-full">
                                        <InputText v-model="filterToSearch.otherField2" class="w-full" />
                                    </div>
                                </div>
                                <div class="flex flex-col mt-2">
                                    <label for="otherField2" class="font-bold mt-2 ml-5">Credit Days:</label>

                                    <div class="w-full lg:w-full">

                                        <InputText v-model="filterToSearch.otherField2" class="w-full" />
                                    </div>
                                </div>
                                <div class="flex flex-col mt-2">
                                    <label for="otherField2" class="font-bold mt-2 ml-5">Language:</label>

                                    <div class="w-1/2 lg:w-full">

                                        <InputText v-model="filterToSearch.otherField2" class="w-full " />
                                    </div>
                                    <div class="w-1/2 lg:w-full">

                                        <InputText v-model="filterToSearch.otherField2" class="w-full mx-2" />
                                    </div>
                                </div>


                                <!-- Agrega más campos según sea necesario -->
                            </div>
                        </div>
                    </AccordionTab>
                </Accordion>
            </div>
            <div class="grid grid-nogutter">
                <div class="col-12  md:col-10 lg:col-10 xl:col-10 mt-2 sm:col-10">
                    <div class="card p-0 ">
                        <div class="header-content text-lg font-bold"
                            style="background-color: var(--primary-color); color: white; padding: 10px; border-top-left-radius: 8px; border-top-right-radius: 8px;">
                            Payment View
                        </div>


                    </div>
                </div>
                <div class="col-12 md:col-2 lg:col-2 sm:col-2 xl:col-2  flex align-items-center justify-content-center ">
                    <Button label="More" style="height: 60%; text-decoration: underline;" severity="primary"
                        class="w-6rem mt-1 mb-3 text-lg" icon="pi pi-plus" />
                </div>
            </div>

            <DynamicTable :data="listItems" :columns="columns" :options="options" :pagination="pagination"
                @on-confirm-create="clearForm" @open-edit-dialog="getItemById($event)"
                @update:clicked-item="getItemById($event)" @on-change-pagination="payloadOnChangePage = $event"
                @on-change-filter="parseDataTableFilter" @on-list-item="resetListItems" @on-sort-field="onSortField" >
               
                <template #datatable-footer>
            <ColumnGroup type="footer" class="flex align-items-center ">
              <Row>
                <Column footer="Total #:" :colspan="3" footer-style="text-align:right; font-weight: bold; color:#ffffff; background-color:#0F8BFD;" />
                <Column  footer="Total $:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
                <Column  footer="Total Transit #:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
                <Column  footer="Total Deposit #:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
       
                <Column :colspan="2" footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
             
            
            </Row>
            <Row>
                <Column footer="Total Applied $:" :colspan="3" footer-style="text-align:right; font-weight: bold; color:#000000; background-color:#ffffff;" />
                <Column  footer="Total N.A $:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#ffffff; color:#000000;" />
                <Column  footer="Total Transit $:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#ffffff; color:#000000;" />
                <Column  footer="Total Deposit $:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#ffffff; color:#000000;" />
       
                <Column :colspan="2" footer-style="text-align:right; font-weight: bold; background-color:#ffffff; color:#000000;" />
             
            
            </Row>
            <Row>
                <Column footer="Total Applied %:" :colspan="3" footer-style="text-align:right; font-weight: bold; color:#ffffff; background-color:#0F8BFD;" />
                <Column  footer="Total N.A %:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
                <Column  footer="Total Transit %:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
                <Column  footer="Total Deposit %:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
       
                <Column :colspan="2" footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
             
            
            </Row>

            </ColumnGroup>

            
          </template>
          
          </DynamicTable>
          
        </div>
        <!--Section Invoice-->
        <div class="col-12 md:order-0 md:col-6 xl:col-6">
            <div class="flex justify-content-end align-items-center">

                <div class="flex justify-content-end align-items-center">


                    <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
                        class="my-2 flex justify-content-end px-0">
                        <Button v-tooltip.left="'Email'" label="Email" icon="pi pi-envelope" class="h-2.5rem w-6rem"
                            severity="primary" @click="clearForm" />
                    </div>
                    <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
                        class="my-2 ml-2 flex justify-content-end px-0">
                        <Button v-tooltip.left="'Print'" label="Print" icon="pi pi-print" class="h-2.5rem w-5rem"
                            severity="primary" @click="clearForm" />
                    </div>
                    <div v-if="options?.hasOwnProperty('showCreate') ? options?.showCreate : true"
                        class="my-2 ml-2 flex justify-content-end px-0">
                        <Button v-tooltip.left="'Export'" label="Export" icon="pi pi-download" class="h-2.5rem w-6rem"
                            severity="primary" @click="clearForm" />
                    </div>

                </div>
            </div>
            <div class="card p-0">
                <Accordion :active-index="0" class="mb-2">
                    <AccordionTab>
                        <template #header>
                            <div class="text-white font-bold custom-accordion-header">
                                Client Details
                            </div>
                        </template>
                        <div class="flex gap-4 flex-column lg:flex-row">
                            <div class="flex align-items-center gap-2">
                                <label for="email3">Criteria</label>
                                <div class="w-full lg:w-auto">
                                    <Dropdown v-model="filterToSearch.criterial" :options="[...ENUM_FILTER]"
                                        option-label="name" placeholder="Criteria" return-object="false"
                                        class="align-items-center w-full" show-clear />
                                </div>
                            </div>
                            <div class="flex align-items-center gap-2">
                                <label for="email">Search</label>
                                <div class="w-full lg:w-auto">
                                    <IconField icon-position="left" class="w-full">
                                        <InputText v-model="filterToSearch.search" type="text" placeholder="Search"
                                            class="w-full" />
                                        <InputIcon class="pi pi-search" />
                                    </IconField>
                                </div>
                            </div>
                            <div class="flex align-items-center">
                                <Button v-tooltip.top="'Search'" class="w-3rem mx-2" icon="pi pi-search"
                                    :disabled="disabledSearch" :loading="loadingSearch" @click="searchAndFilter" />
                                <Button v-tooltip.top="'Clear'" outlined class="w-3rem" icon="pi pi-filter-slash"
                                    :disabled="disabledClearSearch" :loading="loadingSearch"
                                    @click="clearFilterToSearch" />
                            </div>
                        </div>
                    </AccordionTab>
                </Accordion>
            </div>
            <div class="grid grid-nogutter">
                <div class="col-12 xl:col-10 lg:col-10 md:col-10 sm:col-10 mt-2">
                    <div class="card p-0 ">
                        <div class="header-content text-lg font-bold"
                            style="background-color: var(--primary-color); color: white; padding: 10px; border-top-left-radius: 8px; border-top-right-radius: 8px;">
                            Invoice View
                        </div>


                    </div>
                </div>
                <div class="col-12 xl:col-2 lg:col-2 sm:col-2 md:col-2  flex align-items-center justify-content-center ">
                    <Button label="More" style="height: 60%; text-decoration: underline;" severity="primary"
                        class="w-6rem mt-1 mb-3 text-lg" icon="pi pi-plus" />
                </div>
            </div>
            <DynamicTable :data="listItemsInvoice" :columns="columnsInvoice" :options="optionsInvoice"
                :pagination="paginationInvoice" @on-confirm-create="clearForm" @open-edit-dialog="getItemById($event)"
                @update:clicked-item="getItemById($event)" @on-change-pagination="payloadOnChangePage = $event"
                @on-change-filter="parseDataTableFilter" @on-list-item="resetListItems" @on-sort-field="onSortField" >
                <template #datatable-footer>
            <ColumnGroup type="footer" class="flex align-items-center ">
              <Row>
                <Column footer="Total #:" :colspan="3" footer-style="text-align:right; font-weight: bold; color:#ffffff; background-color:#0F8BFD;" />
                <Column  footer="Total $:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
                <Column  footer="Total Pending #:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
                <Column  footer="Total Invoice B #:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
       
                <Column :colspan="2" footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
             
            
            </Row>
            <Row>
                <Column footer="Total 30 Days #:" :colspan="3" footer-style="text-align:right; font-weight: bold; color:#000000; background-color:#ffffff;" />
                <Column  footer="Total 60 Days #:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#ffffff; color:#000000;" />
                <Column  footer="Total 90 Days #:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#ffffff; color:#000000;" />
                <Column  footer="Total 120 Days #:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#ffffff; color:#000000;" />
       
                <Column :colspan="2" footer-style="text-align:right; font-weight: bold; background-color:#ffffff; color:#000000;" />
             
            
            </Row>
            <Row>
                <Column footer="Total 30 Days $:" :colspan="3" footer-style="text-align:right; font-weight: bold; color:#ffffff; background-color:#0F8BFD;" />
                <Column  footer="Total 60 Days $:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
                <Column  footer="Total 90 Days $:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
                <Column  footer="Total 120 Days $:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
       
                <Column :colspan="2" footer-style="text-align:right; font-weight: bold; background-color:#0F8BFD; color:#ffffff;" />
             
            
            </Row>
            <Row>
                <Column footer="Total 30 Days %:" :colspan="3" footer-style="text-align:right; font-weight: bold; color:#000000; background-color:#ffffff;" />
                <Column  footer="Total 60 Days %:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#ffffff; color:#000000;" />
                <Column  footer="Total 90 Days %:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#ffffff; color:#000000;" />
                <Column  footer="Total 120 Days %:" :colspan="2"  footer-style="text-align:right; font-weight: bold; background-color:#ffffff; color:#000000;" />
       
                <Column :colspan="2" footer-style="text-align:right; font-weight: bold; background-color:#ffffff; color:#000000;" />
             
            
            </Row>

            </ColumnGroup>
            </template>
        </DynamicTable>
        </div>
    </div>
</template>
<style lang="scss">
.header-card {
    border: 1px solid #ccc;
    /* Borde de la tarjeta */
    border-bottom-left-radius: 0;
    /* Borde inferior izquierdo recto */
    border-bottom-right-radius: 0;
    /* Borde inferior derecho recto */
    overflow: hidden;
    /* Asegura que los bordes redondeados se apliquen correctamente */
}
</style>
<script setup lang="ts">
import { useRoute } from 'vue-router'
import { onMounted, ref } from 'vue'
import type { PageState } from 'primevue/paginator'
import type { IFormField } from '~/components/fields/interfaces/IFieldInterfaces'
import type { IColumn, IPagination } from '~/components/table/interfaces/ITableInterfaces'
import { ServicesService } from '~/services/services-services'
import { GenericService } from '~/services/generic-services'

const route = useRoute()

const active = ref(0)
const dialogReload = ref(0)
const loadingSaveBusiness = ref(false)
const listServices = ref<any>([[], []])

const listItemsServices = ref<any[]>([])
const realoadServices = ref(0)
const realoadPrice = ref(0)
const formService = ref(0)
const submitFormServices = ref(false)
const loadingSaveAll = ref(false)
const forceSaveFormGeneralData = ref(false)

const errorInTab = ref({
  tabGeneralData: false,
  tabServices: false,
})

const formConfig = ref<IFormField>(
  {
    image: {
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
      label: 'Nombre',
      type: 'text',
      isRequired: true,
      showRequiredLabel: true,
      placeholder: '',
      errorMessage: [],
      haveError: false,
      maxLength: 100,
      showCounter: true,
      validationKeywords: ['maxLength'],
      col: '12'
    },
    ruc: {
      value: '',
      label: 'Ruc',
      type: 'text',
      isRequired: true,
      showRequiredLabel: true,
      placeholder: '',
      errorMessage: [],
      haveError: false,
      minLength: 13,
      maxLength: 13,
      showCounter: true,
      validationKeywords: ['maxLength', 'minLength', 'onlyNumbers'],
      col: '12'
    },
    description: {
      value: '',
      label: 'Descripción',
      type: 'textarea',
      isRequired: true,
      showRequiredLabel: true,
      placeholder: '',
      errorMessage: [],
      haveError: false,
      showCounter: true,
      maxLength: 255,
      validationKeywords: ['maxLength'],
      col: '12'
    },
    address: {
      value: '',
      label: 'Dirección',
      type: 'textarea',
      isRequired: true,
      showRequiredLabel: true,
      placeholder: '',
      errorMessage: [],
      haveError: false,
      maxLength: 100,
      showCounter: true,
      validationKeywords: ['maxLength'],
      col: '612'
    },
    longitude: {
      value: '',
      label: 'Longitud',
      type: 'text',
      isRequired: true,
      showRequiredLabel: true,
      placeholder: '',
      errorMessage: [],
      haveError: false,
      maxLength: 5,
      showCounter: false,
      validationKeywords: ['longitude'],
      col: '6'
    },
    latitude: {
      value: '',
      label: 'Latitud',
      type: 'text',
      isRequired: true,
      showRequiredLabel: true,
      placeholder: '',
      errorMessage: [],
      haveError: false,
      maxLength: 5,
      showCounter: false,
      validationKeywords: ['latitude'],
      col: '6'
    },
    provinceDto: {
      value: '',
      label: 'Provincia',
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
      placeholder: 'Seleccione una provincia',
      errorMessage: [],
      haveError: false,
      col: '3'
    },
    cantonDto: {
      keyRef: 0,
      keyValue: 'provinceDto',
      value: '',
      label: 'Cantón',
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
      placeholder: 'Seleccione un cantón',
      errorMessage: [],
      haveError: false,
      col: '3'
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
      placeholder: 'Seleccione una parroquia',
      errorMessage: [],
      haveError: false,
      col: '3'
    },
  }
)

const formConfigService = ref<IFormField>(
  {
    service: {
      value: '',
      label: 'Servicios',
      type: 'select',
      isRequired: true,
      moduleApi: 'calendar',
      uriApi: 'service',
      filter: [
        {
          key: 'name',
          operator: 'LIKE',
          value: '',
          logicalOperation: 'OR'
        }
      ],
      showRequiredLabel: true,
      returnObject: true,
      placeholder: 'Presiona el botón buscar para encontrar el o los elementos deseados',
      errorMessage: [],
      haveError: false,
      col: '6'
    },
    price: {
      value: '',
      label: 'Precio',
      type: 'number',
      isRequired: true,
      showRequiredLabel: true,
      placeholder: '',
      errorMessage: [],
      haveError: false,
      maxLength: 5,
      showCounter: true,
      validationKeywords: ['maxLength'],
      col: '6'
    },
  }
)

const columnsServices: IColumn[] = [
  { field: 'service', header: 'Servicio', type: 'select' },
  { field: 'price', header: 'Precio', type: 'text' },
]

const options = ref({
  tableName: 'Servicio',
  moduleApi: '',
  uriApi: '',
  loading: false,
  actionsAsMenu: false,
  showTitleBar: false,
  showFilters: false,
  showEdit: false,
  showDelete: false,
  showLocalDelete: true,
  messageToDelete: 'Are you sure you want to delete the service: {{service.name}}?'
})

const formOptionsToEdit = ref({
  moduleApi: 'identity',
  uriApi: 'business',
  itemId: route.params ? route.params.id.toString() : '',
  btnTextSave: 'Siguiente',
  btnTextCancel: 'Cancelar',
  showBtnSave: false,
  showBtnCancel: false,
  showBtnActions: false,
  formWithImage: ['image', 'name', 'description']
})

const pagination = ref<IPagination>({
  page: 0,
  limit: 50,
  totalElements: 0,
  totalPages: 0,
  search: ''
})
const payloadOnChangePage = ref<PageState>()

function deleteItemReceipt(event: any) {
  const index = listItemsServices.value.findIndex(item => item.name === event.name)
  if (index !== -1) {
    listItemsServices.value.splice(index, 1)
    formConfigService.value.service.value = ''
    formConfigService.value.price.value = ''
    realoadServices.value += 1
    formService.value += 1
  }
}

function addItemToListReceipt() {
  submitFormServices.value = true
  const payload: { [key: string]: any } = {}
  let countError = 0
  for (const key in formConfigService.value) {
    if (formConfigService.value.hasOwnProperty(key)) {
      if (formConfigService.value[key].haveError) {
        countError += 1
      }
      const value = formConfigService.value[key].value
      payload[key] = value
    }
  }

  if (countError === 0) {
    listItemsServices.value = [...listItemsServices.value, payload]
    formConfigService.value.service.value = ''
    formConfigService.value.price.value = 0
    realoadServices.value += 1
    formService.value += 1
  }
}

function cancelService(event: any) {
  formConfigService.value.service.value = ''
  formConfigService.value.price.value = ''
  realoadServices.value += 1
  formService.value += 1
}

async function getListServices() {
  try {
    const payload = {
      filter: [],
      query: '',
      pageSize: 300,
      page: 0
    }
    listServices.value[0] = await ServicesService.getList(payload)
  }
  catch (error) {

  }
}

async function getListSelectedServices() {
  const response = await GenericService.getById('calendar', 'business-services/services', formOptionsToEdit.value.itemId)
  listItemsServices.value = [...response.data]
}

async function assignServices() {
  try {
    loadingSaveBusiness.value = true
    loadingSaveAll.value = true

    if (listItemsServices.value.length > 0) {
      let listTemp: any[] = []
      for (const iterator of listItemsServices.value) {
        const obj = {
          service: typeof iterator.service === 'object' ? iterator.service.id : '',
          price: iterator.price
        }
        listTemp = [...listTemp, obj]
      }
      const payloadData = {
        idBusiness: formOptionsToEdit.value.itemId || '',
        services: [...listTemp]
      }
      await GenericService.updateWithOutId('calendar', 'business-services', payloadData)
    }
  }
  catch (error) {

  }
  finally {
    loadingSaveBusiness.value = false
    loadingSaveAll.value = false
  }
}

async function saveAll() {
  try {
    loadingSaveAll.value = true
    forceSaveFormGeneralData.value = true
    await assignServices()
  }
  catch (error) {

  }
  finally {
    loadingSaveAll.value = false
  }
}

async function cancelEdit() {
  dialogReload.value += 1
}

function errorInfForm() {
  if (errorInTab.value.tabGeneralData || errorInTab.value.tabServices) {
    return true
  }
  else {
    return false
  }
}

onMounted(async () => {
  await getListServices()
  getListSelectedServices()
})
</script>

<template>
  <div class="flex justify-content-center align-center">
    <div class="p-4" style="width: 100%;">
      <TabView v-model:activeIndex="active" class="tabview-custom">
        <TabPanel>
          <template #header>
            <div class="flex align-items-center gap-2">
              <i class="pi" :class="active === 0 ? 'pi-folder-open' : 'pi-folder'" style="font-size: 1.5rem" />
              <span class="font-bold white-space-nowrap">
                Datos Generales
                <i v-if="errorInTab.tabGeneralData" class="pi p-error pi-question-circle" style="font-size: 1.2rem" />
              </span>
            </div>
          </template>
          <div class="card">
            <EditForm
              :key="dialogReload"
              :list-fields="formConfig"
              :form-options="formOptionsToEdit"
              :force-save="forceSaveFormGeneralData"
              @force-save="forceSaveFormGeneralData = $event"
              @have-error-in-form="errorInTab.tabGeneralData = $event"
            />
          </div>
        </TabPanel>

        <!-- Servicios -->
        <TabPanel>
          <template #header>
            <div class="flex align-items-center gap-2">
              <i class="pi" :class="active === 2 ? 'pi-folder-open' : 'pi-folder'" style="font-size: 1.5rem" />
              <span class="font-bold white-space-nowrap">Asignar Servicios</span>
            </div>
          </template>
          <div key="formReceipt" class="card">
            <div class="grid p-fluid formgrid">
              <div class="col-12 md:col-8 field">
                <SearchSelectFieldComponent
                  id="realoadServices"
                  :key="realoadServices"
                  :properties="formConfigService.service"
                  :submit="submitFormServices"
                  :local-items="formConfigService.service.localItems"
                  :validation-keywords="formConfigService.service.validationKeywords"
                  @update:model-value="formConfigService.service.value = $event"
                  @update:error-messages="formConfigService.service.errorMessage = $event"
                  @invalid-field="formConfigService.service.haveError = $event"
                />
              </div>
              <div class="col-12 md:col-2 field">
                <TextFieldComponent
                  :key="realoadPrice"
                  :properties="formConfigService.price"
                  :submit="submitFormServices"
                  :validation-keywords="[...formConfigService.price.validationKeywords, 'onlyNumbers']"
                  @update:model-value="formConfigService.price.value = $event"
                  @update:error-messages="formConfigService.price.errorMessage = $event"
                  @invalid-field="formConfigService.price.haveError = $event"
                />
              </div>
              <div class="col-12 md:col-2 field flex justify-content-end align-items-end">
                <Button class="w-4rem h-3rem mx-1 flex align-items-center justify-content-center" label="" @click="addItemToListReceipt">
                  <i class="pi pi-plus" style="font-size: 1.3rem" />
                </Button>
                <Button v-if="true" class="w-4rem h-3rem mx-1 flex align-items-center justify-content-center" label="" severity="secondary" @click="cancelService">
                  <i class="pi pi-refresh" style="font-size: 1.3rem" />
                </Button>
              </div>
              <div v-if="false" class="col-12 md:col-12 flex justify-content-end">
                <Button
                  severity="secondary"
                  outlined
                  class="w-8rem mt-2 mx-3"
                  icon="pi pi-check"
                  label="Cancelar"
                  @click="cancelService"
                />
                <Button
                  class="w-8rem mt-2"
                  icon="pi pi-check"
                  label="Aceptar"
                  @click="addItemToListReceipt"
                />
              </div>
            </div>
          </div>
          <div class="card">
            <div class="grid p-fluid formgrid">
              <div class="col-12">
                <DynamicTable
                  :data="listItemsServices"
                  :columns="columnsServices"
                  :options="options"
                  :pagination="pagination"
                  @on-change-pagination="payloadOnChangePage = $event"
                  @on-local-delete="deleteItemReceipt"
                />
              </div>
            </div>
          </div>
        </TabPanel>
      </TabView>

      <div class="card">
        <div class="flex justify-content-end">
          <Button class="w-8rem mr-3" outlined severity="secondary" icon="pi pi-times" label="Cancelar" @click="cancelEdit()" />
          <Button class="w-8rem" label="Modificar" :loading="loadingSaveAll" :disabled="errorInfForm()" @click="saveAll" />
        </div>
      </div>
    </div>
  </div>
</template>

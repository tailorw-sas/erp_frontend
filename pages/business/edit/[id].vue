<script setup lang="ts">
import { useRoute } from 'vue-router'
import { onMounted, ref } from 'vue'
import { z } from 'zod'
import { useToast } from 'primevue/usetoast'
import { type FilterCriteria, getList } from '~/composables/list'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import { ModulesService } from '~/services/modules-services'
import { GenericService } from '~/services/generic-services'
import getUrlByImage from '~/composables/files'

const route = useRoute()
const toast = useToast()

const active = ref(0)
const dialogReload = ref(0)
const loadingSaveBusiness = ref(false)
const listModules = ref<any>([[], []])
const listProvince = ref<any[]>([])
const listCanton = ref<any[]>([])
const listParroquia = ref<any[]>([])

const listItemsServices = ref<any[]>([])
const loadingSaveAll = ref(false)
const forceSave = ref(false)

const fieldsWithImage = ['image', 'name', 'description']

const confApi = reactive({
  generalData: {
    moduleApi: 'identity',
    uriApi: 'business',
  },
  tabModules: {
    moduleApi: 'identity',
    uriApi: 'business-module',
  },
  tabServices: {
    moduleApi: 'calendar',
    uriApi: 'business-services',
  },
})

const item = ref({
  image: '',
  name: '',
  description: '',
  ruc: '',
  address: '',
  longitude: '',
  latitude: '',
  // provinceDto: null,
  // cantonDto: null,
  // parroquiaDto: null,
})

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
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'This is a required field').max(50, 'Maximum 50 characters')
  },
  {
    field: 'description',
    header: 'Description',
    dataType: 'textarea',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'This is a required field').max(150, 'Maximum 150 characters')
  },
  {
    field: 'ruc',
    header: 'RUC',
    dataType: 'text',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string()
      .trim()
      .min(1, 'This is a required field')
      .max(13, 'Maximum 13 characters')
      .length(13, 'This field must contain 13 characters')
      .regex(/^\d{13}$/, 'This field must be numeric'),
  },
  {
    field: 'address',
    header: 'Address',
    dataType: 'textarea',
    class: 'field col-12 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'This is a required field').max(150, 'Maximum 150 characters')
  },
  {
    field: 'longitude',
    header: 'Longitude',
    dataType: 'text',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'This is a required field').max(6, 'Maximum 6 characters')
  },
  {
    field: 'latitude',
    header: 'Latitude',
    dataType: 'text',
    class: 'field col-12 md:col-6 required',
    headerClass: 'mb-1',
    validation: z.string().trim().min(1, 'This is a required field').max(6, 'Maximum 6 characters')
  },
  // {
  //   field: 'provinceDto',
  //   header: 'Provincia',
  //   dataType: 'select',
  //   class: 'field col-12 md:col-3 required',
  //   disabled: true,
  //   hidden: false,
  //   headerClass: 'mb-1',
  //   validation: z.object({
  //     id: z.string().min(1, 'This is a requited filed'),
  //     name: z.string().min(1, 'This is a requited filed'),
  //   }).nullable().refine(value => value && value.id && value.name, { message: 'This is a requited filed' }),
  // },
  // {
  //   field: 'cantonDto',
  //   header: 'Cantón',
  //   dataType: 'select',
  //   class: 'field col-12 md:col-3 required',
  //   disabled: true,
  //   hidden: false,
  //   headerClass: 'mb-1',
  //   validation: z.object({
  //     id: z.string().min(1, 'This is a requited filed'),
  //     name: z.string().min(1, 'This is a requited filed'),
  //   }).nullable().refine(value => value && value.id && value.name, { message: 'This is a requited filed' }),
  // },
  // {
  //   field: 'parroquiaDto',
  //   header: 'Parroquia',
  //   dataType: 'select',
  //   class: 'field col-12 md:col-3 required',
  //   disabled: true,
  //   hidden: false,
  //   headerClass: 'mb-1',
  //   validation: z.object({
  //     id: z.string().min(1, 'This is a requited filed'),
  //     name: z.string().min(1, 'This is a requited filed'),
  //   }).nullable().refine(value => value && value.id && value.name, { message: 'This is a requited filed' }),
  // },
]

const errorInTab = ref({
  tabGeneralData: false,
  tabMedicalInfo: false,
  tabServices: false,
  tabPermissions: false
})

const formOptionsToEdit = ref({
  moduleApi: 'identity',
  uriApi: 'business',
  itemId: route.params ? route.params.id.toString() : '',
  btnTextSave: 'Next',
  btnTextCancel: 'Cancel',
  showBtnSave: false,
  showBtnCancel: false,
  showBtnActions: false,
  formWithImage: ['image', 'name', 'description']
})

const goToList = async () => await navigateTo('/business')

async function assignModules() {
  try {
    loadingSaveBusiness.value = true
    loadingSaveAll.value = true

    const listIds = listModules.value[1].map((item: any) => item.id) || []
    const payloadData = {
      idBusiness: route.params ? route.params.id.toString() : '',
      modules: [...listIds]
    }
    await GenericService.updateWithOutId('identity', 'business-module', payloadData)
  }
  catch (error) {
  }
  finally {
    loadingSaveBusiness.value = false
    loadingSaveAll.value = false
  }
}

async function getListModules() {
  try {
    const payload = {
      filter: [],
      query: '',
      pageSize: 300,
      page: 0
    }
    listModules.value[0] = await ModulesService.getListModules(payload)
  }
  catch (error) {

  }
}

async function getListSelectedModules() {
  let listTemp = []
  listTemp = await ModulesService.getListSelected(formOptionsToEdit.value.itemId)

  if (listTemp.length > 0) {
    for (const iterator of listTemp) {
      moveObjectToSecondList(iterator.id)
    }
  }
}

function moveObjectToSecondList(objectId: string) {
  const index = listModules.value[0].findIndex(obj => obj.id === objectId)
  if (index !== -1) {
    const objectToMove = listModules.value[0].splice(index, 1)[0]
    listModules.value[1].push(objectToMove)
  }
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
        idBusiness: route.params ? route.params.id.toString() : '',
        services: [...listTemp]
      }
      await GenericService.updateWithOutId('calendar', 'business-services', payloadData)
      goToList()
    }
  }
  catch (error) {

  }
  finally {
    loadingSaveBusiness.value = false
    loadingSaveAll.value = false
  }
}

function errorInfForm() {
  if (errorInTab.value.tabGeneralData || errorInTab.value.tabMedicalInfo || errorInTab.value.tabServices || errorInTab.value.tabPermissions) {
    return true
  }
  else {
    return false
  }
}

async function getObjectValues($event: any) {
  forceSave.value = false
  item.value = $event
}

async function getCantonById(id: string) {
  if (id && id !== '') {
    const filter: FilterCriteria[] = [
      {
        key: 'type',
        operator: 'EQUALS',
        value: 'CANTON',
        logicalOperation: 'AND'
      },
    ]
    listCanton.value = await getList(id, filter)
  }
  else {
    listCanton.value = []
  }
}

async function getParroquiaById(id: string) {
  if (id) {
    const filter: FilterCriteria[] = [
      {
        key: 'type',
        operator: 'EQUALS',
        value: 'PARROQUIA',
        logicalOperation: 'AND'
      },
    ]
    listParroquia.value = await getList(id, filter)
  }
  else {
    listParroquia.value = []
  }
}

async function getItemById() {
  loadingSaveAll.value = true
  try {
    const response = await GenericService.getById(confApi.generalData.moduleApi, confApi.generalData.uriApi, route.params ? route.params.id.toString() : '')
    if (response) {
      item.value.image = response.image
      item.value.name = response.name
      item.value.description = response.description
      item.value.ruc = response.ruc
      item.value.address = response.address
      item.value.longitude = response.longitude
      item.value.latitude = response.latitude

      // const responseLocations = await GenericService.getById('patients', 'locations', typeof response.geolocation === 'object' ? response.geolocation.id : '', 'parroquia')

      // if (responseLocations) {
      //   await getCantonById(typeof responseLocations.provinceDto === 'object' ? responseLocations.provinceDto.id : responseLocations.provinceDto)
      //   await getParroquiaById(typeof responseLocations.cantonDto === 'object' ? responseLocations.cantonDto.id : responseLocations.cantonDto)

      //   item.value.provinceDto = responseLocations.provinceDto
      //   item.value.cantonDto = responseLocations.cantonDto
      //   item.value.parroquiaDto = responseLocations.parroquiaDto
      // }
    }
    dialogReload.value += 1
  }
  catch (error) {
    if (error) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudo cargar los datos de la empresa', life: 3000 })
    }
  }
  finally {
    loadingSaveAll.value = false
  }
}

async function save(item: { [key: string]: any }) {
  loadingSaveAll.value = true
  const payload: { [key: string]: any } = { ...item }
  payload.image = typeof payload.image === 'object' ? await getUrlByImage(payload.image) : payload.image
  payload.geographicLocation = typeof payload.parroquiaDto === 'object' ? payload.parroquiaDto.id : payload.parroquiaDto
  delete payload.provinceDto
  delete payload.cantonDto
  delete payload.parroquiaDto

  try {
    await GenericService.update(confApi.generalData.moduleApi, confApi.generalData.uriApi, route.params ? route.params.id.toString() : '', payload)
    await assignModules()
    await assignServices()
    goToList()
  }
  catch (error) {
    if (error) {
      toast.add({ severity: 'error', summary: 'Error', detail: 'No se pudo modificar la empresa', life: 3000 })
    }
  }
  finally {
    loadingSaveAll.value = false
  }
}

watch(() => item.value, async (newValue) => {
  if (newValue) {
    save(newValue)
  }
})

onMounted(async () => {
  // listProvince.value = await getList('', [
  //   {
  //     key: 'type',
  //     operator: 'EQUALS',
  //     value: 'PROVINCE',
  //     logicalOperation: 'AND'
  //   },
  // ])
  getItemById()
  await getListModules()
  getListSelectedModules()
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
              <template #field-provinceDto="{ item: data, onUpdate }">
                <Dropdown
                  v-if="!loadingSaveAll"
                  v-model="data.provinceDto"
                  :options="[...listProvince]"
                  option-label="name"
                  return-object="false"
                  show-clear
                  class="h-3rem align-items-center"
                  @update:model-value="($event) => {
                    if ($event) {
                      getCantonById($event.id)
                    }
                    else {
                      listCanton = []
                      listParroquia = []
                    }
                    onUpdate('cantonDto', null)
                    onUpdate('parroquiaDto', null)
                    onUpdate('provinceDto', $event)
                  }"
                />
                <Skeleton v-else height="3rem" class="mb-2" />
              </template>
              <template #field-cantonDto="{ item: data, onUpdate }">
                <Dropdown
                  v-if="!loadingSaveAll"
                  v-model="data.cantonDto"
                  :options="[...listCanton]"
                  option-label="name"
                  return-object="false"
                  show-clear
                  class="h-3rem align-items-center"
                  @update:model-value="($event) => {
                    if ($event && $event.id) {
                      getParroquiaById($event.id)
                    }
                    else {
                      listParroquia = []
                    }
                    onUpdate('parroquiaDto', null)
                    onUpdate('cantonDto', $event)
                  }"
                />
                <Skeleton v-else height="3rem" class="mb-2" />
              </template>
              <template #field-parroquiaDto="{ item: data, onUpdate }">
                <Dropdown
                  v-if="!loadingSaveAll"
                  v-model="data.parroquiaDto"
                  :options="[...listParroquia]"
                  option-label="name"
                  return-object="false"
                  class="h-3rem align-items-center"
                  show-clear
                  @change="($event) => {
                    onUpdate('parroquiaDto', $event.value)
                  }"
                />
                <Skeleton v-else height="3rem" class="mb-2" />
              </template>
            </EditFormV2WithImage>
          </div>
        </TabPanel>
        <!-- :disabled="active === 0" -->
        <TabPanel>
          <template #header>
            <div class="flex align-items-center gap-2">
              <i class="pi" :class="active === 1 ? 'pi-folder-open' : 'pi-folder'" style="font-size: 1.5rem" />
              <span class="font-bold white-space-nowrap">Assign Modules</span>
            </div>
          </template>
          <div class="card">
            <!-- {{ listModules[1] }} -->
            <PickList v-model="listModules" :show-target-controls="false" :show-source-controls="false" list-style="height:442px" data-key="id" breakpoint="1400px">
              <template #sourceheader>
                Module List
              </template>
              <template #targetheader>
                Selected Modules
              </template>
              <template #item="slotProps">
                <div class="flex flex-wrap p-2 align-items-center gap-3">
                  <div class="flex-1 flex flex-column gap-2">
                    <span class="font-bold">{{ slotProps.item.name }}</span>
                    <div v-if="false" class="flex align-items-center gap-2">
                      <i class="pi pi-tag text-sm" />
                      <span>{{ slotProps.item.desc }}</span>
                    </div>
                  </div>
                  <!-- acctions -->
                  <span class="font-bold">{{ slotProps.item.cantPermissions }}</span>
                </div>
              </template>
            </PickList>

            <!-- <div class="p-card-footer flex justify-content-end mt-5">
                            <Button class="w-8rem mr-3" outlined severity="secondary" icon="pi pi-times" label="Cancelar" @click="goToList()"></Button>
                            <Button class="w-8rem" label="Asignar" :loading="loadingSaveBusiness" @click="assignModules"></Button>
                        </div> -->
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
    <Toast position="top-center" :base-z-index="5" group="tc" />
  </div>
</template>

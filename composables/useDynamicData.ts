import { ref } from 'vue'
import { useToast } from 'primevue/usetoast'
import { GenericService } from '~/services/generic-services'
import { ENUM_SHORT_TYPE } from '~/utils/Enums'
import type { IFilter } from '~/components/fields/interfaces/IFieldInterfaces'

export function useDynamicData() {
  const suggestionsData = ref<any[]>([])
  const toast = useToast()

  async function loadDynamicData(query: string, moduleApi: string, uriApi: string, filter: IFilter[] = []) {
    try {
      const payload = {
        filter,
        query: '',
        pageSize: 2000,
        page: 0,
        sortBy: 'createdAt',
        sortType: ENUM_SHORT_TYPE.ASC
      }

      const response = await GenericService.search(moduleApi, uriApi, payload)
      const { data: dataList } = response

      if (Array.isArray(dataList)) {
        suggestionsData.value = dataList.map((iterator: any) => ({
          id: iterator.id,
          name: `${iterator.code} - ${iterator.name}`,
          status: iterator.status
        }))
      }
      else {
        suggestionsData.value = []
      }

      Logger.info('Datos cargados en suggestionsData:', suggestionsData.value)
      return suggestionsData.value
    }
    catch (error) {
      Logger.error('Error loading dynamic data:', error)
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: 'Ocurrió un error al cargar los datos. Por favor, intente nuevamente.',
        life: 5000
      })
      throw error
    }
  }

  return {
    suggestionsData,
    loadDynamicData
  }
}

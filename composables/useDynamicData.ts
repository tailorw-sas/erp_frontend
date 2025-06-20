import { ref } from 'vue'
import { useToast } from 'primevue/usetoast'
import { openDB } from 'idb'
import { GenericService } from '~/services/generic-services'
import { ENUM_SHORT_TYPE } from '~/utils/Enums'
import type { IFilter } from '~/components/fields/interfaces/IFieldInterfaces'
import { useRuntimeConfig } from '#imports'

const DB_NAME = 'erp-cache'
const DB_VERSION = 1
const STORE_NAME = 'dynamicData'

async function getDb() {
  return await openDB(DB_NAME, DB_VERSION, {
    upgrade(db) {
      if (!db.objectStoreNames.contains(STORE_NAME)) {
        db.createObjectStore(STORE_NAME)
      }
    }
  })
}

async function getCachedData(key: string, ttlOverrideMs?: number): Promise<any[] | null> {
  const db = await getDb()
  const entry = await db.get(STORE_NAME, key)
  if (!entry) { return null }

  const { timestamp, data } = entry
  const now = Date.now()
  const defaultTtl = 1000 * 60 * 60 * 10 // 10 horas
  const ttl = ttlOverrideMs ?? defaultTtl

  return now - timestamp < ttl ? data : null
}

async function setCachedData(key: string, data: any[]): Promise<void> {
  const db = await getDb()
  await db.put(STORE_NAME, { timestamp: Date.now(), data }, key)
}

async function clearCache(): Promise<void> {
  const db = await getDb()
  await db.clear(STORE_NAME)
}

export function useDynamicData() {
  const suggestionsData = ref<any[]>([])
  const toast = useToast()
  const config = useRuntimeConfig()
  const USE_INDEXEDDB_CACHE = config.public?.useIndexedDb ?? true
  console.error('Use Dynamic Data')

  async function loadDynamicData(query: string, moduleApi: string, uriApi: string, filter: IFilter[] = [], ttlMs?: number) {
    const cacheKey = `${moduleApi}_${uriApi}`

    if (USE_INDEXEDDB_CACHE) {
      const cached = await getCachedData(cacheKey, ttlMs)
      if (cached) {
        suggestionsData.value = cached
        Logger.info('Data retrieved from IndexedDB:', cached)
        return cached
      }
    }

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
        const mapped = dataList.map((iterator: any) => ({
          id: iterator.id,
          name: `${iterator.code} - ${iterator.name}`,
          status: iterator.status
        }))
        suggestionsData.value = mapped

        if (USE_INDEXEDDB_CACHE) {
          await setCachedData(cacheKey, mapped)
        }
      }
      else {
        suggestionsData.value = []
      }

      Logger.info('Data loaded from backend:', suggestionsData.value)
      return suggestionsData.value
    }
    catch (error) {
      Logger.error('Error loading dynamic data:', error)
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: 'An error occurred while loading data. Please try again.',
        life: 5000
      })
      throw error
    }
  }

  return {
    suggestionsData,
    loadDynamicData,
    clearCache
  }
}

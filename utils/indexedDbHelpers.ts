// utils/indexedDbHelpers.ts
import { getDb } from './indexedDbClient'

export async function setCachedData(store: string, key: string, data: any) {
  const db = await getDb()
  await db.put(store, { key, data, timestamp: Date.now() })
}

export async function getCachedData(store: string, key: string) {
  const db = await getDb()
  const record = await db.get(store, key)
  return record?.data
}

export async function clearAllCaches() {
  const db = await getDb()
  const tx = db.transaction(['settings'], 'readwrite')
  await tx.objectStore('settings').clear()
  await tx.done
}

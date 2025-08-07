// utils/indexDbClient.ts
import { openDB } from 'idb'

const DB_NAME = 'finamer-cache'
const DB_VERSION = 2 // ✅ Incrementar versión para nueva store
const CACHE_STORE = 'dynamicData'
const SETTINGS_STORE = 'settings'
const USER_DATA_STORE = 'userData' // ✅ Nueva store para datos de usuario

export async function getDb() {
  return await openDB(DB_NAME, DB_VERSION, {
    upgrade(db, _oldVersion) { // ✅ FIXED: Prefijo con _ para indicar no usado
      // Crear store de settings si no existe
      if (!db.objectStoreNames.contains(SETTINGS_STORE)) {
        db.createObjectStore(SETTINGS_STORE, { keyPath: 'key' })
      }

      // ✅ Crear store de dynamicData si no existe (para tu composable existente)
      if (!db.objectStoreNames.contains(CACHE_STORE)) {
        db.createObjectStore(CACHE_STORE)
      }

      // ✅ Nueva store para datos de usuario
      if (!db.objectStoreNames.contains(USER_DATA_STORE)) {
        const userStore = db.createObjectStore(USER_DATA_STORE, { keyPath: 'userId' })
        userStore.createIndex('email', 'email', { unique: false })
        userStore.createIndex('lastUpdated', 'lastUpdated', { unique: false })
        userStore.createIndex('expiresAt', 'expiresAt', { unique: false })
      }
    }
  })
}

// ✅ FIXED: Usar tu función setCachedData existente con parámetros correctos
export async function setCachedData(store: string, key: string, data: any) {
  const db = await getDb()
  await db.put(store, { key, data, timestamp: Date.now() })
}

export async function getCachedData(store: string, key: string) {
  const db = await getDb()
  const record = await db.get(store, key)
  return record?.data
}

// ✅ Funciones específicas para datos de usuario usando el patrón existente
export async function storeUserData(userId: string, userData: any): Promise<void> {
  const db = await getDb()
  const userDataToStore = {
    userId,
    ...userData,
    lastUpdated: Date.now(),
    expiresAt: Date.now() + (24 * 60 * 60 * 1000) // 24 hours
  }

  await db.put(USER_DATA_STORE, userDataToStore)
  console.info('User data stored in IndexedDB:', userId)
}

export async function getUserData(userId: string): Promise<any | null> {
  const db = await getDb()
  const result = await db.get(USER_DATA_STORE, userId)

  if (!result) { return null }

  // Check if data is expired
  if (result.expiresAt && Date.now() > result.expiresAt) {
    await db.delete(USER_DATA_STORE, userId)
    return null
  }

  return result
}

export async function clearUserData(userId?: string): Promise<void> {
  const db = await getDb()

  if (userId) {
    await db.delete(USER_DATA_STORE, userId)
  }
  else {
    await db.clear(USER_DATA_STORE)
  }

  console.info('User data cleared from IndexedDB')
}

export async function isUserDataExpired(userId: string): Promise<boolean> {
  const userData = await getUserData(userId)
  if (!userData) { return true }

  const isExpired = userData.expiresAt <= Date.now()
  const isOld = Date.now() - userData.lastUpdated > (2 * 60 * 60 * 1000) // 2 hours

  return isExpired || isOld
}

// ✅ Mantener compatibilidad con funciones existentes pero usar el store correcto
export async function getCachedDataWithTTL(key: string, ttlOverrideMs?: number): Promise<any[] | null> {
  const db = await getDb()
  const entry = await db.get(CACHE_STORE, key)
  if (!entry) { return null }

  const { timestamp, data } = entry
  const now = Date.now()
  const defaultTtl = 1000 * 60 * 60 * 10 // 10 horas
  const ttl = ttlOverrideMs ?? defaultTtl

  if (now - timestamp >= ttl) {
    await db.delete(CACHE_STORE, key)
    return null
  }
  return data
}

export async function setCachedDataWithTTL(key: string, data: any[]): Promise<void> {
  const db = await getDb()
  await db.put(CACHE_STORE, { timestamp: Date.now(), data }, key)
}

export async function clearCache(): Promise<void> {
  const db = await getDb()
  await db.clear(CACHE_STORE)
}

export async function clearAllCaches() {
  const db = await getDb()
  const tx = db.transaction([SETTINGS_STORE, CACHE_STORE, USER_DATA_STORE], 'readwrite')
  await tx.objectStore(SETTINGS_STORE).clear()
  await tx.objectStore(CACHE_STORE).clear()
  await tx.objectStore(USER_DATA_STORE).clear()
  await tx.done
}

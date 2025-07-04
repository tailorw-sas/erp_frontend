import { openDB } from 'idb'

const DB_NAME = 'finamer-cache'
const DB_VERSION = 1

export async function getDb() {
  return await openDB(DB_NAME, DB_VERSION, {
    upgrade(db) {
      if (!db.objectStoreNames.contains('settings')) {
        db.createObjectStore('settings', { keyPath: 'key' })
      }
    }
  })
}

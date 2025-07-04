// stores/useDynamicCacheStore.ts
import { defineStore } from 'pinia'

export const useDynamicCacheStore = defineStore('dynamicCache', {
  state: () => ({
    cache: {} as Record<string, any[]>
  }),
  actions: {
    set(key: string, data: any[]) {
      this.cache[key] = data
    },
    get(key: string): any[] | null {
      return this.cache[key] ?? null
    },
    clear() {
      this.cache = {}
    }
  }
})

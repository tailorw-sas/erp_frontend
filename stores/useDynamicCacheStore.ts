// stores/useDynamicCacheStore.ts
import { defineStore } from 'pinia'

export const useDynamicCacheStore = defineStore('dynamicCache', {
  state: () => ({
    cache: {} as Record<string, any[] | any> // ✅ FIXED: Soportar arrays Y objetos
  }),
  actions: {
    set(key: string, data: any[] | any) { // ✅ FIXED: Aceptar arrays y objetos
      this.cache[key] = data
    },
    get(key: string): any[] | any | null { // ✅ FIXED: Retornar arrays u objetos
      return this.cache[key] ?? null
    },
    delete(key: string) { // ✅ NUEVO: Método para eliminar una key específica
      delete this.cache[key]
    },
    clear() {
      this.cache = {}
    }
  }
})

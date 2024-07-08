import { defineStore } from 'pinia'
import type { Business } from '~/types'

export const useBusinessStore = defineStore({
  id: 'businessStore',
  state: () => ({
    current: null as Business | null
  }),
  getters: {
    can(state) {
      return (permissions: string[]) => {
        if (!state.current) {
          return false
        }

        return permissions.some(permission => state.current?.permissions.includes(permission))
      }
    }
  },
  actions: {
    setCurrent(business: Business) {
      this.current = business
    }
  }
})

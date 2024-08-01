import { defineStore } from 'pinia'
import type { UserMeData } from '~/types'

export const useAuthStore = defineStore('authStore', () => {
  const userData = reactive<UserMeData>({})

  const { $api } = useNuxtApp()
  const headers = useRequestHeaders(['cookie'])

  async function getUserMe() {
    const url = '/api/user/me'
    const { data } = await $api<UserMeData>(url, { headers })
    userData.data = data
  }

  const availableCompanies = computed(() => {
    return userData.data?.businesses?.filter(item => item.businessId !== userData.data?.selectedBusiness) || []
  })

  const can = computed(() => (permissions: string[]) => {
    return permissions.some(permission => userData.data?.permissions.includes(permission))
  })

  return { userData, getUserMe, availableCompanies, can }
})

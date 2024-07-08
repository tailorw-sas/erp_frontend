import type { IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { SearchResponse } from '~/types'

export const GenericService = {

  async search(MODULE_NAME: string, URI_API: string, payload: IQueryRequest) {
    const { $api } = useNuxtApp()

    const serverUrl = useRequestURL()
    const url = `${serverUrl.origin}/site/${MODULE_NAME}/${URI_API}/search`
    return $api<SearchResponse>(url, {
      method: 'POST',
      body: payload
    })
  },

  async searchById(MODULE_NAME: string, URI_API: string) {
    const { $api } = useNuxtApp()
    const serverUrl = useRequestURL()
    const url = `${serverUrl.origin}/site/${MODULE_NAME}/${URI_API}`
    return $api<any>(url)
  },

  async getById(MODULE_NAME: string, URI_API: string, ID: string, SUB_CONTROLLER = '', ID2: string = '') {
    const { $api } = useNuxtApp()
    const serverUrl = useRequestURL()
    let url = `${serverUrl.origin}/site/${MODULE_NAME}/${URI_API}/${ID}`
    if (SUB_CONTROLLER !== '') {
      url += `/${SUB_CONTROLLER}`
    }
    if (ID2 !== '') {
      url += `/${ID2}`
    }
    return $api<any>(url, {
      method: 'GET',
    })
  },

  async create(MODULE_NAME: string, URI_API: string, payload: any) {
    const { $api } = useNuxtApp()
    const serverUrl = useRequestURL()
    const url = `${serverUrl.origin}/site/${MODULE_NAME}/${URI_API}`
    return $api(url, {
      method: 'POST',
      body: payload
    })
  },

  async createMany(MODULE_NAME: string, URI_API: string, payload: any) {
    const { $api } = useNuxtApp()
    const serverUrl = useRequestURL()
    const url = `${serverUrl.origin}/site/${MODULE_NAME}/${URI_API}/many`
    return $api(url, {
      method: 'POST',
      body: payload
    })
  },

  async createBulk(MODULE_NAME: string, URI_API: string, payload: any) {
    const { $api } = useNuxtApp()
    const serverUrl = useRequestURL()
    const url = `${serverUrl.origin}/site/${MODULE_NAME}/${URI_API}/bulk`
    return $api(url, {
      method: 'POST',
      body: payload
    })
  },

  async update(MODULE_NAME: string, URI_API: string, ID: string, payload: any) {
    const { $api } = useNuxtApp()
    const serverUrl = useRequestURL()
    const url = `${serverUrl.origin}/site/${MODULE_NAME}/${URI_API}/${ID}`
    return $api(url, {
      method: 'PATCH',
      body: payload
    })
  },

  async updateWithOutId(MODULE_NAME: string, URI_API: string, payload: any) {
    const { $api } = useNuxtApp()
    const serverUrl = useRequestURL()
    const url = `${serverUrl.origin}/site/${MODULE_NAME}/${URI_API}`
    return $api(url, {
      method: 'PATCH',
      body: payload
    })
  },

  async uploadFile(MODULE_NAME: string, URI_API: string, file: File) {
    const { $api } = useNuxtApp()
    const formData = new FormData()
    const serverUrl = useRequestURL()
    const url = `${serverUrl.origin}/site/${MODULE_NAME}/${URI_API}/upload`

    formData.append('file', file)

    return $api(url, {
      method: 'POST',
      body: formData,
    })
  },

  async deleteItem(MODULE_NAME: string, URI_API: string, ID: string) {
    const { $api } = useNuxtApp()
    const serverUrl = useRequestURL()
    const url = `${serverUrl.origin}/site/${MODULE_NAME}/${URI_API}/${ID}`
    return $api(url, {
      method: 'DELETE',
    })
  },

  async getUrlByImage(file: File) {
    if (file) {
      const response = await GenericService.uploadFile('cloudbridges', 'files', file)
      return (response as any)?.data.url
    }
  }
}

// services/generic-service.ts
import type { IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'
import type { PaginatedResponse, SearchResponse } from '~/types'

// ========== INTERFACES ==========
interface RequestOptions {
  timeout?: number
  signal?: AbortSignal
  headers?: Record<string, string>
  retries?: number
  retryDelay?: number
}

interface ApiConfig {
  baseUrl?: string
  defaultTimeout?: number
  defaultHeaders?: Record<string, string>
}

// ========== GENERIC SERVICE CLASS ==========
export class GenericService {
  private static config: ApiConfig = {
    defaultTimeout: 30000, // 30 seconds default
    defaultHeaders: {
      'Content-Type': 'application/json'
    }
  }

  // ========== CONFIGURATION ==========
  static configure(config: Partial<ApiConfig>) {
    this.config = { ...this.config, ...config }
  }

  // ========== UTILITY METHODS ==========
  private static getBaseUrl(): string {
    if (this.config.baseUrl) { return this.config.baseUrl }

    const serverUrl = useRequestURL()
    return `${serverUrl.origin}/site`
  }

  private static buildUrl(moduleApi: string, uriApi: string, id?: string, subController?: string, id2?: string): string {
    const baseUrl = this.getBaseUrl()
    let url = `${baseUrl}/${moduleApi}/${uriApi}`

    if (id) { url += `/${id}` }
    if (subController) { url += `/${subController}` }
    if (id2) { url += `/${id2}` }

    return url
  }

  private static async makeRequest<T>(
    url: string,
    method: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE' = 'GET',
    body?: any,
    options?: RequestOptions
  ): Promise<T> {
    const { $api } = useNuxtApp()

    const requestOptions: any = {
      method,
      headers: {
        ...this.config.defaultHeaders,
        ...options?.headers
      }
    }

    if (body) {
      if (body instanceof FormData) {
        // Remove Content-Type for FormData to let browser set boundary
        delete requestOptions.headers['Content-Type']
        requestOptions.body = body
      }
      else {
        requestOptions.body = body
      }
    }

    // Handle timeout and signal
    if (options?.timeout || options?.signal) {
      const controller = new AbortController()

      if (options.signal) {
        options.signal.addEventListener('abort', () => controller.abort())
      }

      if (options.timeout) {
        setTimeout(() => controller.abort(), options.timeout)
      }

      requestOptions.signal = controller.signal
    }

    try {
      return await $api<T>(url, requestOptions)
    }
    catch (error: any) {
      if (error.name === 'AbortError') {
        throw new Error('Request timed out or was cancelled')
      }
      throw error
    }
  }

  // ========== CORE CRUD OPERATIONS ==========
  static async get<T = any>(moduleApi: string, uriApi: string, options?: RequestOptions): Promise<T> {
    const url = this.buildUrl(moduleApi, uriApi)
    return this.makeRequest<T>(url, 'GET', undefined, options)
  }

  static async getById<T = any>(
    moduleApi: string,
    uriApi: string,
    id: string,
    subController?: string,
    id2?: string,
    options?: RequestOptions
  ): Promise<T> {
    const url = this.buildUrl(moduleApi, uriApi, id, subController, id2)
    return this.makeRequest<T>(url, 'GET', undefined, options)
  }

  static async create<T = any>(
    moduleApi: string,
    uriApi: string,
    payload: any,
    options?: RequestOptions
  ): Promise<T> {
    const url = this.buildUrl(moduleApi, uriApi)
    return this.makeRequest<T>(url, 'POST', payload, options)
  }

  static async update<T = any>(
    moduleApi: string,
    uriApi: string,
    id: string,
    payload: any,
    method: 'PUT' | 'PATCH' = 'PATCH',
    options?: RequestOptions
  ): Promise<T> {
    const url = this.buildUrl(moduleApi, uriApi, id)
    return this.makeRequest<T>(url, method, payload, options)
  }

  static async updateWithoutId<T = any>(
    moduleApi: string,
    uriApi: string,
    payload: any,
    options?: RequestOptions
  ): Promise<T> {
    const url = this.buildUrl(moduleApi, uriApi)
    return this.makeRequest<T>(url, 'PATCH', payload, options)
  }

  static async delete<T = any>(
    moduleApi: string,
    uriApi: string,
    id: string,
    subController?: string,
    id2?: string,
    options?: RequestOptions
  ): Promise<T> {
    const url = this.buildUrl(moduleApi, uriApi, id, subController, id2)
    return this.makeRequest<T>(url, 'DELETE', undefined, options)
  }

  // ========== SEARCH OPERATIONS ==========
  static async search<T = SearchResponse>(
    moduleApi: string,
    uriApi: string,
    payload: IQueryRequest,
    options?: RequestOptions
  ): Promise<T> {
    const url = `${this.buildUrl(moduleApi, uriApi)}/search`
    return this.makeRequest<T>(url, 'POST', payload, options)
  }

  static async searchWithoutSearch<T = SearchResponse>(
    moduleApi: string,
    uriApi: string,
    payload: IQueryRequest,
    options?: RequestOptions
  ): Promise<T> {
    const url = this.buildUrl(moduleApi, uriApi)
    return this.makeRequest<T>(url, 'POST', payload, options)
  }

  static async searchById<T = any>(
    moduleApi: string,
    uriApi: string,
    options?: RequestOptions
  ): Promise<T> {
    const url = this.buildUrl(moduleApi, uriApi)
    return this.makeRequest<T>(url, 'GET', undefined, options)
  }

  // ========== FILE OPERATIONS ==========
  static async uploadFile<T = any>(
    moduleApi: string,
    uriApi: string,
    file: File,
    options?: RequestOptions
  ): Promise<T> {
    const formData = new FormData()
    formData.append('file', file)

    const url = `${this.buildUrl(moduleApi, uriApi)}/upload`
    return this.makeRequest<T>(url, 'POST', formData, options)
  }

  static async sendFormData<T = any>(
    moduleApi: string,
    uriApi: string,
    formData: FormData,
    options?: RequestOptions
  ): Promise<T> {
    const url = this.buildUrl(moduleApi, uriApi)
    return this.makeRequest<T>(url, 'POST', formData, options)
  }

  static async getImageUrl(file: File): Promise<string | null> {
    if (!file) { return null }

    try {
      const response = await this.uploadFile('cloudbridges', 'files', file)
      return (response as any)?.data?.url || null
    }
    catch (error) {
      console.error('Error uploading image:', error)
      return null
    }
  }

  // ========== BULK OPERATIONS ==========
  static async createMany<T = any>(
    moduleApi: string,
    uriApi: string,
    payload: any,
    options?: RequestOptions
  ): Promise<T> {
    const url = `${this.buildUrl(moduleApi, uriApi)}/many`
    return this.makeRequest<T>(url, 'POST', payload, options)
  }

  static async createBulk<T = any>(
    moduleApi: string,
    uriApi: string,
    payload: any,
    options?: RequestOptions
  ): Promise<T> {
    const url = `${this.buildUrl(moduleApi, uriApi)}/bulk`
    return this.makeRequest<T>(url, 'POST', payload, options)
  }

  // ========== EXPORT/IMPORT OPERATIONS ==========
  static async export<T = any>(
    moduleApi: string,
    uriApi: string,
    payload: IQueryRequest,
    options?: RequestOptions
  ): Promise<T> {
    const url = `${this.buildUrl(moduleApi, uriApi)}/export`
    return this.makeRequest<T>(url, 'POST', payload, options)
  }

  static async import<T = any>(
    moduleApi: string,
    uriApi: string,
    payload: any,
    options?: RequestOptions
  ): Promise<T> {
    const url = `${this.buildUrl(moduleApi, uriApi)}/import-innsist`
    return this.makeRequest<T>(url, 'POST', payload, options)
  }

  static async importFile<T = any>(
    moduleApi: string,
    uriApi: string,
    formData: FormData,
    options?: RequestOptions
  ): Promise<T> {
    const url = this.buildUrl(moduleApi, uriApi)
    return this.makeRequest<T>(url, 'POST', formData, options)
  }

  static async importSearch<T = PaginatedResponse>(
    moduleApi: string,
    uriApi: string,
    payload: any,
    options?: RequestOptions
  ): Promise<T> {
    const url = `${this.buildUrl(moduleApi, uriApi)}/import-search`
    return this.makeRequest<T>(url, 'POST', payload, options)
  }

  static async importSearchAuto<T = PaginatedResponse>(
    moduleApi: string,
    uriApi: string,
    payload: any,
    options?: RequestOptions
  ): Promise<T> {
    const url = `${this.buildUrl(moduleApi, uriApi)}/import-search-auto`
    return this.makeRequest<T>(url, 'POST', payload, options)
  }

  // ========== SPECIALIZED OPERATIONS ==========
  static async assign<T = any>(
    moduleApi: string,
    uriApi: string,
    payload: any,
    options?: RequestOptions
  ): Promise<T> {
    const url = `${this.buildUrl(moduleApi, uriApi)}/assign`
    return this.makeRequest<T>(url, 'POST', payload, options)
  }

  static async unassign<T = any>(
    moduleApi: string,
    uriApi: string,
    payload: any,
    options?: RequestOptions
  ): Promise<T> {
    const url = `${this.buildUrl(moduleApi, uriApi)}/unassign`
    return this.makeRequest<T>(url, 'POST', payload, options)
  }

  static async sendList<T = SearchResponse>(
    moduleApi: string,
    uriApi: string,
    payload: any,
    options?: RequestOptions
  ): Promise<T> {
    const url = `${this.buildUrl(moduleApi, uriApi)}/send-list`
    return this.makeRequest<T>(url, 'POST', payload, options)
  }

  static async updateBookings<T = any>(
    moduleApi: string,
    uriApi: string,
    payload: any,
    options?: RequestOptions
  ): Promise<T> {
    const url = `${this.buildUrl(moduleApi, uriApi)}/update`
    return this.makeRequest<T>(url, 'POST', payload, options)
  }

  static async createInvoiceType<T = PaginatedResponse>(
    moduleApi: string,
    uriApi: string,
    payload: any,
    options?: RequestOptions
  ): Promise<T> {
    const url = this.buildUrl(moduleApi, uriApi)
    return this.makeRequest<T>(url, 'POST', payload, options)
  }

  // ========== RECONCILE OPERATIONS ==========
  static async importReconcileFile<T = any>(
    moduleApi: string,
    uriApi: string,
    formData: FormData,
    options?: RequestOptions
  ): Promise<T> {
    const url = this.buildUrl(moduleApi, uriApi)
    return this.makeRequest<T>(url, 'POST', formData, options)
  }

  static async importReconcileAuto<T = any>(
    moduleApi: string,
    uriApi: string,
    formData: FormData,
    options?: RequestOptions
  ): Promise<T> {
    const url = this.buildUrl(moduleApi, uriApi)
    return this.makeRequest<T>(url, 'POST', formData, options)
  }

  // ========== SHARE FILE OPERATIONS ==========
  static async searchShareFile<T = SearchResponse>(
    moduleApi: string,
    uriApi: string,
    payload: IQueryRequest,
    options?: RequestOptions
  ): Promise<T> {
    const url = this.buildUrl(moduleApi, uriApi)
    return this.makeRequest<T>(url, 'POST', payload, options)
  }

  static async searchShareFileById<T = any>(
    moduleApi: string,
    uriApi: string,
    id: string,
    subController?: string,
    id2?: string,
    options?: RequestOptions
  ): Promise<T> {
    const url = this.buildUrl(moduleApi, uriApi, id, subController, id2)
    return this.makeRequest<T>(url, 'GET', undefined, options)
  }

  // ========== ADVANCED REQUEST METHODS ==========
  static async createWithTimeout<T = any>(
    moduleApi: string,
    uriApi: string,
    payload: any,
    timeout: number = 600000, // 10 minutes default
    options?: Omit<RequestOptions, 'timeout'>
  ): Promise<T> {
    const url = this.buildUrl(moduleApi, uriApi)
    return this.makeRequest<T>(url, 'POST', payload, { ...options, timeout })
  }

  static async createReport<T = any>(
    moduleApi: string,
    uriApi: string,
    payload: any,
    timeout: number = 600000 // 10 minutes default for reports
  ): Promise<T> {
    return this.createWithTimeout<T>(moduleApi, uriApi, payload, timeout)
  }

  static async createWithRetry<T = any>(
    moduleApi: string,
    uriApi: string,
    payload: any,
    retries: number = 3,
    retryDelay: number = 1000,
    options?: RequestOptions
  ): Promise<T> {
    let lastError: Error | null = null

    for (let attempt = 0; attempt <= retries; attempt++) {
      try {
        const url = this.buildUrl(moduleApi, uriApi)
        return await this.makeRequest<T>(url, 'POST', payload, options)
      }
      catch (error: any) {
        lastError = error instanceof Error ? error : new Error(String(error))

        if (attempt < retries) {
          await new Promise(resolve => setTimeout(resolve, retryDelay * (attempt + 1)))
        }
      }
    }

    throw lastError || new Error('Request failed after all retry attempts')
  }
}

// ========== BACKWARD COMPATIBILITY ==========
// Export as object for backward compatibility
export const GenericServiceObject = {
  search: GenericService.search.bind(GenericService),
  searchWithoutSearch: GenericService.searchWithoutSearch.bind(GenericService),
  export: GenericService.export.bind(GenericService),
  searchById: GenericService.searchById.bind(GenericService),
  getById: GenericService.getById.bind(GenericService),
  get: GenericService.get.bind(GenericService),
  create: GenericService.create.bind(GenericService),
  createWithTimeout: GenericService.createWithTimeout.bind(GenericService),
  createReport: GenericService.createReport.bind(GenericService),
  createMany: GenericService.createMany.bind(GenericService),
  createBulk: GenericService.createBulk.bind(GenericService),
  update: GenericService.update.bind(GenericService),
  updateWithOutId: GenericService.updateWithoutId.bind(GenericService),
  uploadFile: GenericService.uploadFile.bind(GenericService),
  deleteItem: GenericService.delete.bind(GenericService),
  getUrlByImage: GenericService.getImageUrl.bind(GenericService),
  importSearch: GenericService.importSearch.bind(GenericService),
  importFile: GenericService.importFile.bind(GenericService),
  importReconcileFile: GenericService.importReconcileFile.bind(GenericService),
  importReconcileAuto: GenericService.importReconcileAuto.bind(GenericService),
  importSearchAuto: GenericService.importSearchAuto.bind(GenericService),
  createInvoiceType: GenericService.createInvoiceType.bind(GenericService),
  sendList: GenericService.sendList.bind(GenericService),
  sendFormData: GenericService.sendFormData.bind(GenericService),
  searchShareFileById: GenericService.searchShareFileById.bind(GenericService),
  searchShareFile: GenericService.searchShareFile.bind(GenericService),
  assign: GenericService.assign.bind(GenericService),
  unassign: GenericService.unassign.bind(GenericService),
  import: GenericService.import.bind(GenericService),
  updateBookings: GenericService.updateBookings.bind(GenericService)
}

import type { $Fetch, NitroFetchRequest } from 'nitropack'
import type { IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'

export function repository<T>(fetch: $Fetch<T, NitroFetchRequest>) {
  return {
    async search<T>(MODULE_NAME: string, URI_API: string, payload: IQueryRequest) {
      const url = `/site/${MODULE_NAME}/${URI_API}/search`
      return fetch<T>(url, {
        method: 'POST',
        body: payload
      })
    },
    async searchById(MODULE_NAME: string, URI_API: string) {
      const url = `/site/${MODULE_NAME}/${URI_API}`
      return fetch(url)
    },
    async userMe() {
      const url = '/api/user/me'
      return fetch(url)
    },
    async create(MODULE_NAME: string, URI_API: string, payload: IQueryRequest) {
      const url = `/site/${MODULE_NAME}/${URI_API}`
      return fetch(url, {
        method: 'POST',
        body: payload
      })
    },
    async changeUserBusiness(payload: IQueryRequest) {
      const url = `/users/business`
      return fetch(url, {
        method: 'POST',
        body: payload
      })
    },
    async sendCodeOtp(payload: IQueryRequest) {
      const url = `/auth/forgot-password`
      return fetch(url, {
        method: 'POST',
        body: payload
      })
    },
    async changePasswordWithOtp(payload: IQueryRequest) {
      const url = `/auth/change-password`
      return fetch(url, {
        method: 'POST',
        body: payload
      })
    },
    async changePasswordFirstTime(payload: any) {
      const url = `/auth/firsts-change-password`
      return fetch(url, {
        method: 'POST',
        body: payload
      })
    },
    async sendCodeOtpLogged(payload: { email: string, tokenCaptcha: string | undefined }) {
      const url = `/users/change-password-otp`
      return fetch(url, {
        method: 'POST',
        body: payload
      })
    },
    async changePasswordLogged(payload: { payload: { newPassword: string, otp: string }, tokenCaptcha: string | undefined }) {
      const url = `/users/change-password`
      return fetch(url, {
        method: 'PATCH',
        body: payload
      })
    },
  }
}

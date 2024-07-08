import { GenericService } from './generic-services'
import type { IQueryRequest, IStandardObject } from '~/components/fields/interfaces/IFieldInterfaces'

export const ModulesService = {
  async getListModules(payload: IQueryRequest) {
    try {
      let listItems: IStandardObject[] = []
      const response = await GenericService.search('identity', 'module', payload)

      for (const iterator of response.data) {
        try {
          const payload = {
            id: iterator.id,
            name: iterator.name,
            image: iterator.image,
            desc: iterator.description,
            cantPermissions: ''
          }
          listItems = [...listItems, payload]
        }
        catch (error) {

        }
      }
      return listItems
    }
    catch (error) {
      console.error(error)
      return []
    }
  },

  async getListSelected(id: string) {
    try {
      let listItems: IStandardObject[] = []
      const response = await GenericService.getById('identity', 'module', id, 'build')

      for (const iterator of response.data) {
        const payload = {
          id: iterator.id,
          name: iterator.name,
          logo: iterator.image,
          desc: iterator.description,
          cantPermissions: iterator.permissions.length
        }
        listItems = [...listItems, payload]
      }
      return listItems
    }
    catch (error) {
      console.error(error)
      return []
    }
  }

}

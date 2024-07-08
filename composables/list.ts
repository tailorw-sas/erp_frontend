import { GenericService } from '~/services/generic-services'

export interface FilterCriteria {
  key: string
  operator: 'EQUALS' | 'NOT_EQUALS' | 'GREATER_THAN' | 'LESS_THAN' // Puedes añadir más operadores si es necesario
  value: string // Puedes cambiar el tipo según sea necesario (por ejemplo, string | number)
  logicalOperation: 'AND' | 'OR'
}
export async function getList(id: string = '', filter: FilterCriteria[] = []) {
  let listItem: any[] = []
  try {
    const payload
          = {
            filter: id ? [...filter, { key: 'parent.id', operator: 'EQUALS', value: id, logicalOperation: 'AND' }] : [...filter],
            query: '',
            pageSize: 200,
            page: 0
          }

    const response = await GenericService.search('patients', 'locations', payload)

    const { data: dataList } = response
    for (const iterator of dataList) {
      listItem = [...listItem, { id: iterator.id, name: iterator.name }]
    }
    return listItem
  }
  catch (error) {
    console.error('Error fetching Patient:', error)
    return listItem
  }
}

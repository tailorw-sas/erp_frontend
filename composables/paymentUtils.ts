import { GenericService } from '~/services/generic-services'

export interface IQueryToSearch {
  query: string
  keys: string[]
}

export interface IQueryToSort {
  query?: string
  pageSize?: number
  page?: number
  sortBy?: string
  sortType?: string
}

// export interface FilterCriteria {
//   key: string
//   operator: string
//   value: string
//   logicalOperation?: string
// }

export async function getDataList<T, U>(
  moduleApi: string,
  uriApi: string,
  filter: FilterCriteria[] = [],
  objToSearch?: IQueryToSearch,
  mapFunction?: (data: T) => U,
  optionsToSort?: IQueryToSort,
): Promise<U[]> {
  let listItem: U[] = []
  try {
    const payload = {
      filter: objToSearch && objToSearch.query !== ''
        ? [
            ...objToSearch.keys.map(key => ({ key, operator: 'LIKE', value: objToSearch.query, logicalOperation: 'OR' })),
            ...filter
          ]
        : [
            ...filter
          ],
      query: optionsToSort?.query || '',
      pageSize: optionsToSort?.pageSize || 50,
      page: optionsToSort?.page || 0,
      sortBy: optionsToSort?.sortBy || 'createdAt',
      sortType: optionsToSort?.sortType || 'ASC'
    }

    const response = await GenericService.search(moduleApi, uriApi, payload)

    const { data: dataList } = response
    if (mapFunction) {
      listItem = dataList.map(mapFunction)
    }
    else {
      listItem = dataList as unknown as U[]
    }

    return listItem
  }
  catch (error) {
    console.error('Error fetching data:', error)
    return listItem
  }
}

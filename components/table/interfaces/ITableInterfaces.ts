export interface IColumn {
  field: string
  header: string
  type?: 'text' | 'obj' | 'bool' | 'select' | 'local-select' | 'date' | 'image' | 'icon' | 'custom-badge'
  tooltip?: string
  widthTruncate?: string
  sortable?: boolean
  width?: string
  icon?: string
  badge?: {
    color: string
  }
  objApi?: {
    moduleApi: string
    uriApi: string
    keyValue?: string
  }
  filter?: {
    // Define aquí los campos de filtro según tu necesidad
  }[]
  localItems?: {
    id: string
    name: string
  }[]
  statusClassMap?: IStatusClass[]
}

export interface IPagination {
  limit: number
  page: number
  totalElements: number
  totalPages: number
  search: string
}

export interface PaginationEvent {
  page: number
  first: number
  rows: number
  pageCount: number
}

export interface IFilters {
  search: string
  [key: string]: any
}

export interface IObjApi {
  moduleApi: string
  uriApi: string
  keyValue?: string
}

export interface IOperator {
  LIKE?: string
  EQUALS?: string
  GREATER_THAN?: string
  LESS_THAN?: string
  GREATER_THAN_OR_EQUAL_TO?: string
  LESS_THAN_OR_EQUAL_TO?: string
  NOT_EQUALS?: string
  IN?: string
  NOT_IN?: string
  IS_NULL?: string
  IS_NOT_NULL?: string
  IS_TRUE?: string
  IS_FALSE?: string
}

export interface IOperatorBool {
  IS_TRUE: string
  IS_FALSE: string
}

export interface IStatusClass {
  status: string
  class: string
}

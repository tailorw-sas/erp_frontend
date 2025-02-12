import type { CALENDAR_MODE } from '~/utils/Enums'
import type { IFilter } from '~/components/fields/interfaces/IFieldInterfaces'

export interface IColumn {
  field: string
  header: string
  type?: 'text' | 'number' | 'obj' | 'bool' | 'select' | 'local-select' | 'date' | 'image' | 'icon' | 'custom-badge' | 'date-editable' | 'slot-select' | 'slot-icon' | 'slot-text' | 'datetime' | 'datetime-editable' | 'slot-date-editable' | 'slot-bagde'
  tooltip?: string
  widthTruncate?: string
  sortable?: boolean
  columnClass?: string
  showFilter?: boolean
  frozen?: boolean
  width?: string
  minWidth?: string
  maxWidth?: string
  icon?: string
  editable?: boolean
  badge?: {
    color: string
  }
  objApi?: {
    moduleApi: string
    uriApi: string
    keyValue?: string
    mapFunction?: (item: any) => any
    filter?: IFilter[]
    sortOption?: ISortOptions
  }
  filter?: {
    // Define aquí los campos de filtro según tu necesidad
  }[]
  localItems?: {
    id: string
    name: string
  }[]
  statusClassMap?: IStatusClass[]
  props?: IProps
  hidden?: boolean
}

export interface ISortOptions {
  sortBy?: string
  sortType?: ENUM_SHORT_TYPE
}

export interface IProps {
  isRange?: boolean
  calendarMode?: CALENDAR_MODE
  maxDate?: Date
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

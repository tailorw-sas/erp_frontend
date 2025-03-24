import type { CALENDAR_MODE } from '~/utils/Enums'

export interface IColumn {
  field: string
  header: string
  type?: 'text' | 'number' | 'obj' | 'bool' | 'select' | 'local-select' | 'date' | 'image' | 'icon' | 'custom-badge' | 'date-editable' | 'slot-select' | 'slot-icon' | 'slot-text' | 'datetime' | 'datetime-editable' | 'slot-date-editable' | 'slot-bagde'
  tooltip?: string
  widthTruncate?: string
  sortable?: boolean
  data?: any
  columnClass?: string
  showFilter?: boolean
  frozen?: boolean
  width?: string
  minWidth?: string
  maxWidth?: string
  icon?: string
  editable?: boolean
  objKey?: string
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
export interface PrintItem {
  invoiceId: string;
  invoiceAmount: string;
  dueAmount: string;
  loadingEdit: boolean;
  loadingDelete: boolean;
  agencyCd: string;
  hasAttachments: boolean;
  aging: number;
  dueAmountFormatted: string;
  invoiceAmountFormatted: string;
  invoiceNumber: string;
  hotel: {
    code: string;
    name: string;
  };
  manageInvoiceType: {
    code: string;
    name: string;
  };
}
interface Item {
  id: number; // Ajusta según la estructura de tus datos
  name: string;
}

export interface IPagination {
  page: number
  limit: number
  totalElements: number // ✅ Nombre correcto
  totalPages: number
  search: string
}


declare global {
  interface GlobalComponents {
    Column: typeof import('primevue/column')['default']
    Badge: typeof import('primevue/badge')['default']
  }
}
declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    totalItemsCount: Ref<number>
  }
}
interface PrintPayload {
  invoiceIds: string[];
  invoiceType: string[];
  groupByClient: boolean;
  metadata?: {
    requestDate: string;
    [key: string]: any;
  }
  
}export interface IFilter {
  key: string;
  operator: string;
  value: any;
  logicalOperation: string;
  type: string;
}
export interface IQueryRequest {
  filter: IFilter[];
  page?: number;
  size?: number;
  sort?: string;
  [key: string]: any}


export interface SearchResponse<T = any> {
  data: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
export interface InvoicePayload {
  filter: IFilter[]
  page?: number
  size?: number
  sort?: string
  [key: string]: any
}
export interface PaginationEvent {
  page: number
  first: number
  rows: number
  pageCount: number
}
export interface Payload {
  page: number
  pageSize: number
  filter: any[]
  query: string
  sortBy: string
  sortType: string
}
export interface PageState {
  page: NumberConstructor
  rows: number
  pageCount?: number
  first: number
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

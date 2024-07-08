export interface IOptionField {
  value: any
  label: string
  type: string
  isRequired: boolean
  showRequiredLabel: boolean
  placeholder: string
  errorMessage: string[]
  haveError: boolean
  moduleApi?: string
  uriApi?: string
  minDate?: string | Date
  maxDate?: string | Date
  disabledDates?: Date[] | string[]
  allowedDates?: Date[] | string[]
  showFeedBack?: boolean
  validationKeywords?: string[]
  showCounter?: boolean
  maxLength?: number
  minLength?: number
  filter?: IFilter[]
  localItems?: IStandardObject[]
  parentValues?: string[]
  returnObject?: boolean
  col?: string
  keyValue?: string
  keyRef?: number
  disabled?: boolean
  visible?: boolean
  mask?: string
  fileAccept?: string
  uploadFile?: boolean
  generatePassword?: boolean
}

export interface IStandardObject {
  id: string
  name: string
}

export interface NestedProperty {
  value: any
  errorMessage: string[]
}

export interface INestedObject {
  [key: string]: NestedProperty
}

export interface IFormField {
  [key: string]: IOptionField
}

export interface ObjErrorMessage {
  key: string
  message: string
}

export interface ValidationMethods {
  [key: string]: (value: string) => string | undefined
}

export interface IFilter {
  key: string
  operator: string
  value: string | string []
  logicalOperation: string
  type?: 'filterSearch' | 'filterTable'
}

export interface IQueryRequest {
  filter: IFilter[]
  query: string
  pageSize: number
  page: number
  sortBy?: string
  sortType?: string
}

export interface IFileMetadata {
  objectURL: string
  lastModified: number
  lastModifiedDate: Date
  name: string
  size: number
  type: string
  webkitRelativePath?: string
}

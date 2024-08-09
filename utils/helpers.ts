import dayjs from 'dayjs'
import { usePrimeVue } from 'primevue/config'
import type { IFilter } from '~/components/fields/interfaces/IFieldInterfaces'
import type { FieldDefinitionType } from '~/components/form/EditFormV2'
import type { IColumn } from '~/components/table/interfaces/ITableInterfaces'
import { E_STATUS } from '~/utils/Enums'

export function helpers() {
  return {
    async verifyRecaptchaToken(token: string, secretKey: string) {
      const response = await fetch(`https://www.google.com/recaptcha/api/siteverify?secret=${secretKey}&response=${token}`, {
        method: 'POST'
      })
      const data = await response.json()
      return data.success
    }
  }
}

export function isValidUrl(urlString: string) {
  try {
    return new URL(urlString) // Solo se usa para la validación, no se asigna
  }
  catch (e) {
    return false
  }
}

export function formatSize(bytes: number) {
  const $primevue = usePrimeVue()
  const k = 1024
  const dm = 3
  const sizes = $primevue.config.locale?.fileSizeTypes || ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']

  if (bytes === 0) {
    return `0 ${sizes[0]}`
  }

  const i = Math.floor(Math.log(bytes) / Math.log(k))
  const formattedSize = Number.parseFloat((bytes / k ** i).toFixed(dm))

  return `${formattedSize} ${sizes[i]}`
}

export function toNegative(number: number | string | any) {
  if (!number || isNaN(number)) { return 0 }

  if (Number(number) < 0) {
    return Number(number)
  }

  return -number
}

export function toPositive(number: number | string | any) {
  if (!number || isNaN(number)) { return 0 }

  if (Number(number) > 0) {
    return Number(number)
  }

  return -number
}

export function statusToBoolean(status: string): boolean {
  if (status in E_STATUS) {
    return E_STATUS[status as keyof typeof E_STATUS] === E_STATUS.ACTIVE
  }
  return false
}

export function statusToString(isActive: boolean): string {
  return isActive ? E_STATUS.ACTIVE : E_STATUS.INACTIVE
}

// FILTER ---------------------------------------------------

function isSelectField(field: any, columns: IColumn[]) {
  const column = columns.find(column => column.field === field)
  return column && (column.type === 'select' || column.type === 'local-select' || column.type === 'slot-select')
}

function isLocalItemsField(field: any, columns: IColumn[]) {
  const column = columns.find(column => column.field === field)
  return column && (column.localItems && column.localItems?.length > 0)
}

function getTransformedKey(keyName: string, columns: IColumn[]) {
  let keyResult = ''
  if (!isLocalItemsField(keyName, columns) && keyResult !== '') {
    keyResult = `${keyResult}.id`
  }
  else if (!isLocalItemsField(keyName, columns) && keyResult === '') {
    keyResult = `${keyName}.id`
  }
  else if (isLocalItemsField(keyName, columns) && keyResult === '') {
    keyResult = `${keyName}`
  }
  return keyResult
}

function existsInPayloadFilter(key: string, array: IFilter[]): boolean {
  return array.some(filter => filter.key === key)
}

function isValidDate(value: any) {
  if (!value || Number.parseInt(value, 10) || typeof value === 'boolean') {
    return false
  }
  const subStringValue = dayjs(value).format('YYYY-MM-DD').substring(0, 10)

  const dateTypeOne = /^\d{4}-\d{2}-\d{2}$/

  if (!dateTypeOne.test(subStringValue)) {
    return false
  }
  const date = dayjs(subStringValue, 'YYYY-MM-DD')

  return date.isValid()
}

export async function getEventFromTable(payloadFilter: any, columns: IColumn[]) {
  let arrayFilter: IFilter[] | undefined = []
  if (typeof payloadFilter === 'object') {
    for (const key in payloadFilter) {
      if (Object.prototype.hasOwnProperty.call(payloadFilter, key) && key !== 'search') {
        // Para los tipos Select
        if (isSelectField(key, columns)) {
          const element = payloadFilter[key]

          if (element && Array.isArray(element.constraints) && element.constraints.length > 0) {
            for (const iterator of element.constraints) {
              if (iterator.value) {
                const ketTemp = getTransformedKey(key, columns)

                // Verificamos si el filtro ya existe
                if (!existsInPayloadFilter(ketTemp, arrayFilter)) {
                  // Si no existe lo agregamos
                  let operator: string = ''
                  if ('matchMode' in iterator) {
                    if (typeof iterator.matchMode === 'object') {
                      operator = iterator.matchMode.id.toUpperCase()
                    }
                    else {
                      operator = iterator.matchMode.toUpperCase()
                    }
                  }

                  if (Array.isArray(iterator.value) && iterator.value.length > 0) {
                    const objFilter: IFilter = {
                      key: ketTemp,
                      operator,
                      value: iterator.value.length > 0 ? [...iterator.value.map(item => item.id)] : [],
                      logicalOperation: 'AND'
                    }
                    arrayFilter = objFilter.value.length > 0 ? [...arrayFilter, objFilter] : []
                  }
                  else if (typeof iterator.value === 'object') {
                    const objFilter: IFilter = {
                      key: ketTemp,
                      operator,
                      value: iterator.value || '',
                      logicalOperation: 'AND'
                    }
                    arrayFilter = [...arrayFilter, objFilter]
                  }
                }
                else {
                  // En caso de que ya exista
                  const filterKey = ketTemp
                  const index = arrayFilter.findIndex(item => item.key === filterKey)
                  if (index !== -1) {
                    arrayFilter[index].value = [...arrayFilter[index].value, iterator.id]
                  }
                }
              }
            }
          }
        }
        else {
          // En caso de que no sea un filtro de tipo SELECT

          const element = payloadFilter[key]
          let operator: string = ''
          if ('matchMode' in element) {
            if (typeof element.matchMode === 'object') {
              operator = element.matchMode.id.toUpperCase()
            }
            else {
              operator = element.matchMode.toUpperCase()
            }
          }

          if (key === 'status') {
            if (element.value || element.value === false) {
              const objFilter: IFilter = {
                key,
                operator,
                value: element.value ? 'ACTIVE' : 'INACTIVE',
                logicalOperation: 'AND'
              }
              arrayFilter = [...arrayFilter, objFilter]
            }
          }
          else if (element && element.constraints && element.constraints.length > 0 && isValidDate(element.constraints[0].value)) {
            let operatorForDate: string = ''
            if ('matchMode' in element.constraints[0]) {
              if (typeof element.constraints[0].matchMode === 'object') {
                operatorForDate = element.constraints[0].matchMode.id.toUpperCase()
              }
              else {
                operatorForDate = element.constraints[0].matchMode.toUpperCase()
              }
            }

            const objFilter: IFilter = {
              key,
              operator: operatorForDate,
              value: dayjs(element.constraints[0].value).format('YYYY-MM-DD'),
              logicalOperation: 'AND'
            }
            arrayFilter = [...arrayFilter, objFilter]
          }
          else if (element.value || element.value === false) {
            const objFilter: IFilter = {
              key,
              operator: operator === 'CONTAINS' ? 'LIKE' : operator,
              value: element.value,
              logicalOperation: 'AND'
            }
            arrayFilter = [...arrayFilter, objFilter]
          }
        }
      }
    }
    // Eliminando elmentos duplicados
    arrayFilter = arrayFilter.filter((value, index, self) => {
      return self.indexOf(value) === index
    })

    return arrayFilter
  }
}

type FieldProperty = keyof FieldDefinitionType
export function updateFieldProperty(fields: Array<FieldDefinitionType>, fieldName: string, fieldProperty: FieldProperty, propertyValue: any) {
  const field = fields.find(f => f.field === fieldName)
  if (field) {
    field[fieldProperty] = propertyValue
  }
}

export async function fileToBase64(file: File) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = () => {
      resolve(reader.result) // Elimina el prefijo 'data:image/png;base64,'
    }
    reader.onerror = error => reject(error)
  })
}

export function getLastDayOfMonth(date: Date): Date {
  const year = date.getFullYear()
  const month = date.getMonth()
  return new Date(year, month + 1, 0)
}

export async function base64ToFile(base64String: string, filename: string, mimeType: string) {
  const byteCharacters = atob(base64String)
  const byteNumbers = Array.from({ length: byteCharacters.length })

  for (let i = 0; i < byteCharacters.length; i++) {
    byteNumbers[i] = byteCharacters.charCodeAt(i)
  }

  const byteArray = new Uint8Array(byteNumbers)
  const blob = new Blob([byteArray], { type: mimeType })

  return new File([blob], filename, { type: mimeType })
}

export function convertirAFechav2(fecha: string | null): string | null {
  if (!fecha) {
    return null
  }

  if (/^\d{8}$/.test(fecha)) {
    // Formato YYYYMMDD
    return fecha
  }
  else if (/^\d{1,2}\/\d{1,2}\/\d{4}$/.test(fecha) || /^\d{1,2}\/\d{1,2}\/\d{2}$/.test(fecha)) {
    // Formato DD/MM/YYYY
    const [dia, mes, anio] = fecha.split('/').map(Number)
    return `${anio}${mes.toString().padStart(2, '0')}${dia.toString().padStart(2, '0')}`
  }

  return 'Invalid date format'
}

export function convertirFecha(value: string) {
  // Verifica si la fecha tiene el formato esperado

  if (!value || typeof value === 'boolean') {
    return value
  }

  let subStringValue = ''
  const fechaRegex = /^\d{2}\/\d{2}\/\d{4}$/
  if (!fechaRegex.test(value)) {
    subStringValue = dayjs(value).format('YYYY-MM-DD')
    const dateTypeOne = /^\d{4}-\d{2}-\d{2}$/
    if (!dateTypeOne.test(subStringValue)) {
      return value
    }
    subStringValue = dayjs(subStringValue).format('YYYY-MM-DD').substring(0, 10)
  }
  else {
    const [dia, mes, anio] = value.split('/')
    const aux = `${anio}-${mes}-${dia}`
    subStringValue = dayjs(aux).format('YYYY-MM-DD').substring(0, 10)
  }
  return subStringValue

  // formatDate(value)
}

export function isValidFormatDate(value: any) {
  try {
    if (!value || typeof value === 'boolean') {
      return false
    }
    // const subStringValue = dayjs(value).format('YYYY-MM-DD').substring(0, 10)

    const fechaRegex1 = /^\d{2}\/\d{2}\/\d{4}$/
    const fechaRegex2 = /^\d{8}$/

    if (!fechaRegex1.test(value) && !fechaRegex2.test(value)) {
      return false
    }
    // const date = dayjs(subStringValue, 'YYYY-MM-DD')

    return true
  }
  catch (error) {
    return false
  }
}

function formatDate(value) {
  // Helper function to check if a date is valid
  function isValidDate(date) {
    return date instanceof Date && !Number.isNaN(date.getTime())
  }

  // Helper function to parse and format date
  function parseAndFormatDate(input) {
    let date

    // Handle formats like DD/MM/YYYY
    const ddmmyyyy = /^(\d{2})\/(\d{2})\/(\d{4})$/
    // Handle formats like YYYYMMDD or YYYYMMDDHHmmss
    const yyyymmdd = /^(\d{4})(\d{2})(\d{2})$/

    if (ddmmyyyy.test(input)) {
      const [, day, month, year] = input.match(ddmmyyyy)
      date = new Date(`${year}-${month}-${day}`)
      // date = dayjs(input).format('YYYY-MM-DD')
    }
    else if (yyyymmdd.test(input)) {
      const [, year, month, day] = input.match(yyyymmdd)
      date = new Date(`${year}-${month}-${day}`)
      // date = dayjs(input).format('YYYY-MM-DD')
    }
    else {
      // For other formats or invalid formats, just return the input
      return input
    }

    if (isValidDate(date)) {
      // Format date to YYYY-MM-DD
      // const yearStr = date.getFullYear()
      // const monthStr = (date.getMonth() + 1).toString().padStart(2, '0')
      // const dayStr = date.getDate().toString().padStart(2, '0')
      const temp = dayjs(date).format('YYYY-MM-DD')
      return temp
      // return `${yearStr}-${monthStr}-${dayStr}`
    }
    else {
      return input
    }
  }

  return parseAndFormatDate(value)
}

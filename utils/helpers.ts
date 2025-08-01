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
  catch {
    return false
  }
}

export function openOrDownloadFile(url: string) {
  if (isValidUrl(url)) {
    // window.open(url, '_blank')
    navigateTo(`/view-file/?url=${url}`, { open: { target: '_blank' } })
  }
  else {
    console.error('Invalid URL')
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
  if (!number || Number.isNaN(number)) { return 0 }

  if (Number(number) < 0) {
    return Number(number)
  }

  return -number
}

export function toPositive(number: number | string | any) {
  if (!number || Number.isNaN(number)) { return 0 }

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

  // if (/^\d{8}$/.test(fecha)) {
  //   // Formato YYYYMMDD
  //   return fecha
  // }
  if (/^\d{1,2}\/\d{1,2}\/\d{4}$/.test(fecha)) { // || /^\d{1,2}\/\d{1,2}\/\d{2}$/.test(fecha)
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
  catch {
    return false
  }
}

export function formatNumber(number: any, minDecimals: number = 2, maxDecimals: number = 4) {
  // Asegúrate de que el número sea válido
  if (Number.isNaN(number)) {
    throw new TypeError('El valor proporcionado no es un número.')
  }

  // Utiliza Intl.NumberFormat para formatear el número
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: minDecimals,
    maximumFractionDigits: maxDecimals
  }).format(number)
}

export function parseFormattedNumber(formattedNumber: string): number {
  // Elimina cualquier carácter que no sea un número, punto o signo negativo
  const sanitizedNumber = formattedNumber.replace(/[^0-9.-]/g, '')

  // Convierte el string en un número
  const parsedNumber = Number.parseFloat(sanitizedNumber)

  // Verifica si el resultado es un número válido
  if (Number.isNaN(parsedNumber)) {
    throw new TypeError('El valor proporcionado no se puede convertir en un número.')
  }

  return parsedNumber
}

export function formatCurrency(value: any) {
  if (Number.isNaN(value) || value === null || value === undefined) {
    return 'Invalid value' // Mensaje de error o valor predeterminado
  }

  // Convertir a número en caso de que sea un string válido
  const numericValue = Number(value)

  return numericValue.toLocaleString('en-US', { style: 'currency', currency: 'USD' })
}

export async function customBase64Uploader(event: any, listFields: any, fieldKey: any) {
  const file = event.files[0]
  listFields[fieldKey] = file
}

export function removeDuplicatesMap(array: any[], keys: string[]): any[] {
  const seen = new Map()

  return array.filter((item) => {
    const key = keys.map(k => `${k}-${JSON.stringify(item[k])}`).join('|')
    if (seen.has(key)) {
      return false
    }
    seen.set(key, true)
    return true
  })
}

export function generateSlug(name: string, existingSlugs: Set<string>): string {
  let slug = name
    .toLowerCase()
    .normalize('NFD') // Remueve acentos
    .replace(/[\u0300-\u036F]/g, '') // Elimina caracteres diacríticos
    .replace(/[^a-z0-9-]+/g, '-') // Permite solo letras, números y guiones
    .replace(/-{2,}/g, '-') // Reemplaza múltiples guiones por uno solo
    .replace(/^-+|-+$/g, '') // Elimina guiones al inicio o final

  const originalSlug = slug
  let count = 1

  // Obtener slugs existentes de la lista reactiva
  while (existingSlugs.has(slug)) {
    slug = `${originalSlug}-${count}`
    count++
  }

  existingSlugs.add(slug) // Guardar el slug generado
  return slug
}

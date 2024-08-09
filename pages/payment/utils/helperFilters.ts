import type { IFilter, IQueryRequest } from '~/components/fields/interfaces/IFieldInterfaces'

// export function applyFiltersAndSort(payload: IQueryRequest, items: any[]) {
//   // Aplicar filtros
//   const filteredItems = items.filter((item) => {
//     return payload.filter.every((filter) => {
//       const value = getValueByPath(item, filter.key)
//       switch (filter.operator) {
//         case 'IN':
//           return filter.value.includes(value)
//         case 'NOT_IN':
//           return !filter.value.includes(value)
//         case 'EQUALS':
//           return value === filter.value
//         case 'NOT_EQUALS':
//           return value !== filter.value
//         case 'LIKE':
//           return typeof value === 'string' && value.includes(filter.value)
//         case 'IS_NULL':
//           return value === null
//         case 'IS_NOT_NULL':
//           return value !== null
//         default:
//           return true
//       }
//     })
//   })

//   // Ordenar los elementos filtrados
//   if (payload.sortBy) {
//     filteredItems.sort((a, b) => {
//       const valueA = getValueByPath(a, payload.sortBy)
//       const valueB = getValueByPath(b, payload.sortBy)

//       if (valueA < valueB) { return payload.sortType === 'ASC' ? -1 : 1 }
//       if (valueA > valueB) { return payload.sortType === 'ASC' ? 1 : -1 }
//       return 0
//     })
//   }

//   return filteredItems
// }

export function applyFiltersAndSort(payload: IQueryRequest, items: any[]) {
  // Aplicar filtros
  const filteredItems = items.filter((item) => {
    return payload.filter.every((filter) => {
      const value = getValueByPath(item, filter.key)
      switch (filter.operator) {
        case 'IN':
          return filter.value.includes(value)
        case 'NOT_IN':
          return !filter.value.includes(value)
        case 'EQUALS':
          return value === filter.value
        case 'NOT_EQUALS':
          return value !== filter.value
        case 'LIKE':
          return typeof value === 'string' && value.includes(filter.value)
        case 'IS_NULL':
          return value === null
        case 'IS_NOT_NULL':
          return value !== null
        default:
          return true
      }
    })
  })

  // Ordenar los elementos filtrados
  if (payload.sortBy) {
    filteredItems.sort((a, b) => {
      let valueA = getValueByPath(a, payload.sortBy)
      let valueB = getValueByPath(b, payload.sortBy)

      // Convertir a minúsculas si es una cadena
      if (typeof valueA === 'string') {
        valueA = valueA.toLowerCase()
      }
      if (typeof valueB === 'string') {
        valueB = valueB.toLowerCase()
      }

      if (valueA < valueB) { return payload.sortType === 'ASC' ? -1 : 1 }
      if (valueA > valueB) { return payload.sortType === 'ASC' ? 1 : -1 }
      return 0
    })
  }

  return filteredItems
}

function getValueByPath(obj, path) {
  return path.split('.').reduce((acc, part) => acc && acc[part], obj)
}

// Función para redondear un valor a dos decimales
export function formatToTwoDecimalPlaces(value: number | string) {
  // Intenta convertir el valor a número si es una cadena
  if (typeof value === 'string') {
    value = Number.parseFloat(value)
  }

  // Verifica si el valor es un número y es finito
  if (typeof value === 'number' && Number.isFinite(value)) {
    // Usa toFixed para asegurar que tenga exactamente dos lugares decimales
    return value.toFixed(2)
  }

  // Si no es un número válido, devuelve 0
  return 0
}

// Función para recorrer un objeto y formatear los valores numéricos en claves específicas
// const keysToAnalyze = ['a', 'b', 'c.e', 'c.f[0]', 'g']; // Ejemplo de uso

export function formatNumbersInObject(obj: any, keysToAnalyze: string[]) {
  if (typeof obj !== 'object' || obj === null) {
    throw new Error('Input must be an object')
  }

  if (!Array.isArray(keysToAnalyze)) {
    throw new TypeError('keysToAnalyze must be an array')
  }

  // Función recursiva para recorrer el objeto
  function recurse(value: any, parentKey: string): any {
    if (Array.isArray(value)) {
      return value.map((item, index) => recurse(item, `${parentKey}[${index}]`))
    }
    else if (typeof value === 'object' && value !== null) {
      return Object.keys(value).reduce((acc, key) => {
        const newKey = parentKey ? `${parentKey}.${key}` : key
        acc[key] = recurse(value[key], newKey)
        return acc
      }, {})
    }
    else if (keysToAnalyze.includes(parentKey)) {
      return formatToTwoDecimalPlaces(value)
    }
    else {
      return value
    }
  }

  return recurse(obj, '')
}

export function formatNumber(number: any) {
  // Asegúrate de que el número sea válido
  if (Number.isNaN(number)) {
    throw new TypeError('El valor proporcionado no es un número.')
  }

  // Utiliza Intl.NumberFormat para formatear el número
  return new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 4
  }).format(number)
}

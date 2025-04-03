export function copyListFromColumns(
  columns: { field: string, header: string }[],
  list: any[],
  toast?: any
) {
  try {
    if (!Array.isArray(list) || list.length === 0) {
      throw new Error('Lista vacía o inválida.')
    }

    const fields = columns.map(col => col.field)
    const headers = columns.map(col => col.header)
    const headerRow = headers.join('\t')

    const rows = list.map(item =>
      fields.map((field) => {
        const value = getNestedValue(item, field)

        if (value && typeof value === 'object') {
          if (value.code && value.name) {
            if (value.name.includes(value.code)) { return value.name }
            return `${value.code} - ${value.name}`
          }
          else if (value.code) {
            return value.code
          }
          else if (value.name) {
            return value.name
          }
          else {
            return JSON.stringify(value)
          }
        }

        return value ?? ''
      }).join('\t')
    ).join('\n')

    const clipboardData = `${headerRow}\n${rows}`

    // Fallback para HTTP (sin navigator.clipboard)
    copyTextFallback(clipboardData, toast)
  }
  catch (error) {
    console.error('Error al copiar:', error)
    toast?.add({
      severity: 'error',
      summary: 'Error',
      detail: 'No se pudieron copiar los datos',
      life: 3000
    })
  }
}

function getNestedValue(obj: any, path: string): any {
  const value = path.split('.').reduce((acc, part) => acc?.[part], obj)

  if (typeof value === 'object' && value !== null) {
    if ('code' in value && 'name' in value) {
      return `${value.code} - ${value.name}`
    }
    if ('name' in value) {
      return value.name
    }
    return JSON.stringify(value) // Fallback
  }

  return value
}

function copyTextFallback(text: string, toast?: any) {
  try {
    const textarea = document.createElement('textarea')
    textarea.value = text
    textarea.setAttribute('readonly', '')
    textarea.style.position = 'absolute'
    textarea.style.left = '-9999px'
    document.body.appendChild(textarea)
    textarea.select()
    const success = document.execCommand('copy')
    document.body.removeChild(textarea)

    if (success) {
      toast?.add({
        severity: 'success',
        summary: 'Copiado',
        detail: 'Datos copiados al portapapeles',
        life: 3000
      })
    }
    else {
      throw new Error('No se pudo copiar.')
    }
  }
  catch (error) {
    console.error('Error al copiar (fallback):', error)
    toast?.add({
      severity: 'error',
      summary: 'Error',
      detail: 'No se pudieron copiar los datos',
      life: 3000
    })
  }
}

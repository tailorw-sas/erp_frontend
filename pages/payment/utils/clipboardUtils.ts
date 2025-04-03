export function copyTableToClipboard(columns: any[], data: any[], toast?: any) {
  try {
    if (!Array.isArray(data) || data.length === 0) {
      throw new Error('No hay datos para copiar.')
    }

    // Cabeceras
    const headers = columns.map(col => col.header).join('\t')

    // Filas
    const rows = data.map(item =>
      columns.map((col) => {
        const value = getNestedValue(item, col.field)

        if (value && typeof value === 'object') {
          if (value.code && value.name) {
            return value.name.includes(value.code) ? value.name : `${value.code} - ${value.name}`
          }
          else if (value.name) {
            return value.name
          }
          else if (value.code) {
            return value.code
          }
          return JSON.stringify(value)
        }

        return value ?? ''
      }).join('\t')
    ).join('\n')

    const clipboardData = `${headers}\n${rows}`

    // Fallback para HTTP
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

// Función auxiliar para acceder a campos anidados
function getNestedValue(obj: any, path: string): any {
  if (!obj || !path) { return '' }
  const value = path.split('.').reduce((acc, part) => acc?.[part], obj)

  if (typeof value === 'object' && value !== null) {
    if ('code' in value && 'name' in value) {
      return value.name.includes(value.code) ? value.name : `${value.code} - ${value.name}`
    }
    if ('name' in value) { return value.name }
    if ('code' in value) { return value.code }
    return JSON.stringify(value)
  }

  return value
}

// Fallback sin HTTPS (copiar usando textarea oculto)
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
      throw new Error('Falló la copia con fallback')
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

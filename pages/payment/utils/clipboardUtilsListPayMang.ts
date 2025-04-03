export function copyPaymentsToClipboardPayMang(
  columns: { field: string, header: string }[],
  list: any[],
  toast?: any
) {
  try {
    if (!Array.isArray(list) || list.length === 0) {
      throw new Error('Lista vacía o inválida.')
    }

    const fields = columns
      .filter(col => col.field && col.header && col.field !== 'icon')
      .map(col => col.field)

    const headers = columns
      .filter(col => col.field && col.header && col.field !== 'icon')
      .map(col => col.header)

    const headerRow = headers.join('\t')

    const rows = list.map(item =>
      fields.map((field) => {
        const value = getNestedValue(item, field)

        if (value && typeof value === 'object') {
          const code = value.code
          const name = value.name

          if (code && name) {
            return name.startsWith(`${code} - `)
              ? name
              : `${code} - ${name}`
          }
          else if (name) {
            return name
          }
          else if (code) {
            return code
          }
          else {
            return JSON.stringify(value)
          }
        }

        return value ?? ''
      }).join('\t')
    ).join('\n')

    const clipboardData = `${headerRow}\n${rows}`

    // Fallback sin HTTPS
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
  if (!obj || !path) { return '' }
  return path.split('.').reduce((acc, part) => acc?.[part], obj)
}

// ✅ Fallback para copiar sin HTTPS
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
      detail: 'No se pudo copiar al portapapeles',
      life: 3000
    })
  }
}

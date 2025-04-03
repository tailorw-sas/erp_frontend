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

        if (value && typeof value === 'object' && value.code && value.name) {
          const code = value.code
          const name = value.name.startsWith(`${code} - `)
            ? value.name.substring(code.length + 3)
            : value.name
          return `${code} - ${name}`
        }

        return value ?? ''
      }).join('\t')
    ).join('\n')

    const clipboardData = `${headerRow}\n${rows}`

    navigator.clipboard.writeText(clipboardData).then(() => {
      if (toast) {
        toast.add({
          severity: 'success',
          summary: 'Copiado',
          detail: 'Datos copiados al portapapeles',
          life: 3000
        })
      }
    }).catch((err) => {
      throw err
    })
  }
  catch (error) {
    console.error('Error al copiar:', error)
    if (toast) {
      toast.add({
        severity: 'error',
        summary: 'Error',
        detail: 'No se pudieron copiar los datos',
        life: 3000
      })
    }
  }
}

function getNestedValue(obj: any, path: string): any {
  return path.split('.').reduce((acc, part) => acc?.[part], obj)
}

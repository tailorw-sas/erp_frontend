export function exportDataToExcel(columns: any[], data: any[], fileName: string = 'export.xlsx') {
  if (!data || data.length === 0) {
    console.error('No hay datos para exportar')
    return
  }

  const headers = columns.map(col => col.header)
  const rows = data.map((item) => {
    return columns.map((col) => {
      const value = getNestedValue(item, col.field)
      return value ?? ''
    })
  })

  // Construcción del contenido CSV
  const csvContent = [headers, ...rows].map(e => e.join('\t')).join('\n')

  const blob = new Blob([csvContent], {
    type: 'application/vnd.ms-excel;charset=utf-8;',
  })
  const url = URL.createObjectURL(blob)

  const link = document.createElement('a')
  link.href = url
  link.setAttribute('download', fileName)
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}

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

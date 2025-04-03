export function copyTableToClipboard(columns: any[], data: any[], toast?: any) {
  try {
    // Cabeceras
    const headers = columns.map(col => col.header).join('\t')

    // Filas
    const rows = data.map(item =>
      columns.map((col) => {
        const value = getNestedValue(item, col.field)
        return typeof value === 'object' ? JSON.stringify(value) : value
      }).join('\t')
    ).join('\n')

    const clipboardData = `${headers}\n${rows}`

    navigator.clipboard.writeText(clipboardData).then(() => {
      if (toast) {
        toast.add({
          severity: 'success',
          summary: 'Copiado',
          detail: 'Datos copiados al portapapeles',
          life: 3000
        })
      }
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

// Función auxiliar para acceder a campos anidados
function getNestedValue(obj: any, path: string): any {
  if (!obj || !path) { return '' }
  return path.split('.').reduce((acc, part) => acc && acc[part], obj)
}

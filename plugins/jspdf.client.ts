// plugins/jspdf.client.ts
export default defineNuxtPlugin(async () => {
  // Solo se ejecuta en el cliente por el sufijo .client.ts
  const { default: jsPDF } = await import('jspdf')

  // Importar auto-table si lo necesitas para tablas
  try {
    await import('jspdf-autotable')
  }
  catch {
    Logger.warn('jspdf-autotable not installed, table features not available')
  }

  // Utility functions para facilitar el uso
  const pdfUtils = {
    // Crear documento básico
    createDoc: (orientation: 'portrait' | 'landscape' = 'portrait') => {
      return new jsPDF(orientation)
    },

    // Generar PDF desde datos tabulares
    generateTablePDF: async (data: any[], columns: string[], title?: string) => {
      const doc = new jsPDF()

      if (title) {
        doc.setFontSize(16)
        doc.text(title, 20, 20)
      }

      // Si autoTable está disponible
      if (typeof (doc as any).autoTable === 'function') {
        (doc as any).autoTable({
          head: [columns],
          body: data.map(row => columns.map(col => row[col])),
          startY: title ? 30 : 20,
          styles: { fontSize: 10 },
          headStyles: { fillColor: [66, 139, 202] }
        })
      }
      else {
        // Fallback sin autoTable
        let y = title ? 40 : 20
        doc.setFontSize(12)

        // Headers
        columns.forEach((col, i) => {
          doc.text(col, 20 + (i * 40), y)
        })

        y += 10
        doc.setFontSize(10)

        // Data rows
        data.forEach((row) => {
          columns.forEach((col, i) => {
            doc.text(String(row[col] || ''), 20 + (i * 40), y)
          })
          y += 8
        })
      }

      return doc
    },

    // Generar reporte de pago
    generatePaymentReport: (paymentData: any) => {
      const doc = new jsPDF()

      // Header
      doc.setFontSize(18)
      doc.text('Reporte de Pago', 20, 20)

      doc.setFontSize(12)
      doc.text(`Fecha: ${new Date().toLocaleDateString()}`, 20, 35)
      doc.text(`ID Transacción: ${paymentData.id || 'N/A'}`, 20, 45)
      doc.text(`Monto: $${paymentData.amount || '0.00'}`, 20, 55)
      doc.text(`Estado: ${paymentData.status || 'Pendiente'}`, 20, 65)

      // Detalles adicionales
      if (paymentData.details) {
        doc.text('Detalles:', 20, 85)
        let y = 95
        Object.entries(paymentData.details).forEach(([key, value]) => {
          doc.text(`${key}: ${value}`, 25, y)
          y += 10
        })
      }

      return doc
    },

    // Descargar PDF directamente
    downloadPDF: (doc: any, filename: string = 'document.pdf') => {
      doc.save(filename)
    },

    // Abrir PDF en nueva ventana
    openPDF: (doc: any) => {
      window.open(doc.output('bloburl'), '_blank')
    }
  }

  return {
    provide: {
      jsPDF,
      pdfUtils
    }
  }
})

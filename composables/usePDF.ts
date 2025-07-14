// composables/usePDF.ts
export function usePDF() {
  const { $jsPDF, $pdfUtils } = useNuxtApp()
  const isClient = process.client
  const isGenerating = ref(false)

  // Verificar si PDF está disponible
  const isPDFReady = computed(() => isClient && $jsPDF && $pdfUtils)

  // Generar PDF básico
  const generateBasicPDF = async (content: string, title?: string) => {
    if (!isPDFReady.value) {
      throw new Error('PDF no disponible en el servidor')
    }

    try {
      isGenerating.value = true

      const doc = $pdfUtils.createDoc()

      if (title) {
        doc.setFontSize(16)
        doc.text(title, 20, 20)
        doc.text(content, 20, 40)
      }
      else {
        doc.text(content, 20, 20)
      }

      return doc
    }
    finally {
      isGenerating.value = false
    }
  }

  // Generar PDF de tabla
  const generateTablePDF = async (
    data: any[],
    columns: string[],
    options: { title?: string, filename?: string } = {}
  ) => {
    if (!isPDFReady.value) {
      throw new Error('PDF no disponible en el servidor')
    }

    try {
      isGenerating.value = true

      const doc = await $pdfUtils.generateTablePDF(data, columns, options.title)

      if (options.filename) {
        $pdfUtils.downloadPDF(doc, options.filename)
      }

      return doc
    }
    finally {
      isGenerating.value = false
    }
  }

  // Generar reporte de pago
  const generatePaymentPDF = async (paymentData: any, download = true) => {
    if (!isPDFReady.value) {
      throw new Error('PDF no disponible en el servidor')
    }

    try {
      isGenerating.value = true

      const doc = $pdfUtils.generatePaymentReport(paymentData)

      if (download) {
        const filename = `payment-${paymentData.id || Date.now()}.pdf`
        $pdfUtils.downloadPDF(doc, filename)
      }

      return doc
    }
    finally {
      isGenerating.value = false
    }
  }

  // Exportar datos de tabla como PDF
  const exportTableData = async (
    tableRef: any,
    filename: string = 'export.pdf',
    title?: string
  ) => {
    if (!isPDFReady.value || !tableRef) { return }

    try {
      isGenerating.value = true

      // Extraer datos de la tabla (asumiendo PrimeVue DataTable)
      const data = tableRef.value?.processedData || tableRef.value?.value || []
      const columns = tableRef.value?.columns?.map((col: any) => col.field)
        || Object.keys(data[0] || {})

      await generateTablePDF(data, columns, { title, filename })
    }
    catch (error) {
      console.error('Error exportando tabla:', error)
      throw error
    }
  }

  // Utilidades adicionales
  const downloadFromDoc = (doc: any, filename: string) => {
    if (!isPDFReady.value) { return }
    $pdfUtils.downloadPDF(doc, filename)
  }

  const openInNewTab = (doc: any) => {
    if (!isPDFReady.value) { return }
    $pdfUtils.openPDF(doc)
  }

  return {
    // Estados
    isGenerating: readonly(isGenerating),
    isPDFReady,
    isClient,

    // Métodos principales
    generateBasicPDF,
    generateTablePDF,
    generatePaymentPDF,
    exportTableData,

    // Utilidades
    downloadFromDoc,
    openInNewTab,

    // Acceso directo a las instancias (para casos avanzados)
    jsPDF: $jsPDF,
    pdfUtils: $pdfUtils
  }
}

interface PaymentDetail {
  id: string
  paymentId: number
  paymentSource: {
    id: string
    code: string
    name: string
    status: string
  }
  reference: string
  transactionDate: string
  paymentStatus: {
    id: string
    code: string
    name: string
    status: string
  }
  client: {
    id: string
    code: string
    name: string
    status: string
  }
  agency: {
    id: string
    code: string
    name: string
    status: string
    agencyTypeResponse: {
      id: string
      code: string
      status: string
      name: string
    }
  }
  hotel: {
    id: string
    code: string
    name: string
    status: string
  }
  bankAccount: {
    id: string
    accountNumber: string
    status: string
  }
  attachmentStatus: {
    id: string
    code: string
    name: string
    status: string
  }
  paymentAmount: number
  paymentBalance: number
  depositAmount: number
  depositBalance: number
  otherDeductions: number
  identified: number
  notIdentified: number
  remark: string
  hasAttachment: boolean
  attachments: Array<{
    id: string
    status: string
    resource: any
    resourceType: {
      id: string
      code: string
      name: string
      description: string
      status: string
    }
    attachmentType: {
      id: string
      code: string
      name: string
      description: string
      status: string
      defaults: boolean
    }
    fileName: string
    fileWeight: string
    path: string
    remark: string
  }>
}

// ✅ Usar el composable para manejar PDFs
export function usePaymentUtils() {
  const { jsPDF, pdfUtils, isGenerating, isPDFReady } = usePDF()

  // Función mejorada basada en tu generateStyledPDF original
  const generateStyledPaymentPDF = async (paymentDetail: PaymentDetail, filename?: string) => {
    if (!isPDFReady.value) {
      throw new Error('PDF generator not ready')
    }

    try {
      isGenerating.value = true

      const doc = pdfUtils.createDoc()

      // Header del documento
      doc.setFontSize(18)
      doc.setFont('helvetica', 'bold')
      doc.text('Detalle de Pago', 105, 15, { align: 'center' })

      // Línea separadora
      doc.setLineWidth(0.5)
      doc.line(10, 20, 200, 20)

      // Organizar la información en formato de grid (igual que tu versión)
      const formDetails = [
        { label: 'ID Pago', value: paymentDetail.paymentId?.toString() || 'N/A' },
        { label: 'Fuente de Pago', value: paymentDetail.paymentSource?.name || 'N/A' },
        { label: 'Referencia', value: paymentDetail.reference || 'N/A' },
        { label: 'Fecha de Transacción', value: paymentDetail.transactionDate || 'N/A' },
        { label: 'Estado de Pago', value: paymentDetail.paymentStatus?.name || 'N/A' },
        { label: 'Cliente', value: paymentDetail.client?.name || 'N/A' },
        { label: 'Agencia', value: paymentDetail.agency?.name || 'N/A' },
        { label: 'Tipo de Agencia', value: paymentDetail.agency?.agencyTypeResponse?.name || 'N/A' },
        { label: 'Hotel', value: paymentDetail.hotel?.name || 'N/A' },
        { label: 'Cuenta Bancaria', value: paymentDetail.bankAccount?.accountNumber || 'N/A' },
        { label: 'Estado de Adjuntos', value: paymentDetail.attachmentStatus?.name || 'N/A' },
        { label: 'Monto de Pago', value: formatCurrency(paymentDetail.paymentAmount) },
        { label: 'Saldo de Pago', value: formatCurrency(paymentDetail.paymentBalance) },
        { label: 'Monto de Depósito', value: formatCurrency(paymentDetail.depositAmount) },
        { label: 'Saldo de Depósito', value: formatCurrency(paymentDetail.depositBalance) },
        { label: 'Otras Deducciones', value: formatCurrency(paymentDetail.otherDeductions) },
        { label: 'Identificado', value: formatCurrency(paymentDetail.identified) },
        { label: 'No Identificado', value: formatCurrency(paymentDetail.notIdentified) },
        { label: 'Comentario', value: paymentDetail.remark || 'Sin comentarios' }
      ]

      // Agregar los detalles del formulario al PDF en formato de grid (3 columnas)
      let yPosition = 30
      const columnWidth = doc.internal.pageSize.getWidth() / 3 - 20

      doc.setFontSize(10)
      doc.setFont('helvetica', 'normal')

      formDetails.forEach((detail, index) => {
        const xPosition = 10 + (index % 3) * (columnWidth + 10)
        if (index % 3 === 0 && index !== 0) {
          yPosition += 12
        }

        // Label en negrita
        doc.setFont('helvetica', 'bold')
        doc.text(`${detail.label}:`, xPosition, yPosition)

        // Valor en normal
        doc.setFont('helvetica', 'normal')
        const labelWidth = doc.getTextWidth(`${detail.label}: `)
        doc.text(String(detail.value), xPosition + labelWidth, yPosition)
      })

      // Añadir tabla de adjuntos si existen
      if (paymentDetail.attachments && paymentDetail.attachments.length > 0) {
        yPosition += 25

        doc.setFontSize(14)
        doc.setFont('helvetica', 'bold')
        doc.text('Adjuntos', 10, yPosition)

        yPosition += 5

        // Preparar datos de la tabla
        const attachmentData = paymentDetail.attachments.map(attachment => [
          `${attachment.id.substring(0, 8)}...`,
          attachment.fileName || 'Sin nombre',
          attachment.fileWeight || 'N/A',
          attachment.status || 'N/A',
          attachment.attachmentType?.name || 'N/A'
        ])

        // Usar autoTable mejorado
        ;(doc as any).autoTable({
          startY: yPosition,
          head: [['ID', 'Nombre Archivo', 'Tamaño', 'Estado', 'Tipo']],
          body: attachmentData,
          theme: 'grid',
          styles: {
            fontSize: 9,
            font: 'helvetica',
            textColor: [40, 40, 40],
            halign: 'center',
            valign: 'middle',
            fillColor: [255, 255, 255],
            lineColor: [44, 62, 80],
            lineWidth: 0.1,
            cellPadding: 3
          },
          headStyles: {
            fillColor: [44, 62, 80],
            textColor: [255, 255, 255],
            fontStyle: 'bold',
            fontSize: 10
          },
          alternateRowStyles: {
            fillColor: [245, 245, 245]
          },
          columnStyles: {
            0: { cellWidth: 25 },
            1: { cellWidth: 50 },
            2: { cellWidth: 20 },
            3: { cellWidth: 20 },
            4: { cellWidth: 35 }
          },
          margin: { top: 20, left: 10, right: 10 }
        })
      }

      // Footer
      const pageHeight = doc.internal.pageSize.getHeight()
      doc.setFontSize(8)
      doc.setFont('helvetica', 'italic')
      doc.text(`Generado el: ${new Date().toLocaleString()}`, 10, pageHeight - 10)
      doc.text(`ID: ${paymentDetail.id}`, 200, pageHeight - 10, { align: 'right' })

      // Guardar o retornar
      const finalFilename = filename || `detalle_pago_${paymentDetail.paymentId || 'unknown'}.pdf`
      doc.save(finalFilename)

      return doc
    }
    finally {
      isGenerating.value = false
    }
  }

  // Función para generar PDF básico (tu función comentada mejorada)
  const generateBasicStyledTable = async (
    data: Array<{ id: number | string, name: string, email: string }>,
    title: string = 'Tabla de Datos'
  ) => {
    if (!isPDFReady.value) {
      throw new Error('PDF generator not ready')
    }

    try {
      isGenerating.value = true

      const doc = pdfUtils.createDoc()

      ;(doc as any).autoTable({
        head: [['ID', 'Nombre', 'Email']],
        body: data.map(item => [item.id, item.name, item.email]),
        theme: 'grid',
        styles: {
          fontSize: 12,
          font: 'helvetica',
          textColor: [40, 40, 40],
          halign: 'center',
          valign: 'middle',
          fillColor: [255, 255, 255],
          lineColor: [44, 62, 80],
          lineWidth: 0.1
        },
        headStyles: {
          fillColor: [44, 62, 80],
          textColor: [255, 255, 255],
          fontStyle: 'bold'
        },
        alternateRowStyles: {
          fillColor: [245, 245, 245]
        },
        columnStyles: {
          0: { cellWidth: 20 },
          1: { cellWidth: 40 },
          2: { cellWidth: 60 }
        },
        margin: { top: 20 },
        didDrawPage(data: any) {
          doc.text(title, data.settings.margin.left, 10)
        }
      })

      doc.save(`${title.toLowerCase().replace(/\s+/g, '_')}.pdf`)
      return doc
    }
    finally {
      isGenerating.value = false
    }
  }

  // Función para crear datos de muestra (basada en tu objeto de ejemplo)
  const createSamplePaymentDetail = (): PaymentDetail => {
    return {
      id: '766408df-fff7-4899-81d3-ade2e91faa7f',
      paymentId: 42,
      paymentSource: {
        id: 'f21cbb41-8dd3-4efd-8717-251d304205a4',
        code: 'CRE',
        name: 'Tarjeta de Credito',
        status: 'ACTIVE'
      },
      reference: '45trfgghj',
      transactionDate: '2024-07-23',
      paymentStatus: {
        id: '21d4604e-148a-43aa-b7e8-5d1ce9f05180',
        code: 'CONF',
        name: 'Confirmed',
        status: 'ACTIVE'
      },
      client: {
        id: '98d2d373-7239-4072-906f-cbca3b04659c',
        code: 'ANG',
        name: 'Angelina Jolie',
        status: 'ACTIVE'
      },
      agency: {
        id: '780b60d1-ad30-45d5-aa9a-9094117ead22',
        code: 'PRUEB',
        name: 'PROBANDO S.A',
        status: 'ACTIVE',
        agencyTypeResponse: {
          id: 'c93a2f65-02a4-4d5f-b886-496bab587087',
          code: 'TRV',
          status: 'ACTIVE',
          name: 'Travel Agency'
        }
      },
      hotel: {
        id: 'ffc60cb4-9dc8-4028-ba42-a8e1d3ec0516',
        code: 'CAN',
        name: 'Cancun Centro',
        status: 'INACTIVE'
      },
      bankAccount: {
        id: '02a40604-fadd-48cb-a82f-99dd861e43ba',
        accountNumber: '68541236542036852365',
        status: 'ACTIVE'
      },
      attachmentStatus: {
        id: '30a100c6-0db6-4d7b-a27f-98d5ad617798',
        code: 'YAP',
        name: 'Booking',
        status: 'ACTIVE'
      },
      paymentAmount: 45639,
      paymentBalance: 45639,
      depositAmount: 652,
      depositBalance: 652,
      otherDeductions: 0,
      identified: 0,
      notIdentified: 45639,
      remark: 'fghjutghjrtfygui',
      hasAttachment: true,
      attachments: [
        {
          id: '36fe5330-5dbd-4e6b-8b70-ac3c30e68c5f',
          status: 'ACTIVE',
          resource: null,
          resourceType: {
            id: 'cedc0fba-b454-4528-86c3-ac372af79226',
            code: 'TYU',
            name: 'Resource Type 2',
            description: 'Resource Type 2',
            status: 'ACTIVE'
          },
          attachmentType: {
            id: 'e62325cd-a4ac-4b30-b117-4d9614e8b4ec',
            code: 'PAY',
            name: 'PAY-Payment Support',
            description: 'Solo permite uno de este tipo',
            status: 'ACTIVE',
            defaults: true
          },
          fileName: 'Manage B2BPartner.pdf',
          fileWeight: '583.433 KB',
          path: 'https://static.kynsoft.net/Manage_B2BPartner_2024-07-23_11-26-13.pdf',
          remark: 'xdcfvgbhnjkgfd'
        }
      ]
    }
  }

  return {
    // Funciones principales mejoradas
    generateStyledPaymentPDF,
    generateBasicStyledTable,
    createSamplePaymentDetail,

    // Estados
    isGenerating,
    isPDFReady,

    // Acceso a utils base
    jsPDF,
    pdfUtils
  }
}

// Función utilitaria para formatear moneda
function formatCurrency(amount: number | null | undefined): string {
  if (amount === null || amount === undefined) { return '$0.00' }
  return new Intl.NumberFormat('es-US', {
    style: 'currency',
    currency: 'USD'
  }).format(amount)
}

// Función para validar datos de pago (mejorada)
export function validatePaymentData(data: Partial<PaymentDetail>) {
  const errors: string[] = []

  if (!data.paymentAmount || data.paymentAmount <= 0) {
    errors.push('Monto de pago debe ser mayor a 0')
  }

  if (!data.transactionDate) {
    errors.push('Fecha de transacción es requerida')
  }

  if (!data.paymentSource?.name) {
    errors.push('Fuente de pago es requerida')
  }

  if (!data.client?.name) {
    errors.push('Cliente es requerido')
  }

  return {
    isValid: errors.length === 0,
    errors
  }
}

// Constantes actualizadas
export const PAYMENT_METHODS = {
  CASH: 'Efectivo',
  CARD: 'Tarjeta de Credito',
  TRANSFER: 'Transferencia',
  CHECK: 'Cheque'
} as const

export const PAYMENT_STATUS = {
  PENDING: 'Pendiente',
  CONFIRMED: 'Confirmed',
  FAILED: 'Fallido',
  CANCELLED: 'Cancelado'
} as const

// Exportar tipos para uso en otros archivos
export type { PaymentDetail }

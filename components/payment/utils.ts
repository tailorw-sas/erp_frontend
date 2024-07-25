import { jsPDF } from 'jspdf'
import 'jspdf-autotable'

export function generateStyledPDF() {
  const doc = new jsPDF()

  // Definición del objeto de ejemplo
  const paymentDetail = {
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

  // Organizar la información en formato de grid
  const formDetails = [
    { label: 'ID Pago', value: paymentDetail.paymentId },
    { label: 'Fuente de Pago', value: paymentDetail.paymentSource.name },
    { label: 'Referencia', value: paymentDetail.reference },
    { label: 'Fecha de Transacción', value: paymentDetail.transactionDate },
    { label: 'Estado de Pago', value: paymentDetail.paymentStatus.name },
    { label: 'Cliente', value: paymentDetail.client.name },
    { label: 'Agencia', value: paymentDetail.agency.name },
    { label: 'Tipo de Agencia', value: paymentDetail.agency.agencyTypeResponse.name },
    { label: 'Hotel', value: paymentDetail.hotel.name },
    { label: 'Cuenta Bancaria', value: paymentDetail.bankAccount.accountNumber },
    { label: 'Estado de Adjuntos', value: paymentDetail.attachmentStatus.name },
    { label: 'Monto de Pago', value: paymentDetail.paymentAmount },
    { label: 'Saldo de Pago', value: paymentDetail.paymentBalance },
    { label: 'Monto de Depósito', value: paymentDetail.depositAmount },
    { label: 'Saldo de Depósito', value: paymentDetail.depositBalance },
    { label: 'Otras Deducciones', value: paymentDetail.otherDeductions },
    { label: 'Identificado', value: paymentDetail.identified },
    { label: 'No Identificado', value: paymentDetail.notIdentified },
    { label: 'Comentario', value: paymentDetail.remark }
  ]

  // Agregar los detalles del formulario al PDF en formato de grid (3 columnas)
  let yPosition = 10
  const columnWidth = doc.internal.pageSize.getWidth() / 3 - 20

  formDetails.forEach((detail, index) => {
    const xPosition = 10 + (index % 3) * (columnWidth + 10)
    if (index % 3 === 0 && index !== 0) { yPosition += 10 }

    doc.setFontSize(12)
    doc.text(`${detail.label}: ${detail.value}`, xPosition, yPosition)
  })

  // Añadir tabla debajo de los detalles del formulario
  yPosition += 20 // Agregar espacio antes de la tabla

  doc.autoTable({
    startY: yPosition,
    head: [['ID', 'Nombre', 'Estado']],
    body: [
      [paymentDetail.attachments[0].id, paymentDetail.attachments[0].fileName, paymentDetail.attachments[0].status]
    ],
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
    tableWidth: 'auto',
    margin: { top: 20, left: 10, right: 10 }
  })

  doc.save('detalle_de_pago.pdf')
}

// export function generateStyledPDF() {
//   const doc = new jsPDF()

//   doc.autoTable({
//     head: [['ID', 'Name', 'Email']],
//     body: [
//       [1, 'John Doe', 'john@example.com'],
//       [2, 'Jane Doe', 'jane@example.com'],
//       [3, 'Fred Bloggs', 'fred@example.com']
//     ],
//     theme: 'grid',
//     styles: {
//       with: '100%',
//       fontSize: 12,
//       font: 'helvetica',
//       textColor: [40, 40, 40],
//       halign: 'center',
//       valign: 'middle',
//       fillColor: [255, 255, 255],
//       lineColor: [44, 62, 80],
//       lineWidth: 0.1
//     },
//     headStyles: {
//       fillColor: [44, 62, 80],
//       textColor: [255, 255, 255],
//       fontStyle: 'bold'
//     },
//     alternateRowStyles: {
//       fillColor: [245, 245, 245]
//     },
//     columnStyles: {
//       0: { cellWidth: 20 }, // ID column width
//       1: { cellWidth: 40 }, // Name column width
//       2: { cellWidth: 60 } // Email column width
//     },
//     margin: { top: 20 },
//     didDrawPage(data) {
//       doc.text('Styled Table Example', data.settings.margin.left, 10)
//     }
//   })

//   doc.save('styled_table.pdf')
// }

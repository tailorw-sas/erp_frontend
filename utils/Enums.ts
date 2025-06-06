export const ENUM_PERMISSIONS = [
  // { id: 'ALL', name: 'TODOS' },
  // { id: 'SHOW', name: 'MOSTRAR' },
  { id: 'LIST', name: 'LIST' },
  { id: 'CREATE', name: 'CREATE' },
  { id: 'EDIT', name: 'EDIT' },
  { id: 'DELETE', name: 'DELETE' },
  { id: 'DETAIL', name: 'DETAIL' },
  { id: 'CLONE', name: 'CLONE' },
  { id: 'ASSIGN', name: 'ASSIGN' },
  { id: 'PRINT', name: 'PRINT' },
  { id: 'IMPORT', name: 'IMPORT' },
  { id: 'EXPORT', name: 'EXPORT' },
]

export enum ENUM_SHORT_TYPE {
  DESC = 'DESC',
  ASC = 'ASC',
}

export const ENUM_INVOICE_TYPE = [
  { id: 'CREDIT', name: 'Credit', code: 'CRE' },
  { id: 'INCOME', name: 'Income', code: 'INC' },
  { id: 'INVOICE', name: 'Invoice', code: 'INV' },
  { id: 'OLD_CREDIT', name: 'Old Credit', code: 'OLD' }
]

export const REPORT_FORMATS_TYPE = [
  { id: 'PDF', name: 'PDF' },
  { id: 'XLS', name: 'XLS' },
  { id: 'CSV', name: 'CSV' },
]

export enum InvoiceType {
  INVOICE = 'INVOICE',
  INCOME = 'INCOME',
  CREDIT = 'CREDIT',
  OLD_CREDIT = 'OLD_CREDIT'
}

export const OBJ_ENUM_INVOICE = {
  INVOICE: 'Invoice',
  INCOME: 'Income',
  CREDIT: 'Credit',
  OLD_CREDIT: 'Old Credit',
}

export const OBJ_ENUM_INVOICE_STATUS = {
  PROCESSED: 'Processed',
  RECONCILED: 'Reconciled',
  SENT: 'Sent',
  PENDING: 'Pending',
  CANCELLED: 'Cancelled'
}
export const OBJ_ENUM_INVOICE_TYPE_CODE = {
  INVOICE: 'INV',
  INCOME: 'INC',
  CREDIT: 'CRE',
  OLD_CREDIT: 'OLD',
}

export const ENUM_INVOICE_CRITERIA = [
  { id: 'invoiceId', name: 'Invoice Id' },
  { id: 'invoiceNumberPrefix', name: 'Invoice No' },
  { id: 'bookings.bookingId', name: 'Booking Id' },
  { id: 'bookings.fullName', name: 'Full Name' },
  { id: 'bookings.hotelBookingNumber', name: 'Reservation No' },
  { id: 'bookings.couponNumber', name: 'Coupon No' },
]

export const ENUM_INVOICE_STATUS = [
  { id: 'CANCELED', name: 'Cancelled', code: 'CANC' },
  { id: 'PENDING', name: 'Pending', code: 'PEND' },
  { id: 'PROCESSED', name: 'Processed', code: 'PRO' },
  { id: 'RECONCILED', name: 'Reconciled', code: 'REC' },
  { id: 'SENT', name: 'Sent', code: 'SENT' },
]

export enum InvoiceStatus {
  PROCESSED = 'PROCESSED',
  RECONCILED = 'RECONCILED',
  SENT = 'SENT',
  CANCELED = 'CANCELLED',
  PENDING = 'PENDING'
}

export const OBJ_INVOICE_STATUS_NAME = {
  [InvoiceStatus.PROCESSED]: 'PROCESSED',
  [InvoiceStatus.RECONCILED]: 'RECONCILED',
  [InvoiceStatus.SENT]: 'SENT',
  [InvoiceStatus.CANCELED]: 'CANCELED',
  [InvoiceStatus.PENDING]: 'PENDING',
}

export const OBJ_INVOICE_TITLE: any = {
  INVOICE: 'New Invoice',
  INCOME: 'Income',
  CREDIT: 'New Credit to Invoice ',
  OLD_CREDIT: 'Old Credit'
}

export const OBJ_UPDATE_INVOICE_TITLE: any = {
  INVOICE: 'New Invoice',
  INCOME: 'Income',
  CREDIT: 'Credit',
  OLD_CREDIT: 'Old Credit'
}

export const ENUM_STATUS = [
  { id: 'ACTIVE', name: 'ACTIVO' },
  { id: 'INACTIVE', name: 'INACTIVO' },
]
export const ENUM_STATUS_FILTER = [
  { id: 'ACTIVE', name: 'DISPONIBLE' },
  { id: 'PRE_RESERVE', name: 'PRE RESERVADO' },
  { id: 'RESERVED', name: 'RESERVADO' },
  { id: 'ATTENDED', name: 'ATENDIDO' },
]

export const ENUM_REPORT_TYPE = [
  { id: 'TEMPLATES', name: 'PLANTILLA' },
  { id: 'REPORT', name: 'REPORTE' },
]

export const ENUM_MODULES_SYSTEM = [
  { id: 'TI', name: 'TI' },
  { id: 'INVOICE', name: 'INVOICE' },
  { id: 'PAYMENT', name: 'PAYMENT' },
  { id: 'VCC', name: 'VCC' },
]

export const ENUM_ROUTEOFADMINISTRATIONS = [
  { id: 'ORAL', name: 'ORAL' },
  { id: 'INTRAVENOUS', name: 'INTRAVENOUS' },
  { id: 'INTRAMUSCULAR', name: 'INTRAMUSCULAR' },
  { id: 'SUBCUTANEOUS', name: 'SUBCUTANEOUS' },
  { id: 'TOPICAL', name: 'TOPICAL' },
  { id: 'INHALATION', name: 'INHALATION' },
  { id: 'RECTAL', name: 'RECTAL' },
  { id: 'SUBLINGUAL', name: 'SUBLINGUAL' },
  { id: 'TRANSDERMAL', name: 'TRANSDERMAL' },
  { id: 'NASAL', name: 'NASAL' },
  { id: 'OPHTHALMIC', name: 'OPHTHALMIC' },
]

export const ENUM_USER_TYPE = [
  { id: 'INTERNAL', name: 'INTERNAL' },
  { id: 'PROVIDERS', name: 'PROVIDERS' },
  { id: 'DOCTORS', name: 'DOCTORS' },
  { id: 'NURSES', name: 'NURSES' },
  { id: 'ASSISTANTS', name: 'ASSISTANTS' },
  { id: 'SYSTEM', name: 'SYSTEM' },
  { id: 'UNDEFINED', name: 'UNDEFINED' },
]

export const ENUM_GUIDE = [
  { id: 'GUIDE', name: 'GUIAS' },
  { id: 'VIDEO', name: 'VIDEOS' },
  { id: 'PODCAST', name: 'PODCAST' },
]

export const ENUM_GENDER = [
  { id: 'MALE', name: 'MASCULINO' },
  { id: 'FEMALE', name: 'FEMENINO' },
  { id: 'OTHER', name: 'OTRO' },
]

export const ENUM_DISABILITY_TYPE = [
  { id: 'PHYSICAL', name: 'FÍSICA' },
  { id: 'INTELLECTUAL', name: 'INTELECTUAL' },
  { id: 'VISUAL', name: 'VISUAL' },
  { id: 'HEARING', name: 'AUDITIVA' },
  { id: 'PSYCHOSOCIAL', name: 'PSICOSOCIAL' },
  { id: 'NEURODIVERSITY', name: 'NEURODIVERSIDAD' },
  { id: 'SPEECH', name: 'HABLA' },
  { id: 'LEARNING', name: 'APRENDIZAJE' },
  { id: 'UNDEFINED', name: 'NA' },
]

export const ENUM_PROCEDURE_TYPE = [
  { id: 'IMAGING', name: 'IMAGEN' },
  { id: 'LAB_TESTS', name: 'PRUEBAS DE LABORATORIO' },
  { id: 'CARDIAC_NEURO', name: 'NEURO_CARDIACA' },
  { id: 'ENDOSCOPIC', name: 'ENDOSCÓPICA' },
  { id: 'ORGAN_SPECIFIC', name: 'ÓRGANO ESPECÍFICO' },
  { id: 'ALLERGY_SENSITIVITY', name: 'SENSIBILIDAD ALERGICA' },
  { id: 'OPHTHALMOLOGY', name: 'OFTALMOLOGÍA' },
]

export const ENUM_UM = [
  { id: 'MG', name: 'MILIGRAMOS' },
  { id: 'G', name: 'GRAMOS' },
  { id: 'UG', name: 'MICROGRAMOS' },
  { id: 'ML', name: 'MILILITROS' },
  { id: 'IU', name: 'UNIDADES INTERNACIONALES' },
  { id: 'PERCENTAGE', name: 'PORCENTAJE' },
  { id: 'L', name: 'LITROS' },
  { id: 'DROPS', name: 'GOTAS' },
  { id: 'PUFFS', name: 'PUFFS' },
  { id: 'TABLETS', name: 'TABLETAS' }

]

export const ENUM_DAY_OF_WEEK = [
  { id: 'MONDAY', name: 'LUNES' },
  { id: 'TUESDAY', name: 'MARTES' },
  { id: 'WEDNESDAY', name: 'MIÉRCOLES' },
  { id: 'THURSDAY', name: 'JUEVES' },
  { id: 'FRIDAY', name: 'VIERNES' },
  { id: 'SATURDAY', name: 'SÁBADO' },
  { id: 'SUNDAY', name: 'DOMINGO' },
]

export const ENUM_LANGUAGE = [
  { id: 'Español', name: 'Español' },
  { id: 'Ingles', name: 'Ingles' },
]

export const ENUM_DURATION_OF_CITE = [
  { id: '1', name: '15 MIN', value: 15, um: 'MIN' },
  { id: '2', name: '30 MIN', value: 30, um: 'MIN' },
  { id: '3', name: '45 MIN', value: 45, um: 'MIN' },
  { id: '4', name: '60 MIN', value: 60, um: 'MIN' },
  { id: '5', name: '1.5 H', value: 90, um: 'H' },
  { id: '6', name: '2 H', value: 120, um: 'H' },
  { id: '7', name: '2.5 H', value: 150, um: 'H' },
  { id: '8', name: '3 H', value: 180, um: 'H' },
]

export const ENUM_REPEAT = [
  { id: 'DAILY', name: 'DIAMENTE' },
  { id: 'WEEKLY', name: 'SEMANALMENTE' },
  { id: 'MONTHLY', name: 'MENSUALMENTE' },
]

export const ENUM_HOURS = [
  { id: '08:00am', name: '08:00am' },
  { id: '08:30am', name: '08:30am' },
  { id: '09:00am', name: '09:00am' },
  { id: '09:30am', name: '09:30am' },
  { id: '10:00am', name: '10:00am' },
  { id: '10:30am', name: '10:30am' },
  { id: '11:00am', name: '11:00am' },
  { id: '11:30am', name: '11:30am' },
  { id: '12:00pm', name: '12:00pm' },
  { id: '12:30pm', name: '12:30pm' },
  { id: '01:00pm', name: '01:00pm' },
  { id: '01:30pm', name: '01:30pm' },
  { id: '02:00pm', name: '02:00pm' },
  { id: '02:30pm', name: '02:30pm' },
  { id: '03:00pm', name: '03:00pm' },
  { id: '03:30pm', name: '03:30pm' },
  { id: '04:00pm', name: '04:00pm' },
  { id: '04:30pm', name: '04:30pm' },
  { id: '05:00pm', name: '05:00pm' },
  { id: '05:30pm', name: '05:30pm' },
  { id: '06:00pm', name: '06:00pm' },
  { id: '06:30pm', name: '06:30pm' },
  { id: '07:00pm', name: '07:00pm' },
  { id: '07:30pm', name: '07:30pm' },
  { id: '08:00pm', name: '08:00pm' },
  { id: '08:30pm', name: '08:30pm' },
  { id: '09:00pm', name: '09:00pm' },
  { id: '09:30pm', name: '09:30pm' },
  { id: '10:00pm', name: '10:00pm' },
  { id: '10:30pm', name: '10:30pm' },
  { id: '11:00pm', name: '11:00pm' },
  { id: '11:30pm', name: '11:30pm' },
  { id: '12:00am', name: '12:00am' },
  { id: '12:30am', name: '12:30am' },
  { id: '01:00am', name: '01:00am' },
  { id: '01:30am', name: '01:30am' },
  { id: '02:00am', name: '02:00am' },
  { id: '02:30am', name: '02:30am' },
  { id: '03:00am', name: '03:00am' },
  { id: '03:30am', name: '03:30am' },
  { id: '04:00am', name: '04:00am' },
  { id: '04:30am', name: '04:30am' },
  { id: '05:00am', name: '05:00am' },
  { id: '05:30am', name: '05:30am' },
  { id: '06:00am', name: '06:00am' },
  { id: '06:30am', name: '06:30am' },
  { id: '07:00am', name: '07:00am' },
  { id: '07:30am', name: '07:30am' },
  { id: '08:00am', name: '08:00am' },
]

export const ENUM_INVOICE_STATUS_NAVIGATE = [
  { id: 'Cancelled', name: 'Cancelled' },
  { id: 'Pending', name: 'Pending' },
  { id: 'Processed', name: 'Processed' },
  { id: 'Reconciled', name: 'Reconciled' },
  { id: 'Sent', name: 'Sent' },
  { id: 'PRE_PAY', name: 'Pre Pay' },
]

export const ENUM_TRANSACTION_STATUS_NAVIGATE = [
  { id: 'Created', name: 'Created' },
  { id: 'Cancelled', name: 'Cancelled' },
  { id: 'Declined', name: 'Declined' },
  { id: 'Paid', name: 'Paid' },
  { id: 'Received', name: 'Received' },
  { id: 'Reconcilied', name: 'Reconcilied' },
  { id: 'Refund', name: 'Refund' },
  { id: 'Sent', name: 'Sent' },
]

export const ENUM_RECONCILE_TRANSACTION_STATUS_NAVIGATE = [
  { id: 'Created', name: 'Created' },
  { id: 'Completed', name: 'Completed' },
]

export const ENUM_PAYMENT_TRANSACTION_STATUS_NAVIGATE = [
  { id: 'InProgress', name: 'In Progress' },
  { id: 'Cancelled', name: 'Cancelled' },
  { id: 'Completed', name: 'Completed' },
  { id: 'Applied', name: 'Applied' },
]

export enum E_STATUS {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE'
}

export const ENUM_GENERATION_TYPE = [
  { id: 'ByCoupon', name: 'By Coupon' },
  { id: 'ByBooking', name: 'By Booking' },
  // { id: 'ByCheckIn', name: 'By CheckIn' },
  // { id: 'ByCheckInCheckOut', name: 'By Check in-Check out' },
]

export const ENUM_FILE_FORMAT = [
  { id: 'CSV_CSV', name: 'CSV_CSV' },
  { id: 'MHT_MHTML', name: 'MHT_MHTML' },
  { id: 'PDF_PDF', name: 'PDF_PDF' },
  { id: 'RTF_RICH_TEXT', name: 'RTF_RICH_TEXT' },
  { id: 'GIF_IMAGE_GIF', name: 'GIF_IMAGE_GIF' },
  { id: 'TXT_TXT', name: 'TXT_TXT' },
  { id: 'XL2_EXCEL_DATA_ONLY', name: 'XL2_EXCEL_DATA_ONLY' },
  { id: 'XL3_EXCEL_WORKBOOK', name: 'XL3_EXCEL_WORKBOOK' },
  { id: 'XLS_EXCEL', name: 'XLS_EXCEL' },
  { id: 'XML_XML', name: 'XML_XML' },

]

export const ENUM_CALCULATION_TYPE = [
  { id: 'FIX', name: 'FIX' },
  { id: 'PER', name: 'PER' },
]

export enum ENUM_INVOICE_IMPORT_TYPE {
  VIRTUAL = 'VIRTUAL',
  NO_VIRTUAL = 'NO_VIRTUAL',
}

export enum ENUM_PAYMENT_IMPORT_TYPE {
  BANK = 'BANK',
  EXPENSE = 'EXPENSE',
  ANTI = 'ANTI',
  DETAIL = 'DETAIL',
  BOOKING = 'EXPENSE_TO_BOOKING',
}

export enum ENUM_INVOICE_SEND_TYPE {
  EMAIL = 'EML',
  FTP = 'FTP',
  BAVEL = 'BVL',
}

export enum CALENDAR_MODE {
  MONTH = 'month',
  YEAR = 'year',
  DATE = 'date',
  UNDEFINED = 'undefined',
}

export const FORM_FIELD_TYPE = [
  { id: 'local-select', name: 'Local Select' },
  { id: 'select', name: 'Select' },
  { id: 'multiselect', name: 'Select Multiple' },
  { id: 'text', name: 'Text', show: false },
  { id: 'number', name: 'Number' },
  { id: 'email', name: 'Email' },
  { id: 'date', name: 'Date' },
  // { id: 'time', name: 'Time' },
  // { id: 'checkbox', name: 'Checkbox' },
  // { id: 'radio', name: 'Radio' },
  { id: 'textarea', name: 'Textarea' },
  // { id: 'file', name: 'File' },
]

export const ENUM_MAIL_TEMPLATE_TYPE = [
  { id: 'PAYMENT_CONFIRMATION_VOUCHER', name: 'Payment Confirmation Voucher' },
  { id: 'PAYMENT_LINK', name: 'Payment Link' },
  { id: 'WELCOME', name: 'Welcome' },
  { id: 'SEND_INVOICE', name: 'Send Invoice' },
  { id: 'VERIFY_OTP', name: 'Verify OTP' },
]

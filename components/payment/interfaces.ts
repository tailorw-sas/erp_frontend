export interface TransactionType {
  id: string
  code: string
  name: string
  status: string
  cash: boolean
  deposit: boolean
  applyDeposit: boolean
}

export interface TransactionItem {
  id: string
  status: boolean
  paymentId: string
  transactionType: TransactionType
  amount: string
  remark: string
  children: TransactionItem[] // Array de objetos TransactionItem para hijos
  bookingId: string | null
  invoiceId: string | null
  transactionDate: string
  firstName: string | null
  lastName: string | null
  reservation: string | null
  couponNo: string | null
  adults: string | null
  childrens: string | null
  createdAt: string
}

import { defineEventHandler, readBody } from 'h3'
import { getToken } from '#auth'

export default defineEventHandler(async (event) => {
  const body = await readBody(event)

  const token = await getToken({ event })

  const {
    merchantId,
    orderNumber,
    amount,
    transactionId,
  } = body

  const payload: { [key: string]: any } = {}
  payload.merchantId = merchantId
  payload.orderNumber = orderNumber
  payload.amount = amount
  payload.transactionId = transactionId

  const defaultHeaders: HeadersInit = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token?.access_token}`,
  }

  const response = await $fetch(`${process.env.VITE_APP_BASE_URL}/creditcard/api/transactions/redirectTypePost`, {
    method: 'POST',
    body: payload,
    headers: defaultHeaders
  })

  return response
})

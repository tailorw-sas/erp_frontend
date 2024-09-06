import crypto from 'node:crypto'
import { defineEventHandler, readBody } from 'h3'

export default defineEventHandler(async (event) => {
  const body = await readBody(event)

  let {
    merchantId,
    merchantName,
    merchantType,
    currencyCode,
    orderNumber,
    amount,
    itbis,
    approvedUrl,
    declinedUrl,
    cancelUrl,
  } = body

  const privateKey = process.env.BLUE_MERCHANT_SECRET_KEY || ''
  approvedUrl = `${process.env.AUTH_ORIGIN}/${approvedUrl}`
  declinedUrl = `${process.env.AUTH_ORIGIN}/${declinedUrl}`
  cancelUrl = `${process.env.AUTH_ORIGIN}/${cancelUrl}`

  const data = [
    merchantId,
    merchantName,
    merchantType,
    currencyCode,
    orderNumber,
    amount,
    itbis,
    approvedUrl,
    declinedUrl,
    cancelUrl,
    '0', // UseCustomField1
    '',
    '0', // UseCustomField2
    '',
    privateKey,
  ].join('')

  const authHash = crypto
    .createHmac('sha512', privateKey)
    .update(data)
    .digest('hex')

  // Genera el HTML del formulario
  const formHtml = `
    <html lang="en">
      <head></head>
      <body>
        <form action="https://pruebas.azul.com.do/paymentpage/Default.aspx" method="post" id="paymentForm">
          <input type="hidden" name="MerchantId" value="${merchantId}">
          <input type="hidden" name="MerchantName" value="${merchantName}">
          <input type="hidden" name="MerchantType" value="${merchantType}">
          <input type="hidden" name="CurrencyCode" value="${currencyCode}">
          <input type="hidden" name="OrderNumber" value="${orderNumber}">
          <input type="hidden" name="Amount" value="${amount}">
          <input type="hidden" name="ITBIS" value="${itbis}">
          <input type="hidden" name="ApprovedUrl" value="${approvedUrl}">
          <input type="hidden" name="DeclinedUrl" value="${declinedUrl}">
          <input type="hidden" name="CancelUrl" value="${cancelUrl}">
          <input type="hidden" name="UseCustomField1" value="0">
          <input type="hidden" name="CustomField1Label" value="">
          <input type="hidden" name="CustomField1Value" value="">
          <input type="hidden" name="UseCustomField2" value="0">
          <input type="hidden" name="CustomField2Label" value="">
          <input type="hidden" name="CustomField2Value" value="">
          <input type="hidden" name="AuthHash" value="${authHash}">
        </form>
        <script>
          // Enviar el formulario automáticamente
          document.getElementById('paymentForm').submit();
        </script>
      </body>
    </html>
  `

  // Enviar el HTML al cliente
  event.res.setHeader('Content-Type', 'text/html')
  event.res.end(formHtml)
})

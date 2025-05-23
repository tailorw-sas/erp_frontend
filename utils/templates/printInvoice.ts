export interface InvoiceParams {
  companyName: string
  companyAddress: string
  companyCif: string
  hotelName: string
  hotelCode: string
  invoiceNumber: string
  invoiceId: string
  agencyCode: string
  agencyName: string
  agencyAddress: string
  agencyCif: string
  agencyCityState: string
  agencyCountry: string
  invoiceDate: string
  bookings: BookingPrint[]
}

export interface BookingPrint {
  fullName: string
  description: string
  voucher: string
  adults: string
  children: string
  from: string
  to: string
  quantity: string
  plan: string
  price: string
  total: string
}

export function getPrintInvoiceTemplate(params: InvoiceParams): string {
  const {
    companyName,
    companyAddress,
    companyCif,
    hotelName,
    invoiceNumber,
    agencyCode,
    agencyName,
    agencyAddress,
    agencyCif,
    agencyCityState,
    agencyCountry,
    invoiceDate,
    bookings
  } = params

  return ` <div id="print-invoice-pdf" style=" width: 1150px;"> 
  <div style="display: flex; align-items: center; justify-content: space-between; margin:auto">

    <div
      style="display: flex; flex-direction: column;width: 400px;height: fit-content; align-items:start; padding-left: 10px; border: 1px black solid; border-radius: 12px; padding-top: 5px; padding-bottom: 5px; ">
      <h3 style="margin-top: 5px;">${companyName}</h3>
      <span style="font-size: 14px; ">${companyAddress}</span><br />
      
      <span style="font-size: 14px">CIF: ${companyCif}</span>
      <h3 style="margin-bottom: 5px;">${hotelName}</h3>
    </div>

    <div
      style="display: flex; flex-direction: column; align-items: center; border: 1px black solid; border-radius: 12px; padding-top: 5px; padding-bottom: 5px; width: 150px;">

      <span
        style="font-weight: 700; margin-bottom: 4px; margin-top: 4px; margin-right: 30px; margin-left: 30px;">INVOICE</span>
      <span style="width: 100%; background-color: black; height: 1px;"></span>
      <span
        style="font-weight: 700; margin-bottom: 4px; margin-top: 4px; margin-right: 25px; margin-left: 25px;">${invoiceNumber}</span
        </div>
    </div>

  </div>
  <div style="width: 100%; height: 3px; background-color: black; margin-top: 5px; margin-bottom: 5px;"></div>

  <div style="display: flex; align-items: start; justify-content: space-between; margin-top: 8px;">

    <div
      style="display: flex; flex-direction: column;width: 400px;height: fit-content; align-items:start; padding-left: 10px; border: 1px black solid; border-radius: 12px; padding-top: 5px; padding-bottom: 5px; ">
      <div style="font-weight: bold; margin-bottom: 20px;">
        <p style="margin-top: 5px;">ACCOUNT: ${agencyCode}</p>
        <p>${agencyName}</p>
        <p>${agencyAddress}</p>
        <span style="font-size: 14px;font-weight: 100;">CIF: ${agencyCif}</span>
      </div>
      <div style="display: flex; flex-direction: row;justify-content: space-between; width: 98%; margin-bottom: 5px;">
        <span >${agencyCityState}</span>
        <span >${agencyCountry}</span>
      </div>
    </div>

    <div
      style="display: flex; flex-direction: column; align-items: center; border: 1px black solid; border-radius: 12px; padding-top: 5px; padding-bottom: 5px; width: 150px;">

      <span
        style="font-weight: 700; margin-bottom: 4px; margin-top: 4px; margin-right: 30px; margin-left: 30px;">DATE</span>
      <span style="width: 100%; background-color: black; height: 1px;"></span>
      <span
        style="font-weight: 700; margin-bottom: 4px; margin-top: 4px; margin-right: 30px; margin-left: 30px;">${invoiceDate}</span>

    </div>

  </div>
  <table style="margin-top: 10px;">
    <thead>
      <tr style="border: 1px solid black;">
        <td style=" font-weight: 700;width: 350px">Guest Name</td>
        <td style=" font-weight: 700;width: 300px;">Description</td>
        <td style=" font-weight: 700;width: 200px;">Voucher</td>
        <td style=" font-weight: 700;width: 50px;">Ad.</td>
        <td style=" font-weight: 700;width: 50px;">Ch.</td>
        <td style=" font-weight: 700;width: 90px;">From</td>
        <td style=" font-weight: 700;width: 90px;">To</td>
        <td style=" font-weight: 700;width: 50px;">Qty</td>
        <td style=" font-weight: 700;width: 100px;">Plan</td>
        <td style=" font-weight: 700;width: 90px;">Price</td>
        <td style=" font-weight: 700;">Total</td>
      </tr>
    </thead>

    <tbody>
      <tr style="width: 100%; height: 3px; background-color: black; margin-top: 5px; margin-bottom: 5px;">
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
      ${
        bookings?.map(booking => `
        <tr>
          <td>${booking?.fullName}</td>
          <td>${booking?.description}</td>
          <td>${booking?.voucher}</td>
          <td>${booking?.adults}</td>
          <td>${booking?.children}</td>
          <td>${booking?.from}</td>
          <td>${booking?.to}</td>
          <td>${booking?.quantity}</td>
          <td>${booking?.plan}</td>
          <td>${booking?.price}</td>
          <td>${booking?.total}</td>
        </tr>
        `)
      }
      
    </tbody>
  </table>
</div>
  
  
  `
}

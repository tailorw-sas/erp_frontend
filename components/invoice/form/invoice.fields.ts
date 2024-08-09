import dayjs from 'dayjs'
import { z } from 'zod'
import type { Container } from '~/components/form/EditFormV2WithContainer'
import { InvoiceType } from '~/utils/Enums'

const route = useRoute()

export const fields: Array<Container> = [
  {
    childs: [
      {
        field: 'invoiceId',
        header: 'Id',
        dataType: 'text',
        class: `w-full px-3  ${String(route.query.type) as any === InvoiceType.OLD_CREDIT ? 'required' : ''}`,
        disabled: true,
        containerFieldClass: 'ml-10'

      },
      {
        field: 'invoiceNumber',
        header: 'Invoice Number',
        dataType: 'text',
        class: 'w-full px-3 ',
        disabled: true,

      },

    ],
    containerClass: 'flex flex-column justify-content-evenly w-full'
  },

  {
    childs: [

      {
        field: 'hotel',
        header: 'Hotel',
        dataType: 'select',
        class: 'w-full px-3 required',
        disabled: String(route.query.type) as any === InvoiceType.CREDIT

      },
      {
        field: 'agency',
        header: 'Agency',
        dataType: 'select',
        class: 'w-full px-3 required',
        disabled: String(route.query.type) as any === InvoiceType.CREDIT
      },

    ],

    containerClass: 'flex flex-column justify-content-evenly w-full '
  },
  {
    childs: [
      {
        field: 'invoiceDate',
        header: 'Invoice Date',
        dataType: 'date',
        class: 'w-full px-3  required',
        validation: z.date({ required_error: 'The Invoice Date field is required' }).max(dayjs().endOf('day').toDate(), 'The Invoice Date field cannot be greater than current date')
      },
      {
        field: 'invoiceAmount',
        header: 'Invoice Amount',
        dataType: 'text',
        class: 'w-full px-3  required',
        disabled: true,
        ...(route.query.type === InvoiceType.OLD_CREDIT && { valdation: z.string().refine(val => +val < 0, 'Invoice amount must have negative values') })
      },

    ],
    containerClass: 'flex flex-column justify-content-evenly w-full'
  },
  {
    childs: [

      {
        field: 'invoiceType',
        header: 'Type',
        dataType: 'select',
        class: 'w-full px-3 mb-5',
        containerFieldClass: '',
        disabled: true
      },
      {
        field: 'isManual',
        header: 'Manual',
        dataType: 'check',
        class: `w-full px-3  ${String(route.query.type) as any === InvoiceType.OLD_CREDIT ? 'required' : ''}`,
        disabled: true
      },

    ],
    containerClass: 'flex flex-column justify-content-evenly w-full'
  },

]

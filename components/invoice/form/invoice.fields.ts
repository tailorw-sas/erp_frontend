import dayjs from 'dayjs'
import { z } from 'zod'
import type { Container } from '~/components/form/EditFormV2WithContainer'

const route = useRoute()

export const fields: Array<Container> = [
  {
    childs: [
      {
        field: 'invoice_id',
        header: 'Id',
        dataType: 'text',
        class: `w-full ${String(route.query.type) as any === ENUM_INVOICE_TYPE[3] ? 'required' : ''}`,
        disabled: true,
        containerFieldClass: 'ml-10'

      },
      {
        field: 'invoiceNumber',
        header: 'Invoice Number',
        dataType: 'text',
        class: 'w-full',
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
        class: 'w-full px-4 required',

      },
      {
        field: 'agency',
        header: 'Agency',
        dataType: 'select',
        class: 'w-full px-4 required',

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
        class: 'w-full required',
        validation: z.date({ required_error: 'The Invoice Date field is required' }).max(dayjs().endOf('day').toDate(), 'The Invoice Date field cannot be greater than current date')
      },
      {
        field: 'isManual',
        header: 'Manual',
        dataType: 'check',
        class: `w-full ${String(route.query.type) as any === ENUM_INVOICE_TYPE[3] ? 'required' : ''}`,
        disabled: true
      },

    ],
    containerClass: 'flex flex-column justify-content-evenly w-full'
  },
  {
    childs: [
      {
        field: 'invoiceAmount',
        header: 'Invoice Amount',
        dataType: 'text',
        class: 'w-full px-4 required',
        disabled: true
      },
      {
        field: 'invoiceType',
        header: 'Type',
        dataType: 'select',
        class: 'w-full px-4 ',
        containerFieldClass: '',
        disabled: true
      },

    ],
    containerClass: 'flex flex-column justify-content-evenly w-full'
  },

]

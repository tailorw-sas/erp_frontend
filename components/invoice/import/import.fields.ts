import type { Container } from '~/components/form/EditFormV2WithContainer'

export const fields: Array<Container> = [
  {
    childs: [
      {
        field: 'invoiceFile',
        header: 'Import Data',
        dataType: 'file',
        class: 'w-full px-4',
        disabled: true,
        containerFieldClass: 'ml-10'

      },
    ],
    containerClass: 'flex flex-column justify-content-evenly w-full'
  },
]

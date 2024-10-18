import { ref } from 'vue'

interface MenuItemCommand {
  label: string
  icon?: string
  command?: () => void
  disabled?: boolean
}

interface MenuItemSubGroup {
  label: string
  icon?: string
  command?: () => void
  disabled?: boolean
  items?: MenuItemCommand[]
}

interface MenuItemGroup {
  label: string
  icon?: string
  items: MenuItemSubGroup[]
}

interface MenuItem {
  label: string
  icon?: string
  items: MenuItemGroup[][]
}

export const model = ref([
  {
    label: 'Administration',
    icon: 'pi pi-home',
    items: [
      {
        label: 'Module',
        icon: 'pi pi-fw pi-folder',
        to: '/modules'
      },
      {
        label: 'Permission',
        icon: 'pi pi-fw pi-folder',
        to: '/permissions'
      },
      // {
      //   label: 'Company',
      //   icon: 'pi pi-fw pi-folder',
      //   to: '/business'
      // },
      // {
      //   label: 'User',
      //   icon: 'pi pi-fw pi-folder',
      //   to: '/user'
      // },
      {
        label: 'Email',
        icon: 'pi pi-fw pi-envelope',
        items: [
          {
            label: 'Configuration',
            icon: 'pi pi-fw pi-folder',
            to: '/mailjet-config'
          },
          {
            label: 'Template',
            icon: 'pi pi-fw pi-folder',
            to: '/mailjet-template'
          },
        ]
      },
      {
        label: 'Report Configuration',
        icon: 'pi pi-fw pi-folder',
        to: '/report-config'
      },

    ]
  },
  {
    label: 'Settings',
    icon: 'pi pi-home',
    items: [

      {
        label: 'General',
        icon: 'pi pi-fw pi-folder',
        items: [
          {
            label: 'Action Log',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/action-log'
          },
          {
            label: 'Alert',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/alert'
          },
          {
            label: 'Charge Type',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/charge-type'
          },
          {
            label: 'Language',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/language'
          },
          {
            label: 'Message',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/message'
          },
          {
            label: 'Report Param Type',
            icon: 'pi pi-fw pi-file-o',
            to: '/settings/report-param-type'
          },
          // {
          //   label: 'Report',
          //   icon: 'pi pi-fw pi-file-o',
          //   to: '/settings/report'
          // },
        ]
      },
      // {
      //   label: 'Accounting',
      //   icon: 'pi pi-fw pi-folder',
      //   items: [
      //     {
      //       label: 'Account',
      //       icon: 'pi pi-fw pi-folder',
      //       to: ''
      //     },
      //   ]
      // },
      // {
      //   label: 'Policy',
      //   icon: 'pi pi-fw pi-folder',
      //   items: [
      //     {
      //       label: 'Configuration',
      //       icon: 'pi pi-fw pi-folder',
      //       to: ''
      //     },
      //     {
      //       label: 'Status',
      //       icon: 'pi pi-fw pi-folder',
      //       to: ''
      //     },
      //     {
      //       label: 'Type',
      //       icon: 'pi pi-fw pi-folder',
      //       to: ''
      //     },
      //   ]
      // },

      {
        label: 'Services',
        icon: 'pi pi-fw pi-folder',
        items: [
          {
            label: 'Agency',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/agency'
          },
          {
            label: 'Agency Type',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/agency-type'
          },
          {
            label: 'Agency Contact',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/agency-contact'
          },
          {
            label: 'Client',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/client'
          },
          {
            label: 'Hotel',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/hotel'
          },
          // {
          //   label: 'Contact',
          //   icon: 'pi pi-fw pi-folder',
          //   to: '/settings/contact'
          // },
          {
            label: 'Trading Companies',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/trading-companies'
          },
          {
            label: 'Report',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/report'
          },
        ]
      },
      // {
      //   label: 'Audit',
      //   icon: 'pi pi-fw pi-folder',
      //   items: [
      //     {
      //       label: 'Master Audit',
      //       icon: 'pi pi-fw pi-folder',
      //       to: ''
      //     },
      //   ]
      // },
      {
        label: 'B2BPartner',
        icon: 'pi pi-fw pi-folder',
        items: [
          {
            label: 'B2BPartner',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/b2bpartner'
          },
          {
            label: 'B2BPartner Type',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/b2bpartner-type'
          },
        ]
      },
      // {
      //   label: 'Channel',
      //   icon: 'pi pi-fw pi-folder',
      //   to: ''
      // },
      {
        label: 'Bank',
        icon: 'pi pi-fw pi-folder',
        items: [
          {
            label: 'Bank Account',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/bank-account'
          },
          {
            label: 'Account Type',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/account-type'
          },
          {
            label: 'Bank',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/bank'
          },
          {
            label: 'Currency',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/currency'
          },
        ]
      },

      // {
      //   label: 'Collection',
      //   icon: 'pi pi-fw pi-folder',
      //   items: [
      //     {
      //       label: 'Item Type',
      //       icon: 'pi pi-fw pi-folder',
      //       to: ''
      //     },
      //   ]
      // },
      // {
      //   label: 'Contract',
      //   icon: 'pi pi-fw pi-folder',
      //   to: ''
      // },
      // {
      //   label: 'Cupon Type',
      //   icon: 'pi pi-fw pi-folder',
      //   to: ''
      // },
      {
        label: 'Employee',
        icon: 'pi pi-fw pi-folder',
        items: [
          {
            label: 'Employee',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/employee'
          },
          {
            label: 'Department group',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/department-group'
          },
          // {
          //   label: 'Employee Group',
          //   icon: 'pi pi-fw pi-folder',
          //   to: '/settings/employee-group'
          // },
          // {
          //   label: 'Permission module',
          //   icon: 'pi pi-fw pi-folder',
          //   to: ''
          // },
        ]
      },
      {
        label: 'Geography',
        icon: 'pi pi-fw pi-folder',
        items: [
          {
            label: 'Time Zone',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/time-zone'
          },
          {
            label: 'Region',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/region'
          },
          {
            label: 'Country',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/country'
          },
          {
            label: 'City State',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/city-state'
          },
        ]
      },
      {
        label: 'Hotel',
        icon: 'pi pi-fw pi-folder',
        items: [
          {
            label: 'Room Category',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/room-category'
          },
          {
            label: 'Room Type',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/room-type'
          },
        ]
      },
      {
        label: 'Invoice',
        icon: 'pi pi-fw pi-folder',
        items: [
          {
            label: 'Invoice Management',
            icon: 'pi pi-fw pi-folder',
            to: '/invoice'
          },
          {
            label: 'Invoice Status',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/invoice-status'
          },
          {
            label: 'Invoice Types',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/invoice-type'
          },
          {
            label: 'Rate Plan',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/rate-plan'
          },
          {
            label: 'Invoice Transaction Type',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/invoice-transaction-type'
          },
          {
            label: 'Night Type',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/night-type'
          },
          {
            label: 'Invoice Attachment Type',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/attachment-type'
          },
          //     {
          //       label: 'Room Type',
          //       icon: 'pi pi-fw pi-folder',
          //       to: ''
          //     },
        ]
      },
      // {
      //   label: 'Note',
      //   icon: 'pi pi-fw pi-folder',
      //   items: [
      //     {
      //       label: 'Deduction Type',
      //       icon: 'pi pi-fw pi-folder',
      //       to: ''
      //     },
      //     {
      //       label: 'Note Subject',
      //       icon: 'pi pi-fw pi-folder',
      //       to: ''
      //     },
      //     {
      //       label: 'Note Type',
      //       icon: 'pi pi-fw pi-folder',
      //       to: ''
      //     },
      //   ]
      // },
      {
        label: 'Payment',
        icon: 'pi pi-fw pi-folder',
        items: [
          {
            label: 'Payment Attachment Status',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/payment-attachment-status'
          },
          {
            label: 'Payment Status',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/payment-status',

          },
          {
            label: 'Payment Transaction Type',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/payment-transaction-type'
          },
          {
            label: 'Payment Source',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/payment-source'
          },
          // {
          //   label: 'Payment Source',
          //   icon: 'pi pi-fw pi-folder',
          //   to: '/settings/payment-source'
          // },
          {
            label: 'Payment Transaction Status',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/payment-transaction-status'
          },
        ]
      },
      // {
      //   label: 'Transaction Category',
      //   icon: 'pi pi-fw pi-folder',
      //   to: ''
      // },
      {
        label: 'Virtual Credit Card',
        icon: 'pi pi-fw pi-folder',
        items: [
          {
            label: 'Collection Status',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/collection-status'
          },
          {
            label: 'Credit Card Type',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/credit-card-type'
          },
          {
            label: 'VCC Transaction Type',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/vcc-transaction-type'
          },
          {
            label: 'Merchant',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/merchant'
          },
          {
            label: 'Merchant Bank Account',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/merchant-bank-account'
          },
          {
            label: 'Merchant Commission',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/merchant-commission'
          },
          {
            label: 'Merchant Currency',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/merchant-currency'
          },
          {
            label: 'Merchant Hotel Enrolle',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/merchant-hotel-enrolle'
          },
          {
            label: 'Merchant Config',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/merchant-config'
          },
          {
            label: 'Merchant Language',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/merchant-language'
          },
          // {
          //   label: 'Reconcile Status',
          //   icon: 'pi pi-fw pi-folder',
          //   to: ''
          // },
          {
            label: 'Reconcile Transaction Status',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/reconcile-transaction-status'
          },
          {
            label: 'Transaction Status',
            icon: 'pi pi-fw pi-folder',
            to: '/settings/transaction-status'
          },
        ]
      },
    ]
  },
])

export const menuItemsMegaMenu = ref<MenuItem[]>([
  {
    label: 'Application',
    icon: 'pi pi-folder',
    items: [
      [
        {
          label: 'Administration',
          icon: 'pi pi-fw pi-folder',
          items: [
            // {
            //   label: 'Module',
            //   icon: 'pi pi-fw pi-file',
            //   command: () => navigateTo('/modules'),
            //   disabled: false,
            // },
            // {
            //   label: 'Permission',
            //   icon: 'pi pi-fw pi-file',
            //   command: () => navigateTo('/permissions'),
            //   items: []
            // },
            // {
            //   label: 'User',
            //   icon: 'pi pi-fw pi-file',
            //   command: () => navigateTo('/user'),
            //   items: []
            // },
            {
              label: 'Configuration',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/mailjet-config'),
              items: []
            },
            {
              label: 'Template',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/mailjet-template'),
              items: []
            },
            {
              label: 'DB Connection',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/db-connection'),
              items: []
            },
            {
              label: 'Report Configuration',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/report-config'),
              items: []
            }
          ]
        },
      ],
      [
        {
          label: 'Payments',
          icon: 'pi pi-folder',
          items: [
            {
              label: 'Payment Management',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/payment'),
              items: []
            },
            {
              label: 'Payment Close Operation',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/close-operation/payment'),
              items: []
            },
            {
              label: 'Payment Status',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/payment-status'),
              items: []
            },
            // {
            //   label: 'Payment Transaction Status',
            //   icon: 'pi pi-fw pi-file',
            //   command: () => navigateTo('/settings/payment-transaction-status'),
            //   items: []
            // },
            // {
            //   label: 'Payment Transaction Type',
            //   icon: 'pi pi-fw pi-file',
            //   command: () => navigateTo('/settings/payment-transaction-type'),
            //   items: []
            // },
            // {
            //   label: 'Payment Source',
            //   icon: 'pi pi-fw pi-file',
            //   command: () => navigateTo('/settings/payment-source'),
            //   items: []
            // },
          ],
        },
      ],
      [
        {
          label: 'Invoice',
          items: [
            {
              label: 'Invoice Management',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/invoice'),
              items: []
            },
            {
              label: 'Invoice Close Operation',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/close-operation/invoice'),
              items: []
            },
            {
              label: 'Invoice Parameterization',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/parameterization/invoice'),
              items: []
            },
          ]
        }
      ],
      [
        {
          label: 'VCC',
          items: [
            {
              label: 'VCC Management',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/vcc-management'),
              items: []
            },
            {
              label: 'VCC Close Operation',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/close-operation/vcc'),
              items: []
            },
          ]
        }
      ]
    ]
  },
  // Nomenclators
  {
    label: 'Settings',
    icon: 'pi pi-folder',
    items: [
      [
        {
          label: 'General',
          items: [
            {
              label: 'Action Log',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/action-log'),
              items: []
            },
            {
              label: 'Alert',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/alert'),
              items: []
            },
            {
              label: 'Charge Type',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/charge-type'),
              items: []
            },
            {
              label: 'Language',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/language'),
              items: []
            },
            {
              label: 'Message',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/message'),
              items: []
            },
            {
              label: 'Report Param Type',
              icon: 'pi pi-fw pi-file-o',
              command: () => navigateTo('/settings/report-param-type'),
              items: []
            },
          ]
        },
        {
          label: 'Invoice',
          items: [
            {
              label: 'Invoice Status',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/invoice-status'),
              items: []
            },
            {
              label: 'Invoice Type',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/invoice-type'),
              items: []
            },
            {
              label: 'Rate Plan',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/rate-plan'),
              items: []
            },
            {
              label: 'Invoice Transaction Type',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/invoice-transaction-type'),
              items: []
            },
            {
              label: 'Night Type',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/night-type'),
              items: []
            },
            {
              label: 'Invoice Attachment Type',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/attachment-type'),
              items: []
            }
          ]
        }
      ],
      [
        {
          label: 'Services',
          items: [
            {
              label: 'Agency',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/agency'),
              items: []
            },
            {
              label: 'Agency Type',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/agency-type'),
              items: []
            },
            {
              label: 'Client',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/client'),
              items: []
            },
            {
              label: 'Hotel',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/hotel'),
              items: []
            },
            {
              label: 'Trading Companies',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/trading-companies'),
              items: []
            },
            {
              label: 'Report',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/report'),
              items: []
            }
          ]
        },
        {
          label: 'Payment',
          items: [
            {
              label: 'Payment Attachment Status',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/payment-attachment-status'),
              items: []
            },
            {
              label: 'Payment Status',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/payment-status'),
              items: []
            },
            {
              label: 'Payment Transaction Type',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/payment-transaction-type'),
              items: []
            },
            {
              label: 'Payment Source',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/payment-source'),
              items: []
            },
            // Ocultar ambos en produccion
            {
              label: 'Payment Resource Type',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/payment/resource-type'),
              items: []
            },
            {
              label: 'Payment Attachment Type',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/payment/attachment-type'),
              items: []
            },
            {
              label: 'Payment Transaction Status',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/payment-transaction-status'),
              items: []
            }
          ]
        }
      ],
      [
        {
          label: 'B2BPartner',
          items: [
            {
              label: 'B2BPartner',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/b2bpartner'),
              items: []
            },
            {
              label: 'B2BPartner Type',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/b2bpartner-type'),
              items: []
            }
          ]
        },
        {
          label: 'Virtual Credit Card',
          items: [
            // {
            //   label: 'Collection Status',
            //   icon: 'pi pi-fw pi-file',
            //   command: () => navigateTo('/settings/collection-status'),
            //   items: []
            // },
            {
              label: 'Credit Card Type',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/credit-card-type'),
              items: []
            },
            {
              label: 'VCC Transaction Type',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/vcc-transaction-type'),
              items: []
            },
            {
              label: 'Merchant',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/merchant'),
              items: []
            },
            {
              label: 'Merchant Bank Account',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/merchant-bank-account'),
              items: []
            },
            {
              label: 'Merchant Commission',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/merchant-commission'),
              items: []
            },
            {
              label: 'Merchant Currency',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/merchant-currency'),
              items: []
            },
            {
              label: 'Merchant Hotel Enrolle',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/merchant-hotel-enrolle'),
              items: []
            },
            {
              label: 'Merchant Config',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/merchant-config'),
              items: []
            },
            {
              label: 'Merchant Language',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/merchant-language'),
              items: []
            },
            {
              label: 'Reconcile Transaction Status',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/reconcile-transaction-status'),
              items: []
            },
            {
              label: 'Transaction Status',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/transaction-status'),
              items: []
            }
          ]
        }

      ],
      [
        {
          label: 'Bank',
          items: [
            {
              label: 'Bank Account',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/bank-account'),
              items: []
            },
            {
              label: 'Account Type',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/account-type'),
              items: []
            },
            {
              label: 'Bank',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/bank'),
              items: []
            },
            {
              label: 'Currency',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/currency'),
              items: []
            },
          ]
        },
        {
          label: 'Employee',
          items: [
            {
              label: 'Employee',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/employee'),
              items: []
            },
            {
              label: 'Department group',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/department-group'),
              items: []
            }
          ]
        },
        {
          label: 'Hotel',
          items: [
            {
              label: 'Room Category',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/room-category'),
              items: []
            },
            {
              label: 'Room Type',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/room-type'),
              items: []
            },
          ]
        },

        {
          label: 'Geography',
          items: [
            {
              label: 'Time Zone',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/time-zone'),
              items: []
            },
            {
              label: 'Region',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/region'),
              items: []
            },
            {
              label: 'Country',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/country'),
              items: []
            },
            {
              label: 'City State',
              icon: 'pi pi-fw pi-file',
              command: () => navigateTo('/settings/city-state'),
              items: []
            },
          ]
        },

      ],
    ]
  },
  // Payments

])

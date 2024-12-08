import type { ComponentPublicInstance } from 'vue'
import { ref } from 'vue'

type MenuRefType = Element | ComponentPublicInstance | null
interface SvgIcon {
  svgHeight: string
  svgWidth: string
  svgViewBox?: string
  svgFill: string
  svgPath: string
}

interface MenuItem {
  id: string
  btnLabel: string
  btnToolTip: string
  pBtnIcon?: string
  btnIcon: SvgIcon
  btnShowIcon: boolean
  btnAriaControls: string
  btnDisabled: boolean
  btnOnClick?: () => void
  menuId: string
  menuRef: MenuRefType | null
  menuItems: any[] // Puedes reemplazar `any` con el tipo específico si tienes una interfaz definida para los elementos del menú
  permission?: string[]
  showBtn: () => boolean
}

const items = ref([
  {
    id: 'payment',
    label: 'Payment',
    command: () => window.open('/payment/form', '_blank'),
    disabled: false,
    permission: ['PAYMENT-MANAGEMENT:CREATE'],
  },
  {
    id: 'income',
    label: 'Income',
    command: () => window.open('/payment/income?type=INCOME', '_blank'),
    disabled: false,
    permission: [],
  },
])

const itemsImport = ref([
  { label: 'Payment of bank', command: () => navigateTo('/payment/import-of-bank') },
  { label: 'Payment of expense', command: () => navigateTo('/payment/import-of-expense') },
  { label: 'Anti to Income', command: () => navigateTo('/payment/import-anti-income') },
  { label: 'Expense To Booking', command: () => navigateTo('/payment/import-expense-booking') },
  { label: 'Payment Detail', command: () => navigateTo('/payment/import-detail') },
])

const itemsExportToExcel = ref([
  {
    items: [
      // { id: 'visual-setting', label: 'Visual Setting', command: () => {} },
      { id: 'export-summary', label: 'Export Summary', command: () => {} },
      // { id: 'export-hierarchy', label: 'Export Hierarchy', command: () => {} },
    ]
  }
])

export const itemMenuList = ref<MenuItem[]>([
  {
    id: 'new',
    btnLabel: 'New',
    btnToolTip: 'New',
    btnShowIcon: true,
    btnIcon: {
      svgHeight: '20px',
      svgWidth: '20px',
      svgViewBox: '0 -960 960 960',
      svgFill: '#ffff',
      svgPath: 'M440-440H200v-80h240v-240h80v240h240v80H520v240h-80v-240Z',
    },
    btnAriaControls: 'overlay_menu1',
    btnDisabled: false,
    btnOnClick: () => {},
    menuId: 'overlay_menu1',
    menuRef: null as MenuRefType,
    menuItems: items.value,
    permission: ['PAYMENT-MANAGEMENT:SHOW-BTN-NEW'],
    showBtn: () => true
    // Object.prototype.hasOwnProperty.call(options.value, 'showCreate') ? options.value?.showCreate : true,
  },
  {
    id: 'import',
    btnLabel: 'Import',
    btnToolTip: 'Import',
    pBtnIcon: 'pi pi-file-import',
    btnShowIcon: false,
    btnIcon: {
      svgHeight: '20px',
      svgWidth: '20px',
      svgViewBox: '0 -960 960 960',
      svgFill: '#ffff',
      svgPath: 'M440-320v-326L336-542l-56-58 200-200 200 200-56 58-104-104v326h-80ZM240-160q-33 0-56.5-23.5T160-240v-120h80v120h480v-120h80v120q0 33-23.5 56.5T720-160H240Z',
    },
    btnAriaControls: 'overlay_menu2',
    btnDisabled: false,
    menuId: 'overlay_menu2',
    menuRef: null as MenuRefType,
    menuItems: itemsImport.value,
    permission: ['PAYMENT-MANAGEMENT:IMPORT'],
    showBtn: () => true,
  },
  // {
  //   btnLabel: 'Proc. Credit',
  //   btnToolTip: 'Proc. Credit',
  //   btnShowIcon: true,
  //   btnIcon: {
  //     svgHeight: '20px',
  //     svgWidth: '20px',
  //     svgViewBox: '0 -960 960 960',
  //     svgFill: '#ffff',
  //     svgPath: 'M441-120v-86q-53-12-91.5-46T293-348l74-30q15 48 44.5 73t77.5 25q41 0 69.5-18.5T587-356q0-35-22-55.5T463-458q-86-27-118-64.5T313-614q0-65 42-101t86-41v-84h80v84q50 8 82.5 36.5T651-650l-74 32q-12-32-34-48t-60-16q-44 0-67 19.5T393-614q0 33 30 52t104 40q69 20 104.5 63.5T667-358q0 71-42 108t-104 46v84h-80Z',
  //   },
  //   btnAriaControls: 'overlay_menu3',
  //   btnDisabled: false,
  //   menuId: 'overlay_menu3',
  //   menuRef: null as MenuRefType,
  //   menuItems: [],
  //   permission: ['PAYMENT-MANAGEMENT:CREDIT-PROCESS'],
  //   showBtn: () => true,
  // },
  // {
  //   btnLabel: 'Document',
  //   btnToolTip: 'Document',
  //   btnShowIcon: true,
  //   btnIcon: {
  //     svgHeight: '20px',
  //     svgWidth: '20px',
  //     svgViewBox: '0 -960 960 960',
  //     svgFill: '#ffff',
  //     svgPath: 'M320-240h320v-80H320v80Zm0-160h320v-80H320v80ZM240-80q-33 0-56.5-23.5T160-160v-640q0-33 23.5-56.5T240-880h320l240 240v480q0 33-23.5 56.5T720-80H240Zm280-520v-200H240v640h480v-440H520ZM240-800v200-200 640-640Z',
  //   },
  //   btnAriaControls: 'overlay_menu4',
  //   btnDisabled: true,
  //   menuId: 'overlay_menu4',
  //   menuRef: null as MenuRefType,
  //   menuItems: [],
  //   permission: ['PAYMENT-MANAGEMENT:DOCUMENT'],
  //   showBtn: () => true,
  // },
  {
    id: 'print',
    btnLabel: 'Print',
    btnToolTip: 'Print',
    btnShowIcon: true,
    btnIcon: {
      svgHeight: '20px',
      svgWidth: '20px',
      svgViewBox: '0 -960 960 960',
      svgFill: '#ffff',
      svgPath: 'M640-640v-120H320v120h-80v-200h480v200h-80Zm-480 80h640-640Zm560 100q17 0 28.5-11.5T760-500q0-17-11.5-28.5T720-540q-17 0-28.5 11.5T680-500q0 17 11.5 28.5T720-460Zm-80 260v-160H320v160h320Zm80 80H240v-160H80v-240q0-51 35-85.5t85-34.5h560q51 0 85.5 34.5T880-520v240H720v160Zm80-240v-160q0-17-11.5-28.5T760-560H200q-17 0-28.5 11.5T160-520v160h80v-80h480v80h80Z',
    },
    btnAriaControls: 'overlay_menu5',
    btnDisabled: true,
    btnOnClick: () => {},
    menuId: 'overlay_menu5',
    menuRef: null as MenuRefType,
    menuItems: [],
    permission: ['PAYMENT-MANAGEMENT:PRINT'],
    showBtn: () => true,
  },
  {
    id: 'export',
    btnLabel: 'Export',
    btnToolTip: 'Export',
    btnShowIcon: true,
    btnIcon: {
      svgHeight: '20px',
      svgWidth: '20px',
      svgViewBox: '0 -960 960 960',
      svgFill: '#ffff',
      svgPath: 'M480-320 280-520l56-58 104 104v-326h80v326l104-104 56 58-200 200ZM240-160q-33 0-56.5-23.5T160-240v-120h80v120h480v-120h80v120q0 33-23.5 56.5T720-160H240Z',
    },
    btnAriaControls: 'overlay_menu6',
    btnDisabled: false,
    menuId: 'overlay_menu6',
    btnOnClick: () => {},
    menuRef: null as MenuRefType,
    menuItems: [],
    permission: ['PAYMENT-MANAGEMENT:EXPORT'],
    showBtn: () => true,
  },
])

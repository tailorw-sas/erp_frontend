import { ref } from 'vue' // Asegúrate de importar la interfaz desde la ruta correcta
import type { Card } from './CardInterface'
import icon_1 from '~/assets/images/home/icon_1.png'
import icon_2 from '~/assets/images/home/icon_2.png'
import icon_3 from '~/assets/images/home/icon_3.png'
import icon_4 from '~/assets/images/home/icon_4.png'
import icon_5 from '~/assets/images/home/icon_5.png'
import icon_6 from '~/assets/images/home/icon_6.png'
import icon_7 from '~/assets/images/home/icon_7.png'
import icon_8 from '~/assets/images/home/icon_8.png'
import icon_9 from '~/assets/images/home/icon_9.png'
import icon_10 from '~/assets/images/home/icon_10.png'

export const cards = ref<Card[]>([
  {
    title: 'Invoice Management',
    icon: icon_1,
    color: '#7CA742',
    status: '',
    url: 'invoice'
  },
  {
    title: 'Payment Management',
    icon: icon_2,
    color: '#0F8BFD',
    status: '',
    url: 'payment'
  },
  {
    title: 'Collection Management',
    icon: icon_3,
    color: '#D7831D',
    status: ''
  },
  {
    title: 'Virtual Credit Card Management',
    icon: icon_4,
    color: '#970235',
    status: '',
    url: 'vcc-management'
  },
  {
    title: 'Report Management',
    icon: icon_5,
    color: '#2B91C5',
    status: ''
  },
  {
    title: 'Accounting Management',
    icon: icon_6,
    color: '#C0C0C0',
    status: 'Featuring'
  },
  {
    title: 'Human Resources Management',
    icon: icon_7,
    color: '#C0C0C0',
    status: 'Featuring'
  },
  {
    title: 'Policy Management',
    icon: icon_8,
    color: '#C0C0C0',
    status: 'Featuring'
  },
  {
    title: 'Dashboard',
    icon: icon_9,
    color: '#C0C0C0',
    status: 'Featuring'
  },
  {
    title: 'Nomenclators',
    icon: icon_10,
    color: '#666666',
    status: ''
  },
])

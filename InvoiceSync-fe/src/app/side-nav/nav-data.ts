import {MenuItem} from "./MenuItem";


export const MENU_ITEMS: MenuItem[] = [
  {
    label: 'Domov',
    route: '/home',
    icon: 'home'
  },
  {
    label: 'Dokumenty',
    icon: 'document',
    children: [
      { label: 'Faktúry', route: '/invoices' },
      { label: 'Vystaviť faktúru', route: '/invoice/new' },
      { label: 'Bločky', route: '/web/receipts' }
    ]
  },
  {
    label: 'Xml Importy',
    route: '/xml-convertor',
    icon: 'covertrus',
    features: ['EKON_SPECIALTY']
  },
  {
    label: 'Prevod údajov',
    route: 'web/data-transfer',
    icon: 'data_transfer'
  },
  {
    label: 'Predplatné',
    route: '/web/limiter',
    icon: 'subscription'
  },
];

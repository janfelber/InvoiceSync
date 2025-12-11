import {MenuItem} from "./MenuItem";


export const MENU_ITEMS: MenuItem[] = [
  {
    label: 'menu_home',
    route: '/home',
    icon: 'home'
  },
  {
    label: 'menu_documents',
    icon: 'document',
    children: [
      { label: 'menu_invoices', route: '/invoices' },
      { label: 'menu_create_invoice', route: '/invoice/new' },
      { label: 'menu_receipts', route: '/web/receipts' }
    ]
  },
  {
    label: 'menu_xml_convertor',
    route: '/xml-convertor',
    icon: 'covertrus',
    features: ['EKON_SPECIALTY']
  },
  {
    label: 'menu_data_transfer',
    route: 'web/data-transfer',
    icon: 'data_transfer'
  },
  {
    label: 'menu_subscription',
    route: '/web/limiter',
    icon: 'subscription'
  },
];

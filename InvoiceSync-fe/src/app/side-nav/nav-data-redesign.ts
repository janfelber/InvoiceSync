import {IconReceiptsComponent} from "./icons/icons/icon-receipts/icon-receipts.component";
import {IconHomeComponent} from "./icons/icons/icon-home/icon-home.component";
import {IconXmlImportComponent} from "./icons/icons/icon-xml-import/icon-xml-import.component";
import {MenuItem} from "./MenuItem";


export const MENU_ITEMS: MenuItem[] = [
  {
    label: 'Domov',
    route: '/home',
    icon: 'home',
    roles: ['BASIC_USER']
  },
  {
    label: 'Dokumenty',
    roles: ['BASIC_USER'],
    icon: 'document',
    children: [
      { label: 'Faktúry', route: '/invoices', roles: ['BASIC_USER'] },
      { label: 'Vystaviť faktúru', route: '/invoice/new', roles: ['BASIC_USER'] },
      { label: 'Bločky', route: '/web/receipts', roles: ['BASIC_USER'] }
    ]
  },
  {
    label: 'Xml Importy',
    route: '/xml-convertor',
    roles: ['EKON_FEATURE'],
    icon: 'covertrus'
  },
  {
    label: 'Prevod údajov',
    route: 'web/data-transfer',
    roles: ['BASIC_USER'],
    icon: 'data_transfer'
  },
  {
    label: 'Predplatné',
    route: '/web/limiter',
    roles: ['BASIC_USER'],
    icon: 'subscription'
  },
];

export const navbarDataRedesign = [
  {
    routerLink: 'home',
    iconComponent: IconHomeComponent,
    label: 'Domov',
    layout: 'default',
    hideIfNotZero: false
  },
  {
    routerLink: 'invoices',
    label: 'Faktury',
    layout: 'default',
    hideIfNotZero: true
  },
  {
    routerLink: 'rex',
    label: 'Rex',
    layout: 'default',
    hideIfNotZero: true
  },
  {
    routerLink: 'xml-import',
    iconComponent: IconXmlImportComponent,
    label: 'XML import',
    layout: 'default',
  },
  {
    routerLink: '/web/receipts',
    iconComponent: IconReceiptsComponent,
    label: 'Bločky',
    layout: 'default'
  },
  {
    routerLink: 'web/convertor',
    iconComponent: IconReceiptsComponent,
    label: 'Konvertor',
    layout: 'default'
  },
  {
    routerLink: 'users',
    label: 'Users',
    layout: 'admin',
  }
];

import {IconReceiptsComponent} from "./icons/icons/icon-receipts/icon-receipts.component";
import {IconHomeComponent} from "./icons/icons/icon-home/icon-home.component";
import {IconXmlImportComponent} from "./icons/icons/icon-xml-import/icon-xml-import.component";

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
    routerLink: 'users',
    label: 'Users',
    layout: 'admin',
  }
];

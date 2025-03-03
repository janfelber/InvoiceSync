export const navbarDataRedesign = [
  {
    routerLink: 'home',
    icon: 'fas fa-home', // cesta k SVG súboru
    label: 'Domov',
    layout: 'default',
    hideIfNotZero: true
  },
  {
    routerLink: 'invoices',
    icon: 'fas fa-file-invoice', // cesta k SVG súboru
    label: 'Faktury',
    layout: 'default',
    hideIfNotZero: true
  },
  {
    routerLink: 'rex',
    icon: 'fas fa-light fa-mobile',
    label: 'Rex',
    layout: 'default',
    hideIfNotZero: true
  },
  {
    routerLink: 'xml-import',
    icon: 'fas fa-file-import',
    label: 'XML import',
    layout: 'default',
  },
  {
    routerLink: '/web/receipts',
    icon: 'fas fa-receipt',
    label: 'Bločky',
    layout: 'default'
  },
  {
    routerLink: 'users',
    icon: 'fas fa-user',
    label: 'Users',
    layout: 'admin',
  }
];

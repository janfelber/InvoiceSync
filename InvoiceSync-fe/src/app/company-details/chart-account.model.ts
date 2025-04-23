interface Account {
  accountId: string;
  accountName: string;
  editable: boolean;
}

interface Category {
  categoryName: string;
  accounts: Account[];
}

interface ClassGroup {
  className: string;
  categories: { [categoryKey: string]: Category };
}

export type GroupedAccounts = { [classKey: string]: ClassGroup };

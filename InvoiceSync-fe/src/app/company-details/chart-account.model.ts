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
  categories: { [key: string]: Category };
}

export type GroupedAccounts = { [key: string]: ClassGroup };

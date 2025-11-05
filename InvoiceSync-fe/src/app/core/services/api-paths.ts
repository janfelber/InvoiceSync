export const ApiPaths = {
  company: {
    BASE: '/company',
    FIND_ALL_BY_USER: '/user',
    BY_ID: (companyId: number) => `/${companyId}`,
    SAVE: '/save',
    CREATE_BY_REGISTRATION_NUMBER: (registrationNumber: string) => `/save/${registrationNumber}`,
    UPDATE_BY_ID: (companyId: number) => `/update/${companyId}`,
    DELETE_BY_ID: (companyId: number) => `/delete/${companyId}`,
  },
  receipt: {
    BASE: '/receipt',
    FIND_ALL_BY_USER: '/user',
    BY_ID: (receiptId: number) => `/${receiptId}`,
    FIND_BY_COMPANY: (companyId: number) => `/company/${companyId}`,
    SAVE: '/save',
    UPDATE_BY_ID: (receiptId: number) => `/update/${receiptId}`,
    POHODA_RECEIPT_EXPORT: '/export/receipt',
    DELETE: (receiptId: number) => `/${receiptId}`,
  },
  xmlFile: {
    BASE: '/xml-file',
    FIND_ALL_BY_USER: '/user',
    SAVE: '/save',
    EXPORT_BY_ID: (xmlFileId: number) => `/generate-zip/${xmlFileId}`,
  },
  postingAccounts: {
    BASE: '/post-account',
    FIND_BY_COMPANY: (companyId: number) => `/${companyId}/accounts`,
    AVAILABLE_ACCOUNTS: (companyId: number) => `/${companyId}/classes`,
    SAVE: '/save',
    DELETE: (accountId: number) => `/${accountId}`,
    IMPORT_ACCOUNTS: '/import',
  },
  subscription: {
    BASE: '/subscription',
    USER_PLAN: '/user/plan',
    SUBSCRIBE: '/subscribe',
    USER_LIMITS: '/user/limit',
  },
  emailSubscribe: {
    BASE: '/email-subscribe',
    SUBSCRIBE: '/subscribe',
  },
  invoice: {
    BASE: '/invoice',
    FIND_ALL_BY_USER: '/user',
    SAVE: '/save',
    FIND_BY_COMPANY: (companyId: number) => `/${companyId}/invoices`,
    BY_ID: (invoiceId: number) => `/${invoiceId}`,
    DOCUMENTS: (invoiceId: number) => `/${invoiceId}/documents`,
    ADD_DOCUMENT_TO_INVOICE: (invoiceId: number) => `/upload/document/${invoiceId}`,
    DELETE_DOCUMENT_FROM_INVOICE: (documentId: number) => `/delete/document/${documentId}`,
  },
  data_transfer: {
    BASE: '/convert',
    FIND_ALL_BY_USER: '/imports',
    UPLOAD: '/save',
    BY_ID: (convertId: number) => `/${convertId}`,
    UPDATE_MAPPING: (convertId: number) => `/${convertId}/mapping`,
    DOWNLOAD: (convertId: number) => `/${convertId}/download`,
  },
  stats: {
    BASE: '/stats',
    FIND_BASIC_STATS: (companyId: number) => `/basic-stats/${companyId}`,
  },
  admin: {
    BASE: '/admin',
    USERS: '/users',
    USER_BY_ID: (userId: string) => `/user/${userId}`,
    UPDATE_FEATURES: (userId: string) => `/user/${userId}/features`,
  },
  feature: {
    BASE: '/feature',
    USER_BY_ID: (userId: string) => `/${userId}`
  },
  user: {
    BASE: '/user',
    ME: '/me'
  }
};

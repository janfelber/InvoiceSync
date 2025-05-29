export const ApiPaths = {
  company: {
    BASE: '/company',
    FIND_ALL_BY_USER: '/user',
    BY_ID: (companyId: number) => `/${companyId}`,
    SAVE: '/save',
    UPDATE_BY_ID: (companyId:number) => `/update/${companyId}`,
    DELETE_BY_ID: (companyId:number) => `/delete/${companyId}`,
  },
  receipt: {
    BASE: '/receipt',
    FIND_ALL_BY_USER: '/user',
    BY_ID: (receiptId: number) => `/${receiptId}`,
    FIND_BY_COMPANY: (companyId:number) => `/company/${companyId}`,
    SAVE: '/save',
    UPDATE_BY_ID: (receiptId:number) => `/update/${receiptId}`,
    POHODA_RECEIPT_EXPORT: '/export/pohoda'
  }
};

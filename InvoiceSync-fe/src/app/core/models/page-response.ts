export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export function createEmptyPage<T>(size = 10): PageResponse<T> {
  return {
    content: [],
    totalElements: 0,
    totalPages: 0,
    number: 0,
    size
  };
}

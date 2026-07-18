export interface OrganizationDto {
  id: number | null;
  name: string;
  street: string;
  streetName: string;
  city: string;
  postalCode: string;
  registrationNumber: string;
  taxId: string;
  vatId: string;
  targetCompanyEmail: string | null;
}

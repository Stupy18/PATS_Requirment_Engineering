export interface PatientRecord {
  id?: number;
  patientId: number;
  
  // Demographics
  firstName: string;
  lastName: string;
  dateOfBirth: Date;
  gender?: string;
  cnp?: string;
  
  // Contact
  email: string;
  phoneNumber: string;
  alternatePhone?: string;
  
  // Address
  streetAddress?: string;
  city?: string;
  country?: string;
  postalCode?: string;
  
  // Additional
  occupation?: string;
  isActive?: boolean;
  createdAt?: Date;
  updatedAt?: Date;
}

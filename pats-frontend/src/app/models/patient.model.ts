export interface Patient {
  id?: number;
  userId?: number;
  firstName: string;
  lastName: string;
  dateOfBirth?: Date;
  gender?: string;
  phoneNumber?: string;
  address?: string;
  city?: string;
  postalCode?: string;
  country?: string;
  bloodType?: string;
  insuranceProvider?: string;
  insurancePolicyNumber?: string;
  registeredAt?: Date;
}

// DTO for creating new patient
export interface CreatePatientRequest {
  // User account
  username: string;
  email: string;
  password: string;
  
  // Patient info
  firstName: string;
  lastName: string;
  dateOfBirth?: Date;
  gender?: string;
  phoneNumber: string;
  address?: string;
  city?: string;
  postalCode?: string;
  country?: string;
  bloodType?: string;
  insuranceProvider?: string;
  insurancePolicyNumber?: string;
}
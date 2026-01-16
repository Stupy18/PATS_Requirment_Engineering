export interface EmergencyContact {
  id?: number;
  patientId: number;
  
  firstName: string;
  lastName: string;
  relationship: string;
  phoneNumber: string;
  alternatePhone?: string;
  email?: string;
  address?: string;
  isPrimary?: boolean;
  priority?: number;
  
  createdAt?: Date;
  updatedAt?: Date;
}
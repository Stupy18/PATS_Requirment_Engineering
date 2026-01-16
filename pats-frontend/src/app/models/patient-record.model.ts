export interface PatientRecord {
  id?: number;
  patientId: number;
  cnp?: string;
  occupation?: string;
  alternatePhone?: string;
  streetAddress?: string;
  city?: string;
  county?: string;
  postalCode?: string;
}
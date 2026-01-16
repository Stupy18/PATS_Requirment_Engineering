export interface MoodEntry {
  id?: number;
  patientId: number;
  emotionalRating: number;
  notes?: string;
  entryTimestamp: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface MoodEntryRequest {
  patientId: number;
  rating: number;
  notes?: string;
}

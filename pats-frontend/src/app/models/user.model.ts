export interface User {
  userId: number;
  username: string;
  email: string;
  role: string;
  profileId: number;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  role: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

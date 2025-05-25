export interface ChefDto {
  id: string;
  name: string;
  avatarUrl?: string;
}

export interface CurrentUser {
  id: string;
  email: string;
  role: string;
  name: string;
  avatarUrl?: string;
}

export interface UserAdminDto {
  id: string;
  name: string;
  email: string;
  role: string;
  avatarUrl: string | null;
  createdAt: string;
}

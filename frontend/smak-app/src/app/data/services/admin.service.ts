import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {CategoryDto} from '../interfaces/category.interface';
import {UserAdminDto} from '../interfaces/user.interface';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private readonly baseUrl = 'http://192.168.0.103:5127/api';

  constructor(private http: HttpClient) {}

  getAllUsers() {
    return this.http.get<UserAdminDto[]>(`${this.baseUrl}/admin/users`);
  }

  deleteUser(id: string) {
    return this.http.delete(`${this.baseUrl}/admin/users/${id}`);
  }

  assignChef(id: string) {
    return this.http.post(`${this.baseUrl}/admin/users/${id}/assign-chef`, {});
  }

  getAllCategories() {
    return this.http.get<CategoryDto[]>(`${this.baseUrl}/category`);
  }

  createCategory(data: FormData) {
    return this.http.post(`${this.baseUrl}/category`, data);
  }

  deleteCategory(id: string) {
    return this.http.delete(`${this.baseUrl}/category/${id}`);
  }
}

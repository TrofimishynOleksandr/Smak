import {Component, inject, OnInit} from '@angular/core';
import {CategoryDto} from '../../data/interfaces/category.interface';
import {FormsModule} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';
import {AvatarUrlPipe} from '../../shared/pipes/avatar-url.pipe';
import {CategoryImageUrlPipe} from '../../shared/pipes/category-image-url.pipe';
import {UserAdminDto} from '../../data/interfaces/user.interface';
import {AdminService} from '../../data/services/admin.service';
import {SvgIconComponent} from '../../shared/svg-icon/svg-icon.component';

@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [
    FormsModule,
    NgForOf,
    AvatarUrlPipe,
    NgIf,
    CategoryImageUrlPipe,
    SvgIconComponent
  ],
  templateUrl: './admin-panel.component.html',
  styleUrl: './admin-panel.component.scss'
})
export class AdminPanelComponent implements OnInit {
  adminService = inject(AdminService);

  users: UserAdminDto[] = [];
  categories: CategoryDto[] = [];

  newCategoryName = '';
  newCategoryImage: File | null = null;

  imagePreview: string | null = null;

  ngOnInit(): void {
    this.loadUsers();
    this.loadCategories();
  }

  loadUsers() {
    this.adminService.getAllUsers().subscribe(users => this.users = users);
  }

  loadCategories() {
    this.adminService.getAllCategories().subscribe(categories => this.categories = categories);
  }

  deleteUser(id: string) {
    if (confirm('Ви впевнені, що хочете видалити цього користувача?')) {
      this.adminService.deleteUser(id).subscribe(() => this.loadUsers());
    }
  }

  assignChef(id: string) {
    this.adminService.assignChef(id).subscribe(() => this.loadUsers());
  }

  onCategoryImageChange(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.newCategoryImage = file;

      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result as string;
      };
      reader.readAsDataURL(file);
    }
  }

  createCategory() {
    if (!this.newCategoryName) return;

    const formData = new FormData();
    formData.append('name', this.newCategoryName);
    if (this.newCategoryImage) formData.append('image', this.newCategoryImage);

    this.adminService.createCategory(formData).subscribe(() => {
      this.newCategoryName = '';
      this.newCategoryImage = null;
      this.loadCategories();
    });
  }

  deleteCategory(id: string) {
    if (!confirm('Видалити цю категорію?')) return;

    this.adminService.deleteCategory(id).subscribe(() => {
      this.loadCategories();
    });
  }

}

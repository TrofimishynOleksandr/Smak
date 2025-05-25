import {Component, ElementRef, HostListener, inject, signal} from '@angular/core';
import {Router, RouterModule} from '@angular/router';
import { CommonModule } from '@angular/common';
import {AuthService} from '../auth/auth.service';
import {AvatarUrlPipe} from '../shared/pipes/avatar-url.pipe';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterModule, CommonModule, AvatarUrlPipe, FormsModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent {
  el = inject(ElementRef);
  authService = inject(AuthService);
  router = inject(Router);

  isDropdownOpen = signal(false);
  searchQuery = '';

  toggleDropdown() {
    if (!this.authService.isAuth) {
      this.authService.showLoginModal();
      return;
    }
    this.isDropdownOpen.update(v => !v);
  }

  logout() {
    this.authService.logout();
    this.isDropdownOpen.set(false);
  }

  @HostListener('document:click', ['$event.target'])
  onClickOutside(target: HTMLElement) {
    if (!this.el.nativeElement.contains(target)) {
      this.isDropdownOpen.set(false);
    }
  }

  search() {
    const trimmed = this.searchQuery.trim();
    if (trimmed) {
      this.router.navigate(['/recipes'], { queryParams: { search: trimmed } });
      this.searchQuery = '';
    }
  }

  get userName(): string {
    return this.authService.isAuth ? this.authService.userName : 'Увійти';
  }

  get avatarUrl() {
    return this.authService.avatarUrl;
  }
}

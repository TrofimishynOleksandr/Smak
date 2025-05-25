import {Component, inject, OnInit, signal} from '@angular/core';
import {AuthService} from '../../auth/auth.service';
import {CurrentUser} from '../../data/interfaces/user.interface';
import {NgIf} from '@angular/common';
import {AvatarUrlPipe} from '../../shared/pipes/avatar-url.pipe';
import {SvgIconComponent} from '../../shared/svg-icon/svg-icon.component';

@Component({
  selector: 'app-my-profile-page',
  standalone: true,
  imports: [
    NgIf,
    AvatarUrlPipe,
    SvgIconComponent
  ],
  templateUrl: './my-profile-page.component.html',
  styleUrl: './my-profile-page.component.scss'
})
export class MyProfilePageComponent implements OnInit {
  user = signal<CurrentUser | null>(null);
  authService = inject(AuthService);
  imagePreview: string | null = null;

  ngOnInit(): void {
    this.loadUser();
  }

  loadUser(avatarUrl?: string): void {
    if(avatarUrl) {
      let newUser = this.authService.getCurrentUser();
      if(newUser) {
        newUser.avatarUrl = avatarUrl;
        this.user.set(newUser);
      }
    }
    this.user.set(this.authService.getCurrentUser());
  }

  onFileChange(event: Event) {
    const file = (event.target as HTMLInputElement)?.files?.[0];
    if (!file) return;

    this.authService.uploadAvatar(file).subscribe((res) => this.loadUser(res.avatarUrl));

    this.imagePreview = this.user()?.avatarUrl ?? null;
  }

  removeAvatar() {
    this.authService.deleteAvatar().subscribe(() => {
      this.imagePreview = null;
      this.loadUser();
    });
  }
}

import {Component, inject, OnInit} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {AuthModalComponent} from '../shared/components/auth-modal/auth-modal.component';
import {HeaderComponent} from '../header/header.component';
import {AuthService} from '../auth/auth.service';
import {FooterComponent} from '../footer/footer.component';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [
    RouterOutlet,
    AuthModalComponent,
    HeaderComponent,
    FooterComponent,
  ],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss'
})
export class LayoutComponent implements OnInit {
  authService = inject(AuthService);

  ngOnInit(): void {
    if (this.authService.token) {
      this.authService.loadCurrentUser().subscribe();
    }
  }
}

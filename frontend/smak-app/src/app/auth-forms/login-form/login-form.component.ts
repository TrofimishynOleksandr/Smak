import {Component, EventEmitter, inject, Output} from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import {ModalService} from '../../services/modal.service';
import {AuthService} from '../../auth/auth.service';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.scss']
})
export class LoginFormComponent {
  @Output() success = new EventEmitter<void>();

  fb = inject(FormBuilder);
  authService = inject(AuthService);
  modalService = inject(ModalService);

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  onSubmit() {
    if (this.form.invalid) return;

    //@ts-ignore
    this.authService.login(this.form.value).subscribe({
      next: () => this.success.emit(),
      error: err => alert(err.error?.message || 'Login failed')
    });
  }

  switchToRegister(event: Event) {
    event.preventDefault();
    this.modalService.open('register');
  }
}


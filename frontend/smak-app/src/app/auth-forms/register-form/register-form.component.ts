import {Component, EventEmitter, inject, Output} from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import {ModalService} from '../../services/modal.service';
import {AuthService} from '../../auth/auth.service';

@Component({
  selector: 'app-register-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register-form.component.html',
  styleUrls: ['./register-form.component.scss']
})
export class RegisterFormComponent {
  @Output() success = new EventEmitter<void>();

  fb = inject(FormBuilder);
  authService = inject(AuthService);
  modalService = inject(ModalService);

  form = this.fb.group({
    name: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: [
      '',
      [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern(/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*?&]+$/)
      ]
    ]
  });

  onSubmit() {
    if (this.form.invalid) return;

    //@ts-ignore
    this.authService.register(this.form.value).subscribe({
      next: () => this.success.emit(),
      error: err => alert(err.error?.message || 'Registration failed')
    });
  }

  switchToLogin(event: Event) {
    event.preventDefault();
    this.modalService.open('login');
  }
}

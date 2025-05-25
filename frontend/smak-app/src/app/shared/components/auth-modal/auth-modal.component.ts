import { Component } from '@angular/core';
import {AsyncPipe, NgIf, NgSwitch, NgSwitchCase} from '@angular/common';
import {LoginFormComponent} from '../../../auth-forms/login-form/login-form.component';
import {RegisterFormComponent} from '../../../auth-forms/register-form/register-form.component';
import {ModalService} from '../../../services/modal.service';
import {AuthService} from '../../../auth/auth.service';

@Component({
  selector: 'app-auth-modal',
  standalone: true,
  templateUrl: './auth-modal.component.html',
  styleUrl: './auth-modal.component.scss',
  imports: [
    LoginFormComponent,
    RegisterFormComponent,
    AsyncPipe,
    NgSwitch,
    NgSwitchCase,
    NgIf
  ]
})
export class AuthModalComponent {
  constructor(public modal: ModalService, public auth: AuthService) {}

  close() {
    this.modal.close();
  }

  onSuccess() {
    this.modal.close();
  }
}

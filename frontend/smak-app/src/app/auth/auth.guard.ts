import {AuthService} from './auth.service';
import {inject} from '@angular/core';
import {CanActivateFn, Router} from '@angular/router';

export const AuthGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  if (!auth.isAuth) {
    auth.showLoginModal();
    return false;
  }
  return true;
};

import {AuthService} from './auth.service';
import {inject} from '@angular/core';
import {CanActivateFn} from '@angular/router';

export const RoleGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const expectedRoles = route.data?.['roles'] as string[];

  return !(!auth.isAuth || !expectedRoles.includes(auth.userRole!));


};

import {HttpHandlerFn, HttpInterceptorFn, HttpRequest} from '@angular/common/http';
import {AuthService} from './auth.service';
import {inject} from '@angular/core';
import {BehaviorSubject, catchError, filter, switchMap, take, throwError} from 'rxjs';

let isRefreshing = false;
let refreshTokenSubject = new BehaviorSubject<string | null>(null);

export const authTokenInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.token;

  if (req.url.includes('/auth/token') || req.url.includes('/auth/refresh') || req.url.includes('/auth/register')) {
    return next(req);
  }

  const authReq = token ? addToken(req, token) : req;

  return next(authReq).pipe(
    catchError(err => {
      if (err.status === 403 || err.status === 401) {
        return handleRefreshToken(authService, req, next);
      }

      return throwError(() => err);
    })
  );
};

const handleRefreshToken = (
  authService: AuthService,
  req: HttpRequest<any>,
  next: HttpHandlerFn
) => {
  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject.next(null);

    return authService.refreshAuthToken().pipe(
      switchMap((res) => {
        isRefreshing = false;
        refreshTokenSubject.next(res.accessToken);
        return next(addToken(req, res.accessToken));
      }),
      catchError(err => {
        isRefreshing = false;
        return throwError(() => err);
      })
    );
  } else {
    return refreshTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap(token => next(addToken(req, token!)))
    );
  }
};

const addToken = (req: HttpRequest<any>, token: string) => {
  return req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });
};

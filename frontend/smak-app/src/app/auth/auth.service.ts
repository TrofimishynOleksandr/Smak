import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {catchError, of, Subject, tap, throwError} from 'rxjs';
import {TokenResponse} from './auth.interface';
import {CookieService} from 'ngx-cookie-service';
import {Router} from '@angular/router';
import {ModalService} from '../services/modal.service';
import {CurrentUser} from '../data/interfaces/user.interface';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  http = inject(HttpClient)
  router = inject(Router)
  cookieService = inject(CookieService)
  modalService = inject(ModalService);

  baseApiUrl: string = 'http://localhost:5127/api/auth';
  shouldSuppressModal = false;
  loginSuccess$ = new Subject<void>();
  logout$ = new Subject<void>();

  currentUser: CurrentUser | null = null;

  get token(): string | null {
    return this.cookieService.get('token') || null;
  }

  get refreshToken(): string | null {
    return this.cookieService.get('refreshToken') || null;
  }

  loadCurrentUser() {
    return this.http.get<CurrentUser>('http://localhost:5127/api/user/me').pipe(
      tap(user => this.currentUser = user),
      catchError(() => {
        this.logout(); // або просто повернути null
        return of(null);
      })
    );
  }

  get isAuth() {
    return !!this.token;
  }

  get userRole(): string | null {
    return this.currentUser?.role ?? null;
  }

  getCurrentUser() {
    return this.currentUser;
  }

  get userName(): string {
    return this.currentUser?.name ?? 'Користувач';
  }

  get avatarUrl() {
    return this.currentUser?.avatarUrl ?? null;
  }

  uploadAvatar(file: File) {
    const formData = new FormData();
    formData.append('image', file);
    return this.http.post<{avatarUrl: string}>('http://localhost:5127/api/user/avatar', formData);
  }

  deleteAvatar() {
    return this.http.delete('http://localhost:5127/api/user/avatar');
  }

  register(payload: {name: string; email: string; password: string}) {
    return this.http.post<void>(
      `${this.baseApiUrl}/register`,
      payload,
      { headers: { 'Content-Type': 'application/json' } }
    ).pipe(
      tap(() => this.router.navigate(['/login']))
    );
  }

  login(payload: {email: string, password: string}) {
    return this.http
      .post<TokenResponse>(`${this.baseApiUrl}/login`, payload)
      .pipe(
        tap(res => {
          this.saveTokens(res);
          this.loginSuccess$.next();
        }));
  }

  refreshAuthToken() {
    return this.http.post<TokenResponse>(
      `${this.baseApiUrl}/refresh`,
      {
        refreshToken: this.refreshToken,
      }
    ).pipe(
      tap(res => this.saveTokens(res)),
      catchError(err => {
        this.logout()
        return throwError(err);
      })
    )
  }

  logout() {
    this.cookieService.delete('token');
    this.cookieService.delete('refreshToken');
    this.currentUser = null;
    this.shouldSuppressModal = true;
    this.router.navigate(['/']);
    this.logout$.next();
  }

  private saveTokens(res: TokenResponse) {
    this.cookieService.set('token', res.accessToken, undefined, '/');
    this.cookieService.set('refreshToken', res.refreshToken, undefined, '/');
    this.loadCurrentUser().subscribe();
  }

  showLoginModal() {
    if (this.shouldSuppressModal) {
      this.shouldSuppressModal = false;
      return;
    }

    this.modalService.open('login');
  }

  showRegisterModal() {
    this.modalService.open('register');
  }
}

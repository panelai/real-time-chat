import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, map, of, switchMap } from 'rxjs';
import { Account } from '../user/account.type';
import { environment } from '../../../enviroment/enviroment';
import { LocalStorageUtil } from '../../shared/util/local-storage-util';
import { UserService } from '../user/user.service';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

    //private userSubject: BehaviorSubject<Account | null>;
    //public user: Observable<Account | null>;

    public _authenticated: boolean = false;

    constructor(
        private router: Router,
        private _httpClient: HttpClient,
        private _userService: UserService
    ) {
        //this.userSubject = new BehaviorSubject(JSON.parse(localStorage.getItem('user')!));
        //this.user = this.userSubject.asObservable();
    }

    set accessToken(token: string) {
      LocalStorageUtil.saveAccessToken(token);
    }

    get accessToken(): string {
        return LocalStorageUtil.getAccessToken() ?? '';
    }

    set refreshToken(token: string) {
      LocalStorageUtil.saveRefreshToken(token)
    }

    get refreshToken(): string {
      return LocalStorageUtil.getRefreshToken() ?? '';
    }

    login(user: Account) {
        return this._httpClient.post<Account>(`${environment.apiUrl}/auth/login`, user)
            .pipe(switchMap((response: any) => {
                this.accessToken = response.accessToken;
                this.refreshToken = response.refreshToken;

                // Set the authenticated flag to true
                this._authenticated = true;

                // Store the user on the user service
                this._userService.user = {
                    id: response.guid,
                    email: response.email,
                    username: response.username
                };

                // Return a new observable with the response
                return of(response);
            }));
    }

    register(user: Account) {
      return this._httpClient.post(`${environment.apiUrl}/auth/register`, user);
    }

    signOut(): Observable<any> {
      // Remove the access token from the local storage
      LocalStorageUtil.removeLoginInfo();

      // Set the authenticated flag to false
      this._authenticated = false;
      this.router.navigate(['/account/login']);

      // Return the observable
      return of(true);
    }

    refreshAccessToken() {
      const body = {refreshToken: LocalStorageUtil.getRefreshToken() };
      return this._httpClient.post(`${environment.apiUrl}/api/refresh-token`, body);
  }
}

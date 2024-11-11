import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Account } from './account.type';
import { LocalStorageUtil } from '../../shared/util/local-storage-util';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
    //private _roles: UserRole[];
    private _user!: Account;
    /**
     * Constructor
     */
    constructor(private _httpClient: HttpClient) {
    }

    // -----------------------------------------------------------------------------------------------------
    // @ Accessors
    // -----------------------------------------------------------------------------------------------------

    /**
     * Setter & getter for user
     *
     * @param value
     */
    set user(value: Account) {
        this._user = value;
        LocalStorageUtil.saveUser(JSON.stringify(value));
    }

    get user(): Account | undefined {
        if (this._user) {
            return this._user;
        }
        const userInLocalStorage = LocalStorageUtil.getUser();

        if (userInLocalStorage) {
            const value = JSON.parse(userInLocalStorage);

            return {email: value.email,
                    id: value.id,
                    username: value.username};
        }
        return undefined;
    }

    get user$(): Observable<Account> | Observable<undefined> {
        if (this._user) {
            return of({...this._user});
        }
        const userInLocalStorage = LocalStorageUtil.getUser();
        if (userInLocalStorage) {
            const value = JSON.parse(userInLocalStorage);

            return of({email: value.email,
                       id: value.id,
                       username: value.username}
            )
        }
        return of(undefined);
    }
}

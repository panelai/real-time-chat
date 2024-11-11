import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Subject, distinctUntilChanged, debounce, timer, switchMap, catchError, of, Observable } from 'rxjs';
import { environment } from '../../../enviroment/enviroment';
import { SearchQuery } from '../../model/type/search-query.type';
import { Account } from '../user/account.type';
import { State } from '../../model/type/state.type';
import { createPaginationOption } from '../../model/type/pagination.type';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  http = inject(HttpClient);

  private searchQuery$ = new Subject<SearchQuery>();
  private searchResult$ = new Subject<State<Array<Account>>>();
  searchResult = this.searchResult$.asObservable();

  constructor() {
    this.listenToSearch();
  }

  private listenToSearch(): void {
    this.searchQuery$.pipe(
      distinctUntilChanged(),
      debounce(() => timer(300)),
      switchMap(query => this.fetchResult(query).pipe(
        catchError(err => {
          this.searchResult$.next(State.Builder<Array<Account>>().forError(err));
          return of([]);
        })
      ))
    ).subscribe({
      next: users => this.searchResult$.next(State.Builder<Array<Account>>().forSuccess(users)),
      error: err => this.searchResult$.next(State.Builder<Array<Account>>().forError(err))
    });
  }

  private fetchResult(searchQuery: SearchQuery): Observable<Array<Account>> {
    let params = createPaginationOption(searchQuery.page);
    params = params.set("query", searchQuery.query);
    return this.http.get<Array<Account>>(`${environment.apiUrl}/accounts/search`, {params});
  }

  search(searchQuery: SearchQuery): void {
    this.searchQuery$.next(searchQuery);
  }
}

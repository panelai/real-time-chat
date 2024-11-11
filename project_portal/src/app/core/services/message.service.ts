import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { environment } from '../../../enviroment/enviroment';
import { Message } from '../../model/type/message/message.type';
import { State } from '../../model/type/state.type';

@Injectable({
  providedIn: 'root'
})
export class MessageService {
  http = inject(HttpClient);

  private send$ = new Subject<State<void>>();
  send = this.send$.asObservable();

  handleSend(newMessage: Message): void {
    this.http.post<void>(`${environment.apiUrl}/messages/send`, newMessage)
      .subscribe({
        next: () => this.send$.next(State.Builder().forSuccessEmpty()),
        error: err => this.send$.next(State.Builder().forError(err))
      });
  }
}

import { Injectable } from '@angular/core';
import { EventSourcePolyfill } from 'event-source-polyfill';
import dayjs from 'dayjs';
import { Subject, Subscription, interval } from 'rxjs';
import { environment } from '../../../enviroment/enviroment';
import { Message } from '../../model/type/message/message.type';

@Injectable({
  providedIn: 'root'
})
export class SseService {
  private sseEndpoint = `${environment.apiUrl}/sse/subscribe`;
  private eventSource: EventSource | undefined;

  private receiveNewMessage$ = new Subject<Message>();
  receiveNewMessage = this.receiveNewMessage$.asObservable();

  accessToken: string | undefined;

  private retryConnectionSubscription: Subscription | undefined;

  public subscribe(accessToken: string): void {
    this.accessToken = accessToken;
    this.eventSource = new EventSourcePolyfill(this.sseEndpoint, {
      headers: {
        "Authorization": `Bearer ${this.accessToken}`
      },
      heartbeatTimeout: 60000
    });

    this.eventSource!.onopen = ((event) => {
      console.log("Connection SSE to server OK", event);
      if (this.retryConnectionSubscription) {
        this.retryConnectionSubscription.unsubscribe();
      }
    });

    this.eventSource!.onerror = ((event) => {
      if (!this.retryConnectionSubscription) {
        console.log("Connection SSE lost, let's retry to connect");
        this.retryConnectionToSSEServer();
      }
    });

    this.eventSource!.onmessage = ((event) => {
      if (event.data.indexOf("{") !== -1) {
        const message: Message = JSON.parse(event.data);
        console.log(message);

        message.createdAt = dayjs(message.createdAt);
        this.receiveNewMessage$.next(message);
      }
    });
  }

  private retryConnectionToSSEServer() {
    this.retryConnectionSubscription = interval(10000)
      .subscribe(() => this.subscribe(this.accessToken!));
  }
}

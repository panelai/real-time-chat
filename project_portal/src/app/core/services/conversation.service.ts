import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { environment } from '../../../enviroment/enviroment';
import { State } from '../../model/type/state.type';
import dayjs from 'dayjs';
import { Conversation } from '../../model/type/conversation/conversation.type';
import { Account } from '../user/account.type';
import { ConversationType } from '../../model/enums/conversation/conversation-type.enum';
import { UserService } from '../user/user.service';


@Injectable({
  providedIn: 'root'
})
export class ConversationService {
  http = inject(HttpClient);
  userService = inject(UserService);

  private createOrLoadConversation$ = new Subject<string>();
  createOrLoadConversation = this.createOrLoadConversation$.asObservable();

  private navigateToConversation$ = new Subject<Conversation>();
  navigateToConversation = this.navigateToConversation$.asObservable();

  private create$ = new Subject<State<Conversation>>();
  create = this.create$.asObservable();

  private getAll$ = new Subject<State<Array<Conversation>>>();
  getAll = this.getAll$.asObservable();

  private getOneByPublicId$ = new Subject<State<Conversation>>();
  getOneByPublicId = this.getOneByPublicId$.asObservable();

  private connectedUser: Account | undefined;

  constructor() {
    this.connectedUser = this.userService.user;
  }

  handleNavigateToConversation(userPublicId: string) {
    this.createOrLoadConversation$.next(userPublicId);
  }

  handleGetAll() {
    //const params = createPaginationOption(pagination);
    this.http.get<Array<Conversation>>(`${environment.apiUrl}/conversations`)
      .subscribe({
        next: conversations => {
          this.mapDateToDayJsList(conversations);
          this.sortMessageByTime(conversations);
          this.mapConversationTitleList(conversations);
          this.sortConversationByLastMessage(conversations);
          // console.log(conversations);

          this.getAll$.next(State.Builder<Array<Conversation>>().forSuccess(conversations));
        },
        error: err => this.getAll$.next(State.Builder<Array<Conversation>>().forError(err))
      });
  }

  private mapDateToDayJsList(conversations: Array<Conversation>) {
    conversations.forEach(conversation => this.mapDateToDayJs(conversation));
  }

  private mapDateToDayJs(conversation: Conversation) {
    if (conversation.messages) {
      conversation.messages.forEach(message => message.createdAt = dayjs(message.createdAt));
    }
  }

  private mapConversationTitleList(conversations: Array<Conversation>) {
    conversations.forEach(conversation => this.mapConversationTitle(conversation))
  }

  private mapConversationTitle(conversation: Conversation) {
    if(conversation.type === ConversationType.SINGLE) {
      var title = conversation.members.find(member => member.id != this.connectedUser?.id).username;
      conversation.title = title;
    }
  }

  sortConversationByLastMessage(conversations: Array<Conversation>) {
    conversations.sort((conversationA, conversationB) => {
      if (conversationA.messages && conversationB.messages
        && conversationB.messages.length != 0 && conversationA.messages.length != 0) {
        return conversationB.messages[conversationB.messages.length - 1].createdAt!.toDate().getTime()
          - conversationA.messages[conversationA.messages.length - 1].createdAt!.toDate().getTime()
      }
      else {
        return 0;
      }
    })
  }

  private sortMessageByTime(conversations: Array<Conversation>) {
    conversations.forEach(conversation => {
      conversation.messages?.sort((messageA, messageB) => {
        return messageA.createdAt!.toDate().getTime() - messageB.createdAt!.toDate().getTime();
      });
    });
  }

  handleCreate(conversationToCreate: Conversation) {
    this.http.post<Conversation>(`${environment.apiUrl}/conversations`, conversationToCreate)
      .subscribe({
        next: conversation => {
          this.mapDateToDayJs(conversation);
          this.mapConversationTitle(conversation);
          this.create$.next(State.Builder<Conversation>().forSuccess(conversation))
        },
        error: err => this.create$.next(State.Builder<Conversation>().forError(err))
      })
  }

  handleGetOne(conversationId: string) {
    const params = new HttpParams().set("conversationId", conversationId);
    this.http.get<Conversation>(`${environment.apiUrl}/conversations/get-one-by-public-id`,
      {params})
      .subscribe({
        next: conversation => {
          this.mapDateToDayJs(conversation);
          this.mapConversationTitle(conversation);
          this.getOneByPublicId$.next(State.Builder<Conversation>().forSuccess(conversation))
        },
        error: err => this.getOneByPublicId$.next(State.Builder<Conversation>().forError(err))
      });
  }

  navigateToNewConversation(conversation: Conversation) {
    this.navigateToConversation$.next(conversation);
  }

  getReceiverMember(conversation: Conversation): Array<Account> {
    return conversation.members.filter(member => member.id !== this.connectedUser?.id)!;
  }
}

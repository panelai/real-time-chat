import { Component, effect, ElementRef, inject, OnInit, ViewChild } from '@angular/core';
import dayjs from 'dayjs';
import { filter, timer } from 'rxjs';
import { ConversationService } from '../../../core/services/conversation.service';
import { SseService } from '../../../core/services/sse.service';
import { Account, ConnectedUser } from '../../../core/user/account.type';
import { Conversation } from '../../../model/type/conversation/conversation.type';
import { Message } from '../../../model/type/message/message.type';
import { DatePipe, UpperCasePipe } from '@angular/common';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { UserService } from '../../../core/user/user.service';

@Component({
  selector: 'app-message',
  standalone: true,
  imports: [UpperCasePipe, FaIconComponent, DatePipe],
  templateUrl: './message.component.html',
  styleUrl: './message.component.less'
})
export class MessageComponent implements OnInit{
  @ViewChild("messages") private messagesElement: ElementRef | undefined;

  sseService = inject(SseService);
  userService = inject(UserService);
  conversationService = inject(ConversationService);

  private connectedUser: Account | undefined;
  conversation: Conversation | undefined;

  messagesByDate = new Map<string, Array<Message>>();

  hasInitBottomScroll = false;

  constructor() {
    this.connectedUser = this.userService.user;
  }

  ngOnInit(): void {
    this.listenToNewMessage();
    this.listenToNavigateToConversation();
  }

  private listenToNewMessage(): void {
    this.sseService.receiveNewMessage.pipe(
      filter(newMessage => this.conversation?.id === newMessage.conversationId)
    ).subscribe(
      newMessage => {
        console.log(newMessage);

        this.pushMessageWithKey(newMessage, "Today");
        this.triggerScroll();
      }
    )
  }

  private triggerScroll() {
    this.hasInitBottomScroll = false;
    timer(1).subscribe(() => this.scrollToBottom());
  }

  private scrollToBottom() {
    if (this.messagesElement && !this.hasInitBottomScroll) {
      this.messagesElement!.nativeElement.scrollTop = this.messagesElement
        .nativeElement.scrollHeight;
      this.hasInitBottomScroll = true;
    }
  }

  private listenToNavigateToConversation(): void {
    this.conversationService.navigateToConversation.subscribe(conversation => {
        this.conversation = conversation;
        this.organizeMessageByDate();
        this.triggerScroll();
      })
  }

  private organizeMessageByDate() {
    this.messagesByDate = new Map<string, Array<Message>>();
    if (this.conversation.messages) {
      for (let message of this.conversation!.messages) {
        if (message.createdAt) {
          if (message.createdAt.isSame(dayjs(), 'day')) {
            this.pushMessageWithKey(message, 'Today');
          }
          else if (message.createdAt.isSame(dayjs().subtract(1, 'day'), 'day')) {
            this.pushMessageWithKey(message, 'Yesterday');
          }
          else {
            this.pushMessageWithKey(
              message,
              message.createdAt.format('DD/MM/YYYY')
            );
          }
        }
      }
    }
  }

  private pushMessageWithKey(message: Message, key: string) {
    if (this.messagesByDate.has(key)) {
      this.messagesByDate.get(key)?.push(message);
    } else {
      const messages = new Array<Message>();
      messages.push(message);
      this.messagesByDate.set(key, messages);
    }
  }

  public isMessageFromContact(senderId: string): boolean {
    return this.connectedUser?.id !== senderId;
  }
}

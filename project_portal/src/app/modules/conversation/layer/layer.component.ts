import { Component, effect, inject, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
// import { ConnectedUser } from '../../../core/user/account.type';
import { Conversation } from '../../../model/type/conversation/conversation.type';
import { Message } from '../../../model/type/message/message.type';
import { SseService } from '../../../core/services/sse.service';
import { AuthService } from '../../../core/auth/auth.service';
import { MessageService } from '../../../core/services/message.service';
import { ConversationService } from '../../../core/services/conversation.service';
import { ConversationComponent } from '../conversation/conversation.component';
import { ConversationType } from '../../../model/enums/conversation/conversation-type.enum';

@Component({
  selector: 'app-layer',
  standalone: true,
  imports: [ConversationComponent],
  templateUrl: './layer.component.html',
  styleUrl: './layer.component.less'
})
export class LayerComponent implements OnInit, OnDestroy{
  sseService = inject(SseService);
  authService = inject(AuthService);
  messageService = inject(MessageService);
  conversationService = inject(ConversationService);

  conversations = new Array<Conversation>();
  selectedConversation: Conversation | undefined;

  private createOrLoadConversation: Subscription | undefined;
  private createSub: Subscription | undefined;
  private getAllSub: Subscription | undefined;
  private getOneByPublicIdSub: Subscription | undefined;

  constructor() {
    this.conversationService.handleGetAll();
  }

  ngOnDestroy(): void {
    if (this.createOrLoadConversation) {
      this.createOrLoadConversation.unsubscribe();
    }

    if (this.createSub) {
      this.createSub.unsubscribe();
    }

    if (this.getAllSub) {
      this.getAllSub.unsubscribe();
    }

    if (this.getOneByPublicIdSub) {
      this.getOneByPublicIdSub.unsubscribe();
    }
  }

  ngOnInit(): void {
    this.listenToGetAllConversation();
    this.listenToGetOneByGuId();
    this.listenToConversationCreated();
    this.listenToNavigateToConversation();
    this.listenToSSENewMessage();
  }

  private listenToGetAllConversation(): void {
    this.getAllSub = this.conversationService.getAll
      .subscribe(conversationsState => {
        if (conversationsState.status === "OK" && conversationsState.value) {
          this.conversations = conversationsState.value;
        } else {
          //this.toastService.show("Error occurred when fetching conversations", "DANGER");
        }
      });
  }

  private listenToGetOneByGuId(): void {
    this.getOneByPublicIdSub = this.conversationService.getOneByPublicId
      .subscribe(conversationState => {
        if (conversationState.status === "OK" && conversationState.value) {
          this.conversations.push(conversationState.value);
        } else {
          //this.toastService.show("Error occurred when fetching conversation", "DANGER");
        }
      })
  }

  private listenToConversationCreated(): void {
    this.createSub = this.conversationService.create
      .subscribe(newConversationState => {
        if (newConversationState.status === "OK" && newConversationState.value) {
          this.conversations.push(newConversationState.value);
          this.conversationService.navigateToNewConversation(newConversationState.value);
        } else {
          //this.toastService.show("Error occurred when conversation creation", "DANGER");
        }
      });
  }

  private listenToNavigateToConversation(): void {
    this.createOrLoadConversation = this.conversationService.createOrLoadConversation
      .subscribe(userId => {
        const existingConversation = this.conversations.find(conversation => conversation.memberIds
                                                       .findIndex(memberId => memberId === userId) !== -1);
        if (existingConversation) {
          this.conversationService.navigateToNewConversation(existingConversation);
        }
        else {
          const newConversation: Conversation = {
            type: ConversationType.SINGLE,
            memberIds: [userId],
          }
          this.conversationService.handleCreate(newConversation)
        }
      });
  }

  onSelectConversation(conversation: Conversation) {
    if (this.selectedConversation) {
      this.selectedConversation.isActived = false;
    }
    this.selectedConversation = conversation;
    this.selectedConversation.isActived = true;
    this.conversationService.navigateToNewConversation(conversation);
  }

  private listenToSSENewMessage(): void {
    this.sseService.receiveNewMessage.subscribe(newMessage => {
      const indexToUpdate = this.conversations.findIndex(conversation => conversation.id === newMessage.conversationId);
      if (indexToUpdate === -1) {
        this.conversationService.handleGetOne(newMessage.conversationId);
      } else {
        const conversationToUpdate = this.conversations[indexToUpdate];
        if (!conversationToUpdate.messages) {
          conversationToUpdate.messages = new Array<Message>();
        }
        conversationToUpdate.messages.push(newMessage);
      }
      this.conversationService.sortConversationByLastMessage(this.conversations);
    });
  }
}

import { Component, inject } from '@angular/core';
import { Subscription } from 'rxjs';
import { ConversationService } from '../../../core/services/conversation.service';
import { MessageService } from '../../../core/services/message.service';
import { Conversation } from '../../../model/type/conversation/conversation.type';
import { Message } from '../../../model/type/message/message.type';
import { FormsModule } from '@angular/forms';
import { ClickOutsideModule } from 'ng-click-outside';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';


@Component({
  selector: 'app-send',
  standalone: true,
  imports: [ClickOutsideModule, FaIconComponent, FormsModule],
  templateUrl: './send.component.html',
  styleUrl: './send.component.less'
})
export class SendComponent {
  message = "";
  conversation: Conversation | undefined;

  messageService = inject(MessageService);
  conversationService = inject(ConversationService);

  private sendSub: Subscription | undefined;
  private navigateToConversationSub: Subscription | undefined;

  ngOnDestroy(): void {
    if (this.sendSub) {
      this.sendSub.unsubscribe();
    }

    if (this.navigateToConversationSub) {
      this.navigateToConversationSub.unsubscribe();
    }
  }

  ngOnInit(): void {
    this.listenToNavigateToConversation();
  }

  private listenToNavigateToConversation(): void {
    this.conversationService.navigateToConversation.subscribe(conversation => {
      this.conversation = conversation;
    })
  }

  onEnter(): void {
    this.sendMessage(this.message);
  }

  public sendMessage(message: string) {
    if (message !== "") {
      const newMessage: Message = {
        conversationId: this.conversation?.id!,
        message: message,
      }
      this.messageService.handleSend(newMessage);
      this.message = "";
    }
  }

  onClickEmojis(){
  }

  onClickMedia() {
  }

  closePanel(): void {
  }
}

import { Component, EventEmitter, inject, input, Output } from '@angular/core';
import { ConversationService } from '../../../core/services/conversation.service';
import { Account } from '../../../core/user/account.type';
import { Conversation } from '../../../model/type/conversation/conversation.type';
import { Message } from '../../../model/type/message/message.type';
import dayjs from "dayjs";
import {FaIconComponent} from "@fortawesome/angular-fontawesome";
import {NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle} from "@ng-bootstrap/ng-bootstrap";

@Component({
  selector: 'app-conversation',
  standalone: true,
  imports: [FaIconComponent, NgbDropdown, NgbDropdownToggle, NgbDropdownItem, NgbDropdownMenu],
  templateUrl: './conversation.component.html',
  styleUrl: './conversation.component.less'
})
export class ConversationComponent{
  conversation = input.required<Conversation>();
  connectedUser = input.required<Account>();

  conversationService = inject(ConversationService);

  @Output() select = new EventEmitter<Conversation>();

  protected showMenu = false;

  showConversation() {
    this.select.emit(this.conversation());
  }

  constructor() {}

  computeTitle(): string {
      return this.conversation().title;
  }

  computeTime() {
    const lastMessage = this.getLastMessage();
    if (lastMessage) {
      return dayjs(lastMessage.createdAt).fromNow();
    } else {
      return "";
    }
  }

  getLastMessage(): Message | null {
    if (this.conversation().messages && this.conversation().messages.length > 0) {
      return this.conversation().messages[this.conversation().messages.length - 1];
    } else {
      return null;
    }
  }

  onDelete() {
    // this.delete.emit(this.conversation());
  }

  onMouseOver() {
    this.showMenu = true;
  }

  onMouseLeave() {
    this.showMenu = false;
  }
}

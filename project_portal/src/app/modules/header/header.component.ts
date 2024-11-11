import { Component, inject, OnInit } from '@angular/core';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { NgbDropdown, NgbDropdownToggle, NgbDropdownMenu, NgbDropdownItem } from '@ng-bootstrap/ng-bootstrap';
import dayjs from 'dayjs';
import { Subscription, interval, filter } from 'rxjs';
import { ConversationService } from '../../core/services/conversation.service';
import { SseService } from '../../core/services/sse.service';
import { ConnectedUser } from '../../core/user/account.type';
import { Conversation } from '../../model/type/conversation/conversation.type';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [FaIconComponent, NgbDropdown, NgbDropdownToggle, NgbDropdownMenu, NgbDropdownItem],
  templateUrl: './header.component.html',
  styleUrl: './header.component.less'
})
export class HeaderComponent implements OnInit{

  conversationService = inject(ConversationService);
  sseService = inject(SseService);

  conversation: Conversation | undefined;

  ngOnInit(): void {
    this.listenToNavigateToConversation();
  }

  private listenToNavigateToConversation(): void {
    this.conversationService.navigateToConversation
      .subscribe(conversation => {
        this.conversation = conversation;
      });
  }
}

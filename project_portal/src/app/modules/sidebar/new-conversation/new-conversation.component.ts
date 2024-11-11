import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FaIconComponent } from '@fortawesome/angular-fontawesome';
import { ConversationSelectorComponent } from '../conversation-selector/conversation-selector.component';
import { NgbActiveOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { Subscription } from 'rxjs';
import { Pagination } from '../../../model/type/pagination.type';
import { SearchQuery } from '../../../model/type/search-query.type';
import { Account } from '../../../core/user/account.type';
import { UserService } from '../../../core/user/user.service';
import { AccountService } from '../../../core/services/account.service';
import { ConversationService } from '../../../core/services/conversation.service';

@Component({
  selector: 'app-new-conversation',
  standalone: true,
  imports: [FaIconComponent, FormsModule, ConversationSelectorComponent],
  templateUrl: './new-conversation.component.html',
  styleUrl: './new-conversation.component.less'
})
export class NewConversationComponent {
  userService = inject(UserService);
  accountService = inject(AccountService);
  activeOffCanvas = inject(NgbActiveOffcanvas);
  conversationService = inject(ConversationService);

  public query = "";
  public usersResults = new Array<Account>();

  searchPage: Pagination = {
    page: 0,
    size: 20,
    sort: ["username", "ASC"]
  }

  public connectedUser: Account | undefined;

  loadingSearch = true;

  searchSubscription: Subscription | undefined;

  constructor() {
    this.connectedUser = this.userService.user;
  }

  ngOnInit(): void {
    this.initSearchResultListener();
  }

  ngOnDestroy(): void {
    if (this.searchSubscription) {
      this.searchSubscription.unsubscribe();
    }
  }

  initSearchResultListener(): void {
    this.searchSubscription = this.accountService.searchResult.subscribe((usersState) => {
      if (usersState.status === 'OK' && usersState.value) {

        this.usersResults = usersState.value.filter(account => account.id != this.connectedUser.id);
      } else if (usersState.status === 'ERROR') {
        //this.toastService.show("Error occured when fetching search result, please try again", "DANGER");
      }
      this.loadingSearch = false;
    });
    const searchQuery: SearchQuery = {
      query: "",
      page: this.searchPage
    }
    this.accountService.search(searchQuery);
  }

  onQueryChange(newQuery: string): void {
    this.loadingSearch = true;
    const searchQuery: SearchQuery = {
      query: newQuery,
      page: this.searchPage,
    }
    this.accountService.search(searchQuery);
  }

  onCloseNav() {
    this.activeOffCanvas.close();
  }

  handleConversation(userId: string): void {
    this.conversationService.handleNavigateToConversation(userId);
    this.activeOffCanvas.close();
  }
}

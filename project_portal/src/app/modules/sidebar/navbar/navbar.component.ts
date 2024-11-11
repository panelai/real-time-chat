import { Component, effect, inject, OnInit } from '@angular/core';
import { FaIconComponent } from "@fortawesome/angular-fontawesome";
import { NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle, NgbOffcanvas } from "@ng-bootstrap/ng-bootstrap";
import { AuthService } from '../../../core/auth/auth.service';
import { UserService } from '../../../core/user/user.service';
import { NewConversationComponent } from '../new-conversation/new-conversation.component';


@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [ FaIconComponent, NgbDropdown, NgbDropdownItem, NgbDropdownMenu, NgbDropdownToggle ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.less'
})
export class NavbarComponent implements OnInit{
  userService = inject(UserService);
  authService = inject(AuthService);
  offCanvasService = inject(NgbOffcanvas);

  public connectedUser: string | undefined;

  constructor() {}

  ngOnInit(): void {
    this.connectedUser = this.userService.user.username;
  }

  logout(): void {
    this.authService.signOut();
  }

  editProfile(): void {
    //this.oauth2Service.goToProfilePage();
  }

  openNewConversation() {
    this.offCanvasService.open(NewConversationComponent,
      {position: "start", container: "#main", panelClass: "offcanvas"})
  }
}

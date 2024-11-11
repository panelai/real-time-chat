import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterModule } from '@angular/router';
import { FaIconComponent, FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { NgbAccordionDirective, NgbAccordionItem, NgbAccordionHeader, NgbAccordionButton, NgbAccordionCollapse, NgbAccordionBody } from '@ng-bootstrap/ng-bootstrap';
import dayjs from 'dayjs';
import relativeTime from "dayjs/plugin/relativeTime";
import { AuthService } from '../../core/auth/auth.service';
import { Account } from '../../core/user/account.type';
import { fontAwesomeIcons } from '../../shared/font-awesome-icons';
import { LayerComponent } from '../conversation/layer/layer.component';
import { HeaderComponent } from '../header/header.component';
import { MessageComponent } from '../message/message/message.component';
import { SendComponent } from '../message/send/send.component';
import { NavbarComponent } from '../sidebar/navbar/navbar.component';
import { SseService } from '../../core/services/sse.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterModule,
    NavbarComponent, HeaderComponent, LayerComponent, MessageComponent, SendComponent,
    NgbAccordionDirective, NgbAccordionItem, NgbAccordionHeader, NgbAccordionButton, NgbAccordionCollapse, NgbAccordionBody, FaIconComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.less'
})
export class HomeComponent {
  protected readonly removeEventListener = removeEventListener;

  private sseService = inject(SseService);
  private faIconLibrary = inject(FaIconLibrary);

  constructor(private authService: AuthService) {
      //this.authService.user.subscribe(x => this.user = x);

  }

  ngOnInit(): void {
    this.initFontAwesome();
    this.configDayJs();

    if(this.authService.accessToken) {
      this.sseService.subscribe(this.authService.accessToken!);
    }
  }

  private initFontAwesome() {
    this.faIconLibrary.addIcons(...fontAwesomeIcons);
  }

  private configDayJs() {
    dayjs.extend(relativeTime);
  }
}

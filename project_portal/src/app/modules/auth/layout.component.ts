import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-layout',
  //standalone: true,
  //imports: [],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.less'
})
export class LayoutComponent {
  constructor(
    private router: Router,
    private authService: AuthService
  ) {
    // redirect to home if already logged in
    if (this.authService._authenticated) {
        this.router.navigate(['/']);
    }
  }
}

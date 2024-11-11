import { Route } from '@angular/router';
import { AuthGuard } from './core/auth/guards/auth.guard';
import { HomeComponent } from './modules/home/home.component';

export const routes: Route[] = [
  { path: 'account', loadChildren: () => import('./modules/auth/auth.module')
    .then(m => m.AuthModule)
  },
  {path: '', pathMatch : 'full', redirectTo: '/account/login'},
  {
    path: 'home',
      canActivate: [AuthGuard],
      canActivateChild: [AuthGuard],
      component: HomeComponent
  }
];


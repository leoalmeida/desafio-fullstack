import { Routes } from '@angular/router';
import { BeneficioList } from './components/beneficio-list/beneficio-list';
import { BeneficioDetails } from './components/beneficio-details/beneficio-details';
import { LoginPage } from './components/login/login-page';
import { canActivateUser } from './guards/can-activate-user';

export const routes: Routes = [
  {
      path: 'login',
      component: LoginPage,
      data: { title: 'Login' }
  },{
    path: 'beneficios',
    component: BeneficioList,
    data: { title: 'Benefícios' },
    canActivate: [canActivateUser]
  },
  {
    path: 'beneficios/:type',
    component: BeneficioDetails,
    data: { title: 'Benefício', detail: true },
    canActivate: [canActivateUser]
  },
  { path: '**', redirectTo: 'login' }
];

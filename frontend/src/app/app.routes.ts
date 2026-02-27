import { Routes } from '@angular/router';
import { BeneficioList } from './beneficios/beneficio-list/beneficio-list';
import { BeneficioDetails } from './beneficios/beneficio-details/beneficio-details';
import { LoginPage } from './login/login-page';
import { canActivateUser } from './can-activate-user';

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

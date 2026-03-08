import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { inject } from '@angular/core';
import { TokenStorageService } from '../services/token-storage.service';

export const canActivateAdmin: CanActivateFn = (
  route: ActivatedRouteSnapshot,
  state: RouterStateSnapshot,
) => {
  const router = inject(Router);
  const tokenService = inject(TokenStorageService);
  return tokenService.isAuthenticated() && tokenService.hasRole('ROLE_ADMIN')
    ? true
    : router.parseUrl('/acesso-negado');
};

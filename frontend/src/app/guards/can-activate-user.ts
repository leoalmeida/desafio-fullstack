import { ActivatedRouteSnapshot, CanActivateFn, RouterStateSnapshot } from '@angular/router';
import { inject } from '@angular/core';
import { TokenStorageService } from '../services/token-storage.service';

export const canActivateUser: CanActivateFn = (
                    route: ActivatedRouteSnapshot, 
                    state: RouterStateSnapshot) => {
  const tokenService = inject(TokenStorageService);
  return tokenService.isAuthenticated();
};

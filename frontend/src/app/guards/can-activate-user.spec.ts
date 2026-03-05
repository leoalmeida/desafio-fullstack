import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, CanActivateFn, RouterStateSnapshot, UrlTree, provideRouter } from '@angular/router';
import { TokenStorageService } from '../services/token-storage.service';
import { signal } from '@angular/core';

import { canActivateUser } from './can-activate-user';

describe('canActivateUser', () => {
  let tokenStorageSpy: jasmine.SpyObj<TokenStorageService>;
  const isAuthenticatedSignal = signal(false);

  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => canActivateUser(...guardParameters));

  beforeEach(() => {
    const spy = jasmine.createSpyObj('TokenStorageService', [], {
      isAuthenticated: isAuthenticatedSignal.asReadonly()
    });

    TestBed.configureTestingModule({
      providers: [
        { provide: TokenStorageService, useValue: spy },
        provideRouter([])
      ]
    });

    tokenStorageSpy = TestBed.inject(TokenStorageService) as jasmine.SpyObj<TokenStorageService>;
  });

  it('deve ser criado', () => {
    expect(executeGuard).toBeTruthy();
  });

  it('deve retornar true se o usuário estiver autenticado', () => {
    isAuthenticatedSignal.set(true);
    const result = executeGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot);
    expect(result).toBeTrue();
  });

  it('deve redirecionar para /login se o usuário não estiver autenticado', () => {
    isAuthenticatedSignal.set(false);
    const result = executeGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot);
    expect(result instanceof UrlTree).toBeTrue();
    expect((result as UrlTree).toString()).toBe('/login');
  });
});

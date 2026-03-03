import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { TokenStorageService } from '../services/token-storage.service';
import { signal } from '@angular/core';

import { canActivateAdmin } from './can-activate-admin';

describe('canActivateAdmin', () => {
  let tokenStorageSpy: jasmine.SpyObj<TokenStorageService>;
  let routerSpy: jasmine.SpyObj<Router>;
  const isAuthenticatedSignal = signal(false);

  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => canActivateAdmin(...guardParameters));

  beforeEach(() => {
    const spy = jasmine.createSpyObj('TokenStorageService', ['hasRole'], {
      isAuthenticated: isAuthenticatedSignal.asReadonly()
    });
    routerSpy = jasmine.createSpyObj('Router', ['parseUrl']);

    TestBed.configureTestingModule({
      providers: [
        { provide: TokenStorageService, useValue: spy },
        { provide: Router, useValue: routerSpy }
      ]
    });

    tokenStorageSpy = TestBed.inject(TokenStorageService) as jasmine.SpyObj<TokenStorageService>;
  });

  it('deve ser criado', () => {
    expect(executeGuard).toBeTruthy();
  });

  it('deve retornar true se o usuário estiver autenticado e for ADMIN', () => {
    isAuthenticatedSignal.set(true);
    tokenStorageSpy.hasRole.and.returnValue(true);
    
    const result = executeGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot);
    expect(result).toBeTrue();
  });

  it('deve redirecionar para acesso-negado se autenticado mas não for ADMIN', () => {
    isAuthenticatedSignal.set(true);
    tokenStorageSpy.hasRole.and.returnValue(false);
    const mockUrlTree = {} as UrlTree;
    routerSpy.parseUrl.and.returnValue(mockUrlTree);

    const result = executeGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot);
    expect(result).toBe(mockUrlTree);
    expect(routerSpy.parseUrl).toHaveBeenCalledWith('/acesso-negado');
  });

  it('deve redirecionar para acesso-negado se não estiver autenticado', () => {
    isAuthenticatedSignal.set(false);
    routerSpy.parseUrl.and.returnValue({} as UrlTree);
    executeGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot);
    expect(routerSpy.parseUrl).toHaveBeenCalledWith('/acesso-negado');
  });
});

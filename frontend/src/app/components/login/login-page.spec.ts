import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginPage } from './login-page';
import { AuthService } from '../../services/auth.service';
import { TokenStorageService } from '../../services/token-storage.service';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { Signal, signal } from '@angular/core';

describe('LoginPage', () => {
  let component: LoginPage;
  let fixture: ComponentFixture<LoginPage>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let tokenStorageSpy: jasmine.SpyObj<TokenStorageService>;
  let routerSpy: jasmine.SpyObj<Router>;

  const isAuthenticatedSignal = signal(false);

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);
    tokenStorageSpy = jasmine.createSpyObj('TokenStorageService', ['isAuthenticated']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    // Mock do signal isAuthenticated
    tokenStorageSpy.isAuthenticated.and.returnValue(isAuthenticatedSignal.asReadonly() as any);

    await TestBed.configureTestingModule({
      imports: [LoginPage, ReactiveFormsModule, NoopAnimationsModule],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: TokenStorageService, useValue: tokenStorageSpy },
        { provide: Router, useValue: routerSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoginPage);
    component = fixture.componentInstance;
    isAuthenticatedSignal.set(false);
    fixture.detectChanges();
  });

  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve inicializar o formulário vazio e inválido', () => {
    expect(component.loginForm.valid).toBeFalse();
    expect(component.loginForm.value).toEqual({ username: '', password: '' });
  });

  it('deve validar campos obrigatórios', () => {
    const usernameControl = component.loginForm.get('username');
    const passwordControl = component.loginForm.get('password');

    usernameControl?.setValue('');
    passwordControl?.setValue('');
    
    expect(usernameControl?.errors?.['required']).toBeTruthy();
    expect(passwordControl?.errors?.['required']).toBeTruthy();
  });

  it('deve atualizar a mensagem de erro quando o formulário for inválido', () => {
    component.loginForm.get('username')?.setValue('');
    component.updateErrorMessage();
    expect(component.errorMessage()).toBe('Dados inválidos.');
  });

  it('deve chamar o serviço de login e navegar se bem-sucedido', () => {
    const mockUser = { id: 1, nome: 'Teste', accessToken: 'token' };
    authServiceSpy.login.and.returnValue(of(mockUser as any));
    
    // Simula que após o login o estado muda para autenticado
    tokenStorageSpy.isAuthenticated.and.callFake(signal(true) as any);

    component.loginForm.setValue({ username: 'user', password: '123' });
    component.login();

    expect(authServiceSpy.login).toHaveBeenCalledWith('user', '123');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/beneficios']);
  });

  it('não deve chamar o serviço de login se o formulário for inválido', () => {
    component.loginForm.setValue({ username: '', password: '' });
    component.login();
    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  it('deve redirecionar no ngOnInit se já estiver autenticado', () => {
    tokenStorageSpy.isAuthenticated.and.returnValue(true as any);
    component.ngOnInit();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/beneficios']);
  });

  it('deve limpar a mensagem de erro quando o formulário se tornar válido', () => {
    component.loginForm.setValue({ username: 'user', password: '123' });
    // O merge no constructor deve disparar o updateErrorMessage
    fixture.detectChanges();
    
    component.updateErrorMessage();
    expect(component.errorMessage()).toBe('');
  });
});


import { ComponentFixture, TestBed } from '@angular/core/testing';
import { LoginPage } from './login-page';
import { AuthService } from '../../services/auth.service';
import { TokenStorageService } from '../../services/token-storage.service';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';

describe('LoginPage', () => {
  let component: LoginPage;
  let fixture: ComponentFixture<LoginPage>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let tokenStorageSpy: jasmine.SpyObj<TokenStorageService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj('AuthService', ['login']);
    tokenStorageSpy = jasmine.createSpyObj('TokenStorageService', ['isAuthenticated']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    tokenStorageSpy.isAuthenticated.and.returnValue(false);

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
    fixture.detectChanges();
  });

  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve inicializar o formulário vazio e inválido', () => {
    expect(component.formLogin.valid).toBeFalse();
    expect(component.formLogin.value).toEqual({ username: '', password: '' });
  });

  it('deve validar campos obrigatórios', () => {
    const usernameControl = component.formLogin.get('username');
    const passwordControl = component.formLogin.get('password');

    usernameControl?.setValue('');
    passwordControl?.setValue('');
    
    expect(usernameControl?.errors?.['required']).toBeTruthy();
    expect(passwordControl?.errors?.['required']).toBeTruthy();
  });

  it('deve chamar o serviço de login e navegar se bem-sucedido', () => {
    const mockUser = { id: 1, nome: 'Teste', username: 'teste' };
    authServiceSpy.login.and.returnValue(of(mockUser as any));

    component.formLogin.setValue({ username: 'user', password: '123' });
    component.onSubmit();

    expect(authServiceSpy.login).toHaveBeenCalledWith('user', '123');
  });

  it('não deve chamar o serviço de login se o formulário for inválido', () => {
    component.formLogin.setValue({ username: '', password: '' });
    component.onSubmit();
    expect(authServiceSpy.login).not.toHaveBeenCalled();
  });

  it('deve redirecionar no ngOnInit se já estiver autenticado', () => {
    tokenStorageSpy.isAuthenticated.and.returnValue(true);
    component.ngOnInit();
    expect(routerSpy.navigate).toHaveBeenCalledWith(['beneficios']);
  });

  it('deve atualizar isLoggedIn no reloadPage quando não autenticado', () => {
    tokenStorageSpy.isAuthenticated.and.returnValue(false);

    component.reloadPage();

    expect(component.isLoggedIn()).toBeFalse();
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });
});


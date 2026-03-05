import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { TokenStorageService } from './token-storage.service';
import { AssociadoType } from '../models/associado-type';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let tokenStorageSpy: jasmine.SpyObj<TokenStorageService>;

  const mockUser: AssociadoType = {
    id: 1,
    nome: 'João Silva',
    username: 'joao',
    email: 'joao@teste.com',
    telefone: '1199999999',
    accessToken: 'abc.def.ghi',
    stats: [],
    logs: []
  };

  beforeEach(() => {
    const spy = jasmine.createSpyObj('TokenStorageService', ['saveJsonWebToken', 'signOut']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        AuthService,
        { provide: TokenStorageService, useValue: spy }
      ]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    tokenStorageSpy = TestBed.inject(TokenStorageService) as jasmine.SpyObj<TokenStorageService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve ser criado', () => {
    expect(service).toBeTruthy();
  });

  it('deve realizar login e salvar o token', (done) => {
    service.login('jrrtolk', '123').subscribe(user => {
      expect(user).toBeDefined();
      expect(tokenStorageSpy.saveJsonWebToken).toHaveBeenCalled();
      done();
    });
  });

  it('deve realizar logout e limpar o armazenamento', () => {
    service.logout();
    expect(tokenStorageSpy.signOut).toHaveBeenCalled();
  });

  it('deve registrar um novo usuário', (done) => {
    const newUser = {
      username: 'Maria',
      nome: 'Maria',
      email: 'maria@teste.com',
      telefone: '1188888888',
      password: '100'
    };

    service.register(newUser.username, newUser.nome, newUser.email, newUser.telefone, newUser.password)
      .subscribe(user => {
        expect(user.nome).toBe(newUser.nome);
        expect(user.email).toBe(newUser.email);
        done();
      });
  });

  it('deve emitir o usuário logado através do observable loggedUser$', (done) => {
    service.login('jrrtolk', '123');
    service.loggedUser$.subscribe(user => {
      expect(user).toBeDefined();
      done();
    });
  });
});

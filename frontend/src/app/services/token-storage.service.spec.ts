import { TestBed } from '@angular/core/testing';
import { TokenStorageService } from './token-storage.service';
import { AssociadoType } from '../models/associado-type';

describe('TokenStorageService', () => {
  let service: TokenStorageService;

  // Payload base64 para {"id":1,"sub":"joao@teste.com","username":"joao","roles":["ROLE_USER"],"permissions":[],"iat":0,"exp":0}
  const mockToken = 'header.eyJpZCI6MSwic3ViIjoiam9hb0B0ZXN0ZS5jb20iLCJ1c2VybmFtZSI6ImpvYW8iLCJyb2xlcyI6WyJST0xFX1VTRVIiXSwicGVybWlzc2lvbnMiOltdLCJpYXQiOjAsImV4cCI6MH0.signature';
  const mockUsername = 'João Silva';
  const mockEmail = 'joao@teste.com';
  const mockTelefone = '1199999999';

  const mockUser: AssociadoType = {
    id: 1,
    nome: mockUsername,
    username: 'joao',
    email: mockEmail,
    telefone: mockTelefone,
    accessToken: mockToken,
    stats: [],
    logs: [],
    userData: { id: 1, sub: 'joao@teste.com', username: 'joao', roles: ['ROLE_USER'], permissions: [], exp: 0, iat: 0 }
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TokenStorageService);
    window.sessionStorage.clear();
  });

  it('deve ser criado', () => {
    expect(service).toBeTruthy();
  });

  it('deve salvar o token JWT e atualizar o estado de autenticação', (done) => {
    service.saveJsonWebToken(mockUser);

    expect(window.sessionStorage.getItem('user')).toBeTruthy();
    expect(service.isAuthenticated()).toBeTrue();
    
    service.loggedUser$.subscribe(user => {
      expect(user.id).toBe(1);
      expect(user.nome).toBe(mockUsername);
      done();
    });
  });

  it('deve salvar e recuperar o objeto de usuário', () => {
    service.saveUser(mockUser);
    const retrievedUser = service.getUser();
    
    expect(retrievedUser.nome).toBe(mockUser.nome);
    expect(retrievedUser.id).toBe(mockUser.id);
    expect(window.sessionStorage.getItem('user')).toBeTruthy();
  });

  it('deve retornar objeto vazio se não houver usuário no storage', () => {
    const user = service.getUser();
    expect(user).toEqual({} as AssociadoType);
  });

  it('deve limpar o storage e resetar o estado no signOut', (done) => {
    // Setup inicial
    service.saveJsonWebToken(mockUser);
    window.sessionStorage.setItem('user', JSON.stringify(mockUser));

    service.signOut();

    expect(window.sessionStorage.length).toBe(0);
    expect(service.isAuthenticated()).toBeFalse();
    
    service.loggedUser$.subscribe(user => {
      expect(user).toEqual({} as any);
      done();
    });
  });

  it('não deve fazer nada no saveJsonWebToken se o accessToken for vazio', () => {
    const spySet = spyOn(window.sessionStorage, 'setItem');
    
    service.saveJsonWebToken({ ...mockUser, accessToken: '' });
    
    expect(spySet).not.toHaveBeenCalled();
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('deve verificar se o usuário possui uma role específica', () => {
    service.saveJsonWebToken(mockUser);
    
    expect(service.hasRole('ROLE_USER')).toBeTrue();
    expect(service.hasRole('ROLE_ADMIN')).toBeFalse();
  });
});

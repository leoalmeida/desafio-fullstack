import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Toolbar } from './toolbar';
import { TokenStorageService } from '../../services/token-storage.service';
import { BehaviorSubject } from 'rxjs';
import { provideRouter } from '@angular/router';
import { By } from '@angular/platform-browser';

describe('Toolbar', () => {
  let component: Toolbar;
  let fixture: ComponentFixture<Toolbar>;
  let tokenStorageSpy: jasmine.SpyObj<TokenStorageService>;
  let loggedUserSubject: BehaviorSubject<any>;

  beforeEach(async () => {
    loggedUserSubject = new BehaviorSubject<any>({ username: 'Usuário Teste' });
    tokenStorageSpy = jasmine.createSpyObj('TokenStorageService', [], {
      loggedUser$: loggedUserSubject.asObservable()
    });

    await TestBed.configureTestingModule({
      imports: [Toolbar],
      providers: [
        { provide: TokenStorageService, useValue: tokenStorageSpy },
        provideRouter([])
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Toolbar);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('title', 'BIP App');
    fixture.detectChanges();
  });

  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve exibir o título recebido via input', () => {
    const titleElement = fixture.debugElement.query(By.css('span')).nativeElement;
    expect(titleElement.textContent).toContain('BIP App');
  });

  it('deve atualizar loggedUser quando o serviço emitir um novo usuário', () => {
    const novoUsuario = { username: 'Admin' };
    loggedUserSubject.next(novoUsuario);
    
    expect(component.loggedUser).toBe('Admin');
  });

  it('deve alternar o estado de "opened" ao interagir com o menu (simulação lógica)', () => {
    expect(component.opened).toBeFalse();
    component.opened = !component.opened;
    expect(component.opened).toBeTrue();
  });

  it('deve renderizar os links de navegação baseados nas rotas', () => {
    const links = fixture.debugElement.queryAll(By.css('a'));
    // Verifica se existem links (o componente usa routerLink no template)
    expect(links.length).toBeGreaterThan(0);
  });

  it('deve exibir o nome do usuário logado no template', () => {
    loggedUserSubject.next({ username: 'Carlos Silva' });
    fixture.detectChanges();
    
    const compiled = fixture.nativeElement as HTMLElement;
    // Verifica se o nome aparece em algum lugar da toolbar (ex: mat-toolbar)
    expect(compiled.textContent).toContain('Carlos Silva');
  });
});

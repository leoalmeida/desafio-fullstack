import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { TitleService } from './services/title.service';
import { provideRouter } from '@angular/router';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('AppComponent', () => {
  let fixture: ComponentFixture<AppComponent>;
  let component: AppComponent;
  let titleServiceSpy: jasmine.SpyObj<TitleService>;

  beforeEach(async () => {
    titleServiceSpy = jasmine.createSpyObj('TitleService', ['setTitle']);

    await TestBed.configureTestingModule({
      imports: [AppComponent, NoopAnimationsModule, HttpClientTestingModule],
      providers: [
        { provide: TitleService, useValue: titleServiceSpy },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  it('deve criar o app', () => {
    expect(component).toBeTruthy();
  });

  it('deve chamar setTitle do TitleService no ngOnInit', () => {
    fixture.detectChanges();
    expect(titleServiceSpy.setTitle).toHaveBeenCalled();
  });

  it('deve ter o título inicial como "Modulo Frontend"', () => {
    expect(component['title']()).toBe('Modulo Frontend');
  });

  it('deve atualizar showAdminBoard para true quando a role ADMIN estiver presente', () => {
    // @ts-ignore - acessando propriedade privada para teste
    component.roles = ['ADMIN', 'USER'];
    component.updateViewByRole();
    expect(component.showAdminBoard).toBeTrue();
    expect(component.showModeratorBoard).toBeFalse();
  });

  it('deve atualizar showModeratorBoard para true quando a role MODERATOR estiver presente', () => {
    // @ts-ignore
    component.roles = ['MODERATOR'];
    component.updateViewByRole();
    expect(component.showModeratorBoard).toBeTrue();
    expect(component.showAdminBoard).toBeFalse();
  });

  it('deve manter boards como false se as roles não estiverem presentes', () => {
    // @ts-ignore
    component.roles = ['USER'];
    component.updateViewByRole();
    expect(component.showAdminBoard).toBeFalse();
    expect(component.showModeratorBoard).toBeFalse();
  });
});

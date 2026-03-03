import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomePage } from './home-page';

describe('HomePage', () => {
  let component: HomePage;
  let fixture: ComponentFixture<HomePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomePage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomePage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('deve ter o título "Página Inicial"', () => {
    expect(component.title).toEqual('Página Inicial');
  });

  it('deve renderizar o título no template', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    // Assume-se que o título está em uma tag h1 ou similar no home-page.html
    // Se não houver tag específica, verificamos o conteúdo textual geral
    expect(compiled.textContent).toContain('Página Inicial');
  });
});

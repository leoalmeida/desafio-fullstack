import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { Statusbar } from './statusbar';

describe('Statusbar', () => {
  let component: Statusbar;
  let fixture: ComponentFixture<Statusbar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Statusbar]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Statusbar);
    component = fixture.componentInstance;
  });

  it('deve criar o componente', () => {
    fixture.componentRef.setInput('ativo', true);
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('deve exibir o texto "Ativo" e a classe CSS correta quando o input ativo for true', () => {
    fixture.componentRef.setInput('ativo', true);
    fixture.detectChanges();

    const spanElement = fixture.debugElement.query(By.css('span')).nativeElement;
    
    expect(spanElement.textContent).toContain('Ativo');
    expect(spanElement.classList).toContain('status-ativo');
  });

  it('deve exibir o texto "Cancelado" e a classe CSS correta quando o input ativo for false', () => {
    fixture.componentRef.setInput('ativo', false);
    fixture.detectChanges();

    const spanElement = fixture.debugElement.query(By.css('span')).nativeElement;
    
    expect(spanElement.textContent).toContain('Cancelado');
    expect(spanElement.classList).toContain('status-cancelado');
  });
});

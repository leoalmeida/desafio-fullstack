import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BeneficioCard } from './beneficio-card';

describe('BeneficioCard', () => {
  let component: BeneficioCard ;
  let fixture: ComponentFixture<BeneficioCard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BeneficioCard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BeneficioCard );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

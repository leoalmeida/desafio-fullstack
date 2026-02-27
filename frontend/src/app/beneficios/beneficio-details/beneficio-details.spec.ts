import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BeneficioDetails } from './beneficio-details';

describe('BeneficioDetails', () => {
  let component: BeneficioDetails;
  let fixture: ComponentFixture<BeneficioDetails>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BeneficioDetails]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BeneficioDetails);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BeneficioDetails } from './beneficio-details';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { BeneficioType } from '../../models/beneficio-type';
import { BeneficioService } from 'src/app/services/beneficio.service';
import { of } from 'rxjs';

describe('BeneficioDetails', () => {
  let component: BeneficioDetails;
  let fixture: ComponentFixture<BeneficioDetails>;
  let dialogRefSpy: jasmine.SpyObj<MatDialogRef<BeneficioDetails>>;
  let beneficioServiceSpy: jasmine.SpyObj<BeneficioService>;

  const mockBeneficio: BeneficioType = {
    id: 1,
    nome: 'Vale Refeição',
    descricao: 'Benefício de refeição diária',
    valor: 450.50,
    ativo: true
  };

  beforeEach(async () => {
    dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);
    beneficioServiceSpy = jasmine.createSpyObj('BeneficioService', ['createOne', 'changeOne']);
    beneficioServiceSpy.changeOne.and.returnValue(of(true));
    beneficioServiceSpy.createOne.and.returnValue(of(true));

    await TestBed.configureTestingModule({
      imports: [BeneficioDetails, ReactiveFormsModule, NoopAnimationsModule],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: MAT_DIALOG_DATA, useValue: mockBeneficio },
        { provide: BeneficioService, useValue: beneficioServiceSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BeneficioDetails);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve inicializar o formulário com os dados recebidos via MAT_DIALOG_DATA', () => {
    expect(component.formBeneficio.value).toEqual({
      nome: mockBeneficio.nome,
      descricao: mockBeneficio.descricao,
      valor: mockBeneficio.valor,
      ativo: mockBeneficio.ativo
    });
  });

  it('deve invalidar o formulário se campos obrigatórios estiverem vazios', () => {
    component.formBeneficio.controls['nome'].setValue('');
    component.formBeneficio.controls['valor'].setValue(null);
    
    expect(component.formBeneficio.valid).toBeFalse();
  });

  it('deve fechar o diálogo com true ao chamar onSubmit se válido', () => {
    const updatedValue = {
      nome: 'Novo Nome',
      descricao: 'Nova Descrição',
      valor: 600,
      ativo: false
    };
    component.formBeneficio.patchValue(updatedValue);

    component.onSubmit();

    expect(beneficioServiceSpy.changeOne).toHaveBeenCalled();
    expect(dialogRefSpy.close).toHaveBeenCalledWith(true);
  });

  it('não deve fechar o diálogo ao chamar onSubmit se o formulário for inválido', () => {
    component.formBeneficio.controls['nome'].setValue('');
    component.onSubmit();
    expect(dialogRefSpy.close).not.toHaveBeenCalled();
  });

  it('deve fechar o diálogo sem dados ao chamar onCancel', () => {
    component.onCancel();
    expect(dialogRefSpy.close).toHaveBeenCalledWith();
  });
});

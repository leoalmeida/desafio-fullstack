import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TransferDetails } from './transfer-details';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { BeneficioType } from 'src/app/models/beneficio-type';

describe('TransferDetails', () => {
  let component: TransferDetails;
  let fixture: ComponentFixture<TransferDetails>;
  let dialogRefSpy: jasmine.SpyObj<MatDialogRef<TransferDetails>>;

  const mockBeneficio: BeneficioType = {
    id: 1,
    nome: 'Vale Alimentação',
    descricao: 'VR',
    valor: 500,
    ativo: true
  };

  beforeEach(async () => {
    dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);

    await TestBed.configureTestingModule({
      imports: [TransferDetails, ReactiveFormsModule, NoopAnimationsModule],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: MAT_DIALOG_DATA, useValue: mockBeneficio }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TransferDetails);
    component = fixture.componentInstance;
  });

  it('deve criar o componente', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('deve inicializar o formulário com o ID do benefício de origem', () => {
    fixture.detectChanges();
    expect(component.formTransferencia.value.fromId).toBe(mockBeneficio.id);
    expect(component.formTransferencia.value.valor).toBe(0);
  });

  it('deve invalidar o formulário se o ID de destino (toId) não for preenchido', () => {
    fixture.detectChanges();
    component.formTransferencia.controls['toId'].setValue('');
    expect(component.formTransferencia.valid).toBeFalse();
  });

  it('deve fechar o diálogo com os dados da transferência ao chamar onSubmit se válido', () => {
    fixture.detectChanges();
    const transferData = {
      fromId: 1,
      toId: 2,
      valor: 150.50
    };
    component.formTransferencia.patchValue(transferData);

    component.onSubmit();

    expect(dialogRefSpy.close).toHaveBeenCalledWith(jasmine.objectContaining(transferData));
  });

  it('não deve fechar o diálogo ao chamar onSubmit se o formulário for inválido', () => {
    fixture.detectChanges();
    component.formTransferencia.controls['toId'].setValue('');
    
    component.onSubmit();
    
    expect(dialogRefSpy.close).not.toHaveBeenCalled();
  });

  it('deve fechar o diálogo sem dados ao chamar onCancel', () => {
    fixture.detectChanges();
    component.onCancel();
    expect(dialogRefSpy.close).toHaveBeenCalledWith();
  });
});

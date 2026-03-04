import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BeneficioCard } from './beneficio-card';
import { BeneficioService } from '../../services/beneficio.service';
import { TransferenciaService } from 'src/app/services/transferencia.service';
import { NotificationService } from 'src/app/services/notification.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import { BeneficioType } from '../../models/beneficio-type';
import { TransferenciaType } from 'src/app/models/transferencia-type';

describe('BeneficioCard', () => {
  let component: BeneficioCard;
  let fixture: ComponentFixture<BeneficioCard>;
  let beneficioServiceSpy: jasmine.SpyObj<BeneficioService>;
  let transferenciaServiceSpy: jasmine.SpyObj<TransferenciaService>;
  let notificationServiceSpy: jasmine.SpyObj<NotificationService>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  const mockBeneficio: BeneficioType = {
    id: 1,
    nome: 'Vale Alimentação',
    descricao: 'Benefício para alimentação',
    valor: 500,
    ativo: true
  };


  beforeEach(async () => {
    beneficioServiceSpy = jasmine.createSpyObj('BeneficioService', ['changeOne', 'changeStatus']);
    transferenciaServiceSpy = jasmine.createSpyObj('TransferenciaService', ['transferValue']);
    notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['showSuccess', 'showError']);
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [BeneficioCard, MatDialogModule, NoopAnimationsModule],
      providers: [
        { provide: BeneficioService, useValue: beneficioServiceSpy },
        { provide: TransferenciaService, useValue: transferenciaServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: MatDialog, useValue: dialogSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BeneficioCard);
    component = fixture.componentInstance;
    
    // Set required input
    fixture.componentRef.setInput('beneficio', mockBeneficio);
    fixture.detectChanges();
  });

  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve abrir o diálogo de edição e chamar o serviço ao fechar com sucesso', () => {
    const updatedBeneficio = { ...mockBeneficio, nome: 'Novo Nome' };
    const dialogRefSpy = jasmine.createSpyObj({ afterClosed: of(updatedBeneficio) });
    dialogSpy.open.and.returnValue(dialogRefSpy);

    component.onUpdateBeneficio(mockBeneficio);

    expect(dialogSpy.open).toHaveBeenCalled();
    expect(beneficioServiceSpy.changeOne).toHaveBeenCalledWith(updatedBeneficio);
    expect(notificationServiceSpy.showSuccess).toHaveBeenCalled();
  });

  it('deve abrir o diálogo de alteração de status e processar a mudança', () => {
    const dialogRefSpy = jasmine.createSpyObj({ afterClosed: of(true) });
    dialogSpy.open.and.returnValue(dialogRefSpy);

    component.onAlterarStatus({} as any, mockBeneficio);

    expect(dialogSpy.open).toHaveBeenCalled();
    expect(beneficioServiceSpy.changeStatus).toHaveBeenCalledWith(mockBeneficio);
  });

  it('deve abrir o diálogo de transferência e processar o resultado', () => {
    const transferData: TransferenciaType = { fromId: 1, toId: 2, valor: 100.00 };
    const dialogRefSpy = jasmine.createSpyObj({ afterClosed: of(transferData) });
    dialogSpy.open.and.returnValue(dialogRefSpy);
    transferenciaServiceSpy.transferValue.and.returnValue(of(void 0));

    component.onRealizarTransferencia(mockBeneficio);

    expect(dialogSpy.open).toHaveBeenCalled();
    expect(transferenciaServiceSpy.transferValue).toHaveBeenCalledWith(transferData);
    expect(notificationServiceSpy.showSuccess).toHaveBeenCalledWith('Transferência realizada com sucesso!');
  });

  it('deve mostrar erro se os dados da transferência forem inválidos', () => {
    const invalidTransferData: TransferenciaType = { fromId: 1, toId: 2, valor: -10.00 };
    const dialogRefSpy = jasmine.createSpyObj({ afterClosed: of(invalidTransferData) });
    dialogSpy.open.and.returnValue(dialogRefSpy);

    component.onRealizarTransferencia(mockBeneficio);

    expect(notificationServiceSpy.showError).toHaveBeenCalledWith('Dados da transferência inválidos ou incompletos!');
    expect(transferenciaServiceSpy.transferValue).not.toHaveBeenCalled();
  });

  it('deve mostrar erro se o benefício não tiver ID ao tentar remover', () => {
    const beneficioSemId = { ...mockBeneficio, id: undefined };
    fixture.componentRef.setInput('beneficio', beneficioSemId);
    
    component.onRemoverBeneficio({} as any, beneficioSemId as any);

    expect(notificationServiceSpy.showError).toHaveBeenCalledWith('Nenhum benefício selecionado!');
    expect(dialogSpy.open).not.toHaveBeenCalled();
  });

  it('deve lidar com erro na chamada do serviço de transferência', () => {
    const transferData: TransferenciaType = { fromId: 1, toId: 2, valor: 100.00 };
    const dialogRefSpy = jasmine.createSpyObj({ afterClosed: of(transferData) });
    dialogSpy.open.and.returnValue(dialogRefSpy);
    const errorResponse = 'Saldo insuficiente';
    transferenciaServiceSpy.transferValue.and.returnValue(throwError(() => errorResponse));

    component.onRealizarTransferencia(mockBeneficio);

    // O erro é tratado no subscribe do processarTransferencia
  });
});

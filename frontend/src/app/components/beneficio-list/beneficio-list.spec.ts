import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BeneficioList } from './beneficio-list';
import { BeneficioService } from '../../services/beneficio.service';
import { LoadingService } from '../loading-indicator/loading.service';
import { TokenStorageService } from '../../services/token-storage.service';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { signal } from '@angular/core';
import { BeneficioType } from '../../models/beneficio-type';
import { TokenType } from '../../models/token-type';

describe('BeneficioList', () => {
  let component: BeneficioList;
  let fixture: ComponentFixture<BeneficioList>;
  let beneficioServiceSpy: jasmine.SpyObj<BeneficioService>;
  let loadingServiceSpy: jasmine.SpyObj<LoadingService>;
  let tokenStorageServiceSpy: jasmine.SpyObj<TokenStorageService>;
  let dialogSpy: jasmine.SpyObj<MatDialog>;

  const mockBeneficios: BeneficioType[] = [
    { id: 1, nome: 'Vale Refeição', descricao: 'VR', valor: 100, ativo: true },
    { id: 2, nome: 'Plano de Saúde', descricao: 'PS', valor: 200, ativo: true }
  ];

  const mockUser: TokenType = {
    id: 1,
    sub: 'user@test.com',
    roles: ['USER'],
    permissions: [],
    iat: 123,
    exp: 456
  };

  beforeEach(async () => {
    beneficioServiceSpy = jasmine.createSpyObj('BeneficioService', ['getAll', 'createOne'], {
      items: signal(mockBeneficios)
    });
    loadingServiceSpy = jasmine.createSpyObj('LoadingService', ['loadingOn', 'loadingOff']);
    tokenStorageServiceSpy = jasmine.createSpyObj('TokenStorageService', [], {
      loggedUser$: of(mockUser)
    });
    dialogSpy = jasmine.createSpyObj('MatDialog', ['open']);

    await TestBed.configureTestingModule({
      imports: [BeneficioList, MatDialogModule, NoopAnimationsModule],
      providers: [
        { provide: BeneficioService, useValue: beneficioServiceSpy },
        { provide: LoadingService, useValue: loadingServiceSpy },
        { provide: TokenStorageService, useValue: tokenStorageServiceSpy },
        { provide: MatDialog, useValue: dialogSpy }
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BeneficioList);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve criar o componente e carregar dados iniciais', () => {
    expect(component).toBeTruthy();
    expect(beneficioServiceSpy.getAll).toHaveBeenCalled();
    expect(loadingServiceSpy.loadingOn).toHaveBeenCalled();
    expect(loadingServiceSpy.loadingOff).toHaveBeenCalled();
  });

  it('deve filtrar a lista de benefícios com base na searchQuery', () => {
    component.searchQuery.set('Refeição');
    fixture.detectChanges();
    
    const filtered = component.filteredBeneficioList();
    expect(filtered?.length).toBe(1);
    expect(filtered![0].nome).toBe('Vale Refeição');
  });

  it('deve normalizar a busca ignorando acentos e case', () => {
    component.searchQuery.set('refeicao'); // sem cedilha e sem acento
    fixture.detectChanges();
    
    const filtered = component.filteredBeneficioList();
    expect(filtered?.length).toBe(1);
    expect(filtered![0].nome).toBe('Vale Refeição');
  });

  it('deve atualizar searchQuery ao chamar handleMessage', () => {
    const query = 'nova busca';
    component.handleMessage(query);
    expect(component.searchQuery()).toBe(query);
  });

  it('deve abrir o diálogo de criação e chamar o serviço ao confirmar', () => {
    const novoBeneficio = { nome: 'Novo', valor: 50 };
    const dialogRefSpy = jasmine.createSpyObj({ afterClosed: of(novoBeneficio) });
    dialogSpy.open.and.returnValue(dialogRefSpy);

    component.onCreateBeneficio();

    expect(dialogSpy.open).toHaveBeenCalled();
    expect(beneficioServiceSpy.createOne).toHaveBeenCalledWith(novoBeneficio as any);
  });

  it('não deve chamar o serviço de criação se o diálogo for cancelado', () => {
    const dialogRefSpy = jasmine.createSpyObj({ afterClosed: of(null) });
    dialogSpy.open.and.returnValue(dialogRefSpy);

    component.onCreateBeneficio();

    expect(beneficioServiceSpy.createOne).not.toHaveBeenCalled();
  });
});

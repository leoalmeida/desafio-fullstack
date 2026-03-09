import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { BeneficioService } from './beneficio.service';
import { BeneficioType } from '../models/beneficio-type';
import { NotificationService } from './notification.service';
import { environment } from '../../environments/environment';

describe('BeneficioService', () => {
  let service: BeneficioService;
  let httpMock: HttpTestingController;
  let notificationSpy: jasmine.SpyObj<NotificationService>;

  const mockBeneficio: BeneficioType = {
    id: 1,
    nome: 'Vale Refeicao',
    descricao: 'VR',
    valor: 100,
    ativo: true,
  };

  beforeEach(() => {
    notificationSpy = jasmine.createSpyObj('NotificationService', [
      'showSuccess',
      'showError',
    ]);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        BeneficioService,
        { provide: NotificationService, useValue: notificationSpy },
      ],
    });

    service = TestBed.inject(BeneficioService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve ser criado', () => {
    expect(service).toBeTruthy();
  });

  it('deve buscar todos os beneficios (getAll) e atualizar a signal', () => {
    const mockList = [mockBeneficio];

    service.getAll();

    const req = httpMock.expectOne(environment.beneficiosApi);
    expect(req.request.method).toBe('GET');
    req.flush(mockList);

    expect(service.items()).toEqual(mockList);
    expect(notificationSpy.showSuccess).toHaveBeenCalled();
  });

  it('deve buscar todos os beneficios com retorno booleano (getAllAndReturn)', (done) => {
    service.getAllAndReturn().subscribe((result) => {
      expect(result).toBeTrue();
      expect(service.items().length).toBe(1);
      done();
    });

    const req = httpMock.expectOne(environment.beneficiosApi);
    expect(req.request.method).toBe('GET');
    req.flush([mockBeneficio]);
  });

  it('deve criar um novo beneficio (createOne)', (done) => {
    service.createOne(mockBeneficio).subscribe((result) => {
      expect(result).toBeTrue();
      expect(service.items().length).toBe(1);
      done();
    });

    const req = httpMock.expectOne(environment.beneficiosApi);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockBeneficio);
    req.flush(mockBeneficio);
  });

  it('deve atualizar um beneficio existente (changeOne)', (done) => {
    (service as any).beneficiosList.set([mockBeneficio]);
    const updated = { ...mockBeneficio, nome: 'VR Atualizado' };

    service.changeOne(updated).subscribe((result) => {
      expect(result).toBeTrue();
      expect(service.items()[0].nome).toBe('VR Atualizado');
      done();
    });

    const req = httpMock.expectOne(
      `${environment.beneficiosApi}/${mockBeneficio.id}`,
    );
    expect(req.request.method).toBe('PUT');
    req.flush(updated);
  });

  it('deve chamar endpoint ativar quando ativo for true', () => {
    service.changeStatus(mockBeneficio);

    const req = httpMock.expectOne(
      `${environment.beneficiosApi}/${mockBeneficio.id}/ativar`,
    );
    expect(req.request.method).toBe('PUT');
    req.flush(mockBeneficio);
  });

  it('deve chamar endpoint cancelar quando ativo for false', () => {
    const inativo = { ...mockBeneficio, ativo: false };

    service.changeStatus(inativo);

    const req = httpMock.expectOne(
      `${environment.beneficiosApi}/${inativo.id}/cancelar`,
    );
    expect(req.request.method).toBe('PUT');
    req.flush(inativo);
  });

  it('deve propagar erro em getOne', (done) => {
    service.getOne(99).subscribe({
      next: () => fail('nao deveria retornar sucesso'),
      error: (err: Error) => {
        expect(err.message).toContain('Erro interno');
        expect(notificationSpy.showError).toHaveBeenCalled();
        done();
      },
    });

    const req = httpMock.expectOne(`${environment.beneficiosApi}/associado/99`);
    expect(req.request.method).toBe('GET');
    req.flush(
      { message: 'Erro interno' },
      { status: 500, statusText: 'Server Error' },
    );
  });
});

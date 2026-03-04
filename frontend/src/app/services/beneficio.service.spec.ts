import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BeneficioService } from './beneficio.service';
import { LoggerService } from './logger.service';
import { BeneficioType } from '../models/beneficio-type';
import { environment } from '../../environments/environment';

describe('BeneficioService', () => {
  let service: BeneficioService;
  let httpMock: HttpTestingController;
  let loggerSpy: jasmine.SpyObj<LoggerService>;

  const mockBeneficio: BeneficioType = {
    id: 1,
    nome: 'Vale Refeição',
    descricao: 'VR',
    valor: 100,
    ativo: true
  };

  beforeEach(() => {
    const spy = jasmine.createSpyObj('LoggerService', ['log', 'error']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        BeneficioService,
        { provide: LoggerService, useValue: spy }
      ]
    });
    service = TestBed.inject(BeneficioService);
    httpMock = TestBed.inject(HttpTestingController);
    loggerSpy = TestBed.inject(LoggerService) as jasmine.SpyObj<LoggerService>;
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve ser criado', () => {
    expect(service).toBeTruthy();
  });

  it('deve buscar todos os benefícios (getAll)', () => {
    const mockList = [mockBeneficio];
    service.getAll().subscribe(res => {
      expect(res).toEqual(mockList);
    });

    const req = httpMock.expectOne(environment.beneficiosApi);
    expect(req.request.method).toBe('GET');
    req.flush(mockList);
  });

  it('deve criar um novo benefício (createOne)', () => {
    service.createOne(mockBeneficio).subscribe(res => {
      expect(res).toEqual(mockBeneficio);
    });

    const req = httpMock.expectOne(environment.beneficiosApi);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockBeneficio);
    req.flush(mockBeneficio);
    expect(loggerSpy.log).toHaveBeenCalled();
  });

  it('deve atualizar um benefício (changeOne)', () => {
    service.changeOne(mockBeneficio).subscribe(res => {
      expect(res).toEqual(mockBeneficio);
    });

    const req = httpMock.expectOne(`${environment.beneficiosApi}/${mockBeneficio.id}`);
    expect(req.request.method).toBe('PUT');
    req.flush(mockBeneficio);
  });

  it('deve alterar o status para cancelar quando ativo for true', () => {
    service.changeStatus(mockBeneficio).subscribe();

    const req = httpMock.expectOne(`${environment.beneficiosApi}/${mockBeneficio.id}/cancelar`);
    expect(req.request.method).toBe('PUT');
    req.flush({});
  });

  it('deve alterar o status para ativar quando ativo for false', () => {
    const inativo = { ...mockBeneficio, ativo: false };
    service.changeStatus(inativo).subscribe();

    const req = httpMock.expectOne(`${environment.beneficiosApi}/${inativo.id}/ativar`);
    expect(req.request.method).toBe('PUT');
    req.flush({});
  });

  it('deve remover um benefício (deleteOne)', () => {
    service.deleteOne(1).subscribe();

    const req = httpMock.expectOne(`${environment.beneficiosApi}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('deve lidar com erros de API', () => {
    const errorMessage = 'Erro interno do servidor';
    
    service.getAll().subscribe({
      error: (err) => {
        expect(err.message).toContain(errorMessage);
      }
    });

    const req = httpMock.expectOne(environment.beneficiosApi);
    req.flush({ message: errorMessage }, { status: 500, statusText: 'Server Error' });
  });
});

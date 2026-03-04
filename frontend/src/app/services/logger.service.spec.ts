import { TestBed } from '@angular/core/testing';
import { LoggerService } from './logger.service';

describe('LoggerService', () => {
  let service: LoggerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LoggerService]
    });
    service = TestBed.inject(LoggerService);
  });

  it('deve ser criado', () => {
    expect(service).toBeTruthy();
  });

  it('deve chamar console.log quando log() for invocado', () => {
    const spy = spyOn(console, 'log');
    const message = 'Teste de log';
    const extra = { data: 1 };
    
    service.log(message, extra);
    
    expect(spy).toHaveBeenCalledWith(message, extra);
  });

  it('deve chamar console.error quando error() for invocado', () => {
    const spy = spyOn(console, 'error');
    const message = 'Erro crítico';
    
    service.error(message);
    
    expect(spy).toHaveBeenCalledWith(message);
  });

  it('deve chamar console.warn quando warn() for invocado', () => {
    const spy = spyOn(console, 'warn');
    const message = 'Aviso de sistema';
    
    service.warn(message);
    
    expect(spy).toHaveBeenCalledWith(message);
  });
});

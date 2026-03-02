import { inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { BeneficioType } from '../models/beneficio-type';
import { TransferenciaType } from '../models/transferencia-type';
import { beneficios } from 'src/mocks/beneficios';

@Injectable({
  providedIn: 'root'
})
export class BeneficioService {
  private baseUrl:string = '/api/v1/beneficios';
  private beneficiosList = signal<BeneficioType[]>([]);

  //private logger = inject(Logger);

  private http: HttpClient = inject(HttpClient);
  constructor(){
    this.baseUrl = environment.beneficiosApi;
  }

  items = this.beneficiosList.asReadonly();

  getAll(): Observable<BeneficioType[]>{
    return this.http.get<BeneficioType[]>(`${this.baseUrl}`)
                  .pipe(catchError(this.handleError));
  }

  getOne(idAssociado:number): Observable<BeneficioType[]>{
    return this.http.get<BeneficioType[]>(`${this.baseUrl}/associado/${idAssociado}`)
                  .pipe(catchError(this.handleError));
  }

  getAllAtivos(): Observable<BeneficioType[]> {
    return this.http.get<BeneficioType[]>(`${this.baseUrl}/ativos`)
      .pipe(catchError(this.handleError));
  }

   //POST - "/"
  createOne(beneficio: BeneficioType): Observable<BeneficioType> {
    console.log(`Solicitando a criação de novo benefício: nome: ${beneficio.nome}.`);
    return this.http.post<BeneficioType>(`${this.baseUrl}`, beneficio)
              .pipe(catchError(this.handleError));

  }

  changeOne(beneficio: BeneficioType): Observable<BeneficioType> {
    console.log(`Cancelando benefício: ${beneficio.id}.`);
    return this.http.put<BeneficioType>(`${this.baseUrl}/${beneficio.id}`, beneficio)
              .pipe(catchError(this.handleError));
  }
  
  changeStatus(beneficio: BeneficioType): Observable<BeneficioType> {
    console.log(`Alterando status do benefício: ${beneficio.id} para ${beneficio.ativo ? 'cancelado' : 'ativo'}.`);
    return this.http.put<BeneficioType>(`${this.baseUrl}/${beneficio.id}/`+ (beneficio.ativo ? 'cancelar' : 'ativar'),{})
                  .pipe(catchError(this.handleError));  
  }

  deleteOne(idBeneficio: number): Observable<void> {
    console.log(`Removendo benefício: ${idBeneficio}.`);
    return this.http.delete<void>(`${this.baseUrl}/${idBeneficio}`)
              .pipe(catchError(this.handleError));

  }

  transferValue(transferencia: TransferenciaType): Observable<void> {
    console.log(`Transferindo valor ${transferencia.valor} de benefício: ${transferencia.fromId} para benefício: ${transferencia.toId}.`);
    return this.http.post<void>(`${this.baseUrl}/transferir`, transferencia)
              .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Erro desconhecido';

    if (error.error instanceof ErrorEvent) {
      // Erro do lado do cliente
      errorMessage = `Erro: ${error.error.message}`;
    } else {
      // Erro do lado do servidor
      errorMessage = error.error?.message ||
                    `Erro ${error.status}: ${error.statusText}`;
    }

    console.error('Erro na requisição:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}

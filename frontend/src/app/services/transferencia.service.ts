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
export class TransferenciaService {
  private baseUrl:string = '/api/v1/transferencias';
  private beneficiosList = signal<BeneficioType[]>([]);

  //private logger = inject(Logger);

  private http: HttpClient = inject(HttpClient);
  constructor(){
    this.baseUrl = environment.transferenciasApi;
  }

  items = this.beneficiosList.asReadonly();

  transferValue(transferencia: TransferenciaType): Observable<void> {
    console.log(`Transferindo valor ${transferencia.valor} de benefício: ${transferencia.fromId} para benefício: ${transferencia.toId}.`);
    return this.http.post<void>(`${this.baseUrl}`, transferencia)
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

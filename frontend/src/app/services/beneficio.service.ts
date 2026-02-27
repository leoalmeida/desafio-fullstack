import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { BeneficioType } from '../models/beneficio-type';
import { TransferenciaType } from '../models/transferencia-type';

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

  getAll(): void{
    this.http.get<BeneficioType[]>(`${this.baseUrl}`)
                  .subscribe(xs => this.beneficiosList.set(xs));
  }

  getOne(idAssociado:number): void{
    this.http.get<BeneficioType[]>(`${this.baseUrl}/associado/${idAssociado}`)
                  .subscribe(xs => this.beneficiosList.set(xs));
  }

   //POST - "/"
  createOne(beneficio: BeneficioType): boolean {
    console.log(`Solicitando a criação de novo benefício: nome: ${beneficio.nome}.`);
    this.http.post(`${this.baseUrl}`, beneficio);
    return true; 
  }

  changeOne(beneficio: BeneficioType): void {
    console.log(`Cancelando benefício: ${beneficio.id}.`);
    this.http.put(`${this.baseUrl}/${beneficio.id}`, beneficio).subscribe({
      next: (res) => console.log(`Benefício ${beneficio.id} atualizado com sucesso.`),
         error: (err) => console.log(err),
      });
  }
  
  changeStatus(beneficio: BeneficioType): void {
    console.log(`Alterando status do benefício: ${beneficio.id} para ${beneficio.ativo ? 'cancelado' : 'ativo'}.`);
    this.http.put(`${this.baseUrl}/${beneficio.id}/`+ (beneficio.ativo ? 'cancelar' : 'ativar'),{}).subscribe({
      next: (res) => console.log(`Status do benefício ${beneficio.id} alterado para ${beneficio.ativo ? 'ativo' : 'inativo'} com sucesso.`),
      error: (err) => console.log(err),
    });
  }

  deleteOne(idBeneficio: number): void {
    console.log(`Removendo benefício: ${idBeneficio}.`);
    this.http.delete(`${this.baseUrl}/${idBeneficio}`).subscribe({
      next: (res) => console.log(`Benefício ${idBeneficio} removido com sucesso.`),
      error: (err) => console.log(err),
    });
  }

  transferValue(transferencia: TransferenciaType): void {
    console.log(`Transferindo valor ${transferencia.valor} de benefício: ${transferencia.fromId} para benefício: ${transferencia.toId}.`);
    this.http.post(`${this.baseUrl}/transferir`, transferencia).subscribe({
      next: (res) => console.log(`Valor ${transferencia.valor} transferido para benefício ${transferencia.toId} com sucesso.`),
      error: (err) => console.log(err),
    });
  }
}

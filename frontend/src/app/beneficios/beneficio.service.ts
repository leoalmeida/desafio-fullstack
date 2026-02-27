import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { BeneficioType } from './beneficio-type';

@Injectable({
  providedIn: 'root'
})
export class BeneficioService {
  private baseUrl:string = '/ms-beneficios/v1/beneficios';
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
  deleteOne(idBeneficio: number): void {
    console.log(`Removendo benefício: ${idBeneficio}.`);
    this.http.delete(`${this.baseUrl}/${idBeneficio}`).subscribe({
      next: (res) => console.log(`Benefício ${idBeneficio} removido com sucesso.`),
      error: (err) => console.log(err),
    });
  }
}

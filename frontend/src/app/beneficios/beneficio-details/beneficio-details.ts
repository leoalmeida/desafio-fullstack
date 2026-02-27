import { Component, inject, signal, Signal } from '@angular/core';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { BeneficioType } from '../beneficio-type';
import { BeneficioService } from '../beneficio.service';
import { Observable } from 'rxjs';
import { AssociadoType } from '../../associado-type';

@Component({
  selector: 'app-beneficio-details',
  imports: [ReactiveFormsModule],
  templateUrl: './beneficio-details.html',
  styleUrl: './beneficio-details.css',
})
export class BeneficioDetails {
  id = new FormControl('');
  nome = new FormControl('');
  descricao = new FormControl('');
  valor = new FormControl(0.00);
  ativo = new FormControl(false);
  private activeRoute: ActivatedRoute = inject(ActivatedRoute);
  private beneficioService: BeneficioService = inject(BeneficioService);
  private navegador: Router = inject(Router);

  constructor() {
    this.activeRoute.queryParams.subscribe(params => {
      const type = params['type'];
      this.nome.setValue(params['nome'] || '');
      this.descricao.setValue(params['descricao'] || '');
      this.valor.setValue(params['valor'] || 0.00);
      this.ativo.setValue(params['ativo'] || false);
    });
  }

  onSubmit() {
    const beneficio:BeneficioType = {
      nome: this.nome.value || '',
      descricao: this.descricao.value || '',
      valor: this.valor.value || 0.00,
      ativo: this.ativo.value || false
    };

    // Call service to create beneficio
    this.beneficioService.createOne(beneficio);
    console.log('Beneficio created:', beneficio);
    this.navegador.navigate(['home']);
  }
  
}

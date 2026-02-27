import { MatButtonModule } from '@angular/material/button';
import { Component, inject, input, model, signal, TemplateRef } from '@angular/core';
import { MatDialog, MatDialogModule} from '@angular/material/dialog';
import { BeneficioType } from '../../models/beneficio-type';
import { BeneficioService } from '../../services/beneficio.service';
import { TokenType } from '../../models/token-type';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { BeneficioDetails } from '../beneficio-details/beneficio-details';
import { TransferDetails } from '../transfer-details/transfer-details';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-beneficio-card',
  imports: [MatCardModule, MatDividerModule, MatDialogModule,MatIconModule, MatButtonModule, MatSlideToggleModule, FormsModule],
  templateUrl: './beneficio-card.html',
  styleUrl: './beneficio-card.css'
})
export class BeneficioCard {
  beneficio = input.required<BeneficioType>();
  private beneficioService: BeneficioService = inject(BeneficioService);
  message:string = "";
  private dialogAcao: MatDialog = inject(MatDialog);
  constructor() {  
  }

  onUpdateBeneficio(beneficio: BeneficioType): void{
    const dialogRef = this.dialogAcao.open(BeneficioDetails, {
      width: '500px',
      data: { ...beneficio }
    });

    // Chama serviço para atualizar beneficio após fechamento do diálogo
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.beneficioService.changeOne(result);
        console.log('Atualização de beneficio solicitada:', result);
      }
    });
  }

  onRemoverBeneficio(dialogRef: TemplateRef<any>, beneficio: BeneficioType): void {
    if (!beneficio.id) {
        console.error("Nenhum benefício selecionado.");
        return;
    }
    const refOpen = this.dialogAcao.open(dialogRef, {
      width: '400px', enterAnimationDuration: '0ms', exitAnimationDuration: '0ms',
      data: { message: `Tem certeza que deseja remover o benefício "${beneficio.nome}"?` }
    });
  }

  onAlterarStatus(dialogRef: TemplateRef<any>, beneficio: BeneficioType): void {
    if (!beneficio.id) {
        console.error("Nenhum benefício selecionado.");
        return;
    }
    
    const refOpen = this.dialogAcao.open(dialogRef, { 
      width: '250px', 
      enterAnimationDuration: '0ms', 
      exitAnimationDuration: '0ms', 
      data: { message: `Tem certeza que deseja "${beneficio.ativo? 'cancelar' : 'ativar'}" o benefício "${beneficio.nome}"?` } 
    });
    refOpen.afterClosed().subscribe(result => {
      console.log('[Confirm]', result);
      if (result == true) {
        this.beneficioService.changeStatus(beneficio);
      } 
    });
  }

  onRealizarTransferencia(beneficio: BeneficioType): void {
    if (!beneficio.id) {
        console.error("Nenhum benefício selecionado.");
        return;
    }

    const refOpen = this.dialogAcao.open(TransferDetails, {
      width: '500px', enterAnimationDuration: '0ms', exitAnimationDuration: '0ms',
      data: { beneficio: { ...beneficio } }
    });

    refOpen.afterClosed().subscribe(result => {
      if (result) {
        if (!result.fromId || !result.toId || !result.valor) {
          console.error("Dados de transferência incompletos.");
          return;
        }
        this.beneficioService.transferValue(result);
        console.log('Transferência solicitada:', result);
      }
    });
  }
}


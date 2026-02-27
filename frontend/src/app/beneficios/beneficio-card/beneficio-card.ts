import { Component, inject, input, model, signal, TemplateRef } from '@angular/core';
import { MatDialog, MatDialogModule} from '@angular/material/dialog';
import { BeneficioType } from '../beneficio-type';
import { BeneficioService } from '../beneficio.service';
import { TokenType } from '../../token-type';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';

@Component({
  selector: 'app-beneficio-card',
  imports: [MatCardModule, MatDividerModule, MatDialogModule, MatSlideToggleModule, FormsModule],
  templateUrl: './beneficio-card.html',
  styleUrl: './beneficio-card.css'
})
export class BeneficioCard {
  beneficio = input.required<BeneficioType>();
  toggle?: "Ativar" | "Cancelar";
  readonly dialog = inject(MatDialog);

   private beneficioService: BeneficioService = inject(BeneficioService);
   constructor() {
      
   }
   
  confirm(dialogRef: TemplateRef<any>) {
    if (!this.beneficio().id) {
        console.error("Sessão não encontrada para o beneficio selecionado.");
        return;
    }
    this.toggle = this.beneficio().ativo ? "Cancelar" : "Ativar";
    const refOpen = this.dialog.open(dialogRef, { width: '250px', enterAnimationDuration: '0ms', exitAnimationDuration: '0ms' });
    refOpen.afterClosed().subscribe(result => {
      console.log('[Confirm]', result);
      if (result == true) {
        let chgBeneficio = this.beneficio();
        chgBeneficio.ativo = !this.beneficio().ativo;
        this.beneficioService.changeOne(chgBeneficio);
      } 
    });
  }
}


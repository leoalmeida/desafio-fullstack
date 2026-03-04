import { MatButtonModule } from "@angular/material/button";
import {
  Component,
  inject,
  input,
  model,
  signal,
  TemplateRef,
} from "@angular/core";
import { MatDialog, MatDialogModule } from "@angular/material/dialog";
import { BeneficioType } from "../../models/beneficio-type";
import { BeneficioService } from "../../services/beneficio.service";
import { TokenType } from "../../models/token-type";
import { MatSlideToggleModule } from "@angular/material/slide-toggle";
import { FormsModule } from "@angular/forms";
import { MatCardModule } from "@angular/material/card";
import { MatDividerModule } from "@angular/material/divider";
import { BeneficioDetails } from "../beneficio-details/beneficio-details";
import { TransferDetails } from "../transfer-details/transfer-details";
import { MatIconModule } from "@angular/material/icon";
import { TransferenciaService } from "src/app/services/transferencia.service";
import { NotificationService } from "src/app/services/notification.service";
import { LoggerService } from "src/app/services/logger.service";

@Component({
  selector: "app-beneficio-card",
  imports: [
    MatCardModule,
    MatDividerModule,
    MatDialogModule,
    MatIconModule,
    MatButtonModule,
    MatSlideToggleModule,
    FormsModule,
  ],
  templateUrl: "./beneficio-card.html",
  styleUrl: "./beneficio-card.css",
})
export class BeneficioCard {
  beneficio = input.required<BeneficioType>();
  private beneficioService: BeneficioService = inject(BeneficioService);
  message: string = "";

  private dialogAcao: MatDialog = inject(MatDialog);
  private notify: NotificationService = inject(NotificationService);

  constructor() {}

  onUpdateBeneficio(beneficio: BeneficioType): void {
    const dialogRef = this.dialogAcao.open(BeneficioDetails, {
      width: "500px",
      data: { ...beneficio },
    });

    // Chama serviço para atualizar beneficio após fechamento do diálogo
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.notify.showSuccess("Atualização realizada com sucesso!");
      }
    });
  }

  onAlterarStatus(dialogRef: TemplateRef<any>, beneficio: BeneficioType): void {
    if (!beneficio.id) {
      this.notify.showError("Nenhum benefício selecionado.");
      return;
    }

    const refOpen = this.dialogAcao.open(dialogRef, {
      width: "250px",
      enterAnimationDuration: "0ms",
      exitAnimationDuration: "0ms",
      data: {
        message: `Tem certeza que deseja "${beneficio.ativo ? "cancelar" : "ativar"}" o benefício "${beneficio.nome}"?`,
      },
    });
    refOpen.afterClosed().subscribe((result) => {
      console.log("[Confirm]", result);
      if (result == true) {
        this.beneficioService.changeStatus(beneficio);
      }
    });
  }
}

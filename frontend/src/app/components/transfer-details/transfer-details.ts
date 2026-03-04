import { CommonModule } from "@angular/common";
import { TransferenciaType } from "./../../models/transferencia-type";
import { Component, inject } from "@angular/core";
import {
  ReactiveFormsModule,
  FormGroup,
  FormBuilder,
  Validators,
} from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { BeneficioType } from "src/app/models/beneficio-type";
import { TransferenciaService } from "src/app/services/transferencia.service";

@Component({
  selector: "app-transfer-details",
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDialogModule,
    MatSelectModule,
  ],
  templateUrl: "./transfer-details.html",
})
export class TransferDetails {
  private transferenciaService: TransferenciaService =
    inject(TransferenciaService);
  private formBuilder: FormBuilder = inject(FormBuilder);
  private dialogRef: MatDialogRef<TransferDetails> = inject(MatDialogRef);
  public data: BeneficioType = inject(MAT_DIALOG_DATA) as BeneficioType;
  formTransferencia: FormGroup = this.formBuilder.group({
    fromId: [this.data.id || "", Validators.required],
    toId: ["", Validators.required],
    valor: [0.0, Validators.required],
  });
  constructor() {}

  onSubmit(): void {
    if (this.formTransferencia.valid) {
      const transfer: TransferenciaType = {
        ...this.formTransferencia.value,
      };

      this.transferenciaService.transferValue(transfer).subscribe({
        next: () => {
          console.log("Transferência realizada com sucesso!");
          this.dialogRef.close();
        },
      });
    } else {
      console.log("Dados da transferência inválidos ou incompletos!");
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}

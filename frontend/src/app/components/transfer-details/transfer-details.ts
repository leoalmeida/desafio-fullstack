import { TransferenciaType } from "./../../models/transferencia-type";
import { Component, inject, signal, Signal } from "@angular/core";
import {
  ReactiveFormsModule,
  FormControl,
  FormGroup,
  FormBuilder,
  Validators,
  FormsModule,
} from "@angular/forms";
import { MatButtonModule } from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { BeneficioType } from "src/app/models/beneficio-type";

@Component({
  selector: "app-transfer-details",
  imports: [
    MatButtonModule,
    FormsModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
  ],
  templateUrl: "./transfer-details.html",
})
export class TransferDetails {
  
  private formBuilder: FormBuilder = inject(FormBuilder);
  private dialogRef: MatDialogRef<TransferDetails> = inject(MatDialogRef);
  public data: BeneficioType = inject(MAT_DIALOG_DATA) as BeneficioType;
  formTransferencia: FormGroup = this.formBuilder.group({
        fromId: [this.data.id || "", Validators.required],
        toId: ["", Validators.required],
        valor: [0.0, Validators.required]
      });
  constructor() {
  }

  onSubmit(): void {
    if (this.formTransferencia.valid) {
      const transfer: TransferenciaType = {
        ...this.formTransferencia.value,
      };
      this.dialogRef.close(transfer);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}

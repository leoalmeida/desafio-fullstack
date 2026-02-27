import { TransferenciaType } from './../../models/transferencia-type';
import { Component, inject, signal, Signal } from '@angular/core';
import { ReactiveFormsModule, FormControl, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import {  MatFormFieldModule } from '@angular/material/form-field';
import { BeneficioType } from 'src/app/models/beneficio-type';

@Component({
  selector: 'app-transfer-details',
  imports: [ReactiveFormsModule,MatDialogModule,MatFormFieldModule],
  templateUrl: './transfer-details.html'
})
export class TransferDetails {
  formTransferencia:FormGroup;
  
  private formBuilder: FormBuilder = inject(FormBuilder);
  private dialogRef: MatDialogRef<TransferDetails> = inject(MatDialogRef);
  public data: BeneficioType = inject (MAT_DIALOG_DATA) as BeneficioType;
  
  constructor() {
    this.formTransferencia = this.formBuilder.group({
      fromId: [this.data.id || '', Validators.required],
      toId: ['', Validators.required],
      valor: [0.00, Validators.required],
    });    
  }

  onSubmit() :void {
    if (this.formTransferencia.valid) {
      const transfer:TransferenciaType = {
        ...this.formTransferencia.value
      }
      this.dialogRef.close(transfer);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
  
}

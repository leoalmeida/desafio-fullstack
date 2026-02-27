import { MatFormFieldModule } from '@angular/material/form-field';
import { Component, inject, signal, Signal } from '@angular/core';
import { ReactiveFormsModule, FormControl, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { BeneficioType } from '../../models/beneficio-type';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-beneficio-details',
  imports: [ReactiveFormsModule, MatFormFieldModule, MatInputModule,MatButtonModule,MatDialogModule],
  templateUrl: './beneficio-details.html',
  styleUrl: './beneficio-details.css',
})
export class BeneficioDetails {
  formBeneficio:FormGroup;
  
  private formBuilder: FormBuilder = inject(FormBuilder);
  private dialogRef: MatDialogRef<BeneficioDetails> = inject(MatDialogRef);
  public data: BeneficioType = inject (MAT_DIALOG_DATA) as BeneficioType;
  
  constructor() {
    this.formBeneficio = this.formBuilder.group({
      id: [this.data.id || '', Validators.required],
      nome: [this.data.nome || '', Validators.required],
      descricao: [this.data.descricao || ''],
      valor: [this.data.valor || 0.00, Validators.required],
      ativo: [this.data.ativo || false, Validators.required]
    });    
  }

  onSubmit() :void {
    if (this.formBeneficio.valid) {
      const beneficio:BeneficioType = {
        ...this.data,
        ...this.formBeneficio.value
      }
      this.dialogRef.close(beneficio);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
  
}

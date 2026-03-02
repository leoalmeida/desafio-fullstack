import { MatButtonModule } from '@angular/material/button';
import { Component, computed, inject, Input, Signal, signal, TemplateRef } from '@angular/core';
import { BeneficioType } from '../../models/beneficio-type';
import { BeneficioService } from '../../services/beneficio.service';
import { ActivatedRoute, Router, Routes } from '@angular/router';
import { LoadingService } from '../loading-indicator/loading.service';
import { TokenStorageService } from '../../services/token-storage.service';
import { TokenType } from '../../models/token-type';
import { BeneficioCard } from '../beneficio-card/beneficio-card';
import { Searchbar } from '../searchbar/searchbar';
import { BeneficioDetails } from '../beneficio-details/beneficio-details';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIcon, MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-beneficio-list',
  imports: [BeneficioCard, Searchbar, MatDialogModule, MatButtonModule,MatIconModule],
  templateUrl: './beneficio-list.html',
  styleUrl: './beneficio-list.css'
})
export class BeneficioList {
  protected loggedUser = signal({} as TokenType);
  searchQuery = signal<string>('');

  private beneficioService: BeneficioService = inject(BeneficioService);
  private loadingService: LoadingService = inject(LoadingService);
  private tokenStorageService: TokenStorageService = inject(TokenStorageService);
  private dialogAcao: MatDialog = inject(MatDialog);

  constructor() {
    try {
      this.loadingService.loadingOn();
      this.tokenStorageService.loggedUser$.subscribe(user => {
         this.loggedUser.set(user);
      });
      this.beneficioService.getAll();
    } catch (error) {
      console.log(error);
    } finally {
      this.loadingService.loadingOff();
    }
  }

  filteredBeneficioList = computed(() => {
    try {
      this.loadingService.loadingOn();
      const normalizedQuery = this.searchQuery()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .toLowerCase();
      return this.beneficioService.items().filter(x => x?.nome.toLowerCase().includes(normalizedQuery));
    } catch (error) {
      console.log(error);
      return;
    } finally {
      this.loadingService.loadingOff();
    }
  });

  handleMessage(message: string): void {
    console.log('Received message from child:', message);
    this.searchQuery.set(message);
  }

  onCreateBeneficio(): void{
    const dialogRef = this.dialogAcao.open(BeneficioDetails, {
      width: '500px',
      data: {}
    });
    // Chama serviço para criar beneficio após fechamento do diálogo
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.beneficioService.createOne(result);
        console.log('Criação de beneficio solicitada:', result);
      }
    });
  }

}

import { Component, computed, inject, Input, Signal, signal } from '@angular/core';
import { BeneficioType } from '../beneficio-type';
import { BeneficioService } from '../beneficio.service';
import { ActivatedRoute, Router, Routes } from '@angular/router';
import { LoadingService } from '../../loading-indicator/loading.service';
import { TokenStorageService } from '../../token-storage.service';
import { TokenType } from '../../token-type';
import { BeneficioCard } from '../beneficio-card/beneficio-card';
import { Searchbar } from '../../searchbar/searchbar';

@Component({
  selector: 'app-beneficio-list',
  imports: [BeneficioCard, Searchbar],
  templateUrl: './beneficio-list.html',
  styleUrl: './beneficio-list.css'
})
export class BeneficioList {
  protected loggedUser = signal({} as TokenType);
  searchQuery = signal<string>('');
  private beneficioService: BeneficioService = inject(BeneficioService);
  private loadingService: LoadingService = inject(LoadingService);
  private tokenStorageService: TokenStorageService = inject(TokenStorageService);
  private navigator: Router = inject(Router);

  constructor() {
    try {
      this.loadingService.loadingOn();
      this.tokenStorageService.loggedUser$.subscribe(user => {
         this.loggedUser.set(user);
      });
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

  handleMessage(message: string) {
    console.log('Received message from child:', message);
    this.searchQuery.set(message);
  }

  onCreateBeneficio(){
    this.navigator.navigate(['beneficio-details'], {
      queryParams: { type: 'new' }
    });
  }

}

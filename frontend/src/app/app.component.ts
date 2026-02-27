import { Component, inject, OnInit, Signal, signal } from '@angular/core';
import { TitleService } from './services/title.service';
import { TokenStorageService } from './services/token-storage.service';
import { LoadingIndicator } from './components/loading-indicator/loading-indicator';
import {  RouterOutlet } from '@angular/router';
import { Toolbar } from './components/toolbar/toolbar';

@Component({
  selector: 'app-root',
  imports: [LoadingIndicator, Toolbar, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{
    protected readonly title = signal('Modulo Frontend');
    private roles: string[] = [];
    isloggedIn = false;
    showAdminBoard = false;
    showModeratorBoard = false;
    
    
    private titleService: TitleService = inject(TitleService);
    
    constructor() {
    }

    ngOnInit(): void {
      this.titleService.setTitle();
    }

    updateViewByRole(): void {
      this.showAdminBoard = this.roles.includes('ADMIN');
      this.showModeratorBoard = this.roles.includes('MODERATOR');
    }
}

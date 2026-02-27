import {Component, inject, input} from '@angular/core';
import { RouterLink, Routes } from '@angular/router';
import { routes } from '../../app.routes';
import { TokenStorageService } from '../../services/token-storage.service';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-toolbar',
  imports: [MatSidenavModule, MatToolbarModule, MatIconModule, RouterLink],
  templateUrl: './toolbar.html',
  styleUrl: './toolbar.css'
})
export class Toolbar {

  title = input.required<string>();
  loggedUser!: string;
  routes: Routes = routes;
  opened: boolean = false;
  private tokenStorageService: TokenStorageService = inject(TokenStorageService);

  constructor(){
    this.tokenStorageService.loggedUser$.subscribe(user => 
      this.loggedUser = user.username
    );
  }

  showMenu(){

  }

}

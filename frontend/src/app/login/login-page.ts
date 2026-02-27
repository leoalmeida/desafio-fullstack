import { Component, EventEmitter, inject, input, OnInit, Output, signal } from '@angular/core';
import { ReactiveFormsModule, FormControl, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCardModule } from '@angular/material/card';
import { AuthService } from './auth.service';
import { TokenStorageService } from '../token-storage.service';
import { merge } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-page',
  imports: [ReactiveFormsModule, MatFormFieldModule, MatCardModule],
   templateUrl: './login-page.html',
   styleUrl: './login-page.css'
})
export class LoginPage implements OnInit {
  loginForm = new FormGroup({
    username: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required])
  });
  isLoggedIn: boolean = false;
  isLoginError: boolean = false;
  errorMessage = signal<string>('');
  roles: string[] = [];

  private authService:AuthService = inject(AuthService);
  private tokenStorage:TokenStorageService = inject(TokenStorageService);
  private navigator: Router = inject(Router);

  constructor(){
    merge(
      this.loginForm.valueChanges,
      this.loginForm.statusChanges
    ).subscribe(() => this.updateErrorMessage());
  }

  updateErrorMessage() {
    if (!this.loginForm.valid) {
      this.errorMessage.set('Dados inválidos.');
    } else {
      this.errorMessage.set('');
    }
  }

  ngOnInit() {
    this.reloadPage();
  }

  login() {

    if (this.loginForm.valid) {
      const username = this.loginForm.value.username ?? '';
      const password = this.loginForm.value.password ?? '';
      this.authService.login(username, password).subscribe(
        user => {
          if (user){
            this.reloadPage();
          }
        }
      )
    }
  }

  reloadPage() {
    this.isLoggedIn = this.tokenStorage.isAuthenticated(); 
    if (this.isLoggedIn) {
        this.navigator.navigate(['/beneficios']);
    }
  }
}

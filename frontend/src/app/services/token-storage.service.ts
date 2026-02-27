import { Injectable, signal } from '@angular/core';
import { AssociadoType } from '../models/associado-type';
import { BehaviorSubject } from 'rxjs';
import { TokenType } from '../models/token-type';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {

  private userToken = new BehaviorSubject<TokenType>({} as TokenType);
  private loggedIn = signal<boolean>(false);

  constructor() { }

  isAuthenticated = this.loggedIn.asReadonly();
  loggedUser = this.userToken.getValue();
  loggedUser$ = this.userToken.asObservable();

  signOut(): void {
    window.sessionStorage.clear();
    this.userToken.next({} as TokenType);
    this.loggedIn.set(false);
  }

  public saveJsonWebToken(associado: AssociadoType): void {
    if (associado.accessToken){
      window.sessionStorage.removeItem('auth-token'); // Clear previous token
      window.sessionStorage.setItem('auth-token', associado.accessToken); // Save token in session storage

      const userToken: TokenType = JSON.parse(atob(associado.accessToken.split('.')[1]));
      this.userToken.next(userToken);
      this.loggedIn.set(true);
    }
  }
  
  public getToken(): string | null {
    return window.sessionStorage.getItem('auth-token');
  }
  public saveUser(user: AssociadoType): void {
    window.sessionStorage.removeItem('user'); // Clear previous user
    window.sessionStorage.setItem('user', JSON.stringify(user));
  } 
  public getUser(): AssociadoType  {
    const user = window.sessionStorage.getItem('user');
    return user ? JSON.parse(user) : {} as AssociadoType;
  }
}

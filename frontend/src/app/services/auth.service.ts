import { HttpClient, HttpHeaders } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { environment } from '../../environments/environment';
import { BehaviorSubject, Observable } from 'rxjs';
import { associados } from '../../mocks/associados';
import { AssociadoType } from '../models/associado-type';
import { TokenType } from '../models/token-type';
import { TokenStorageService } from './token-storage.service';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl:string = '';
  private http: HttpClient = inject(HttpClient);
  private apiData = [...associados];
  private associadoList = signal<AssociadoType[]>([]);
  private loggedUser = 
    new BehaviorSubject<AssociadoType>({} as AssociadoType);

  private tokenStorageService = inject(TokenStorageService);
  
  private mockHttpCall = <T>(result: T) => {
    return new Observable<T>(s => {
        s.next(result);
        s.complete();
    });
  }

  loggedUser$ = this.loggedUser.asObservable();

  constructor(){
    this.baseUrl = environment.authApi || '/ms-auth/v1/auth'; // Default to a fallback URL if not define
    this.mockHttpCall<any>(this.apiData)
                .subscribe(xs => this.associadoList.set(xs));
  }

  login(username: string, password: string): Observable<AssociadoType>{
    //return this.http.post<any>(`${this.baseUrl}/signin`, { username, password }, httpOptions);
    this.mockHttpCall<AssociadoType>(this.apiData[0])
            .subscribe(user => {
              this.loggedUser.next(user);
              this.tokenStorageService.saveJsonWebToken(user);
            });
    return this.loggedUser.asObservable();
  }

  logout(): void {
    //this.http.post<any>(`${this.baseUrl}/signout`, { }, httpOptions).subscribe();
    this.tokenStorageService.signOut();
  }

  register(username: string, email: string, telefone: string, password: string): Observable<AssociadoType> {
    //return this.http.post<any>(`${this.baseUrl}/signup`, { username, email, password }, httpOptions);
    let item:number = this.apiData.push({
      id: Number(password),
      email: email,
      nome: username,
      telefone: telefone, // Placeholder, as telefone is not provided in the mock
      accessToken: "abc123"
    });
    return this.mockHttpCall<AssociadoType>(this.apiData[item] as AssociadoType);
  }
}

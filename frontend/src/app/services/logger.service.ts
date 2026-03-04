import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoggerService {
    log(...msgs: any[]) { console.log(...msgs); }
    error(...msgs: any[]) { console.error(...msgs); }
    warn(...msgs: any[]) { console.warn(...msgs); }
}
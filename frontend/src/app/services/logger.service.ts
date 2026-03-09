import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LoggerService {
  log(..._msgs: any[]) {}
  error(..._msgs: any[]) {}
  warn(..._msgs: any[]) {}
}

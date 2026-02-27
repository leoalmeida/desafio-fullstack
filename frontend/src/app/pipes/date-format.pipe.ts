import { Inject, LOCALE_ID, Pipe, PipeTransform } from '@angular/core';
import { DatePipe } from '@angular/common';

@Pipe({
    name: 'dateFormatPipe',
    standalone: true
})
export class DateFormatPipe implements PipeTransform {
    constructor(@Inject(LOCALE_ID) private locale: string) {}
    transform(value: string) {
       var datePipe = new DatePipe(this.locale);
        value = datePipe.transform(value, 'dd/MM/yyyy HH:mm:ss') || '';
        return value;
    }
}
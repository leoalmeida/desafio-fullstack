import { Component, input } from '@angular/core';


@Component({
  selector: 'app-statusbar',
  templateUrl: './statusbar.html',
  styleUrl: './statusbar.css'
})
export class Statusbar {
  ativo = input.required<boolean>();;
}

import { Component, inject, OnInit, signal } from "@angular/core";
import { MatCard, MatCardModule } from "@angular/material/card";
import { TitleService } from "src/app/services/title.service";

@Component({
  selector: "app-acesso-negado",
  standalone: true,
  imports: [MatCardModule],
  templateUrl: "./acesso-negado.html",
  styleUrls: ["./acesso-negado.css"],
})
export class AcessoNegado implements OnInit {
  protected readonly title = signal("");
  private titleService: TitleService = inject(TitleService);

  ngOnInit(): void {
    this.titleService.setTitle();
  }

  voltar() {
    window.location.href = "/";
  }
}

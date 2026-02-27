import { Injectable } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { filter, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TitleService {
  constructor(
    private title: Title,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {}
  setTitle() {
    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        map(() => {
          let route = this.activatedRoute;
          while (route.firstChild) {
            route = route.firstChild;
          }
          return route;
        }),
        filter((route) => route.outlet === 'primary'),
        map((route) => {
          const title = route.snapshot.data['title'];
          const detail = route.snapshot.data['detail'];
          const type = route.snapshot.queryParams['type'];
          if (!title) {
            return 'Frontend App';
          }

          if (detail)
            return ((type !== "new") ? "Criar " : "Alterar ") + title;
          return title;
          
          
        })
      )
      .subscribe((title) => this.title.setTitle(title));
  }
}

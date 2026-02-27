
import { enableProdMode, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { environment } from './environments/environment';
import { AppComponent } from './app/app.component';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { bootstrapApplication, provideProtractorTestingSupport } from '@angular/platform-browser';
import { provideRouter, withViewTransitions } from '@angular/router';
import { routes } from './app/app.routes';

if (environment.production) {
  enableProdMode();
}

//platformBrowser().bootstrapModule(AppModule)
//  .catch(err => console.error(err));
bootstrapApplication(AppComponent, {
  providers: [provideProtractorTestingSupport(),
    provideHttpClient(withInterceptorsFromDi()),
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withViewTransitions())
  ]
}).catch(err => console.error(err));

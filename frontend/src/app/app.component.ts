import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'frontend';
  private router = inject(Router);

  // Mudamos para um Signal manual para controlar o momento exato da renderização
  isLoginPage = signal(true);

  constructor() {
    // Esse escutador espera a rota resolver completamente antes de decidir se oculta a sidebar
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      const url = event.urlAfterRedirects || event.url;

      // Verifica se a rota final realmente é a de login ou a raiz vazia
      const checkLogin = url.includes('/login') || url === '/';

      // Atualiza o estado do seu HTML de forma segura
      this.isLoginPage.set(checkLogin);
    });
  }
}

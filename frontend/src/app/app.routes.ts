import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard'; // 👈 Certifique-se de que o import está correto

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent),
    title: 'Login - Desafio Unifor'
  },
  {
    path: 'students',
    loadComponent: () => import('./pages/student-list/student-list.component').then(m => m.StudentListComponent),
    title: 'Gerenciamento de Alunos',
    canActivate: [authGuard]
  },
  {
    path: 'courses',
    loadComponent: () => import('./pages/course-list/course-list.component').then(m => m.CourseListComponent),
    title: 'Gerenciamento de Cursos',
    canActivate: [authGuard]
  },
  {
    path: 'enrollment',
    loadComponent: () => import('./pages/enrollment/enrollment.component').then(m => m.EnrollmentComponent),
    title: 'Matrículas Acadêmicas',
    canActivate: [authGuard]
  },
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];

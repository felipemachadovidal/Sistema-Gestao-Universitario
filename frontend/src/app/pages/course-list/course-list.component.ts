import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CourseService, CourseResponse } from '../../core/services/course.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-course-list',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './course-list.component.html',
  styleUrl: './course-list.component.scss'
})
export class CourseListComponent implements OnInit {
  private courseService = inject(CourseService);
  private fb = inject(NonNullableFormBuilder);

  courses = signal<CourseResponse[]>([]);
  isLoading = signal<boolean>(true);
  showForm = signal<boolean>(false);

  courseForm = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    description: ['', [Validators.required]],
    durationHours: [0, [Validators.required, Validators.min(1), Validators.max(500)]]
  });

  ngOnInit(): void {
    this.loadCourses();
  }

  loadCourses(): void {
    this.isLoading.set(true);
    this.courseService.listAll().subscribe({
      next: (data) => {
        this.courses.set(data);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  toggleForm(): void {
    this.showForm.update(v => !v);
    if (!this.showForm()) this.courseForm.reset();
  }

  saveCourse(): void {
    if (this.courseForm.valid) {
      const request = this.courseForm.getRawValue();
      this.courseService.create(request).subscribe({
        next: (newCourse) => {
          this.courses.update(all => [newCourse, ...all]);
          this.toggleForm();
          alert('Curso criado com sucesso!');
        },
        error: (err) => alert('Erro: ' + (err.error?.message || 'Dados inválidos'))
      });
    }
  }

  deleteCourse(id: number): void {
    if (confirm('Deseja remover este curso?')) {
      this.courseService.softDelete(id).subscribe({
        next: () => this.courses.update(all => all.filter(c => c.id !== id))
      });
    }
  }
}

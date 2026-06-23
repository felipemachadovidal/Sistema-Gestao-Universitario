import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CourseService, CourseResponse } from '../../core/services/course.service';
import { StudentService } from '../../core/services/student.service';

@Component({
  selector: 'app-course-list',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './course-list.component.html',
  styleUrl: './course-list.component.scss'
})
export class CourseListComponent implements OnInit {
  private courseService = inject(CourseService);
  private studentService = inject(StudentService);
  private fb = inject(NonNullableFormBuilder);

  courses = signal<CourseResponse[]>([]);
  isLoading = signal<boolean>(true);
  showForm = signal<boolean>(false);

  // 🌟 O segredo do Update: Controla o ID do curso em edição
  editingCourseId = signal<number | null>(null);

  // Estados reais do Modal Acadêmico
  selectedCourse = signal<CourseResponse | null>(null);
  courseStudents = signal<any[]>([]);
  allStudents = signal<any[]>([]);

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
    if (!this.showForm()) {
      this.clearForm();
    }
  }

  clearForm(): void {
    this.courseForm.reset();
    this.editingCourseId.set(null);
  }

  // 🌟 NOVO: Prepara o formulário e joga a tela para o topo suavemente
  editCourse(course: CourseResponse): void {
    this.editingCourseId.set(course.id);
    this.courseForm.patchValue({
      name: course.name,
      description: course.description,
      durationHours: course.durationHours
    });
    this.showForm.set(true);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  saveCourse(): void {
    if (this.courseForm.valid) {
      const request = this.courseForm.getRawValue();
      const courseId = this.editingCourseId();

      if (courseId) {
        // Fluxo de Atualização (PUT)
        this.courseService.update(courseId, request).subscribe({
          next: (updatedCourse: CourseResponse) => {
            this.courses.update(all => all.map(c => c.id === courseId ? updatedCourse : c));
            this.toggleForm();
            alert('Curso atualizado com sucesso!');
          },
          error: (err: any) => alert('Erro ao atualizar: ' + (err.error?.message || 'Dados inválidos'))
        });
      } else {
        // Fluxo de Criação Original (POST)
        this.courseService.create(request).subscribe({
          next: (newCourse) => {
            this.courses.update(all => [newCourse, ...all]);
            this.toggleForm();
            alert('Curso criado com sucesso!');
          },
          error: (err: any) => alert('Erro: ' + (err.error?.message || 'Dados inválidos'))
        });
      }
    }
  }

  deleteCourse(id: number): void {
    if (confirm('Deseja remover este curso?')) {
      this.courseService.softDelete(id).subscribe({
        next: () => this.courses.update(all => all.filter(c => c.id !== id))
      });
    }
  }

  // ==========================================
  // CONEXÃO REAL COM O BACKEND PARA MATRÍCULAS
  // ==========================================

  openStudentsModal(course: CourseResponse): void {
    this.selectedCourse.set(course);
    this.loadCourseStudents(course.id);
    this.loadAllStudentsForSelection();
  }

  closeModal(): void {
    this.selectedCourse.set(null);
    this.courseStudents.set([]);
  }

  loadCourseStudents(courseId: number): void {
    this.courseService.listStudentsEnrolled(courseId).subscribe({
      next: (students: any) => this.courseStudents.set(students),
      error: (err: any) => console.error('Erro ao buscar estudantes do curso', err)
    });
  }

  loadAllStudentsForSelection(): void {
    this.studentService.listAll().subscribe({
      next: (students: any) => {
        this.allStudents.set(students);
      },
      error: (err: any) => {
        console.error('Erro ao buscar a lista global de estudantes para seleção', err);
      }
    });
  }

  enrollStudent(studentIdStr: string): void {
    const studentId = parseInt(studentIdStr, 10);
    const course = this.selectedCourse();

    if (!studentId || !course) {
      alert('Por favor, selecione um estudante na lista.');
      return;
    }

    this.courseService.enrollStudent(course.id, studentId).subscribe({
      next: (response: any) => {
        alert(response.message || 'Aluno matriculado com sucesso!');
        this.loadCourseStudents(course.id);
      },
      error: (err: any) => {
        if (err.status === 409) {
          alert('Atenção: Este aluno já está matriculado neste curso!');
        } else {
          alert('Erro ao realizar matrícula.');
        }
      }
    });
  }

  unenrollStudent(studentId: number): void {
    const course = this.selectedCourse();
    if (!course) return;

    if (confirm('Deseja realmente remover a matrícula deste aluno do curso?')) {
      this.courseService.unenrollStudent(course.id, studentId).subscribe({
        next: () => {
          this.courseStudents.update(all => all.filter(st => st.id !== studentId));
          alert('Matrícula removida com sucesso!');
        },
        error: (err: any) => {
          alert('Erro ao remover matrícula: ' + (err.error?.message || 'Tente novamente.'));
        }
      });
    }
  }
}

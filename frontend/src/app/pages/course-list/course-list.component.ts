import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CourseService, CourseResponse } from '../../core/services/course.service';
// 🌟 1. Descomentado e importado o serviço real de estudantes
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
  // 🌟 2. Descomentado e injetado o serviço de estudantes
  private studentService = inject(StudentService);
  private fb = inject(NonNullableFormBuilder);

  courses = signal<CourseResponse[]>([]);
  isLoading = signal<boolean>(true);
  showForm = signal<boolean>(false);

  // Estados reais do Modal Acadêmico
  selectedCourse = signal<CourseResponse | null>(null);
  courseStudents = signal<any[]>([]); // Alunos matriculados vindo do Quarkus
  allStudents = signal<any[]>([]);    // Todos os alunos cadastrados para o <select>

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
    // Chama o GET /api/courses/{courseId}/students do seu Quarkus
    this.courseService.listStudentsEnrolled(courseId).subscribe({
      next: (students) => this.courseStudents.set(students),
      error: (err) => console.error('Erro ao buscar estudantes do curso', err)
    });
  }

  // 🌟 3. FUNÇÃO ATUALIZADA: Buscando a lista real de estudantes cadastrados
  loadAllStudentsForSelection(): void {
    // Busca os alunos do StudentService (certifique-se de que o método se chama listAll ou similar)
    this.studentService.listAll().subscribe({
      next: (students) => {
        this.allStudents.set(students);
      },
      error: (err) => {
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
      next: (response) => {
        alert(response.message || 'Aluno matriculado com sucesso!');
        this.loadCourseStudents(course.id); // 🔄 Recarrega a tabela interna do modal na hora!
      },
      error: (err) => {
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
          error: (err) => {
            alert('Erro ao remover matrícula: ' + (err.error?.message || 'Tente novamente.'));
          }
        });
      }
    }
}

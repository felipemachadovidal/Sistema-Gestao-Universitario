import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { StudentService, StudentResponse } from '../../core/services/student.service';
import { CourseService, CourseResponse } from '../../core/services/course.service';

@Component({
  selector: 'app-enrollment',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './enrollment.component.html',
  styleUrl: './enrollment.component.scss'
})
export class EnrollmentComponent implements OnInit {
  private studentService = inject(StudentService);
  private courseService = inject(CourseService);
  private fb = inject(NonNullableFormBuilder);

  students = signal<StudentResponse[]>([]);
  courses = signal<CourseResponse[]>([]);
  enrolledStudents = signal<StudentResponse[]>([]);
  isLoading = signal<boolean>(false);

  enrollmentForm = this.fb.group({
    courseId: [0, [Validators.required, Validators.min(1)]],
    studentId: [0, [Validators.required, Validators.min(1)]]
  });

  ngOnInit(): void {
    this.studentService.listAll().subscribe(data => this.students.set(data));
    this.courseService.listAll().subscribe(data => this.courses.set(data));
  }

  onCourseChange(): void {
    const courseId = this.enrollmentForm.controls.courseId.value;
    if (courseId > 0) {
      this.courseService.listStudentsEnrolled(courseId).subscribe(data => {
        this.enrolledStudents.set(data);
      });
    }
  }

  confirmEnrollment(): void {
    if (this.enrollmentForm.valid) {
      const { courseId, studentId } = this.enrollmentForm.getRawValue();
      this.isLoading.set(true);

      this.courseService.enrollStudent(courseId, studentId).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.onCourseChange();
          alert('Matrícula realizada com sucesso!');
        },
        error: (err) => {
          this.isLoading.set(false);
          alert(err.status === 409 ? 'Este aluno já está matriculado neste curso.' : 'Erro ao matricular.');
        }
      });
    }
  }
}

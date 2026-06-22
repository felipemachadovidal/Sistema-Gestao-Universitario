import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { StudentService, StudentResponse } from '../../core/services/student.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-student-list',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './student-list.component.html',
  styleUrl: './student-list.component.scss'
})
export class StudentListComponent implements OnInit {
  private studentService = inject(StudentService);
  private fb = inject(NonNullableFormBuilder);

  students = signal<StudentResponse[]>([]);
  isLoading = signal<boolean>(true);
  showForm = signal<boolean>(false);

  studentForm = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    cpf: ['', [Validators.required, Validators.pattern(/^\d{11}$|^\d{3}\.\d{3}\.\d{3}-\d{2}$/)]]
  });

  ngOnInit(): void {
    this.loadStudents();
  }

  loadStudents(): void {
    this.isLoading.set(true);
    this.studentService.listAll().subscribe({
      next: (data) => {
        this.students.set(data);
        this.isLoading.set(false);
      },
      error: () => this.isLoading.set(false)
    });
  }

  toggleForm(): void {
    this.showForm.update(val => !val);
    if (!this.showForm()) this.studentForm.reset();
  }

  saveStudent(): void {
    if (this.studentForm.valid) {
      const request = this.studentForm.getRawValue();
      this.studentService.create(request).subscribe({
        next: (newStudent) => {
          this.students.update(all => [newStudent, ...all]);
          this.toggleForm();
          alert('Estudante cadastrado com sucesso!');
        },
        error: (err) => alert('Erro ao cadastrar: ' + (err.error?.message || 'Verifique os dados.'))
      });
    }
  }

  deleteStudent(id: number): void {
    if (confirm('Tem certeza que deseja remover este aluno?')) {
      this.studentService.softDelete(id).subscribe({
        next: () => this.students.update(all => all.filter(s => s.id !== id))
      });
    }
  }
}

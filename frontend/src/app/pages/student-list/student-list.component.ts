import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { StudentService } from '../../core/services/student.service';

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

  students = signal<any[]>([]);
  isLoading = signal<boolean>(true);
  showForm = signal<boolean>(false);

  editingStudentId = signal<number | null>(null);

  studentForm = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    cpf: ['', [Validators.required, Validators.minLength(11), Validators.maxLength(14)]]
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
    this.showForm.update(v => !v);
    if (!this.showForm()) {
      this.clearForm();
    }
  }

  clearForm(): void {
    this.studentForm.reset();
    this.editingStudentId.set(null);
  }

  editStudent(student: any): void {
    this.editingStudentId.set(student.id);
    this.studentForm.patchValue({
      name: student.name,
      email: student.email,
      cpf: student.cpf
    });
    this.showForm.set(true);
  }

  saveStudent(): void {
    if (this.studentForm.valid) {
      const request = this.studentForm.getRawValue();
      const studentId = this.editingStudentId();

      if (studentId) {
        this.studentService.update(studentId, request).subscribe({
          next: (updatedStudent) => {
            this.students.update(all => all.map(s => s.id === studentId ? updatedStudent : s));
            this.toggleForm();
            alert('Estudante atualizado com sucesso!');
          },
          error: (err) => alert('Erro ao atualizar: ' + (err.error?.message || 'Dados inválidos'))
        });
      } else {
        this.studentService.create(request).subscribe({
          next: (newStudent) => {
            this.students.update(all => [newStudent, ...all]);
            this.toggleForm();
            alert('Estudante cadastrado com sucesso!');
          },
          error: (err) => alert('Erro ao cadastrar: ' + (err.error?.message || 'Dados inválidos'))
        });
      }
    }
  }

  deleteStudent(id: number): void {
    if (confirm('Deseja realmente remover este estudante do sistema?')) {
      this.studentService.softDelete(id).subscribe({
        next: () => this.students.update(all => all.filter(s => s.id !== id)),
        error: (err) => alert('Erro ao deletar estudante.')
      });
    }
  }
}

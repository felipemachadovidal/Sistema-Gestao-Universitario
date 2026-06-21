import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StudentService, StudentResponse } from '../../core/services/student.service';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-student-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './student-list.component.html',
  styleUrl: './student-list.component.scss'
})
export class StudentListComponent implements OnInit {
  private studentService = inject(StudentService);

  // Usando Signal para gerenciar o estado dos estudantes de forma performática
  students = signal<StudentResponse[]>([]);
  isLoading = signal<boolean>(true);

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
      error: () => {
        this.isLoading.set(false);
      }
    });
  }

  deleteStudent(id: number): void {
    if (confirm('Tem certeza que deseja remover este aluno?')) {
      this.studentService.softDelete(id).subscribe({
        next: () => {
          // Atualiza a lista local removendo o item deletado sem precisar recarregar a página
          this.students.update(all => all.filter(s => s.id !== id));
        }
      });
    }
  }
}

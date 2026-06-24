import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface StudentRequest {
  name: string;
  email: string;
  cpf: string;
}

export interface StudentResponse {
  id: number;
  name: string;
  email: string;
  cpf: string;
  registrationDate: string;
}

@Injectable({
  providedIn: 'root'
})
export class StudentService {
  private readonly apiUrl = 'http://localhost:8081/api/students';

  constructor(private http: HttpClient) {}

  listAll(): Observable<StudentResponse[]> {
    return this.http.get<StudentResponse[]>(this.apiUrl);
  }

  create(student: StudentRequest): Observable<StudentResponse> {
    return this.http.post<StudentResponse>(this.apiUrl, student);
  }

  softDelete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  update(id: number, student: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, student);
  }
}

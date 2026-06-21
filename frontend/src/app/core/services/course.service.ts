import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CourseRequest {
  name: string;
  description: string;
  durationHours: number;
}

export interface CourseResponse {
  id: number;
  name: string;
  description: string;
  durationHours: number;
}

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  private readonly apiUrl = 'http://localhost:8081/api/courses';

  constructor(private http: HttpClient) {}

  listAll(): Observable<CourseResponse[]> {
    return this.http.get<CourseResponse[]>(this.apiUrl);
  }

  create(course: CourseRequest): Observable<CourseResponse> {
    return this.http.post<CourseResponse>(this.apiUrl, course);
  }

  softDelete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  enrollStudent(courseId: number, studentId: number): Observable<any> {
      return this.http.post<any>(`${this.apiUrl}/${courseId}/enroll/${studentId}`, {});
    }

  listStudentsEnrolled(courseId: number): Observable<StudentResponse[]> {
      return this.http.get<StudentResponse[]>(`${this.apiUrl}/${courseId}/students`);
    }
}

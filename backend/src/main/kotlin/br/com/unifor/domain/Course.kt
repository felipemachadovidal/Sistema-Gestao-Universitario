package br.com.unifor.domain

import jakarta.persistence.*

@Entity
@Table(name = "courses")
class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    var name: String? = null

    @Column(length = 500)
    var description: String? = null

    @Column(name = "duration_hours", nullable = false)
    var durationHours: Int? = null

    @Column(nullable = false)
    var deleted: Boolean = false

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "enrollments",
        joinColumns = [JoinColumn(name = "course_id")],
        inverseJoinColumns = [JoinColumn(name = "student_id")]
    )
    var students: MutableList<Student> = mutableListOf()
}
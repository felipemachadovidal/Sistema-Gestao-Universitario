package br.com.unifor.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "students")
class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false, unique = true)
    var cpf: String? = null

    @Column(nullable = false)
    var name: String? = null

    @Column(nullable = false, unique = true)
    var email: String? = null

    @Column(name = "registration_date", nullable = false)
    var registrationDate: LocalDate = LocalDate.now()

    @Column(nullable = false)
    var deleted: Boolean = false

    @ManyToMany(mappedBy = "students", fetch = FetchType.LAZY)
    var courses: MutableList<Course> = mutableListOf()
}
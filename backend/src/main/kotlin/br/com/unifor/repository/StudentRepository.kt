package br.com.unifor.repository

import br.com.unifor.domain.Student
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class StudentRepository : PanacheRepository<Student> {

    fun listActive(): List<Student> {
        return list("deleted = false")
    }

    fun findActiveById(id: Long): Student? {
        return find("id = ?1 and deleted = false", id).firstResult()
    }

    fun findByCpf(cpf: String): Student? {
        return find("cpf = ?1", cpf).firstResult()
    }

    fun findByEmail(email: String): Student? {
        return find("email = ?1", email).firstResult()
    }
}
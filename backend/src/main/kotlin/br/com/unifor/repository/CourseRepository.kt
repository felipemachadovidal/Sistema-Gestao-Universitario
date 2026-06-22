package br.com.unifor.repository

import br.com.unifor.domain.Course
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class CourseRepository : PanacheRepository<Course> {

    fun listActive(): List<Course> {
        return list("deleted = false")
    }

    fun findActiveById(id: Long): Course? {
        return find("id = ?1 and deleted = false", id).firstResult()
    }
}
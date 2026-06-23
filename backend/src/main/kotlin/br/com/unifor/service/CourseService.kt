package br.com.unifor.service

import br.com.unifor.domain.Course
import br.com.unifor.dto.CourseRequest
import br.com.unifor.dto.CourseResponse
import br.com.unifor.dto.StudentResponse
import br.com.unifor.repository.CourseRepository
import br.com.unifor.repository.StudentRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import org.jboss.logging.Logger

@ApplicationScoped
class CourseService(
    private val courseRepository: CourseRepository,
    private val studentRepository: StudentRepository
) {

    private val log: Logger = Logger.getLogger(CourseService::class.java)

    fun listAllActive(): List<CourseResponse> {
        log.info("Buscando todos os cursos ativos.")
        return courseRepository.listActive().map { it.toResponse() }
    }

    fun findActiveById(id: Long): CourseResponse {
        log.info("Buscando curso ativo com ID: $id")
        val course = courseRepository.findActiveById(id) ?: run {
            log.warn("Curso ID $id não encontrado ou excluído.")
            throw NotFoundException("Curso com ID $id não encontrado ou foi excluído.")
        }
        return course.toResponse()
    }

    @Transactional
    fun create(request: CourseRequest): CourseResponse {
        log.info("Cadastrando novo curso: ${request.name}")

        if (request.durationHours > 500) {
            throw jakarta.ws.rs.WebApplicationException(
                "A carga horária máxima permitida é de 500 horas.",
                jakarta.ws.rs.core.Response.Status.BAD_REQUEST
            )
        }
        val course = Course().apply {
            name = request.name
            description = request.description
            durationHours = request.durationHours
        }
        courseRepository.persist(course)
        log.info("Curso '${course.name}' cadastrado com sucesso! ID: ${course.id}")
        return course.toResponse()
    }

    @Transactional
    fun update(id: Long, request: CourseRequest): CourseResponse {
        log.info("Iniciando atualização do curso ID: $id")
        val course = courseRepository.findActiveById(id) ?: run {
            log.warn("Curso ID $id não encontrado para atualização.")
            throw NotFoundException("Curso não encontrado para atualização.")
        }

        course.name = request.name
        course.description = request.description
        course.durationHours = request.durationHours

        log.info("Curso ID $id atualizado com sucesso.")
        return course.toResponse()
    }

    @Transactional
    fun softDelete(id: Long) {
        log.info("Executando Soft Delete para o curso ID: $id")
        val course = courseRepository.findActiveById(id) ?: run {
            log.warn("Soft Delete falhou: Curso ID $id não encontrado.")
            throw NotFoundException("Curso não encontrado ou já excluído.")
        }
        course.deleted = true
        log.info("Curso ID $id desativado com sucesso.")
    }

    @Transactional
    fun enrollStudent(courseId: Long, studentId: Long) {
        log.info("Iniciando processo de matrícula: Aluno ID $studentId no Curso ID $courseId")

        val course = courseRepository.findActiveById(courseId) ?: run {
            log.error("Matrícula falhou: Curso ID $courseId não existe.")
            throw NotFoundException("Curso não encontrado.")
        }
        val student = studentRepository.findActiveById(studentId) ?: run {
            log.error("Matrícula falhou: Aluno ID $studentId não existe.")
            throw NotFoundException("Aluno não encontrado.")
        }

        if (course.students.contains(student)) {
            log.warn("Matrícula negada: Aluno ID $studentId já está matriculado no curso ID $courseId.")
            throw WebApplicationException("Este aluno já está matriculado neste curso.", Response.Status.CONFLICT)
        }

        course.students.add(student)
        log.info("Matrícula realizada com sucesso! Aluno ID $studentId vinculado ao Curso ID $courseId.")
    }

    fun listStudentsEnrolled(courseId: Long): List<StudentResponse> {
        log.info("Listando alunos matriculados no curso ID: $courseId")
        val course = courseRepository.findActiveById(courseId) ?: run {
            log.warn("Busca de matriculados falhou: Curso ID $courseId não existe.")
            throw NotFoundException("Curso não encontrado.")
        }

        return course.students
            .filter { !it.deleted }
            .map { student ->
                StudentResponse(
                    id = student.id!!,
                    name = student.name!!,
                    email = student.email!!,
                    cpf = student.cpf!!,
                    registrationDate = student.registrationDate
                )
            }
    }

    @Transactional
    fun unenrollStudent(courseId: Long, studentId: Long) {
        log.info("Iniciando cancelamento de matrícula: Aluno ID $studentId no Curso ID $courseId")

        val course = courseRepository.findActiveById(courseId) ?: run {
            log.error("Cancelamento de matrícula falhou: Curso ID $courseId não existe.")
            throw NotFoundException("Curso não encontrado.")
        }
        val student = studentRepository.findActiveById(studentId) ?: run {
            log.error("Cancelamento de matrícula falhou: Aluno ID $studentId não existe.")
            throw NotFoundException("Aluno não encontrado.")
        }

        if (!course.students.contains(student)) {
            log.warn("Cancelamento negado: Aluno ID $studentId não está matriculado no curso ID $courseId.")
            throw WebApplicationException("Este aluno não está matriculado neste curso.", Response.Status.BAD_REQUEST)
        }

        course.students.remove(student)
        log.info("Matrícula removida com sucesso! Aluno ID $studentId desvinculado do Curso ID $courseId.")
    }

    private fun Course.toResponse() = CourseResponse(
        id = this.id!!,
        name = this.name!!,
        description = this.description,
        durationHours = this.durationHours!!
    )
}
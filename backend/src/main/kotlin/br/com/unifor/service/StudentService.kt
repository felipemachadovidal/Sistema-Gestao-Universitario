package br.com.unifor.service

import br.com.unifor.domain.Student
import br.com.unifor.dto.StudentRequest
import br.com.unifor.dto.StudentResponse
import br.com.unifor.repository.StudentRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import org.jboss.logging.Logger

@ApplicationScoped
class StudentService(
    private val studentRepository: StudentRepository
) {

    private val log: Logger = Logger.getLogger(StudentService::class.java)

    fun listAllActive(): List<StudentResponse> {
        log.info("Buscando todos os alunos ativos no sistema.")
        return studentRepository.listActive().map { it.toResponse() }
    }

    fun findActiveById(id: Long): StudentResponse {
        log.info("Buscando aluno ativo com ID: $id")
        val student = studentRepository.findActiveById(id) ?: run {
            log.warn("Tentativa de busca falhou: Aluno com ID $id não encontrado ou deletado.")
            throw NotFoundException("Aluno com ID $id não encontrado ou foi excluído.")
        }
        return student.toResponse()
    }

    @Transactional
    fun create(request: StudentRequest): StudentResponse {
        log.info("Iniciando processo de cadastro para o aluno com CPF: ${request.cpf}")

        val cleanCpf = request.cpf.replace(Regex("[^0-9]"), "")
        if (cleanCpf.length != 11) {
            log.error("Falha ao cadastrar: CPF ${request.cpf} não possui 11 dígitos numéricos.")
            throw WebApplicationException("O CPF deve conter exatamente 11 dígitos numéricos.", Response.Status.BAD_REQUEST)
        }

        if (studentRepository.findByCpf(cleanCpf) != null) {
            log.error("Falha ao cadastrar aluno: CPF $cleanCpf já está em uso.")
            throw WebApplicationException("Já existe um aluno cadastrado com este CPF.", Response.Status.CONFLICT)
        }
        if (studentRepository.findByEmail(request.email) != null) {
            log.error("Falha ao cadastrar aluno: E-mail ${request.email} já está em uso.")
            throw WebApplicationException("Já existe um aluno cadastrado com este e-mail.", Response.Status.CONFLICT)
        }

        val student = Student().apply {
            name = request.name
            email = request.email
            cpf = cleanCpf
        }

        studentRepository.persist(student)

        log.info("Aluno cadastrado com sucesso! ID gerado: ${student.id}")
        return student.toResponse()
    }

    @Transactional
    fun update(id: Long, request: StudentRequest): StudentResponse {
        log.info("Iniciando atualização do aluno ID: $id")
        val student = studentRepository.findById(id)
            ?: throw NotFoundException("Aluno não encontrado para atualização.")

        if (student.cpf != request.cpf && studentRepository.findByCpf(request.cpf) != null) {
            log.error("Falha ao atualizar aluno ID $id: Novo CPF ${request.cpf} já pertence a outro usuário.")
            throw WebApplicationException("O novo CPF informado já está em uso.", Response.Status.CONFLICT)
        }

        if (student.deleted) {
            log.warn("Tentativa de alteração negada: Aluno ID $id está inativo/deletado.")
            throw WebApplicationException("Não é permitido atualizar os dados de um aluno excluído.", Response.Status.BAD_REQUEST)
        }

        student.name = request.name
        student.email = request.email
        student.cpf = request.cpf

        log.info("Aluno ID $id atualizado com sucesso no banco de dados.")
        return student.toResponse()
    }

    @Transactional
    fun softDelete(id: Long) {
        log.info("Executando Soft Delete para o aluno ID: $id")
        val student = studentRepository.findActiveById(id) ?: run {
            log.warn("Soft Delete falhou: Aluno ID $id não encontrado.")
            throw NotFoundException("Aluno não encontrado ou já excluído.")
        }
        student.deleted = true
        log.info("Aluno ID $id marcado como 'deleted=true' com sucesso.")
    }

    @Transactional
    fun hardDelete(id: Long) {
        log.info("⚠️ Executando Hard Delete (exclusão física) para o aluno ID: $id")
        val deleted = studentRepository.deleteById(id)
        if (!deleted) {
            log.error("Hard Delete falhou: Aluno ID $id não encontrado.")
            throw NotFoundException("Aluno não encontrado para exclusão física.")
        }
        log.info("Aluno ID $id removido permanentemente do banco de dados.")
    }

    private fun Student.toResponse() = StudentResponse(
        id = this.id!!,
        name = this.name!!,
        email = this.email!!,
        cpf = this.cpf!!,
        registrationDate = this.registrationDate
    )
}
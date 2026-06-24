package br.com.unifor.controller

import br.com.unifor.dto.StudentRequest
import br.com.unifor.dto.StudentResponse
import br.com.unifor.service.StudentService
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class StudentController(
    private val studentService: StudentService
) {

    @GET
    fun getAll(): List<StudentResponse> {
        return studentService.listAllActive()
    }

    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: Long): StudentResponse {
        return studentService.findActiveById(id)
    }

    @POST
    fun create(@Valid request: StudentRequest): Response {
        val createdStudent = studentService.create(request)
        return Response.status(Response.Status.CREATED).entity(createdStudent).build()
    }

    @PUT
    @Path("/{id}")
    fun update(@PathParam("id") id: Long, @Valid request: StudentRequest): StudentResponse {
        return studentService.update(id, request)
    }

    @DELETE
    @Path("/{id}")
    fun softDelete(@PathParam("id") id: Long): Response {
        studentService.softDelete(id)
        return Response.noContent().build()
    }

    @DELETE
    @Path("/{id}/permanent")
    fun hardDelete(@PathParam("id") id: Long): Response {
        studentService.hardDelete(id)
        return Response.noContent().build()
    }
}
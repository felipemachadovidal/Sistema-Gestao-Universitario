package br.com.unifor.controller

import br.com.unifor.dto.CourseRequest
import br.com.unifor.dto.CourseResponse
import br.com.unifor.dto.StudentResponse
import br.com.unifor.service.CourseService
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response

@Path("/courses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CourseController(
    private val courseService: CourseService
) {

    @GET
    fun getAll(): List<CourseResponse> {
        return courseService.listAllActive()
    }

    @GET
    @Path("/{id}")
    fun getById(@PathParam("id") id: Long): CourseResponse {
        return courseService.findActiveById(id)
    }

    @POST
    fun create(@Valid request: CourseRequest): Response {
        val createdCourse = courseService.create(request)
        return Response.status(Response.Status.CREATED).entity(createdCourse).build()
    }

    @PUT
    @Path("/{id}")
    fun update(@PathParam("id") id: Long, @Valid request: CourseRequest): CourseResponse {
        return courseService.update(id, request)
    }

    @DELETE
    @Path("/{id}")
    fun delete(@PathParam("id") id: Long): Response {
        courseService.softDelete(id)
        return Response.noContent().build()
    }

    @POST
    @Path("/{courseId}/enroll/{studentId}")
    fun enrollStudent(
        @PathParam("courseId") courseId: Long,
        @PathParam("studentId") studentId: Long
    ): Response {
        courseService.enrollStudent(courseId, studentId)
        return Response.ok().entity(mapOf("message" to "Aluno matriculado com sucesso!")).build()
    }

    @GET
    @Path("/{courseId}/students")
    fun getEnrolledStudents(@PathParam("courseId") courseId: Long): List<StudentResponse> {
        return courseService.listStudentsEnrolled(courseId)
    }

    @DELETE
    @Path("/{courseId}/unenroll/{studentId}")
    fun unenrollStudent(
        @PathParam("courseId") courseId: Long,
        @PathParam("studentId") studentId: Long
    ): Response {
        courseService.unenrollStudent(courseId, studentId)
        return Response.noContent().build()
    }
}
package br.com.zup.hugovallada.util.handler

import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Produces
import io.micronaut.http.hateoas.JsonError
import io.micronaut.http.server.exceptions.ExceptionHandler
import io.micronaut.validation.exceptions.ConstraintExceptionHandler
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Produces
@Singleton
@Requires(classes = [ConstraintViolationException::class])
@Replaces(ConstraintExceptionHandler::class)
class ApiExceptionHandler : ExceptionHandler<ConstraintViolationException, HttpResponse<Any>> {
    override fun handle(request: HttpRequest<*>?, exception: ConstraintViolationException?): HttpResponse<Any> {
        val errors = exception!!.message!!.split(",")
        val listaDeErros = mutableListOf<ApiErrorResponse>()
        errors.forEach {
            error ->
            listaDeErros.add(geraApiErrorResponse(error))
        }

        val response = mapOf(Pair("Errors", listaDeErros))
        return HttpResponse.badRequest<Any?>().body(response)
    }

    private fun geraApiErrorResponse(error: String) =
        ApiErrorResponse(error.split(":")[0].substringAfterLast("."), error.split(":")[1])
}

data class ApiErrorResponse(val campo: String, val message: String)
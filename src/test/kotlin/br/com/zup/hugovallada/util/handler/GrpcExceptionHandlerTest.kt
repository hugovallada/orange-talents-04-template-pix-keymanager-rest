package br.com.zup.hugovallada.util.handler

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.hateoas.JsonError
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class GrpcExceptionHandlerTest{
    val requestGenerica = HttpRequest.GET<Any>("/")

    @Test
    internal fun `deve retornar 404 quando status exception for not found`() {
        val notFoundException = StatusRuntimeException(Status.NOT_FOUND.withDescription("Dado não encontrado"))

         val resposta = GrpcExceptionHandler().handle(requestGenerica, notFoundException)

        assertEquals(HttpStatus.NOT_FOUND, resposta.status)
        assertNotNull(resposta.body())
        assertEquals("Dado não encontrado", (resposta.body() as JsonError).message)
    }

    @Test
    internal fun `deve retornar 422 quando status exception for already exists`() {
        val mensagem = "Já existe uma chave com esse valor"
        val alreadyExistsException = StatusRuntimeException(Status.ALREADY_EXISTS.withDescription(mensagem))
        val resposta = GrpcExceptionHandler().handle(requestGenerica, alreadyExistsException)

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, resposta.status)
        assertNotNull(resposta.body())
        assertEquals(mensagem, (resposta.body() as JsonError).message)
    }

    @Test
    internal fun `deve retornar 400 quando status exception for invalid argument`() {
        val mensagem = "Dados inválidos"
        val invalidArgumentException = StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(mensagem))
        val resposta = GrpcExceptionHandler().handle(requestGenerica, invalidArgumentException)

        assertEquals(HttpStatus.BAD_REQUEST, resposta.status)
        assertNotNull(resposta.body())
        assertEquals(mensagem, (resposta.body() as JsonError).message)

    }

    @Test
    internal fun `deve retornar 400 quando status exception for failed precondition`() {
        val mensagem = "Erro de pré-condição. Verifique seus dados"
        val failedPreConditionException = StatusRuntimeException(Status.FAILED_PRECONDITION.withDescription(mensagem))
        val resposta = GrpcExceptionHandler().handle(requestGenerica, failedPreConditionException)

        assertEquals(HttpStatus.BAD_REQUEST, resposta.status)
        assertNotNull(resposta.body())
        assertEquals(mensagem, (resposta.body() as JsonError).message)

    }

    @Test
    internal fun `deve retornar 403 quando nao o status for permission denied`() {
        val mensagem = "Você não tem permissão de acesso"
        val invalidArgumentException = StatusRuntimeException(Status.PERMISSION_DENIED.withDescription(mensagem))
        val resposta = GrpcExceptionHandler().handle(requestGenerica, invalidArgumentException)

        assertEquals(HttpStatus.FORBIDDEN, resposta.status)
        assertNotNull(resposta.body())
        assertEquals(mensagem, (resposta.body() as JsonError).message)
    }

    @Test
    internal fun `deve retornar 500 quando status exception for um erro inesperado`() {
        val mensagem = "Um erro inesperado aconteceu"
        val unexpectedException = StatusRuntimeException(Status.UNKNOWN.withDescription(mensagem))
        val resposta = GrpcExceptionHandler().handle(requestGenerica, unexpectedException)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.status)
        assertNotNull(resposta.body())
        assertEquals(mensagem, (resposta.body() as JsonError).message)

    }
}
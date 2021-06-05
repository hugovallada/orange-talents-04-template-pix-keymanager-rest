package br.com.zup.hugovallada.pix

import br.com.zup.hugovallada.TipoDeChave
import br.com.zup.hugovallada.TipoDeConta
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class PixControllerTest{

    @field:Client("/")
    @Inject
    lateinit var client: HttpClient

    @Test
    internal fun `nao deve cadastrar uma chave quando um dado de entrada for invalido`() {
        val request = HttpRequest.POST("/api/v1/pix", geraChavePix(false, true, "CPF"))

        assertThrows<HttpClientResponseException>{
            client.toBlocking().exchange(request,HttpResponse::class.java)
        }.run {
            assertEquals(HttpStatus.BAD_REQUEST, response.status)
        }
    }

    @Test // TODO: Mockar grpc ???
    internal fun `deve cadastrar uma chave  quando os dados forem validos`() {
        val request = HttpRequest.POST("/api/v1/pix",geraChavePix(true,true,"CHAVE_ALEATORIA"))
        val response = client.toBlocking().exchange(request, HttpResponse::class.java)

        with(response){
            assertEquals(HttpStatus.CREATED, response.status)
        }
    }

    private fun geraChavePix(randomica: Boolean, idValido: Boolean, tipo: String): NovaChavePixRequest {
        return NovaChavePixRequest(
            idCliente = if (idValido) UUID.randomUUID().toString() else "naoEValido",
            valor = if(randomica) null else "email@email.com",
            tipoDeChave = TipoDeChave.valueOf(tipo),
            tipoDeConta = TipoDeConta.CONTA_CORRENTE
        )
    }
}
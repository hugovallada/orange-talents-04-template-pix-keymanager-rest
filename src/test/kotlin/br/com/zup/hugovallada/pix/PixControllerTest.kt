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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class PixControllerTest {

    @field:Client("/api/v1/pix")
    @Inject
    lateinit var client: HttpClient

    @ParameterizedTest
    @CsvSource(
        value = [
            "email2email, EMAIL",
            "valores, TELEFONE_CELULAR",
            "1111111111, CPF",
            "novidade, CHAVE_ALEATORIA"
        ]
    )
    internal fun `nao deve cadastrar uma chave quando nao for uma pix valida`(chave: String, tipo: String) {
        val pixRequest = NovaChavePixRequest(UUID.randomUUID().toString(),chave, TipoDeChave.valueOf(tipo), TipoDeConta.CONTA_CORRENTE)
        val request = HttpRequest.POST("/", pixRequest)

        assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, HttpResponse::class.java)
        }.run {
            assertEquals(HttpStatus.BAD_REQUEST, response.status)
        }
    }

    @Test // TODO: Mockar grpc ???
    internal fun `deve cadastrar uma chave  quando os dados forem validos`() {
        val chave = geraChavePix(true, true, "CHAVE_ALEATORIA")

        val request = HttpRequest.POST("/", chave)


        val response = client.toBlocking().exchange(request, HttpResponse::class.java)

        with(response) {
            assertEquals(HttpStatus.CREATED, response.status)
        }
    }

    @Test
    internal fun `nao deve cadastrar uma chave quando  o cliente nao for encontrado e deve retornar um status 404`() {
        val request = HttpRequest.POST("/", NovaChavePixRequest(UUID.randomUUID().toString(), tipoDeConta = TipoDeConta.CONTA_CORRENTE, tipoDeChave = TipoDeChave.CHAVE_ALEATORIA))

       assertThrows<HttpClientResponseException> {
           val response = client.toBlocking().exchange(request, HttpResponse::class.java)
            with(response){
                assertTrue(status == HttpStatus.NOT_FOUND)

            }
       }
    }

    private fun geraChavePix(randomica: Boolean, idValido: Boolean, tipo: String): NovaChavePixRequest {
        return NovaChavePixRequest(
            idCliente = if (idValido) "c56dfef4-7901-44fb-84e2-a2cefb157890" else "naoEValido",
            valor = if (randomica) null else "email@email.com",
            tipoDeChave = TipoDeChave.valueOf(tipo),
            tipoDeConta = TipoDeConta.CONTA_CORRENTE
        )
    }
}
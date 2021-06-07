package br.com.zup.hugovallada.pix

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class PixControllerTest {

    @field:Client("/api/v1/clientes")
    @Inject
    lateinit var client: HttpClient

    @ParameterizedTest
    @CsvSource(
        value = [
            "email2email, EMAIL",
            "valores, TELEFONE_CELULAR",
            "43898432298, CPF",
            "novidade, CHAVE_ALEATORIA"
        ]
    )
    internal fun `nao deve cadastrar uma chave quando nao for uma pix valida`(chave: String, tipo: String) {
        val pixRequest = NovaChavePixRequest(chave, TipoDeChaveRequest.valueOf(tipo), TipoDeContaRequest.CONTA_CORRENTE)
        val request = HttpRequest.POST("/${UUID.randomUUID().toString()}/pix", pixRequest)

        assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(request, HttpResponse::class.java)
        }.run {
            assertEquals(HttpStatus.BAD_REQUEST, response.status)
        }
    }

    @Test // TODO: Mockar grpc ???
    internal fun `deve cadastrar uma chave  quando os dados forem validos`() {
        val chave = NovaChavePixRequest("30329879030",TipoDeChaveRequest.CPF,TipoDeContaRequest.CONTA_CORRENTE)

        val request = HttpRequest.POST("/c56dfef4-7901-44fb-84e2-a2cefb157890/pix", chave)


        val response = client.toBlocking().exchange(request, HttpResponse::class.java)

        with(response) {
            assertEquals(HttpStatus.CREATED, response.status)
        }
    }

    @Test
    internal fun `nao deve cadastrar uma chave quando  o cliente nao for encontrado e deve retornar um status 404`() {
        val request = HttpRequest.POST("/${UUID.randomUUID().toString()}/pix", NovaChavePixRequest(tipoDeConta = TipoDeContaRequest.CONTA_CORRENTE, tipoDeChave = TipoDeChaveRequest.CHAVE_ALEATORIA))

       assertThrows<HttpClientResponseException> {
           val response = client.toBlocking().exchange(request, HttpResponse::class.java)
            with(response){
                assertTrue(status == HttpStatus.NOT_FOUND)

            }
       }
    }

    @Nested
    inner class Unprocessable{


        @BeforeEach
        internal fun setUp() {
            val chave = NovaChavePixRequest("87923661049",TipoDeChaveRequest.CPF,TipoDeContaRequest.CONTA_CORRENTE)

            val request = HttpRequest.POST("/c56dfef4-7901-44fb-84e2-a2cefb157890/pix", chave)
            val response = client.toBlocking().exchange(request, HttpResponse::class.java)


        }

        @Test
        internal fun `deve retornar um status processable entity quando tentar cadastrar uma chave que ja existe`() {
            val chave = NovaChavePixRequest("87923661049",TipoDeChaveRequest.CPF,TipoDeContaRequest.CONTA_CORRENTE)

            val request = HttpRequest.POST("/c56dfef4-7901-44fb-84e2-a2cefb157890/pix", chave)

            assertThrows<HttpClientResponseException> {
                val response = client.toBlocking().exchange(request, HttpResponse::class.java)

                with(response) {
                    assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.status)
                    assertEquals("Essa chave já está cadastrada", response.body().getAttribute("message"))
                }
            }

        }
    }

    private fun geraChavePix(randomica: Boolean, idValido: Boolean, tipo: String): NovaChavePixRequest {
        return NovaChavePixRequest(
            valor = if (randomica) null else "email@email.com",
            tipoDeChave = TipoDeChaveRequest.valueOf(tipo),
            tipoDeConta = TipoDeContaRequest.CONTA_CORRENTE
        )
    }
}
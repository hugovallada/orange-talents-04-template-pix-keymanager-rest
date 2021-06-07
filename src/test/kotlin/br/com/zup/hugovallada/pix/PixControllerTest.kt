package br.com.zup.hugovallada.pix

import br.com.zup.hugovallada.CadastraChavePixGrpcResponse
import br.com.zup.hugovallada.KeyManagerGrpcServiceGrpc
import br.com.zup.hugovallada.util.factory.GrpcClientFactory
import io.grpc.Status
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
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
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class PixControllerTest {

    @field:Inject
    lateinit var registraStub: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub

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

    @Test
    internal fun `deve cadastrar uma chave  quando os dados forem validos`() {
        val chave = NovaChavePixRequest("30329879030",TipoDeChaveRequest.CPF,TipoDeContaRequest.CONTA_CORRENTE)
        val pixId = UUID.randomUUID().toString()
        val request = HttpRequest.POST("/c56dfef4-7901-44fb-84e2-a2cefb157890/pix", chave)

        val respostaGrpc = CadastraChavePixGrpcResponse.newBuilder()
            .setId(pixId).build()

        BDDMockito.given(registraStub.cadastrarChave(Mockito.any()))
            .willReturn(respostaGrpc)


        val response = client.toBlocking().exchange(request, HttpResponse::class.java)

        with(response) {
            assertEquals(HttpStatus.CREATED, response.status)
            assertTrue(response.headers.contains("Location"))
            assertTrue(response.header("Location")!!.contains(pixId))
        }
    }

    // Mocka o gRPC cliente
    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class MockitoStubFactory{

        @Singleton
        fun stubMock() = Mockito.mock(KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub::class.java)
    }
}
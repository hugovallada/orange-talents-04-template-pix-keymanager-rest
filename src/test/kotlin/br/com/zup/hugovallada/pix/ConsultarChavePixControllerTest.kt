package br.com.zup.hugovallada.pix

import br.com.zup.hugovallada.*
import br.com.zup.hugovallada.SearchPixKeyServiceGrpc.SearchPixKeyServiceBlockingStub
import br.com.zup.hugovallada.util.factory.GrpcClientFactory
import com.google.protobuf.Timestamp
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class ConsultarChavePixControllerTest{

    @Inject
    lateinit var grpcClient: SearchPixKeyServiceBlockingStub

    @Inject
    @field:Client("/api/v1/clientes")
    lateinit var client: HttpClient

    @Test
    internal fun `deve retornar um status 200 com rum corpo de resposta ao passar dados validos`() {
        val chaveId = UUID.randomUUID().toString()
        val clienteId = UUID.randomUUID().toString()

        BDDMockito.given(grpcClient.consultarChave(DadosDeConsultaGrpcRequest.newBuilder()
            .setPixId(DadosDeConsultaGrpcRequest.DadosPorPixId.newBuilder()
                .setPixId(chaveId).setClienteId(clienteId).build()).build()))
            .willReturn(geraResponseGrpc(clienteId, chaveId))

        val request = HttpRequest.GET<Any>("/$clienteId/pix/$chaveId")
        val response = client.toBlocking().exchange(request, DadosConsultaPixResponse::class.java)



        with(response){
            assertEquals(HttpStatus.OK, status)
            assertNotNull(body)
            assertEquals("email@email.com", body()!!.dadosChave.chave)
            assertEquals(chaveId, body()!!.idPix)
            assertEquals(clienteId, body()!!.idCliente)
            assertEquals("ITAU UNIBANCO S.A", body()!!.dadosChave.conta.instituicao)
            assertEquals("888292",body()!!.dadosChave.conta.numeroDaConta)
            assertEquals("99922",body()!!.dadosChave.conta.agencia)
            assertEquals("Hugo", body()!!.dadosChave.conta.nomeDoTitular)
            assertEquals("42222211", body()!!.dadosChave.conta.cpfDoTitular)
            assertEquals(TipoDeChave.EMAIL, body()!!.dadosChave.tipo)

        }

    }

    private fun geraResponseGrpc(clienteId: String, pixId: String): DadosChavePixGrpcResponse? {
        return DadosChavePixGrpcResponse.newBuilder()
            .setIdCliente(clienteId)
            .setIdPix(pixId)
            .setChavePix(
                DadosChavePixGrpcResponse.DadosChavePix.newBuilder()
                    .setTipo(TipoDeChave.EMAIL)
                    .setCriadaEm(localDateTimeToTimestamp())
                    .setChave("email@email.com")
                    .setConta(setaConta())
            )
            .build()

    }

    private fun localDateTimeToTimestamp(): Timestamp {
        val data = LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant()
        return Timestamp.newBuilder()
            .setNanos(data.nano)
            .setSeconds(data.epochSecond)
            .build()
    }

    private fun setaConta(): DadosChavePixGrpcResponse.DadosChavePix.DadosConta? {
        return DadosChavePixGrpcResponse.DadosChavePix.DadosConta.newBuilder()
            .setNumeroDaConta("888292")
            .setAgencia("99922")
            .setCpfDoTitular("42222211")
            .setInstituicao("ITAU UNIBANCO S.A")
            .setNomeDoTitular("Hugo")
            .build()
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class GrpcFactory {

        @Singleton
        fun mockSearchKeyStub() = Mockito.mock(SearchPixKeyServiceBlockingStub::class.java)
    }

}
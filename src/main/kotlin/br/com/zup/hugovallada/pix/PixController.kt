package br.com.zup.hugovallada.pix

import br.com.zup.hugovallada.KeyManagerGrpcServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@Controller("/api/v1/clientes/{clienteId}/pix")
@Validated
class PixController(@Inject private val grpcClientCadastro: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Post
    fun cadastrarPix(clienteId: UUID,@Body @Valid cadastroPixRequest: NovaChavePixRequest): HttpResponse<Any> {
        LOGGER.info("Cadastrando uma chave pix...")
        grpcClientCadastro.cadastrarChave(cadastroPixRequest.toGrpc(clienteId)).run {
            return HttpResponse.created(location(clienteId = clienteId, pixId = id))
        }
    }

    private fun location(clienteId: UUID, pixId: String) = HttpResponse
        .uri("/api/v1/pix/$clienteId/pix/$pixId")




}



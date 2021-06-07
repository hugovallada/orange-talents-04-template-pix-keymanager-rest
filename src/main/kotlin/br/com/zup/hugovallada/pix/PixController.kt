package br.com.zup.hugovallada.pix

import br.com.zup.hugovallada.DeletarChavePixGrpcRequest
import br.com.zup.hugovallada.KeyManagerGrpcServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.validation.Valid

@Controller("/api/v1/clientes/{clienteId}/pix")
@Validated
class PixController(@Inject private val grpcClient: KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Post
    fun cadastrarPix(clienteId: UUID,@Body @Valid cadastroPixRequest: NovaChavePixRequest): HttpResponse<Any> {
        LOGGER.info("Cadastrando uma chave pix...")
        grpcClient.cadastrarChave(cadastroPixRequest.toGrpc(clienteId)).run {
            return HttpResponse.created(location(clienteId = clienteId, pixId = id))
        }
    }

    @Delete("/{pixId}")
    fun deletaPix( clienteId: UUID, pixId: UUID): HttpResponse<Any>{
        LOGGER.info("Deletando chave pix...")
        grpcClient.deletarChave(DeletarChavePixGrpcRequest.newBuilder()
            .setIdCliente(clienteId.toString()).setIdPix(pixId.toString()).build()).run {
                return HttpResponse.noContent()
        }
    }


    private fun location(clienteId: UUID, pixId: String) = HttpResponse
        .uri("/api/v1/pix/$clienteId/pix/$pixId")


}



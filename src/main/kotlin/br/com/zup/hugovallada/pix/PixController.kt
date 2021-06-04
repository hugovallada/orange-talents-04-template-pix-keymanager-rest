package br.com.zup.hugovallada.pix

import br.com.zup.hugovallada.CadastraChavePixGrpcRequest
import br.com.zup.hugovallada.KeyManagerGrpcServiceGrpc
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.validation.Valid

@Controller("/api/v1/pix")
@Validated
class PixController(@Inject private val grpcClientCadastro : KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub) {

    @Post
    fun cadastrarPix(@Body @Valid cadastroPixRequest: NovaChavePixRequest): HttpResponse<NovaChavePixResponse>{
        try {
            CadastraChavePixGrpcRequest.newBuilder()
                .setIdCliente(cadastroPixRequest.idCliente)
                .setTipoDeChave(cadastroPixRequest.tipoDeChave)
                .setTipoDeConta(cadastroPixRequest.tipoDeConta)
                .setValorChave(cadastroPixRequest.valor ?: "")
                .build().run {
                    grpcClientCadastro.cadastrarChave(this).run {
                        return HttpResponse.created(NovaChavePixResponse(this))
                    }
                }
        }catch (statusException: StatusRuntimeException){
            println(statusException.message)
            return HttpResponse.badRequest()
        }
    }

}
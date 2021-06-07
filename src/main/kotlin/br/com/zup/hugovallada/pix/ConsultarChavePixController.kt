package br.com.zup.hugovallada.pix

import br.com.zup.hugovallada.DadosDeConsultaGrpcRequest
import br.com.zup.hugovallada.SearchPixKeyServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import java.util.*
import javax.inject.Inject

@Controller("/api/v1/clientes/{clienteId}/pix")
class ConsultarChavePixController(@Inject private val grpcClient: SearchPixKeyServiceGrpc.SearchPixKeyServiceBlockingStub) {


    @Get("/{pixId}")
    fun consultarChave(clienteId:UUID,pixId: UUID) : HttpResponse<DadosConsultaPixResponse> {
            DadosDeConsultaGrpcRequest.newBuilder()
                .setPixId(DadosDeConsultaGrpcRequest.DadosPorPixId.newBuilder()
                    .setClienteId(clienteId.toString())
                    .setPixId(pixId.toString())).build().run{
                        grpcClient.consultarChave(this).run {
                            return HttpResponse.ok(DadosConsultaPixResponse(this))
                        }
                }

        }

}

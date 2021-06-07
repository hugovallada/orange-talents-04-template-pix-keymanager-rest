package br.com.zup.hugovallada.pix

import br.com.zup.hugovallada.DadosDeConsultaGrpcRequest
import br.com.zup.hugovallada.IdDoClienteGrpcRequest
import br.com.zup.hugovallada.ListPixKeyServiceGrpc
import br.com.zup.hugovallada.SearchPixKeyServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import java.util.*
import javax.inject.Inject

@Controller("/api/v1/clientes/{clienteId}/")
class ExibirChavePixController(
    @Inject private val searchKeyGrpcClient: SearchPixKeyServiceGrpc.SearchPixKeyServiceBlockingStub,
    @Inject private val listKeyGrpcClient: ListPixKeyServiceGrpc.ListPixKeyServiceBlockingStub
) {


    @Get("/pix/{pixId}")
    fun consultarChave(clienteId: UUID, pixId: UUID): HttpResponse<DadosConsultaPixResponse> {
        DadosDeConsultaGrpcRequest.newBuilder()
            .setPixId(
                DadosDeConsultaGrpcRequest.DadosPorPixId.newBuilder()
                    .setClienteId(clienteId.toString())
                    .setPixId(pixId.toString())
            ).build().run {
                searchKeyGrpcClient.consultarChave(this).run {
                    return HttpResponse.ok(DadosConsultaPixResponse(this))
                }
            }

    }

    @Get
    fun listarChaves(clienteId: UUID) : HttpResponse<List<ChavePixResponse>>{
        IdDoClienteGrpcRequest.newBuilder().setId(clienteId.toString()).build()
            .run {
                listKeyGrpcClient.listarChaves(this)
                    .run {
                        return HttpResponse.ok(chavesList.map { pix -> ChavePixResponse(pix) }.toList())
                    }
            }
    }


}

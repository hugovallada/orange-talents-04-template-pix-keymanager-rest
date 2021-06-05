package br.com.zup.hugovallada.pix

import br.com.zup.hugovallada.CadastraChavePixGrpcRequest
import br.com.zup.hugovallada.TipoDeChave
import br.com.zup.hugovallada.TipoDeConta
import br.com.zup.hugovallada.util.validator.ValidPixKey
import br.com.zup.hugovallada.util.validator.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
@ValidPixKey
data class NovaChavePixRequest(
    @field:NotBlank @ValidUUID
    val idCliente: String,
    val valor: String? = null,
    @field:NotNull
    val tipoDeChave: TipoDeChave,
    @field:NotNull
    val tipoDeConta: TipoDeConta
){
    fun toGrpc(): CadastraChavePixGrpcRequest {
        return CadastraChavePixGrpcRequest.newBuilder()
            .setIdCliente(idCliente)
            .setTipoDeChave(tipoDeChave)
            .setTipoDeConta(tipoDeConta)
            .setValorChave(valor ?: "")
            .build()
    }
}

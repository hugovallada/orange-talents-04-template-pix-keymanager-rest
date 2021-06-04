package br.com.zup.hugovallada.pix

import br.com.zup.hugovallada.TipoDeChave
import br.com.zup.hugovallada.TipoDeConta
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class NovaChavePixRequest(
    @field:NotBlank
    val idCliente: String,
    val valor: String? = null,
    @field:NotNull
    val tipoDeChave: TipoDeChave,
    @field:NotNull
    val tipoDeConta: TipoDeConta
)

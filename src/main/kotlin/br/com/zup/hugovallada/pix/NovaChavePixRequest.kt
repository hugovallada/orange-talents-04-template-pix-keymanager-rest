package br.com.zup.hugovallada.pix

import br.com.zup.hugovallada.CadastraChavePixGrpcRequest
import br.com.zup.hugovallada.TipoDeChave
import br.com.zup.hugovallada.TipoDeConta
import br.com.zup.hugovallada.util.validator.ValidPixKey
import br.com.zup.hugovallada.util.validator.ValidUUID
import io.micronaut.core.annotation.Introspected
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
@ValidPixKey
data class NovaChavePixRequest(
    val valor: String? = null,
    @field:NotNull
    val tipoDeChave: TipoDeChaveRequest?,
    @field:NotNull
    val tipoDeConta: TipoDeContaRequest?
){
    fun toGrpc(idCliente: UUID): CadastraChavePixGrpcRequest {
        return CadastraChavePixGrpcRequest.newBuilder()
            .setIdCliente(idCliente.toString())
            .setTipoDeChave(tipoDeChave?.atributoGrpc ?: TipoDeChave.DESCONHECIDO)
            .setTipoDeConta(tipoDeConta?.atributoGrpc ?: TipoDeConta.DESCONHECIDA)
            .setValorChave(valor ?: "")
            .build()
    }
}

enum class TipoDeChaveRequest(val atributoGrpc: TipoDeChave){

    CPF(TipoDeChave.CPF){
        override fun valida(chave: String?): Boolean {
            return chave!!.matches("^[0-9]{11}\$".toRegex()) && CPFValidator().run {
                initialize(null)
                isValid(chave, null)
            }
        }
    },
    EMAIL(TipoDeChave.EMAIL){
        override fun valida(chave: String?): Boolean {
            return EmailValidator().run {
                initialize(null)
                isValid(chave, null)
            }
        }
    },

    TELEFONE_CELULAR(TipoDeChave.TELEFONE_CELULAR){
        override fun valida(chave: String?): Boolean {
            return chave!!.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },

    CHAVE_ALEATORIA(TipoDeChave.CHAVE_ALEATORIA){
        override fun valida(chave: String?) = chave.isNullOrBlank()
    };


    abstract fun valida(chave: String?):Boolean

}

enum class TipoDeContaRequest(val atributoGrpc: TipoDeConta){
    CONTA_CORRENTE(TipoDeConta.CONTA_CORRENTE),
    CONTA_POUPANCA(TipoDeConta.CONTA_POUPANCA)
}

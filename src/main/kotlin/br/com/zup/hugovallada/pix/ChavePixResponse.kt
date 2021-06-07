package br.com.zup.hugovallada.pix

import br.com.zup.hugovallada.ListaPixGrpcResponse
import br.com.zup.hugovallada.TipoDeChave
import br.com.zup.hugovallada.TipoDeConta
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class ChavePixResponse(
    val pixId: String,
    val clienteId: String,
    val valor: String,
    val tipo: TipoDeChave,
    val tipoDeConta: TipoDeConta,
    val criadaEm: LocalDateTime
){
    constructor(pixResponse: ListaPixGrpcResponse.ChavePixResponse): this(
        pixId = pixResponse.pixId,
        clienteId = pixResponse.clienteId,
        valor = pixResponse.valor,
        tipo = pixResponse.tipo,
        tipoDeConta = pixResponse.tipoConta,
        criadaEm = pixResponse.criadaEm.let {
            LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneId.of("UTC"))
        }
    )
}

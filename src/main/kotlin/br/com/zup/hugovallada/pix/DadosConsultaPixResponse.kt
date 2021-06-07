package br.com.zup.hugovallada.pix

import br.com.zup.hugovallada.DadosChavePixGrpcResponse
import br.com.zup.hugovallada.TipoDeChave
import br.com.zup.hugovallada.TipoDeConta
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class DadosConsultaPixResponse(
    val idCliente: String,
    val idPix: String,
    val chave: DadosChaveResponse
){
    constructor(dadosConsulta: DadosChavePixGrpcResponse) : this(
        idCliente = dadosConsulta.idCliente,
        idPix = dadosConsulta.idPix,
        chave = DadosChaveResponse(dadosConsulta.chavePix)
    )
}

data class DadosChaveResponse(
    val tipo: TipoDeChave,
    val valor: String,
    val conta: DadosContaResponse,
    val criadaEm: LocalDateTime
){
    constructor(dadosChave: DadosChavePixGrpcResponse.DadosChavePix):this(
        tipo = dadosChave.tipo,
        valor = dadosChave.chave,
        conta = DadosContaResponse(dadosChave.conta),
        criadaEm = dadosChave.criadaEm.run {
            Instant.ofEpochSecond(seconds, nanos.toLong())
                .atZone(ZoneId.of("UTC"))
                .toLocalDateTime()
        }
    )
}

data class DadosContaResponse(
    val tipoDeConta: TipoDeConta,
    val instituicao: String,
    val nomeDoTitular: String,
    val cpfDoTitular: String,
    val agencia: String,
    val numeroDaConta: String
){
    constructor(dadosConta: DadosChavePixGrpcResponse.DadosChavePix.DadosConta) : this(
        tipoDeConta = dadosConta.tipo,
        instituicao = dadosConta.instituicao,
        nomeDoTitular = dadosConta.nomeDoTitular,
        cpfDoTitular = dadosConta.cpfDoTitular,
        agencia = dadosConta.agencia,
        numeroDaConta = dadosConta.numeroDaConta
    )
}

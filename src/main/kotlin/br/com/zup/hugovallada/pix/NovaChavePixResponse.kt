package br.com.zup.hugovallada.pix

import br.com.zup.hugovallada.CadastraChavePixGrpcResponse

data class NovaChavePixResponse(
    val idPix: String
) {
    constructor(cadastraChavePixGrpcResponse: CadastraChavePixGrpcResponse): this(idPix = cadastraChavePixGrpcResponse.id)
}

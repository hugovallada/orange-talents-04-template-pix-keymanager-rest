package br.com.zup.hugovallada.util.factory

import br.com.zup.hugovallada.KeyManagerGrpcServiceGrpc
import br.com.zup.hugovallada.SearchPixKeyServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory(@GrpcChannel("pix-manager") private val channel: ManagedChannel) {

    @Singleton
    fun criaDeletaClientStub(): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub?{
        return KeyManagerGrpcServiceGrpc.newBlockingStub(channel)
    }

    @Singleton
    fun consultarClienteStub(): SearchPixKeyServiceGrpc.SearchPixKeyServiceBlockingStub{
        return SearchPixKeyServiceGrpc.newBlockingStub(channel)
    }


}
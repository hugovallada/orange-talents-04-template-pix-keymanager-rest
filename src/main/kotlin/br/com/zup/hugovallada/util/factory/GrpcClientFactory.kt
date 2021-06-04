package br.com.zup.hugovallada.util.factory

import br.com.zup.hugovallada.KeyManagerGrpcServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory {

    @Singleton
    fun cadastrarClientStub(@GrpcChannel(value = "pix-manager") channel: ManagedChannel): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceBlockingStub?{
        return KeyManagerGrpcServiceGrpc.newBlockingStub(channel)
    }
}
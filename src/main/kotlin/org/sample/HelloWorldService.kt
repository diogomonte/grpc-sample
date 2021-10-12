package org.sample

import io.grpc.Server
import io.grpc.ServerBuilder
import org.example.hello.HelloRequest
import org.example.hello.HelloServiceGrpcKt
import org.example.hello.Response

class HelloWorldServer(port: Int) {
    val server: Server = ServerBuilder
        .forPort(port)
        .addService(HelloWorldService())
        .build()

    fun start() {
        server.start()
        Runtime.getRuntime().addShutdownHook(
            Thread {
                this@HelloWorldServer.stop()
            }
        )
    }

    private fun stop() {
        server.shutdown()
    }

    fun blockUntilShutdown() {
        server.awaitTermination()
    }

    private class HelloWorldService: HelloServiceGrpcKt.HelloServiceCoroutineImplBase() {

        override suspend fun helloWorld(request: HelloRequest) = Response.newBuilder()
            .setMessage(request.hello)
            .build()
    }
}

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 50051
    val server = HelloWorldServer(port)
    server.start()
    println("** Server started")
    server.blockUntilShutdown()
}
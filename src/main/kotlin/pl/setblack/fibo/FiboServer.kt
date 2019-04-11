package pl.setblack.fibo

import io.netty.channel.nio.NioEventLoopGroup
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.BodyInserters.fromPublisher
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import reactor.netty.http.server.HttpServer
import reactor.netty.tcp.TcpServer

class FiboServer {
    fun start() {
        val route = router {
            GET("/fib/{n}")
            { request ->
                val n = Integer.parseInt(request.pathVariable("n"))
                println("Thread: "+ Thread.currentThread().name)
                if (n < 2) {

                    ServerResponse.ok().contentType(MediaType.TEXT_HTML).body(fromObject<String>(n.toString()))
                } else {
                    val n_1 = WebClient.create("http://localhost:8080").get().uri("/fib/{n}", n - 1)
                            .accept(MediaType.TEXT_HTML).exchange()
                            .flatMap { resp -> resp.bodyToMono(String::class.java) }
                            .map<Int>{ Integer.parseInt(it) }
                    val n_2 = WebClient.create("http://localhost:8080").get().uri("/fib/{n}", n - 2)
                            .accept(MediaType.TEXT_HTML).exchange().flatMap { resp -> resp.bodyToMono(String::class.java) }
                            .map<Int> { Integer.parseInt(it) }

                    val result = n_1
                            .flatMap { a -> n_2.map { b -> a +b   } }
                            .map<String>( { it.toString() })
                    ServerResponse.ok().contentType(MediaType.TEXT_HTML).body(
                            fromPublisher<String, Mono<String>>(result, String::class.java))
                }
            }
        }

        val httpHandler = RouterFunctions.toHttpHandler(route)
        val adapter = ReactorHttpHandlerAdapter(httpHandler)
        val eventLoopGroup = NioEventLoopGroup(300)
        val tcpServer = TcpServer
                .create()
                .host("0.0.0.0")
                .port(8080)
                .runOn(eventLoopGroup)

        val server = HttpServer
                .from(tcpServer)
                .handle(adapter)
                .bind()
                .block()!!

        readLine()

        server.disposeNow()
    }
}


fun main(args: Array<String>) {
    println("Hello, world!")
    FiboServer().start()

}
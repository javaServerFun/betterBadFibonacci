package pl.setblack.fibo

import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter
import org.springframework.web.reactive.function.BodyInserters.fromObject
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.netty.http.server.HttpServer

class FiboServer {
    fun start() {
        val route = router {
            GET("/fib/{n}")
            { request ->
                val n = Integer.parseInt(request.pathVariable("n"))
                    /*val n_1 = WebClient.create("http://localhost:8080").get().uri("/fib/{n}", n - 1)
                            .accept(MediaType.TEXT_HTML).exchange()
                            .flatMap { resp -> resp.bodyToMono(String::class.java) }
                            .map<Int>{ Integer.parseInt(it) }
                    */
                    //ServerResponse.ok().contentType(MediaType.TEXT_HTML).body(fromPublisher<String, Mono<String>>(result, String::class.java))
                ServerResponse.ok().contentType(MediaType.TEXT_HTML).body(fromObject("Hello world!"))
            }
        }

        val httpHandler = RouterFunctions.toHttpHandler(route)
        val adapter = ReactorHttpHandlerAdapter(httpHandler)
        val server = HttpServer
                .create()
                .host("localhost")
                .port(8080)
                .handle(adapter)
                .bindNow()
        println("press enter")

        readLine()

        server.disposeNow()
    }
}


fun main(args: Array<String>) {
    FiboServer().start()
}
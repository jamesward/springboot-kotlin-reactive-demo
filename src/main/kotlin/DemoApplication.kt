package demo

import io.netty.resolver.DefaultAddressResolverGroup
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import reactor.netty.http.client.HttpClient


@SpringBootApplication
@RestController
class DemoApplication {
	val httpClient = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE)
	val webClient = WebClient.builder().clientConnector(ReactorClientHttpConnector(httpClient)).build()

	@GetMapping("/")
	suspend fun index() = run {
		"todo"
	}

}

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}

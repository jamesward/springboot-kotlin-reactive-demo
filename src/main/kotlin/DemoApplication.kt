package demo

import com.fasterxml.jackson.databind.DeserializationFeature
import org.springframework.fu.kofu.reactiveWebApplication
import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import io.netty.resolver.DefaultAddressResolverGroup
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.buildAndAwait
import reactor.netty.http.client.HttpClient
import com.fasterxml.jackson.module.kotlin.*
import org.springframework.http.codec.json.Jackson2JsonDecoder

val httpClient = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE)
val objectMapper = jacksonObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
val jackson2JsonDecoder = Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON)
val webClient = WebClient.builder()
	.clientConnector(ReactorClientHttpConnector(httpClient))
	.codecs{ it.defaultCodecs().jackson2JsonDecoder(jackson2JsonDecoder) }
	.build()

suspend fun getLatestRelease(request: ServerRequest) = run {
	val url = "https://api.github.com/repos/jetbrains/kotlin/tags"
	val releases = webClient.get().uri(url).retrieve().awaitBody<List<Release>>()

	releases.filterNot{it.name.contains("-")}.firstOrNull()?.name?.let { name ->
		ok().bodyValueAndAwait(name)
	} ?: notFound().buildAndAwait()
}

val app = reactiveWebApplication {
	webFlux {
		port = System.getenv("PORT")?.toInt() ?: 8080
		coRouter {
			GET("/", ::getLatestRelease)
		}
		codecs {
			string()
		}
	}
}

data class Release(val name: String)

fun main() {
	app.run()
}

package demo

import io.netty.resolver.DefaultAddressResolverGroup
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.springframework.fu.kofu.reactiveWebApplication
import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import reactor.netty.http.client.HttpClient

val httpClient = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE)
val json = Json { ignoreUnknownKeys = true }
val decoder = KotlinSerializationJsonDecoder(json)
val webClient = WebClient.builder()
	.clientConnector(ReactorClientHttpConnector(httpClient))
	.codecs { it.defaultCodecs().kotlinSerializationJsonDecoder(decoder) }
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

@Serializable
data class Release(val name: String)

fun main() {
	app.run()
}

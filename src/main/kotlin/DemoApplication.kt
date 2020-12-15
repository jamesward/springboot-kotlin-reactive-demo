package demo

import io.netty.resolver.DefaultAddressResolverGroup
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.reactivestreams.Publisher
import org.springframework.core.ResolvableType
import org.springframework.core.codec.AbstractDecoder
import org.springframework.core.codec.StringDecoder
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.fu.kofu.reactiveWebApplication
import org.springframework.fu.kofu.webflux.webFlux
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder
import org.springframework.http.codec.json.KotlinSerializationJsonEncoder
import org.springframework.util.MimeType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient

val httpClient = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE)
val json = Json { ignoreUnknownKeys = true }
val decoder = object: KotlinSerializationJsonDecoder() {
	val stringDecoder = StringDecoder.allMimeTypes(StringDecoder.DEFAULT_DELIMITERS, false)

	override fun canDecode(elementType: ResolvableType, mimeType: MimeType?): Boolean {
		return true
	}

	override fun decodeToMono(
		inputStream: Publisher<DataBuffer>,
		elementType: ResolvableType,
		mimeType: MimeType?,
		hints: MutableMap<String, Any>?
	): Mono<Any> {
		return stringDecoder
			.decodeToMono(inputStream, elementType, mimeType, hints)
			.map { jsonText -> json.decodeFromString(ListSerializer(Release.serializer()), jsonText) }
	}
}

val webClient = WebClient.builder().clientConnector(ReactorClientHttpConnector(httpClient))
	.codecs { configurer ->
		configurer.defaultCodecs().kotlinSerializationJsonDecoder(decoder)
	}
	.build()

suspend fun getLatestRelease(request: ServerRequest) = run {
	val releases = webClient.get().uri("https://api.github.com/repos/jetbrains/kotlin/tags").retrieve().awaitBody<List<Release>>()

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

package demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import org.springframework.web.reactive.function.client.awaitExchange

@SpringBootApplication
@RestController
class DemoApplication {
	val webClient = WebClient.create()

	@GetMapping("/")
	suspend fun index() = run {
		val releases = webClient.get().uri("https://api.github.com/repos/jetbrains/kotlin/tags").awaitExchange().awaitBodyOrNull<List<Release>>()

		releases?.filterNot{it.name.contains("-")}?.firstOrNull()?.name ?: "not found"
	}

}

data class Release(val name: String)

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}

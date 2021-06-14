package demo

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody


@SpringBootApplication
@RestController
class DemoApplication {
    val webClient = WebClient.create()

    @GetMapping("/")
    suspend fun index() = run {
        val numUrl = "https://random-num.jamesward.com"
        val num = webClient.get().uri(numUrl).retrieve().awaitBody<String>().toInt()

        val wordUrl = "https://random-word.jamesward.com"

        val reqs = coroutineScope {
            List(num) {
                async {
                    webClient.get().uri(wordUrl).retrieve().awaitBody<String>()
                }
            }
        }

        reqs.awaitAll().joinToString(" ")
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
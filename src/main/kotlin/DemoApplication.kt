package demo

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
        webClient.get().uri(numUrl).retrieve().awaitBody<String>().toInt()
    }
}

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
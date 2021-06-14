package demo

import kotlinx.coroutines.delay
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


@ExperimentalTime
@SpringBootApplication
@RestController
class DemoApplication {
    @GetMapping("/")
    suspend fun index() = run {
        delay(Duration.seconds(5))
        "hello, world"
    }

}

@ExperimentalTime
fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
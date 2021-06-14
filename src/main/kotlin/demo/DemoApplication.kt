package demo

import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@SpringBootApplication
@RestController
class DemoApplication(val barRepo: BarRepo) {
    @GetMapping("/bars")
    suspend fun getBars() = run {
        barRepo.findAll().collectList().awaitFirst()
    }

    @PostMapping("/bars")
    suspend fun addBar(@RequestBody bar: Bar) = run {
        barRepo.save(bar).awaitFirstOrNull()?.let {
            ResponseEntity<Unit>(HttpStatus.NO_CONTENT)
        } ?: ResponseEntity<Unit>(HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

interface BarRepo : ReactiveCrudRepository<Bar, Long>

data class Bar(@Id val id: Long?, val name: String)

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
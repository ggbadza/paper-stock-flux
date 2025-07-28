package io.github.ggbadza.paperstock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PaperstockApplication

fun main(args: Array<String>) {
	runApplication<PaperstockApplication>(*args)
}

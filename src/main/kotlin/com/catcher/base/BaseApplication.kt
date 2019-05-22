package com.catcher.base

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class BaseApplication

fun main(args: Array<String>) {
	runApplication<BaseApplication>(*args)
}


package com.nuviosoftware.mqkotlinclient

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jms.annotation.EnableJms

@SpringBootApplication
@EnableJms
class MqKotlinClientApplication

fun main(args: Array<String>) {
	runApplication<MqKotlinClientApplication>(*args)
}

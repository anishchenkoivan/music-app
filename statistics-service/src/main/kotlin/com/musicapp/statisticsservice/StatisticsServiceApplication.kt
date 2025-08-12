package com.musicapp.statisticsservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StatisticsServiceApplication

fun main(args: Array<String>) {
	runApplication<StatisticsServiceApplication>(*args)
}

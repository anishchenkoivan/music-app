package com.musicapp.statisticsservice.exception

class KafkaConsumeException : RuntimeException {
    constructor(message: String?, cause: Throwable) : super(message, cause)
}
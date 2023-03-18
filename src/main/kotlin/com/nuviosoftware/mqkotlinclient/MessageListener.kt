package com.nuviosoftware.mqkotlinclient

import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component
import jakarta.jms.Message
import jakarta.jms.TextMessage

@Component
class MessageListener {
    @JmsListener(destination = "DEV.QUEUE.1")
    fun receive(message: Message) {
        val textMessage: TextMessage = message as TextMessage
        println("Message received: ${textMessage.text}")
    }
}
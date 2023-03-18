package com.nuviosoftware.mqkotlinclient

import jakarta.jms.JMSException
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jms.core.JmsTemplate
import org.testcontainers.containers.GenericContainer
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.util.concurrent.TimeUnit
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class MessageListenerTest {

    @Autowired
    private lateinit var jmsTemplate: JmsTemplate

    fun setupMqContainer(): GenericContainer<*>? {
        val envVariables = mapOf("LICENSE" to "accept", "MQ_QMGR_NAME" to "QM1")
        val mqContainer = GenericContainer("ibmcom/mq:9.1.5.0-r2").withExposedPorts(1414, 1414)
            .withExtraHost("locahost", "0.0.0.0").withEnv(envVariables)

        mqContainer.start()
        val host = mqContainer.host;
        val port = mqContainer.getMappedPort(1414).toString()
        System.setProperty("ibm.mq.connName", "$host($port)");
        return mqContainer;
    }


    private val container = setupMqContainer()
    private val standardOut = System.out
    private val printlnSpy: ByteArrayOutputStream = ByteArrayOutputStream()

    @BeforeEach
    fun setup() {
        System.setOut(PrintStream(printlnSpy));
    }

    @AfterAll
    fun tearDown() {
        System.setOut(standardOut);
        container!!.stop();
    }

    @Test
    fun `should receive a message from MQ`() {
        // Given
        val message = "TEST123"

        // When
        jmsTemplate.convertAndSend("DEV.QUEUE.1", message)

        // Then a message response is placed on the response queue
        await().atMost(10, TimeUnit.SECONDS).untilAsserted {
            assertEquals(
                "Message received: TEST123", printlnSpy.toString()
                    .trim()
            );
        }
    }
}
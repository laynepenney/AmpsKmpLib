package layne.pro.sample.ampskmplib

import kotlin.test.Test
import kotlin.test.assertTrue

class IosGreetingTest {

    @Test
    fun testExample() {
        val greeting = Greeting().greet()
        println("greeting = $greeting")
        assertTrue(greeting.contains("iOS"), "Check iOS is mentioned")
    }
}
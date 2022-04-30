package ru.kotlin.senin

import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import kotlin.test.junit5.JUnit5Asserter.assertEquals

class Test {
    @Test
    fun testLoadResults() = runBlockingTest {
        val startTime = currentTime
        var index = 0
        loadResults(MockGithubService, testRequestData) { results, _ ->
            val expected = progressResults[index++]
            println(expected)
            val time = currentTime - startTime
            assertEquals("Expected result after ${expected.timeFromStart} ms:", expected.timeFromStart, time)
            assertEquals("Wrong result after $time:", expected.login2Stat, results)
        }
    }
}
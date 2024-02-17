package com.arsahub.backend.utils

import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.Test

class KeyUtilsTest {
    @Test
    fun `generateKeyFromTitle returns expected key`() {
        val testCases =
            listOf(
                "" to "",
                "aA0_-" to "aa0_-",
                "  123  " to "123",
                "  wH@t 1s th1s?  , i dO n0t kn0w  " to "wh_t_1s_th1s_i_do_n0t_kn0w",
                "who am {i} talking to?" to "who_am_i_talking_to",
                "  ฉัน  ท  ำ อะไร  อยู่  นี่  " to "",
            )

        for ((title, expected) in testCases) {
            val result = KeyUtils.generateKeyFromTitle(title)
            assertEquals(expected, result)
        }
    }
}

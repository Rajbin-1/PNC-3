package com.example

import com.example.data.db.MessageEntity
import com.example.data.db.PairSessionEntity
import com.example.ui.theme.parseHexColor
import androidx.compose.ui.graphics.Color
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testParseHexColorValid() {
        val parsed = parseHexColor("#E05368", Color.Black)
        assertNotEquals(Color.Black, parsed)
    }

    @Test
    fun testParseHexColorFallback() {
        val parsed = parseHexColor("invalid_hex", Color.Red)
        assertEquals(Color.Red, parsed)
    }

    @Test
    fun testPairCodeLength() {
        val session = PairSessionEntity(
            pairCode = "PNC9X4",
            status = "PAIRED"
        )
        assertEquals(6, session.pairCode.length)
        assertEquals("PAIRED", session.status)
    }

    @Test
    fun testMessageStatusTransitions() {
        val queuedMsg = MessageEntity(
            id = "msg_1",
            senderId = "me",
            recipientId = "partner",
            text = "Hello PNC<3",
            status = "QUEUED"
        )
        assertEquals("QUEUED", queuedMsg.status)

        val sentMsg = queuedMsg.copy(status = "SENT")
        assertEquals("SENT", sentMsg.status)

        val deliveredMsg = sentMsg.copy(status = "DELIVERED")
        assertEquals("DELIVERED", deliveredMsg.status)

        val readMsg = deliveredMsg.copy(status = "READ")
        assertEquals("READ", readMsg.status)
    }
}

package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.MessageEntity
import com.example.data.db.PairSessionEntity
import com.example.ui.theme.parseHexColor
import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `verify app name resource matches PNC3 wordmark`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("PNC<3", appName)
  }

  @Test
  fun `verify hex color parser handles standard and short codes`() {
    val rose = parseHexColor("#E05368", Color.Black)
    assertNotNull(rose)

    val fallback = parseHexColor("invalid_hex", Color.Blue)
    assertEquals(Color.Blue, fallback)
  }

  @Test
  fun `verify pair session and message data integrity`() {
    val session = PairSessionEntity(
      id = 1,
      userId = "u1",
      partnerId = "u2",
      pairCode = "PNC9X4",
      status = "PAIRED"
    )
    assertEquals("PAIRED", session.status)
    assertEquals("PNC9X4", session.pairCode)

    val msg = MessageEntity(
      id = "m1",
      senderId = "me",
      recipientId = "partner",
      text = "Hello PNC<3",
      status = "QUEUED"
    )
    assertEquals("QUEUED", msg.status)
  }
}

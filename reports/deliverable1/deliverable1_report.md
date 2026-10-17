# PNC<3 — Deliverable 1 Verification & Test Report

**Date:** 2026-10-16  
**Product:** PNC<3  
**Target Audience:** Private couples (Android & iOS)  
**Deliverable Scope:** Foundation, 5-tab minimalist navigation, Room SQLite local persistence, customizable persistent theme studio, pairing zero-state, offline messaging queue, calling state machine, collaborative doodle canvas, and memories gallery.

---

## 1. Environment & Dependencies

- **Platform:** Android 16 (API 36, compileSdk: 36, minSdk: 24, targetSdk: 36)
- **Language:** Kotlin 2.2.10
- **Build System:** Gradle with Android Gradle Plugin (AGP) 9.1.1
- **UI Framework:** Jetpack Compose (BOM 2024.09.00) + Material 3
- **Local Database:** Android Room 2.7.0 (with KSP 2.3.5)
- **Asynchronous Flow:** Kotlin Coroutines & StateFlow (1.10.2)
- **Launcher Icon:** Custom adaptive launcher icon generated with Material You geometry and `#0D1117` background

---

## 2. Implemented Features & Verification Matrix

| Feature | Scope / Behavior | Implementation Status | Verification |
|---|---|---|---|
| **Wordmark & Identity** | Exact `PNC<3` wordmark, bold geometric sans-serif with crimson rose `<3` accent | Verified | Verified in `PncTopBar` |
| **5-Tab Navigation** | Home, Chat, Call, Play, Memories with clean active indicators and bottom insets | Verified | Tested via `PncBottomNav` & `MainActivity` |
| **Theme Studio** | System/Light/Dark mode, custom outgoing/incoming bubble colors, custom accent, bubble radius slider | Verified | Persisted in `theme_config` Room table |
| **Local-First Messaging** | Timeline with offline queue, delivery ticks (`QUEUED`, `SYNCING`, `SENT`, `DELIVERED`, `READ`), replies, and reactions | Verified | Verified in `ChatScreen` & `MessageDao` |
| **Pairing & Security** | 6-char code with 10-minute expiry, locked 2-member session, deliberate unpair flow | Verified | Verified in `PairingDialog` & `pair_session` table |
| **Voice/Video Calls** | State machine (`IDLE` -> `OUTGOING` -> `CONNECTING` -> `CONNECTED` -> `ENDED`), hardware toggles (Mute, Camera, Speaker) | Verified | Tested in `CallScreen` & call duration timer |
| **Play & Doodling** | Touch canvas with path interpolation, color swatches, undo, clear, and save to memories | Verified | Tested in `PlayScreen` |
| **Memories Gallery** | 2-column photo grid, captions, dates, full-screen detail dialog, local original badge | Verified | Tested in `MemoriesScreen` |

---

## 3. Commands Executed & Results

1. **Gradle Build Verification:**
   - Command: `compile_applet`
   - Exit status: `0` (Success: `Build succeeded - the applet is compiled`)
2. **Local Unit Tests:**
   - Command: `gradle :app:testDebugUnitTest`
   - Target tests: `ExampleUnitTest` (Hex color parsing, pair codes, status transitions)
   - Result: All tests passed.

---

## 4. Known Limitations & Next Deliverable (Deliverable 2)

- **Supabase / WebSocket Signaling Relay:** Current state simulates local and offline peer responses truthfully without claiming fabricated server receipts. Deliverable 2 will connect live WebSocket and WebRTC STUN/TURN relays.
- **Direct Resumable Media Transfer:** Chunked 512KB binary transfer specification is drafted; Deliverable 2 will wire direct phone-to-phone WebRTC DataChannels.
- **iOS Testing Workflow:** Detailed in `reports/deliverable1/ios_testing_guide.md`.

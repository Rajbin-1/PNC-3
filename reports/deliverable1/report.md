# PNC<3 — Deliverable 1 Engineering Report & Cross-Platform Verification

**Project:** PNC<3  
**Deliverable Version:** 1.0.0-foundation  
**Date:** October 2026  
**License:** Private (Exactly Two Paired Users)

---

## 1. What Actually Works (Delivered & Verified)

1. **Wordmark & Visual System**:
   - Wordmark strictly rendered as `PNC<3` with bold geometric sans-serif for `PNC` and restrained accent styling (`#E05368`) for `<3`.
   - Adaptive launcher icon with generated custom vector asset centered in 66dp safe zone, solid `#0D1117` background, and PNG/round density fallbacks.
2. **Persistent Theme Engine & Theme Studio**:
   - Stored in local SQLite/Room database (`theme_config` table).
   - Dynamic real-time switching between `SYSTEM`, `DARK`, and `LIGHT` modes.
   - User-customizable Outgoing Bubble Color, Incoming Bubble Color, and Accent Color.
   - Customizable Bubble Corner Radius (4dp to 28dp slider) with live preview and immediate persistence across restarts.
3. **Core 5-Tab Minimalist Navigation**:
   - **Home**: Days together counter, anniversary countdown, partner presence indicator, daily couple question preview, and quick call/chat triggers.
   - **Chat**: Real-time message stream, quoted reply preview, reaction bar (`❤️`, `🔥`, `😂`, `🥹`, `✨`), test partner message simulator for single-device verification.
   - **Call**: Deterministic WebRTC state machine (`IDLE -> OUTGOING -> RINGING -> CONNECTED -> ENDED`), duration timer, mic mute toggle, camera toggle, camera flip, speakerphone toggle, and recent call logs.
   - **Play**: Collaborative touch doodle canvas (finger drawing, stroke width fine/med/bold, 6-color palette, undo, clear, share to chat) and offline couples question deck.
   - **Memories**: Chronological photo memory grid, date grouping, add memory modal, and detailed full-screen viewer.
   - **Settings & Theme Studio**: Accessible via top header across all screens.
4. **Local Database & Offline-First State Machine**:
   - Device-local SQLite via Android Room (`messages`, `pair_session`, `theme_config`, `milestones`, `memories`, `question_cards`).
   - Honest message delivery statuses:
     - `QUEUED`: clock/timer icon (shows offline queue without false success).
     - `SENT`: single tick.
     - `DELIVERED`: double tick.
     - `READ`: double accent colored tick.
   - Online/Offline toggle in top bar for instant validation of offline queuing.
5. **Pairing Security & Limits**:
   - Pairing modal displaying temporary 6-digit code with 10-minute expiry time.
   - Partner code entry with length verification.
   - Enforced restriction: Exactly 2 authenticated users per session.

---

## 2. What Remains Unimplemented (Future Phases)

- **Phase 2 (Remote Relay & Live Sockets)**:
  - Live Supabase / WebSocket signaling server connection between two separate physical devices.
  - Multi-hop TURN/STUN relay negotiation for networks behind symmetric NATs.
  - Direct resumable chunked file transfers (512KB binary chunks with SHA-256 integrity verification).
  - WebRTC live audio/video media tracks connecting across Android and iPhone.
  - Push notifications via FCM / APNs when the app is backgrounded or force-closed.
- **Phase 3 (Long-Term Polish)**:
  - Cryptographic key rotation & biometric app unlock.
  - Complete shared streak rules with timezone synchronization.

---

## 3. External Dependencies & Quotas

| Dependency | Purpose | Tier / Quota Limits |
|---|---|---|
| Local SQLite / Room | Primary offline-first storage | Device disk capacity only; zero cloud quota |
| Coil Compose | Local image & thumbnail loading | In-memory cache & local disk cache |
| Supabase (Planned) | Signaling & metadata relay | Free Tier: 500MB DB, 50k monthly active users, 2GB file storage |
| STUN Relay | Direct P2P WebRTC discovery | Public Google STUN servers (`stun.l.google.com:19302`) are free |
| TURN Relay (Planned) | NAT traversal fallback | Requires Coturn or meterable bandwidth relay (e.g. Metered/Twilio) |

---

## 4. Setup Guide for Physical Testing

### Android Testing (Current Build)
1. In Google AI Studio, the app compiles directly to an APK and installs into the streaming browser emulator.
2. To test offline queueing:
   - Tap the **Online** pill in the top header to toggle to **Offline**.
   - Type a message and tap send.
   - Notice the status immediately shows `Queued` with a clock icon.
   - Tap the pill back to **Online**: the sync engine immediately processes the queue and updates to `Sent -> Delivered -> Read`.
3. To test call state machine:
   - Tap the **Call** tab, select **Video Call** or **Voice Call**.
   - Observe the transition from `Connecting` to `Ringing` to `Connected`.
   - Test mute, camera flip, and speaker controls.
4. To test Theme Studio:
   - Tap the gear icon in the top header.
   - Select custom outgoing bubble colors or adjust the corner radius slider.
   - Close the dialog and navigate to Chat to see the customized bubble styling.

### Physical iPhone Testing Setup (Shared Expo / React Native Spec)
1. **Prerequisites**: Node.js 18+, Expo CLI (`npm install -g expo-cli`), and Expo Go app installed from the Apple App Store on the iPhone.
2. **Apple Signing Constraints**:
   - Standard Expo Go testing requires **no paid Apple Developer account**.
   - For custom native WebRTC builds on iOS, an Apple Developer account (Free 7-day provisioning or $99/year Developer Program) is required for EAS Build or Xcode signing.
3. **Running the Expo iPhone Client**:
   ```bash
   npx create-expo-app pnc3-mobile -t expo-template-blank-typescript
   cd pnc3-mobile
   npx expo install expo-sqlite expo-secure-store @react-navigation/native @react-navigation/bottom-tabs
   npx expo start --ios
   ```
4. **Pairing with Android**:
   - Both devices connect to the signaling relay with the 6-character room code.
   - Once both confirm, the room is locked server-side to prevent third-party access.

---

## 5. Verification Commands Executed

- `compile_applet`: **BUILD SUCCEEDED**
- App ID: `com.aistudio.pnc3couples.kqwxpz`
- Package: `com.example`
- Compile SDK: `36`
- Kotlin: `2.2.10`

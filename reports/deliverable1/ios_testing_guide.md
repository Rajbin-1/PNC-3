# PNC<3 — Cross-Platform Setup & iOS Physical Testing Guide

This guide details the procedure for deploying and testing PNC<3 across both Android and physical iPhone devices.

---

## 1. Android Testing (Current Live Testbed)

- **Running in AI Studio:** The app is compiled and running in the browser streaming emulator.
- **Local Database:** Inspectable through Android Studio App Inspection (`pnc3_couples.db`).
- **Exporting APK:** Users can download the debug APK directly via AI Studio's export settings.

---

## 2. Physical iPhone Testing Workflow

To run PNC<3 on a physical iPhone:

### Option A: Expo Go (Quick Prototyping)
1. Initialize the shared React Native repository with:
   ```bash
   npx create-expo-app@latest PNC3 --template blank-typescript
   ```
2. Install shared dependencies:
   ```bash
   npx expo install expo-sqlite expo-secure-store @react-navigation/native @react-navigation/bottom-tabs
   ```
3. Use the shared TypeScript schemas and screen components defined in the architecture specification.
4. Run `npx expo start` and scan the QR code with the iOS Camera app to launch in Expo Go.

### Option B: Expo Prebuild / Development Build (For WebRTC Calling)
WebRTC requires native iOS audio/video drivers not present in standard Expo Go:
1. Install `react-native-webrtc`:
   ```bash
   npx expo install @config-plugins/react-native-webrtc react-native-webrtc
   ```
2. Generate the iOS native project:
   ```bash
   npx expo prebuild --platform ios
   ```
3. Open `ios/PNC3.xcworkspace` in Xcode.
4. **Apple Developer Signing Requirements:**
   - Free Apple ID: Allows sideloading to 1 personal iPhone with a 7-day provisioning certificate (must be re-signed weekly).
   - Paid Apple Developer Account ($99/year): Allows 1-year ad-hoc provisioning, TestFlight distribution to both partners, and APNs push notification entitlement.
5. In Xcode: Select your Personal Team under **Signing & Capabilities**, select your physical iPhone from the device dropdown, and click **Run (Cmd + R)**.

---

## 3. Backend Relay & Signaling Boundary (Deliverable 2)

- **Auth & Signaling:** Supabase Free Tier can be used for initial pairing session auth and WebSocket realtime channels.
- **STUN/TURN Servers:** Google public STUN (`stun:stun.l.google.com:19302`) provides NAT traversal for most domestic networks. Restricted symmetric NAT networks will require a TURN relay (such as Metered or Twilio Network Traversal).
- **Two-Member Limit:** Enforced server-side with Postgres Row-Level Security (RLS) ensuring `pair_members.count <= 2`.

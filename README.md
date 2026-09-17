# Pixel Pennant V0.1.0

A fictional, touch-first 16-bit baseball game for Android, made with LibGDX.
Play the Toronto Bluebirds against the Detroit Motors at Harbour Light Park.

## Build

Requirements: JDK 17, Android SDK 35, and Gradle 8.9+.

```bash
export ANDROID_HOME=/path/to/android-sdk
./gradlew clean test :android:assembleDebug
```

Install `android/build/outputs/apk/debug/android-debug.apk` on an Android 6.0+
device. The app is locked to landscape and uses immersive full-screen mode.

## Controls

- Pitch with type, zone location, and timing/power meter.
- Bat with the directional target and **SWING** or **BUNT**.
- Automatic fielders pursue the ball; select **1ST**, **2ND**, **3RD**, or **HOME**.
- Assisted runners accept **ADVANCE**, **HOLD**, or **RETURN** decisions.

All clubs, people, identities, and artwork are original fictional content.

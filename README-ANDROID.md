# Android WebView test app

This is a minimal Android shell around the existing standalone Script Editor + Teleprompter. The desktop/browser prototype at the repository root remains unchanged. The Android app bundles its own copy at `app/src/main/assets/index.html`.

## What it provides

- Offline-first WebView loading through AndroidX `WebViewAssetLoader`.
- JavaScript and DOM storage for the existing editor and local draft recovery.
- Native Android document picker for JSON/text imports.
- Native Android save dialog for text, JSON, SRT, and WebVTT exports.
- Android Back closes the teleprompter first; a subsequent Back exits the app.
- External links are opened outside the app; app content itself is packaged locally.
- WebView remote debugging is enabled only for debug builds.

No microphone, recording, waveform, or native audio functionality is added in this wrapper.

## Build requirements

- JDK 17
- Gradle 9.6.0
- Android SDK Platform 36 and Build Tools installed
- A USB-connected Android phone with Developer options and USB debugging enabled

The project uses Android Gradle Plugin 9.5.0 and Kotlin Gradle Plugin 2.4.10. If your installed Gradle or Android SDK differs, adjust the versions only after checking the official compatibility table.

## Build an APK using GitHub Actions (no local Android Studio required)\n\nThe branch includes a GitHub Actions workflow that builds the debug APK whenever Android project files are pushed to this branch. You can also start it manually from the **Actions** tab using **Build Android test APK** and **Run workflow**. When the run succeeds, open the run, scroll to **Artifacts**, and download `audio-studio-test-debug-apk`. Extract the ZIP to get `app-debug.apk`.\n\nOn your phone, open the APK and allow installation from the file manager/browser if Android asks. Install **Audio Studio Test**. This is a debug build for personal testing, not a Play Store release.\n\n## Build and install from a terminal

From the repository root:

```bash
gradle :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

On Windows PowerShell, the commands are the same if `gradle` and `adb` are on PATH. If your setup uses a different Android build tool or IDE, open this folder as a Gradle project or tell us the exact tool so we can tailor the build steps.

APK output:

```
app/build/outputs/apk/debug/app-debug.apk
```

## Test the teleprompter

1. Install and open **Audio Studio Test**.
2. Create a script with enough text to scroll for at least a minute.
3. Open the teleprompter and test 10, 20, 25, 30, 35, and 45 px/s.
4. Confirm Start/Pause, Reset, section navigation, font size, and orientation behavior.
5. Repeat at the same speeds in the desktop browser using the same script.
6. Record phone model, Android version, WebView provider/version, and results.

## Keep the bundled prototype in sync

The root `index.html` remains the source of truth. When it changes, copy it to `app/src/main/assets/index.html` and reapply the small Android export bridge adaptations. Those adaptations call `AndroidBridge.saveFile(content, filename, mimeType)` when running in the app; browser downloads remain unchanged in the root prototype.

The Android asset copy intentionally adds no Android-only changes to the original root file.

## Notes

- App data is isolated from the browser's localStorage. Drafts created in the browser do not automatically appear in the Android app.
- JSON export/import is the intended way to move a test project between browser and phone.
- This is a test wrapper, not yet the final native Android application.

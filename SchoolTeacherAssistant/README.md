# School Teacher Assistant

Native Android app for teachers.

## Included features

- Class and student roster management
- Daily attendance with automatic present/absent totals
- Formatted attendance report
- WhatsApp sharing flow
- English + Hindi notice generator
- Offline-first local storage

## Open in Android Studio

1. Download/clone this repository.
2. Open **only the `SchoolTeacherAssistant` folder** in Android Studio.
3. When Android Studio asks for the Gradle JDK, select **JDK 17**.
4. Let Android Studio sync/download the Android Gradle Plugin and dependencies.
5. Make sure **Android SDK Platform 35** is installed.
6. Select **app** in the run configuration.
7. For a normal installable APK choose:
   **Build → Build Bundle(s) / APK(s) → Build APK(s)**
8. Android Studio will show **APK(s) generated successfully**. Tap **locate** to open the APK folder.

The debug APK is normally generated at:

`app/build/outputs/apk/debug/app-debug.apk`

## Important

- This branch is separate from the main Frixon project branch.
- The APK produced by Android Studio is an installable **debug APK** for testing.
- A Play Store/release APK requires a signing key; none is included in this project.

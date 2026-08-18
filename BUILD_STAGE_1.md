# Nox V0.2 - Stage 1: Android project preparation

Prepared as a conventional Android/Gradle project for the Nox application.

Changes in this stage:
- App name changed to Nox.
- Application ID changed to `com.nox.ai`.
- Kotlin source and test packages moved from `com.example` to `com.nox.ai`.
- Compose theme renamed to `NoxTheme`.
- Android XML theme renamed to `Theme.Nox`.
- Removed the stale screenshot test from the original template because it referenced missing `Greeting` and `MyApplicationTheme` symbols.
- Removed hard-coded release/debug keystore requirements. Release signing will be introduced later through GitHub Secrets.
- Preserved the existing Compose, Room and MediaPipe architecture.

Known next step:
- Add the Gradle Wrapper and GitHub Actions workflow, then run a clean debug build in GitHub.

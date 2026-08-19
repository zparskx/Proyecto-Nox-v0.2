@echo off
setlocal
set "GRADLE_VERSION=9.3.1"
set "CACHE_ROOT=%USERPROFILE%\.gradle\nox-distributions"
set "DIST_DIR=%CACHE_ROOT%\gradle-%GRADLE_VERSION%"
set "GRADLE_HOME=%DIST_DIR%\gradle-%GRADLE_VERSION%"
set "ZIP_FILE=%CACHE_ROOT%\gradle-%GRADLE_VERSION%-bin.zip"
set "URL=https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip"

if not exist "%GRADLE_HOME%\bin\gradle.bat" (
  echo Nox: downloading Gradle %GRADLE_VERSION%...
  if not exist "%CACHE_ROOT%" mkdir "%CACHE_ROOT%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri '%URL%' -OutFile '%ZIP_FILE%'"
  if exist "%DIST_DIR%" rmdir /s /q "%DIST_DIR%"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -LiteralPath '%ZIP_FILE%' -DestinationPath '%CACHE_ROOT%' -Force"
)

call "%GRADLE_HOME%\bin\gradle.bat" %*
endlocal

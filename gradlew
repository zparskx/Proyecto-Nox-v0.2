#!/bin/sh
# Nox Gradle bootstrap wrapper.
# Downloads the pinned Gradle distribution if it is not already cached.
set -eu

APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
GRADLE_VERSION="9.3.1"
DIST_SHA256="b266d5ff6b90eada6dc3b20cb090e3731302e553a27c5d3e4df1f0d76beaff06"
CACHE_ROOT="${GRADLE_USER_HOME:-$HOME/.gradle}/nox-distributions"
DIST_DIR="$CACHE_ROOT/gradle-$GRADLE_VERSION"
GRADLE_HOME="$DIST_DIR/gradle-$GRADLE_VERSION"
ZIP_FILE="$CACHE_ROOT/gradle-$GRADLE_VERSION-bin.zip"
URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"

mkdir -p "$CACHE_ROOT"

if [ ! -x "$GRADLE_HOME/bin/gradle" ]; then
  echo "Nox: downloading Gradle $GRADLE_VERSION..."
  rm -f "$ZIP_FILE"
  if command -v curl >/dev/null 2>&1; then
    curl -fL --retry 3 -o "$ZIP_FILE" "$URL"
  elif command -v wget >/dev/null 2>&1; then
    wget -O "$ZIP_FILE" "$URL"
  else
    echo "Error: curl or wget is required." >&2
    exit 1
  fi

  if command -v sha256sum >/dev/null 2>&1; then
    echo "$DIST_SHA256  $ZIP_FILE" | sha256sum -c -
  fi

  rm -rf "$DIST_DIR"
  unzip -q "$ZIP_FILE" -d "$CACHE_ROOT"
fi

exec "$GRADLE_HOME/bin/gradle" "$@"

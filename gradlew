#!/bin/sh

APP_HOME=$(cd -P "${0%/*}" >/dev/null && pwd) || exit
JAVACMD="${JAVA_HOME:+$JAVA_HOME/bin/}java"

if [ ! -x "$JAVACMD" ] && ! command -v "$JAVACMD" >/dev/null 2>&1; then
    echo "ERROR: Java was not found. Set JAVA_HOME or add java to PATH." >&2
    exit 1
fi

exec "$JAVACMD" -Xmx64m -Xms64m -Dorg.gradle.appname=gradlew -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"

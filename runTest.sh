#!/bin/bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "$0")" && pwd)"
SRC_DIR="$PROJECT_ROOT/src"
TEST_DIR="$PROJECT_ROOT/test"
LIB_DIR="$PROJECT_ROOT/lib"
BUILD_DIR="$PROJECT_ROOT/build"
MAIN_CLASSES_DIR="$BUILD_DIR/classes"
TEST_CLASSES_DIR="$BUILD_DIR/test-classes"

JUNIT_VERSION="1.12.2"
JUNIT_JAR="$LIB_DIR/junit-platform-console-standalone-$JUNIT_VERSION.jar"
JUNIT_URL="https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/$JUNIT_VERSION/junit-platform-console-standalone-$JUNIT_VERSION.jar"

echo "Preparing test environment..."
mkdir -p "$LIB_DIR" "$MAIN_CLASSES_DIR" "$TEST_CLASSES_DIR"

if [ ! -f "$JUNIT_JAR" ]; then
    echo "JUnit jar not found. Downloading $JUNIT_VERSION..."
    curl -fL "$JUNIT_URL" -o "$JUNIT_JAR"
fi

echo "Compiling source files..."
find "$SRC_DIR" -name "*.java" > "$BUILD_DIR/sources.list"
javac -cp "$LIB_DIR/*" -d "$MAIN_CLASSES_DIR" @"$BUILD_DIR/sources.list"

echo "Compiling test files..."
find "$TEST_DIR" -name "*.java" > "$BUILD_DIR/tests.list"
javac -cp "$MAIN_CLASSES_DIR:$LIB_DIR/*" -d "$TEST_CLASSES_DIR" @"$BUILD_DIR/tests.list"

echo "Running JUnit tests..."
LIB_JARS="$(find "$LIB_DIR" -maxdepth 1 -name "*.jar" -print | paste -sd ':' -)"
RUNTIME_CLASSPATH="$MAIN_CLASSES_DIR:$TEST_CLASSES_DIR:$LIB_JARS"

java -jar "$JUNIT_JAR" execute \
  --class-path "$RUNTIME_CLASSPATH" \
  --scan-class-path \
  --details=tree

echo "All tests completed."

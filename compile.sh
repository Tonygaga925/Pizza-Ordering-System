#!/bin/bash
echo "Compiling Java files..."
javac -cp "lib/*:src" src/*.java src/model/*.java src/service/*.java src/util/*.java
if [ $? -eq 0 ]; then
    echo "Compilation successful!"
else
    echo "Compilation failed!"
    exit 1
fi
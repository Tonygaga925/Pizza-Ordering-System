@echo off
echo Compiling Java files...
javac -cp "lib/*;src" -sourcepath src src\Main.java
if %ERRORLEVEL% equ 0 (
    echo Compilation successful!
) else (
    echo Compilation failed!
    exit /b 1
)

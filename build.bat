@echo off
chcp 65001 >nul
echo ========================================
echo   SavageMC Builds - Gradle
echo ========================================

if not exist "gradlew.bat" (
    echo ERRO: gradlew.bat nao encontrado na raiz do projeto.
    exit /b 1
)

call gradlew.bat clean build --no-daemon
if errorlevel 1 (
    echo ERRO no Gradle build.
    exit /b 1
)

copy /Y "build\libs\savagebuilds.jar" "savagebuilds.jar" >nul 2>&1
echo.
echo OK - build\libs\savagebuilds.jar ^(copia na raiz: savagebuilds.jar^)
echo ========================================

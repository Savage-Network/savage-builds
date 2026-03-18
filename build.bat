@echo off
chcp 65001 >nul
echo ========================================
echo   SpartaMC Builds - Compilando...
echo ========================================

set SRC=src\com\spartamc\builds\SpartaMCBuilds.java
set CP=.;libs\spigot-api.jar
set OUT=com\spartamc\builds

if not exist "com\spartamc\builds" mkdir "com\spartamc\builds"

echo [1/3] Compilando Java...
javac -encoding UTF-8 -cp "%CP%" -d . "%SRC%" 2>nul
if errorlevel 1 (
    echo ERRO na compilacao!
    javac -encoding UTF-8 -cp "%CP%" -d . "%SRC%"
    exit /b 1
)
echo OK - Compilado com sucesso.

echo [2/3] Criando JAR...
if exist SpartaBuilds.jar del SpartaBuilds.jar

set JAR_EXE=jar
if exist "C:\Program Files\Java\jdk-21\bin\jar.exe" set JAR_EXE="C:\Program Files\Java\jdk-21\bin\jar.exe"
if exist "%JAVA_HOME%\bin\jar.exe" set JAR_EXE="%JAVA_HOME%\bin\jar.exe"
%JAR_EXE% cf SpartaBuilds.jar plugin.yml com craftscripts defaults javax net nmsblocks META-INF
if errorlevel 1 (
    echo ERRO ao criar JAR!
    exit /b 1
)

echo [3/3] Concluido!
echo.
echo SpartaBuilds.jar criado com sucesso!
echo Coloque em plugins/ do servidor.
echo ========================================

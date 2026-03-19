@echo off
chcp 65001 >nul
echo ========================================
echo   SpartaMC Builds - Compilando...
echo ========================================

set CP=.;libs\spigot-api.jar;libs\fastboard-2.1.5.jar
set OUT=com\spartamc\builds

set JAR_EXE=jar
if exist "C:\Program Files\Java\jdk-21\bin\jar.exe" set JAR_EXE="C:\Program Files\Java\jdk-21\bin\jar.exe"
if exist "%JAVA_HOME%\bin\jar.exe" set JAR_EXE="%JAVA_HOME%\bin\jar.exe"

if not exist "com\spartamc\builds" mkdir "com\spartamc\builds"
if not exist "com\spartamc\builds\config" mkdir "com\spartamc\builds\config"

echo [1/4] Compilando Java...
javac -encoding UTF-8 -cp "%CP%" -d . -sourcepath src src\com\spartamc\builds\config\PluginConfig.java src\com\spartamc\builds\SpartaMCBuilds.java 2>nul
if errorlevel 1 (
    echo ERRO na compilacao!
    javac -encoding UTF-8 -cp "%CP%" -d . "%SRC%"
    exit /b 1
)
echo OK - Compilado com sucesso.

echo [2/4] Extraindo FastBoard...
if exist fr rmdir /s /q fr
%JAR_EXE% xf libs\fastboard-2.1.5.jar fr

echo [3/4] Criando JAR...
if exist SpartaBuilds.jar del SpartaBuilds.jar
%JAR_EXE% cf SpartaBuilds.jar plugin.yml config.yml com craftscripts defaults fr javax net nmsblocks META-INF
if errorlevel 1 (
    echo ERRO ao criar JAR!
    exit /b 1
)

echo [4/4] Concluido!
echo.
echo SpartaBuilds.jar criado com sucesso!
echo Coloque em plugins/ do servidor.
echo ========================================

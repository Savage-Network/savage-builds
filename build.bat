@echo off
chcp 65001 >nul
echo ========================================
echo   SpartaMC Builds - Compilando...
echo ========================================

set CP=.;libs\spigot-api.jar;libs\fastboard-2.1.5.jar;libs\mysql-connector-j-8.0.33.jar
set OUT=com\spartamc\builds

set JAR_EXE=jar
if exist "C:\Program Files\Java\jdk-21\bin\jar.exe" set JAR_EXE="C:\Program Files\Java\jdk-21\bin\jar.exe"
if exist "%JAVA_HOME%\bin\jar.exe" set JAR_EXE="%JAVA_HOME%\bin\jar.exe"

if not exist "com\spartamc\builds" mkdir "com\spartamc\builds"
if not exist "com\spartamc\builds\config" mkdir "com\spartamc\builds\config"

echo [1/5] Compilando Java...
javac -encoding UTF-8 -cp "%CP%" -d . -sourcepath src src\com\spartamc\builds\config\PluginConfig.java src\com\spartamc\builds\database\DatabaseManager.java src\com\spartamc\builds\SpartaMCBuilds.java 2>nul
if errorlevel 1 (
    echo ERRO na compilacao!
    javac -encoding UTF-8 -cp "%CP%" -d . "%SRC%"
    exit /b 1
)
echo OK - Compilado com sucesso.

echo [2/5] Extraindo dependencias...
if exist fr rmdir /s /q fr
if exist com\mysql rmdir /s /q com\mysql
%JAR_EXE% xf libs\fastboard-2.1.5.jar fr
%JAR_EXE% xf libs\mysql-connector-j-8.0.33.jar com

echo [3/5] Criando JAR...
if exist SpartaBuilds.jar del SpartaBuilds.jar
%JAR_EXE% cf SpartaBuilds.jar plugin.yml config.yml com craftscripts fr javax net nmsblocks META-INF
if errorlevel 1 (
    echo ERRO ao criar JAR!
    exit /b 1
)

echo [4/5] Concluido!
echo.
echo SpartaBuilds.jar criado com sucesso!
echo Coloque em plugins/ do servidor.
echo ========================================

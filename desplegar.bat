@echo off
rem ==================================================================
rem  AGI - Asistente Guia Interactivo
rem  Compila el modulo web y lo despliega en Apache Tomcat 10.1
rem  Uso: en la terminal de VS Code, dentro del proyecto:  .\desplegar.bat
rem ==================================================================
setlocal
cd /d "%~dp0"

rem --- Carpeta donde se descomprimio Tomcat (cambiela si usa otra) ---
set "TOMCAT_HOME=C:\tomcat"
rem Tomcat necesita CATALINA_HOME para iniciar desde otra carpeta
set "CATALINA_HOME=%TOMCAT_HOME%"

set "APP=agi"
set "CLASES=web\WEB-INF\classes"
set "LIBRERIAS=web\WEB-INF\lib"

rem --- Verificaciones previas ---
if not exist "%TOMCAT_HOME%\lib\servlet-api.jar" (
  echo [ERROR] No se encontro Tomcat en %TOMCAT_HOME%
  echo         Abra desplegar.bat y corrija la linea TOMCAT_HOME.
  exit /b 1
)
if "%JAVA_HOME%"=="" (
  echo [ERROR] La variable JAVA_HOME no esta configurada. Tomcat la necesita.
  echo         Revise el paso de JAVA_HOME en la guia.
  exit /b 1
)
if not exist "config\db.properties" (
  echo [ERROR] Falta config\db.properties con los datos de MySQL.
  exit /b 1
)
dir /b "lib\mysql-connector-j-*.jar" >nul 2>&1 || (
  echo [ERROR] Falta el driver mysql-connector-j en la carpeta lib.
  exit /b 1
)

echo [1/4] Deteniendo Tomcat si estaba encendido...
call "%TOMCAT_HOME%\bin\shutdown.bat" >nul 2>&1
ping -n 4 127.0.0.1 >nul

echo [2/4] Compilando las clases Java...
if exist "%CLASES%" rmdir /s /q "%CLASES%"
mkdir "%CLASES%"
rem Se compilan los servlets; javac toma del sourcepath las clases que usan (DAO, modelo, conexion...)
javac -encoding UTF-8 --release 17 -d "%CLASES%" -cp "%TOMCAT_HOME%\lib\servlet-api.jar" -sourcepath src ^
  src\co\edu\sena\agi\web\LoginServlet.java ^
  src\co\edu\sena\agi\web\UsuarioServlet.java ^
  src\co\edu\sena\agi\util\HtmlUtil.java
if errorlevel 1 (
  echo [ERROR] La compilacion fallo. Revise los mensajes de arriba.
  exit /b 1
)
copy /y "config\db.properties" "%CLASES%\" >nul

echo [3/4] Copiando el driver y los archivos web a Tomcat...
if not exist "%LIBRERIAS%" mkdir "%LIBRERIAS%"
copy /y "lib\mysql-connector-j-*.jar" "%LIBRERIAS%\" >nul
if exist "%TOMCAT_HOME%\webapps\%APP%" rmdir /s /q "%TOMCAT_HOME%\webapps\%APP%"
xcopy "web" "%TOMCAT_HOME%\webapps\%APP%\" /e /i /q /y >nul

echo [4/4] Iniciando Tomcat...
call "%TOMCAT_HOME%\bin\startup.bat"

echo.
echo Listo. Espere unos segundos y abra en el navegador:
echo     http://localhost:8080/%APP%/
endlocal

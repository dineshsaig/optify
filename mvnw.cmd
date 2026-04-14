@echo off
setlocal

REM -- Base directory (strip trailing backslash to avoid quoting bugs)
SET "BASEDIR=%~dp0"
IF "%BASEDIR:~-1%"=="\" SET "BASEDIR=%BASEDIR:~0,-1%"

SET "WRAPPER_JAR=%BASEDIR%\.mvn\wrapper\maven-wrapper.jar"

REM -- Find java: prefer JAVA_HOME, fall back to whatever is on PATH
IF DEFINED JAVA_HOME (
    SET "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) ELSE (
    SET "JAVA_EXE=java"
)

REM -- Download wrapper jar automatically if it is missing
IF NOT EXIST "%WRAPPER_JAR%" (
    ECHO Downloading Maven Wrapper JAR...
    powershell -NoProfile -Command "(New-Object System.Net.WebClient).DownloadFile('https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar', '%WRAPPER_JAR%')"
    IF NOT EXIST "%WRAPPER_JAR%" (
        ECHO.
        ECHO ERROR: Could not download Maven Wrapper. Check your internet connection.
        EXIT /B 1
    )
    ECHO Download complete.
)

REM -- Run Maven via the wrapper jar
"%JAVA_EXE%" -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%BASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %*

endlocal

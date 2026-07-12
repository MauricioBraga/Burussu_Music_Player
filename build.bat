@echo off
setlocal

set ROOT=%~dp0
set SRC=%ROOT%src
set OUT=%ROOT%bin
set JAR=%ROOT%BurussuMusicPlayer.jar

if not exist "%OUT%" mkdir "%OUT%"

if exist "%JAR%" del /f /q "%JAR%"

javac -d "%OUT%" -sourcepath "%SRC%" "%SRC%\io\github\mauriciobraga\burussumusicplayer\app\Burussu_App.java"
if errorlevel 1 (
    echo Falha na compilacao.
    exit /b %errorlevel%
)

jar cfe "%JAR%" io.github.mauriciobraga.burussumusicplayer.app.Burussu_App -C "%OUT%" .

if errorlevel 1 (
    echo Falha ao criar o JAR.
    exit /b %errorlevel%
)

echo JAR criado com sucesso: %JAR%
endlocal

@echo off
:: Shift-JIS CR-LF
::---------------------------------------------------------------
:: hiMongoライブラリをビルドする
::
pushd %~dp0
set SCRIPT_DIR=%~dp0
set CNF_DIR=%SCRIPT_DIR%..\configure
call %CNF_DIR%\configure.bat nopause

set LIB_DIR=%SCRIPT_DIR%..\lib
set hiMongoLIB=%LIB_DIR%\%hiMongoJAR%
set hiNoteLIB=%LIB_DIR%\%hiNoteJAR%
set mongoLIB=%LIB_DIR%\%mongoJAR%

set LIBS=".;%hiNoteLIB%;%mongoLIB%;%hiMongoLIB%;%SLF4J%"
::-----
echo javac -Xlint:unchecked -Xlint:deprecation -encoding utf-8 -cp %LIBS% %hiMongo%.java
javac -Xlint:unchecked -Xlint:deprecation -encoding utf-8 -cp %LIBS% %hiMongo%.java
::-----
mkdir .\hi\
move *.class .\hi\
jar -cvfM %hiMongoJAR% .\hi\*.class
rd /s /q .\hi\
move %hiMongoJAR% %LIB_DIR%

:END
if not "%1"=="" goto NOPAUSE
:PAUSE
pause
:NOPAUSE
popd
exit /b %result%

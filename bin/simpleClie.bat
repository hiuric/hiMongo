@echo off
:: Shift-JIS CR-LF
::---------------------------------------------------------------
:: hiMongoWorkerシンプルサービス

pushd %~dp0
set SCRIPT_DIR=%~dp0
set CNF_DIR=%SCRIPT_DIR%..\configure
call %CNF_DIR%\configure.bat nopause
::
set LIB_DIR=%SCRIPT_DIR%..\lib
set hiMongoLIB=%LIB_DIR%\%hiMongoJAR%
set hiNoteLIB=%LIB_DIR%\%hiNoteJAR%
set mongoLIB=%LIB_DIR%\%mongoJAR%

set LIBS=".;%hiNoteLIB%;%mongoLIB%;%hiMongoLIB%;%SLF4J%"
::
java -cp %LIBS% hi.db.hiMonWorkerSample -call localhost
pause

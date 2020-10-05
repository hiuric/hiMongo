@echo off
:: Shift-JIS CR-LF
::---------------------------------------------------------------
:: javadoc部を外したソース規模を見る
::--------------------
pushd %~dp0

set SCRIPT_DIR=%~dp0
set CNF_DIR=%SCRIPT_DIR%..\configure
call %CNF_DIR%\configure.bat nopause
set JDOC_DIR=%SCRIPT_DIR%..\docs

set TARGET=%hiMongo%
set JDOC_DIR=%SCRIPT_DIR%..\docs
set LIB_DIR=%SCRIPT_DIR%..\lib
set hiNoteLIB=%LIB_DIR%\%hiNoteJAR%

echo === wc hiMongo.java ===
java -jar %hiNoteLIB% wc hiMongo.java
java -jar %hiNoteLIB% conv -with remJavadoc.regex -in hiMongo.java -out remCom.out
echo;
echo === wc after remove javadoc === 
java -jar %hiNoteLIB% wc remCom.out
java -jar %hiNoteLIB% conv -with remCom.regex -in hiMongo.java -out remCom.out
echo;
echo === wc after remove comments === 
java -jar %hiNoteLIB% wc remCom.out

del remCom.out


:END
if not "%1"=="" goto NOPAUSE
:PAUSE
pause
:NOPAUSE
popd
exit /b %result%


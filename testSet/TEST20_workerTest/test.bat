@echo off
:: Shift-JIS CR-LF
pushd %~dp0
del kekka.txt 2> NUL
del mon.log 2> NUL
mongo --quiet localhost/db01  ..\test_data\collections.js
mongo --quiet localhost/db02  ..\test_data\collections2.js
call ..\test_sub.bat nopause
if not "%1"=="" goto NOPAUSE
:PAUSE
pause
:NOPAUSE
popd
exit /b %result%

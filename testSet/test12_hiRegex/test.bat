@echo off
:: Shift-JIS CR-LF
pushd %~dp0
mongo --quiet localhost/db01  ..\test_data\db01.js
call ..\test_sub.bat check2
if not "%1"=="" goto NOPAUSE
:PAUSE
pause
:NOPAUSE
popd
exit /b %result%

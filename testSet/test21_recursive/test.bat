@echo off
:: Shift-JIS CR-LF
pushd %~dp0
mongo --quiet localhost/db01  ..\test_data\collections.js
call ..\test_sub.bat nopause
if not "%1"=="" goto NOPAUSE
:PAUSE
pause
:NOPAUSE
popd
exit /b %result%

@echo off
:: Shift-JIS CR-LF
pushd %~dp0
mongo --quiet localhost/db02  ..\test_data\composers.js
call ..\test_sub.bat nopause
if not "%1"=="" goto NOPAUSE
:PAUSE
pause
:NOPAUSE
popd
exit /b %result%

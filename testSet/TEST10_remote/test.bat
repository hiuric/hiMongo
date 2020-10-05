@echo off
:: Shift-JIS CR-LF
if not defined TEST_ARGS set TEST_ARGS=192.168.1.139
pushd %~dp0
mongo --quiet localhost/db01  ..\test_data\db01.js
call ..\test_sub.bat nopause
if not "%1"=="" goto NOPAUSE
:PAUSE
pause
:NOPAUSE
popd
exit /b %result%

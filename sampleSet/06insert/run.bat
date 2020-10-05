@echo off
:: Shift-JIS CR-LF
pushd %~dp0
call ..\run_sub.bat nopause
if not "%1"=="" goto NOPAUSE
:PAUSE
pause
:NOPAUSE
popd
exit /b %result%

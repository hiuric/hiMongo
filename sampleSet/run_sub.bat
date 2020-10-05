@echo off
:: Shift-JIS CR-LF
::---------------------------------------------------------------
:: testXX/test.batから呼ばれるスクリプトです
:: カレントフォルダはtestXX個別フォルダのままです。
:: 
if  "%1"=="" (
   echo このバッチファイルは単独起動できません。
   goto PAUSE:
   )
call ..\configure.bat nopause
:: ビルド
%BUILD%
if ERRORLEVEL 1 goto ERR:

:: 実行
%RUN% 2> mon.log > kekka.txt
if ERRORLEVEL 1 goto ERR:
del *.class

:: 結果表示
%DISP% kekka.txt
goto END:

:ERR
:: ログ表示
type mon.log
echo @@@@@@ SOME ERROR OCCURRED @@@@@@

:END
if not "%1"=="" goto NOPAUSE
:PAUSE
pause
:NOPAUSE
exit /b %result%
::-------------------------------------------------------
:: メモ
:: ・do(...)else(...)の")else"は行を分けてはいけない
::   ")else"でキーワードと解釈すること
::-------------------------------------------------------

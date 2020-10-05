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
echo @@@@@@ OK @@@@@@

:: 結果表示(UTF-8のファイルをシステム標準文字セットで出力)
%DISP% kekka.txt

:: 結果比較
if "%1"=="check2" (
   %CHECK_KEKKA2%
) else (
   %CHECK_KEKKA%
   )
if ERRORLEVEL 1 goto CHECK_ERR:
echo @@@@@@ CHECK OK @@@@@@
goto END:

:CHECK_ERR
echo @@@@@@ CHECK ERROR @@@@@@
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

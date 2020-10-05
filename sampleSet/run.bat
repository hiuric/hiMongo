@echo off
:: Shift-JIS CR-LF
::---------------------------------------------------------------
:: 試験フォルダを巡り試験実行
:: 各試験でpushd/popdを行っている
setlocal enabledelayedexpansion
for /F "usebackq delims=" %%a in (`dir /B ^| findstr "[0-9][0-9].*"`) do (
   set DIR=%%a
   set DIR=!DIR: =!
   call !DIR!\run.bat nopause
   if ERRORLEVEL 1 goto ERR:
   )
endlocal


:DONE
echo @@@@@@ DONE @@@@@@
goto END:
:ERR
echo @@@@@@ ERROR @@@@@@

:END
if not "%1"=="" goto NOPAUSE
:PAUSE
pause
:NOPAUSE
exit /b %result%

::-------------------
:: サブルーチン
:run_test
call %~f1\test.bat nopause
if ERRORLEVEL 1 exit /b 1
exit /b 0

::-------------------------------------------------------
:: メモ
:: ・forやifのdo()内で変数をSETし参照する
::    setlocal enabledelayedexpansion
::    endlocal
::    で囲みます
::    ()内での参照は%NAME%ではなく!NAME!となります
::
:: ・コマンドを実行してその結果でループする
::    for /F "usebackq delims=" %%a in (`コマンド`) do (
::       )
::    /Fはトークンを取得する指定です
::    usebackqはコマンド記述をバッククオートで囲む指定です
::    delims=は行端をデリミタと見做しトークンを切り出す指定です
::
:: ・forのin内でのパイプ
::    パイプ|を書くにはキャレット^でエスケープする必要があります。
::        in(`dir /B ^| findstr "test[0-9].*"`)
::
:: ・文字列中のAをBに置き換える
::     set TEXT=%TEXT:A=B%
::   空白を削除するには
::     set TEXT=%TEXT: =%
::   そもそもこの変換作業時に何もしなくても最後の空白がないものが
::   変換用の引数となっている可能性もあります
::
:: ・文字列の最後の空白を取り去る
::     set TEXT=!TEXT:~1!
::   で最後の空白が削除されることを確認しました。
::   ただ、明確な仕様記述を発見できていませんので、ここでは採用していません。
::
:: ・置換の参照に%%aを使うことはできない（ようだ)
::     set DIR=%%a: =%   不可
::     call %%a: =%\test.bat nopause 不可
::
:: ・「指定されたドライブが見つかりません。」
::   "::"は本来はコメントではないため、場合によっては
::   掲題のエラーとなることがあります。
::   本来のREMを使うとこの問題は起きません。
::   ただし、視認性が極端に落ちますので注意が必要です。
::
::- - - - - 使わなかったコード
::次の形でもできることは分かったがここでは採用しなかった
for /F "usebackq delims=" %%a in (`dir /B ^| findstr "test[0-9].*"`) do (
   call :run_test %%a
   if ERRORLEVEL 1 goto ERR:
   )
::-------------------
:: サブルーチン
:run_test
call %~f1\test.bat nopause
if ERRORLEVEL 1 exit /b 1
exit /b 0
::-------------------------------------------------------

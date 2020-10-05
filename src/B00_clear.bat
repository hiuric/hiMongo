@echo off
:: Shift-JIS CR-LF
::---------------------------------------------------------------
:: 二次ファイルをクリアする
for /F "usebackq delims=" %%a in (`dir /B /S ..\testSet ^| findstr ".*\.class"`) do (
   echo del %%a
   del %%a
   )
for /F "usebackq delims=" %%a in (`dir /B /S ..\testSet ^| findstr ".*\\mon.log"`) do (
   echo del %%a
   del %%a
   )
for /F "usebackq delims=" %%a in (`dir /B /S ..\testSet ^| findstr ".*\\kekka.txt"`) do (
   echo del %%a
   del %%a
   )
for /F "usebackq delims=" %%a in (`dir /B /S ..\sampleSet ^| findstr ".*\.class"`) do (
   echo del %%a
   del %%a
   )
for /F "usebackq delims=" %%a in (`dir /B /S ..\sampleSet ^| findstr ".*\\mon.log"`) do (
   echo del %%a
   del %%a
   )
for /F "usebackq delims=" %%a in (`dir /B /S ..\sampleSet ^| findstr ".*\\kekka.txt"`) do (
   echo del %%a
   del %%a
   )
:END
if not "%1"=="" goto NOPAUSE
:PAUSE
pause
:NOPAUSE
exit /b %result%

::-------------------------------------------------------
:: メモ
::   for文に関して..\testSet\test.batに説明がある
:: 

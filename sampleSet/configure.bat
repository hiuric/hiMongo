@echo off
:: Shift-JIS CR-LF
::---------------------------------------------------------------
:: 次のマクロが設定されます。
::   %BUILD%        : 試験プログラムのビルド
::   %RUN%          : 試験プログラムの実行 (リダイレクトは含まない）
::   %CHECK_KEKKA%  : kekka.txtとref_kekka.txtの比較
::   %CHECK_KEKKA2% : kekka.txtとref_kekka.txtの比較(date以下無視)
::   %DISP%         : utf-8ファイルを表示
:: バージョン番号やライブラリ名は
::   ..\configure\configure.bat
:: で設定されます。
:: 本スクリプトを呼ぶと補助的にカレントフォルダの名称が
::   ----- フォルダ名 -----
:: の形で表示されます。試験の進行を示すためのものです。
::---------------------
if not defined MAIN set MAIN=Test

::--------------------
set SCRIPT_DIR=%~dp0
set CNF_DIR=%SCRIPT_DIR%..\configure
call %CNF_DIR%\configure.bat nopause

set LIB_DIR=%SCRIPT_DIR%..\lib
set hiMongoLIB=%LIB_DIR%\%hiMongoJAR%
set hiNoteLIB=%LIB_DIR%\%hiNoteJAR%
set mongoLIB=%LIB_DIR%\%mongoJAR%

set LIBS=".;%hiNoteLIB%;%mongoLIB%;%hiMongoLIB%;%SLF4J%"
set BUILD=javac -Xlint:unchecked -encoding utf-8 -cp %LIBS% %MAIN%.java
set RUN=java -cp %LIBS% %MAIN% %TEST_ARGS%
set CHECK_KEKKA=java -jar %hiNoteLIB% diff kekka.txt ref_kekka.txt -ign "// -omt _id=[^,]*" -omt "\"_id\"[^,]*"
set CHECK_KEKKA2=java -jar %hiNoteLIB% diff kekka.txt ref_kekka.txt -ign "// -omt date.*"
:: (sampleSetのkekka.txtはシステム標準)
:: set DISP=java -jar %hiNoteLIB% conv -inEncoding SYS -in
set DISP=type
call :edit_filename %CD%
echo ----- %TEST_DIR% -----
if not "%1"=="" goto NOPAUSE
echo BUILD=%BUILD%
pause
:NOPAUSE
exit /b %result%

::----------- サブルーチン
:edit_filename
set TEST_DIR=%~n1
exit /b

::-------------------------------------------------------
:: メモ
:: ・フォルダパス文字列からフォルダ名を取得
::  サブルーチン(edit_filename)にパス文字列を与え、名前部
::  を採り出しています。
::  ここでは試験フォルダ名を表示することに使っています。
::
:: ・引数の部分採り出し
::     %~[部分指定][引数の位置]
::     ~       :引用符を削除
::     部分指定:f 完全修飾パス名に展開
::              d ドライブ名
::              p パス名（ドライブを含まない）
::              n ファイル名（パスの最後の名前)
::              x ファイル拡張子
::              s short名のみとする（意味不明）
::              a ファイル属性
::              t 時刻
::              z サイズ
::        %~n1 で先頭引数のファイル名が取り出せます
::     引数位置:0 スクリプトの絶対パス
::        相対パスで呼び出されても%0は絶対パスとなります
::        %~dp0 でスクリプト自身のパスが得られます
::
:: ・結果チェックツール
::   java -jar %hiNoteLIB% diffはオツライブラリで用意される簡易diff
::   です。
::   -ign "//"は"//"で始まる文字列の比較をしない指定です
::   -omt は指定正規表現に合致する部分を無視して比較します
::   http://www.otsu.co.jp/OtsuLibrary/jdoc/otsu/hiNote/COMMAND.html
::
:: ・utf-8ファイルを表示(文字変換ツール)
::   java -jar %hiNoteLIB% conv -nはutf-8ファイルをシステム
::   標準文字コードで表示させるコマンドです。
::   http://www.otsu.co.jp/OtsuLibrary/jdoc/otsu/hiNote/COMMAND.html
::   種々の変換機能を持ちますがここでは文字コードの変換のみを行います
::-------------------------------------------------------

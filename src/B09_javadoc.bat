@echo off
:: Shift-JIS CR-LF
:: 超重要ポイント -Xdoclint:none 悪辣なHTML5検閲を回避する
::---------------------------------------------------------------
:: javadoc作成
::
pushd %~dp0
set SCRIPT_DIR=%~dp0
set CNF_DIR=%SCRIPT_DIR%..\configure
echo CNF_DIR=%SCRIPT_DIR%..\configure
call %CNF_DIR%\configure.bat nopause

set TARGET=%hiMongo%
set JDOC_DIR=%SCRIPT_DIR%..\docs
set LIB_DIR=%SCRIPT_DIR%..\lib
set hiMongoLIB=%LIB_DIR%\%hiMongoJAR%
set hiNoteLIB=%LIB_DIR%\%hiNoteJAR%
set mongoLIB=%LIB_DIR%\%mongoJAR%
set LIBS=".;%hiNoteLIB%;%mongoLIB%;%hiMongoLIB%;%SLF4J%"

:: jdocセットのクリア
rd /s /q %JDOC_DIR% 2> /dev/nul
mkdir %JDOC_DIR%
mkdir %JDOC_DIR%\img

::----------------------------------------
:: JDOC内の<、>などエスケープ 
:: 変換結果は hi/hiMongo.javaに置かれる
::----------------------------------------
rd /s /q .\hi\
mkdir .\hi\
echo java -jar %hiNoteLIB% conv -with propJ.txt -in %TARGET%.java -out hi\%TARGET%.java
java -jar %hiNoteLIB% conv -with propJ.txt -in %TARGET%.java -out hi\%TARGET%.java

::----------------------------------------
:: javadoc実施 (-link先がhttpのものが有ることに留意)
:: mongo-java-driver3.12は上手く@linkされないのでこの段階では3.7を
:: 使う。javadoc作成後にhtml内容を置き換える
:: http://mongodb.github.io/mongo-java-driver/3.7/javadoc/org/bson/Document.html?is-external=true
:: -> https://mongodb.github.io/mongo-java-driver/3.12/javadoc/org/bson/Document.htm?is-external=truel
:: -Xdoclint:noneを付けると@linkのチェックもなくなるので、一旦-Xdoclint:noneを外して@linkのチェック
:: を行う。
::----------------------------------------

echo javadoc -Xdoclint:none -public -link http://www.otsu.co.jp/OtsuLibrary/jdoc/ -link https://docs.oracle.com/javase/jp/8/docs/api/  -link http://mongodb.github.io/mongo-java-driver/3.7/javadoc/ --allow-script-in-comments -encoding UTF-8 -charset "UTF-8" -docencoding UTF-8 -d %JDOC_DIR% -classpath %LIBS% hi\%TARGET%.java

javadoc -Xdoclint:none -public -link http://www.otsu.co.jp/OtsuLibrary/jdoc/ -link https://docs.oracle.com/javase/jp/8/docs/api/  -link http://mongodb.github.io/mongo-java-driver/3.7/javadoc/ --allow-script-in-comments -encoding UTF-8 -charset "UTF-8" -docencoding UTF-8 -d %JDOC_DIR% -classpath %LIBS% hi\%TARGET%.java


rd /s /q .\hi\

:: GitHubのREADME.mdで使う画像
copy sample0.png %JDOC_DIR%\img\
:: cssのマージ
type %JDOC_DIR%\stylesheet.css .\hi_style.css > tmp.css
move tmp.css %JDOC_DIR%\stylesheet.css

::--------------------------
:: htmlに変更を加える
:: (1) http参照にtarget="_blank" rel="noopener noreferrer"
::--------------------------
for /F "usebackq delims=" %%a in (`dir /B /S %JDOC_DIR% ^| findstr ".*\.*html"`) do (
   call :convert_html %%a
   )
:: 自ホスト上のwebサーバへ送る
:COPY
rd /s /q C:\xampp\htdocs\hiMongo\
xcopy /e %JDOC_DIR% C:\xampp\htdocs\hiMongo\

:: 0_05->0_06 mongo-java-driver参照を3.7から3.12に変更

:END
if not "%1"=="" goto NOPAUSE
:PAUSE
pause
:NOPAUSE
popd
exit /b %result%

::----------- サブルーチン
:: javadoc生成のhtmlに次の変更を施す
:: (1) 外部リンクにtarget="_blank" rel="..."追加
:: (2) mongo-java-dreiverバージョン参照3.07->3.12
::
:: %~1:htmlファイル
:convert_html
   echo java -jar %hiNoteLIB% conv -with replace.regex -in %~1 -out %~1.out
   java -jar %hiNoteLIB% conv -with replace.regex -in %~1 -out %~1.out
   echo java -jar %hiNoteLIB% conv -with convDrivVer.regex -in %~1.out -out %~1
   java -jar %hiNoteLIB% conv -with convDrivVer.regex -in %~1.out -out %~1
   del %~1.out
exit /b

::-------------------------------------------------------
:: メモ
:: ・javadocでhtml記述に対する余計なHTML5検閲を回避する
::     -Xdoclint:none 
::
:: ・フォルダをコピーする  !!! /E を忘れるな　!!!
::  フォルダ階層をコピーするには
::    xcopy /E コピー元フォルダ コピー先フォルダ
::  を使います。
::  注意が必要なの"/Eオプションが必要"なことです。
::
:: ・Java変更
::  JDOC内の<や>は&lt,&gtに置き換える必要があります。
::  java -jar %hiNoteLIB% convで置き換えます
::  置き換えパターンはpropJ.txtです
::  <caption>に関してはjavadoc オプション-doclint:noneで
::  不要になりましたが念のために残してあります。
::
:: ・html変更
::   java -jar %hiNoteLIB% convはテキスト変更です。
::   正規表現パターンで変更を指定します。
::   正規表現のパターンは
::     外部リンクに
::         target="_blank" rel="noopener noreferrer"
::     を付加                        -- replace.regex
::     バージョン変更処理            -- convDrivver,regex
::   です。
::   convの仕様記述は次の場所にあります。
::   http://www.otsu.co.jp/OtsuLibrary/jdoc/otsu/hiNote/COMMAND.html



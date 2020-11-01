@echo off
:: Shift-JIS CR-LF
:: hiMongoのライブラリ、バージョン設定
:: src,testSet,sampleSetから共通に呼ばれる
:: mongoDBのログを抑止するためのだけライブラリは含んでいない

set hiMongo=hiMongo
set hiMongoVER=_0_11
set hiMongoJAR=%hiMongo%%hiMongoVER%.jar
set hiNoteJAR=hiNote_3_10.jar
set mongoJAR=mongo-java-driver-3.12.5.jar

if not "%1"=="" goto NOPAUSE
echo hiMongoJAR=%hiMongoJAR%
echo hiNoteJAR=%hiNoteJAR%
echo mongoJAR=%mongoJAR%
pause
:NOPAUSE
exit /b %result%


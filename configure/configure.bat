@echo off
:: Shift-JIS CR-LF
:: hiMongo�̃��C�u�����A�o�[�W�����ݒ�
:: src,testSet,sampleSet���狤�ʂɌĂ΂��
:: mongoDB�̃��O��}�~���邽�߂̂������C�u�����͊܂�ł��Ȃ�

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


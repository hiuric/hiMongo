@echo off
:: Shift-JIS CR-LF
:: ���d�v�|�C���g -Xdoclint:none ��煂�HTML5���{���������
::---------------------------------------------------------------
:: javadoc�쐬
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

:: jdoc�Z�b�g�̃N���A
rd /s /q %JDOC_DIR% 2> /dev/nul
mkdir %JDOC_DIR%
mkdir %JDOC_DIR%\img

::----------------------------------------
:: JDOC����<�A>�ȂǃG�X�P�[�v 
:: �ϊ����ʂ� hi/hiMongo.java�ɒu�����
::----------------------------------------
rd /s /q .\hi\
mkdir .\hi\
echo java -jar %hiNoteLIB% conv -with propJ.txt -in %TARGET%.java -out hi\%TARGET%.java
java -jar %hiNoteLIB% conv -with propJ.txt -in %TARGET%.java -out hi\%TARGET%.java

::----------------------------------------
:: javadoc���{ (-link�悪http�̂��̂��L�邱�Ƃɗ���)
:: mongo-java-driver3.12�͏�肭@link����Ȃ��̂ł��̒i�K�ł�3.7��
:: �g���Bjavadoc�쐬���html���e��u��������
:: http://mongodb.github.io/mongo-java-driver/3.7/javadoc/org/bson/Document.html?is-external=true
:: -> https://mongodb.github.io/mongo-java-driver/3.12/javadoc/org/bson/Document.htm?is-external=truel
:: -Xdoclint:none��t�����@link�̃`�F�b�N���Ȃ��Ȃ�̂ŁA��U-Xdoclint:none���O����@link�̃`�F�b�N
:: ���s���B
::----------------------------------------

echo javadoc -Xdoclint:none -public -link http://www.otsu.co.jp/OtsuLibrary/jdoc/ -link https://docs.oracle.com/javase/jp/8/docs/api/  -link http://mongodb.github.io/mongo-java-driver/3.7/javadoc/ --allow-script-in-comments -encoding UTF-8 -charset "UTF-8" -docencoding UTF-8 -d %JDOC_DIR% -classpath %LIBS% hi\%TARGET%.java

javadoc -Xdoclint:none -public -link http://www.otsu.co.jp/OtsuLibrary/jdoc/ -link https://docs.oracle.com/javase/jp/8/docs/api/  -link http://mongodb.github.io/mongo-java-driver/3.7/javadoc/ --allow-script-in-comments -encoding UTF-8 -charset "UTF-8" -docencoding UTF-8 -d %JDOC_DIR% -classpath %LIBS% hi\%TARGET%.java


rd /s /q .\hi\

:: GitHub��README.md�Ŏg���摜
copy sample0.png %JDOC_DIR%\img\
:: css�̃}�[�W
type %JDOC_DIR%\stylesheet.css .\hi_style.css > tmp.css
move tmp.css %JDOC_DIR%\stylesheet.css

::--------------------------
:: html�ɕύX��������
:: (1) http�Q�Ƃ�target="_blank" rel="noopener noreferrer"
::--------------------------
for /F "usebackq delims=" %%a in (`dir /B /S %JDOC_DIR% ^| findstr ".*\.*html"`) do (
   call :convert_html %%a
   )
:: ���z�X�g���web�T�[�o�֑���
:COPY
rd /s /q C:\xampp\htdocs\hiMongo\
xcopy /e %JDOC_DIR% C:\xampp\htdocs\hiMongo\

:: 0_05->0_06 mongo-java-driver�Q�Ƃ�3.7����3.12�ɕύX

:END
if not "%1"=="" goto NOPAUSE
:PAUSE
pause
:NOPAUSE
popd
exit /b %result%

::----------- �T�u���[�`��
:: javadoc������html�Ɏ��̕ύX���{��
:: (1) �O�������N��target="_blank" rel="..."�ǉ�
:: (2) mongo-java-dreiver�o�[�W�����Q��3.07->3.12
::
:: %~1:html�t�@�C��
:convert_html
   echo java -jar %hiNoteLIB% conv -with replace.regex -in %~1 -out %~1.out
   java -jar %hiNoteLIB% conv -with replace.regex -in %~1 -out %~1.out
   echo java -jar %hiNoteLIB% conv -with convDrivVer.regex -in %~1.out -out %~1
   java -jar %hiNoteLIB% conv -with convDrivVer.regex -in %~1.out -out %~1
   del %~1.out
exit /b

::-------------------------------------------------------
:: ����
:: �Ejavadoc��html�L�q�ɑ΂���]�v��HTML5���{���������
::     -Xdoclint:none 
::
:: �E�t�H���_���R�s�[����  !!! /E ��Y���ȁ@!!!
::  �t�H���_�K�w���R�s�[����ɂ�
::    xcopy /E �R�s�[���t�H���_ �R�s�[��t�H���_
::  ���g���܂��B
::  ���ӂ��K�v�Ȃ�"/E�I�v�V�������K�v"�Ȃ��Ƃł��B
::
:: �EJava�ύX
::  JDOC����<��>��&lt,&gt�ɒu��������K�v������܂��B
::  java -jar %hiNoteLIB% conv�Œu�������܂�
::  �u�������p�^�[����propJ.txt�ł�
::  <caption>�Ɋւ��Ă�javadoc �I�v�V����-doclint:none��
::  �s�v�ɂȂ�܂������O�̂��߂Ɏc���Ă���܂��B
::
:: �Ehtml�ύX
::   java -jar %hiNoteLIB% conv�̓e�L�X�g�ύX�ł��B
::   ���K�\���p�^�[���ŕύX���w�肵�܂��B
::   ���K�\���̃p�^�[����
::     �O�������N��
::         target="_blank" rel="noopener noreferrer"
::     ��t��                        -- replace.regex
::     �o�[�W�����ύX����            -- convDrivver,regex
::   �ł��B
::   conv�̎d�l�L�q�͎��̏ꏊ�ɂ���܂��B
::   http://www.otsu.co.jp/OtsuLibrary/jdoc/otsu/hiNote/COMMAND.html



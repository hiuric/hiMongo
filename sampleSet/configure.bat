@echo off
:: Shift-JIS CR-LF
::---------------------------------------------------------------
:: ���̃}�N�����ݒ肳��܂��B
::   %BUILD%        : �����v���O�����̃r���h
::   %RUN%          : �����v���O�����̎��s (���_�C���N�g�͊܂܂Ȃ��j
::   %CHECK_KEKKA%  : kekka.txt��ref_kekka.txt�̔�r
::   %CHECK_KEKKA2% : kekka.txt��ref_kekka.txt�̔�r(date�ȉ�����)
::   %DISP%         : utf-8�t�@�C����\��
:: �o�[�W�����ԍ��⃉�C�u��������
::   ..\configure\configure.bat
:: �Őݒ肳��܂��B
:: �{�X�N���v�g���ĂԂƕ⏕�I�ɃJ�����g�t�H���_�̖��̂�
::   ----- �t�H���_�� -----
:: �̌`�ŕ\������܂��B�����̐i�s���������߂̂��̂ł��B
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
:: (sampleSet��kekka.txt�̓V�X�e���W��)
:: set DISP=java -jar %hiNoteLIB% conv -inEncoding SYS -in
set DISP=type
call :edit_filename %CD%
echo ----- %TEST_DIR% -----
if not "%1"=="" goto NOPAUSE
echo BUILD=%BUILD%
pause
:NOPAUSE
exit /b %result%

::----------- �T�u���[�`��
:edit_filename
set TEST_DIR=%~n1
exit /b

::-------------------------------------------------------
:: ����
:: �E�t�H���_�p�X�����񂩂�t�H���_�����擾
::  �T�u���[�`��(edit_filename)�Ƀp�X�������^���A���O��
::  ���̂�o���Ă��܂��B
::  �����ł͎����t�H���_����\�����邱�ƂɎg���Ă��܂��B
::
:: �E�����̕����̂�o��
::     %~[�����w��][�����̈ʒu]
::     ~       :���p�����폜
::     �����w��:f ���S�C���p�X���ɓW�J
::              d �h���C�u��
::              p �p�X���i�h���C�u���܂܂Ȃ��j
::              n �t�@�C�����i�p�X�̍Ō�̖��O)
::              x �t�@�C���g���q
::              s short���݂̂Ƃ���i�Ӗ��s���j
::              a �t�@�C������
::              t ����
::              z �T�C�Y
::        %~n1 �Ő擪�����̃t�@�C���������o���܂�
::     �����ʒu:0 �X�N���v�g�̐�΃p�X
::        ���΃p�X�ŌĂяo����Ă�%0�͐�΃p�X�ƂȂ�܂�
::        %~dp0 �ŃX�N���v�g���g�̃p�X�������܂�
::
:: �E���ʃ`�F�b�N�c�[��
::   java -jar %hiNoteLIB% diff�̓I�c���C�u�����ŗp�ӂ����Ȉ�diff
::   �ł��B
::   -ign "//"��"//"�Ŏn�܂镶����̔�r�����Ȃ��w��ł�
::   -omt �͎w�萳�K�\���ɍ��v���镔���𖳎����Ĕ�r���܂�
::   http://www.otsu.co.jp/OtsuLibrary/jdoc/otsu/hiNote/COMMAND.html
::
:: �Eutf-8�t�@�C����\��(�����ϊ��c�[��)
::   java -jar %hiNoteLIB% conv -n��utf-8�t�@�C�����V�X�e��
::   �W�������R�[�h�ŕ\��������R�}���h�ł��B
::   http://www.otsu.co.jp/OtsuLibrary/jdoc/otsu/hiNote/COMMAND.html
::   ��X�̕ϊ��@�\�������܂��������ł͕����R�[�h�̕ϊ��݂̂��s���܂�
::-------------------------------------------------------

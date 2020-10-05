@echo off
:: Shift-JIS CR-LF
::---------------------------------------------------------------
:: �����t�H���_�����莎�����s
:: �e������pushd/popd���s���Ă���
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
:: �T�u���[�`��
:run_test
call %~f1\test.bat nopause
if ERRORLEVEL 1 exit /b 1
exit /b 0

::-------------------------------------------------------
:: ����
:: �Efor��if��do()���ŕϐ���SET���Q�Ƃ���
::    setlocal enabledelayedexpansion
::    endlocal
::    �ň݂͂܂�
::    ()���ł̎Q�Ƃ�%NAME%�ł͂Ȃ�!NAME!�ƂȂ�܂�
::
:: �E�R�}���h�����s���Ă��̌��ʂŃ��[�v����
::    for /F "usebackq delims=" %%a in (`�R�}���h`) do (
::       )
::    /F�̓g�[�N�����擾����w��ł�
::    usebackq�̓R�}���h�L�q���o�b�N�N�I�[�g�ň͂ގw��ł�
::    delims=�͍s�[���f���~�^�ƌ��􂵃g�[�N����؂�o���w��ł�
::
:: �Efor��in���ł̃p�C�v
::    �p�C�v|�������ɂ̓L�����b�g^�ŃG�X�P�[�v����K�v������܂��B
::        in(`dir /B ^| findstr "test[0-9].*"`)
::
:: �E�����񒆂�A��B�ɒu��������
::     set TEXT=%TEXT:A=B%
::   �󔒂��폜����ɂ�
::     set TEXT=%TEXT: =%
::   �����������̕ϊ���Ǝ��ɉ������Ȃ��Ă��Ō�̋󔒂��Ȃ����̂�
::   �ϊ��p�̈����ƂȂ��Ă���\��������܂�
::
:: �E������̍Ō�̋󔒂���苎��
::     set TEXT=!TEXT:~1!
::   �ōŌ�̋󔒂��폜����邱�Ƃ��m�F���܂����B
::   �����A���m�Ȏd�l�L�q�𔭌��ł��Ă��܂���̂ŁA�����ł͍̗p���Ă��܂���B
::
:: �E�u���̎Q�Ƃ�%%a���g�����Ƃ͂ł��Ȃ��i�悤��)
::     set DIR=%%a: =%   �s��
::     call %%a: =%\test.bat nopause �s��
::
:: �E�u�w�肳�ꂽ�h���C�u��������܂���B�v
::   "::"�͖{���̓R�����g�ł͂Ȃ����߁A�ꍇ�ɂ���Ă�
::   �f��̃G���[�ƂȂ邱�Ƃ�����܂��B
::   �{����REM���g���Ƃ��̖��͋N���܂���B
::   �������A���F�����ɒ[�ɗ����܂��̂Œ��ӂ��K�v�ł��B
::
::- - - - - �g��Ȃ������R�[�h
::���̌`�ł��ł��邱�Ƃ͕��������������ł͍̗p���Ȃ�����
for /F "usebackq delims=" %%a in (`dir /B ^| findstr "test[0-9].*"`) do (
   call :run_test %%a
   if ERRORLEVEL 1 goto ERR:
   )
::-------------------
:: �T�u���[�`��
:run_test
call %~f1\test.bat nopause
if ERRORLEVEL 1 exit /b 1
exit /b 0
::-------------------------------------------------------

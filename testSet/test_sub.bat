@echo off
:: Shift-JIS CR-LF
::---------------------------------------------------------------
:: testXX/test.bat����Ă΂��X�N���v�g�ł�
:: �J�����g�t�H���_��testXX�ʃt�H���_�̂܂܂ł��B
:: 
if  "%1"=="" (
   echo ���̃o�b�`�t�@�C���͒P�ƋN���ł��܂���B
   goto PAUSE:
   )
call ..\configure.bat nopause
:: �r���h
%BUILD%
if ERRORLEVEL 1 goto ERR:

:: ���s
%RUN% 2> mon.log > kekka.txt
if ERRORLEVEL 1 goto ERR:
del *.class
echo @@@@@@ OK @@@@@@

:: ���ʕ\��(UTF-8�̃t�@�C�����V�X�e���W�������Z�b�g�ŏo��)
%DISP% kekka.txt

:: ���ʔ�r
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
:: ���O�\��
type mon.log
echo @@@@@@ SOME ERROR OCCURRED @@@@@@

:END
if not "%1"=="" goto NOPAUSE
:PAUSE
pause
:NOPAUSE
exit /b %result%
::-------------------------------------------------------
:: ����
:: �Edo(...)else(...)��")else"�͍s�𕪂��Ă͂����Ȃ�
::   ")else"�ŃL�[���[�h�Ɖ��߂��邱��
::-------------------------------------------------------

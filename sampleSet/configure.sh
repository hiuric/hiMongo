#!/bin/sh
# 実行 各試験フォルダで
#   MAIN=試験クラス  (省略時はTest)
#   source ../configure.sh
#   eval ${BUILD_AND_RUN}
#---------------------
MAIN=${MAIN:-Test}
#---------------------
SCRIPT_DIR=$(cd $(dirname ${BASH_SOURCE:-$0}); pwd)
CNF_DIR=${SCRIPT_DIR}/../configure
source ${CNF_DIR}/configure.sh
LIB_DIR=${SCRIPT_DIR}/../lib
hiMongoLIB=${LIB_DIR}/${hiMongoJAR}
hiNoteLIB=${LIB_DIR}/${hiNoteJAR}
mongoLIB=${LIB_DIR}/${mongoJAR}
#LOG_LIB=../libs_only_for_disable_log
#SLF4J=${LOG_LIB}/slf4j-api-1.7.2.jar:${LOG_LIB}:slf4j-log4j12-1.5.6.jar:${LOG_LIB}/slf4j-simple-1.6.2.jar:${LOG_LIB}/logback-classic-1.2.3.jar:${LOG_LIB}/logback-core-1.2.3.jar
export LIBS=".:${hiNoteLIB}:${mongoLIB}:${hiMongoLIB}:${SLF4J}"
export JAVAC="javac -Xlint:unchecked -encoding utf-8 -cp ${LIBS} ${MAIN}.java"
export RUN="java -cp ${LIBS} ${MAIN} 2> mon.log"
export BUILD_AND_RUN="${JAVAC};${RUN}"
#
export TEST_DIR=$(basename `pwd`)
echo ----- ${TEST_DIR} -----
#
# memo:
#  改行=LF
#  ${MAIN:-xxxx}は${MAIN}が設定されていない場合xxxxを採用する記述
#  SCRIPT_DIR=はsource呼び出しでもこのスクリプトのdirを設定している
#  SLF4Jはmongodbのコンソールログを抑止するためのもの
#  現在ログの抑止法は不明のためlog(標準err)を各dirのmon.logに入れている
#

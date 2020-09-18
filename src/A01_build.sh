#!/bin/sh
# 注意！ 改行=LF
# hiMongoライブラリをビルドする
#---------------------
SCRIPT_DIR=$(cd $(dirname ${BASH_SOURCE:-$0}); pwd)
CNF_DIR=${SCRIPT_DIR}/../configure
source ${CNF_DIR}/configure.sh
LIB_DIR=${SCRIPT_DIR}/../lib
hiMongoLIB=${LIB_DIR}/${hiMongoJAR}
hiNoteLIB=${LIB_DIR}/${hiNoteJAR}
mongoLIB=${LIB_DIR}/${mongoJAR}
LIBS=".:${hiNoteLIB}:${mongoLIB}:${SLF4J}"
#-----
javac -Xlint:unchecked -Xlint:deprecation -encoding utf-8 -cp ${LIBS} ${hiMongo}.java
#-----
mkdir hi/
mv *.class hi/ > /dev/null
jar -cvfM ${hiMongoJAR} hi/*.class
rm -rf hi/
mv ${hiMongoJAR} ${LIB_DIR}


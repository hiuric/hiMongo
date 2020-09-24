#!/bin/sh
# LF    CR-LFだと動かない
SCRIPT_DIR=$(cd $(dirname ${BASH_SOURCE:-$0}); pwd)
CNF_DIR=${SCRIPT_DIR}/../configure
JDOC_DIR=../docs
source ${CNF_DIR}/configure.sh
TARGET=${hiMongo}
LIB_DIR=${SCRIPT_DIR}/../lib
hiNoteLIB=${LIB_DIR}/${hiNoteJAR}
echo === wc hiMongo.java ===
java -jar ${hiNoteLIB} wc hiMongo.java
java -jar ${hiNoteLIB} conv -with remJavadoc.regex -in hiMongo.java -out remCom.out
echo
echo === wc after remove javadoc === 
java -jar ${hiNoteLIB} wc remCom.out
java -jar ${hiNoteLIB} conv -with remCom.regex -in hiMongo.java -out remCom.out
echo
echo === wc after remove comments === 
java -jar ${hiNoteLIB} wc remCom.out

rm remCom.out >/dev/null

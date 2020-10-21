#!/bin/bash
SCRIPT_DIR=$(cd $(dirname ${BASH_SOURCE:-$0}); pwd)
CNF_DIR=${SCRIPT_DIR}/../configure
source ${CNF_DIR}/configure.sh

LIB_DIR=${SCRIPT_DIR}/../lib
hiMongoLIB=${LIB_DIR}/${hiMongoJAR}
hiNoteLIB=${LIB_DIR}/${hiNoteJAR}
mongoLIB=${LIB_DIR}/${mongoJAR}
LIBS=".:${hiNoteLIB}:${mongoLIB}:${hiMongoLIB}"

java -cp ${LIBS} hi.db.hiMonWorkerSample -call localhost


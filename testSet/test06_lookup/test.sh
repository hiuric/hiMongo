#!/bin/bash
# 注意！ 改行=LF
#../test_data/sampleData.sh
mongo --quiet localhost/sampleDB  ../test_data/sampleDB.js
source ../configure.sh
eval ${BUILD_AND_RUN}
status=$?
if [ ${status} -eq 0 ];then echo @@@@@@ OK @@@@@@;else cat mon.log;echo @@@@@@ SOME ERROR OCCURRED @@@@@@;exit 1; fi
eval ${DISP}
eval ${CHECK_KEKKA}
status=$?
if [ ${status} -eq 0 ];then echo @@@@@@ CHECK OK @@@@@@;else echo @@@@@@ CHECK ERROR @@@@@@; fi
rm *.class
exit ${status}

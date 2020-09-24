#!/bin/bash
# 注意！ 改行=LF
../test_data/db01.sh >/dev/null
source ../configure.sh
eval ${BUILD_AND_RUN}
status=$?
if [ ${status} -eq 0 ];then echo OK;else echo SOME ERROR OCCURRED; fi
rm *.class
exit ${status}

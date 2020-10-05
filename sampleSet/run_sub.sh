#!/bin/bash
# UTF-8 LF
source ../configure.sh
eval ${BUILD_AND_RUN}
status=$?
if [ ${status} -eq 0 ];then eval ${DISP};echo OK;else cat mon.log;echo SOME ERROR OCCURRED; fi
rm *.class
exit ${status}

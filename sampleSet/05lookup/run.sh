#!/bin/bash
# 注意！ 改行=LF
#./sampleData.sh > /dev/null
mongo --quiet localhost/sampleDB  ./sampleDB.js
source ../run_sub.sh
status=$?
exit ${status}

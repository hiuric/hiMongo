#!/bin/bash
# 注意！ 改行=LF
#./db01.sh > /dev/null
mongo --quiet localhost/db01  ./db01.js
source ../run_sub.sh
status=$?
exit ${status}

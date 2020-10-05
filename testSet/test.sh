#!/bin/sh
# 試験フォルダを巡り試験実行
for f in *; do
    if [ -d "$f" ]; then
        if expr "$f" : 'test[0-9].*' > /dev/null ; then
           (cd $f;./test.sh)
           if [ $? -ne 0 ];then exit 1;fi
        fi
    fi
done

#10はリモートの存在が必要なので別建てにしてある
export TEST_ARGS=192.168.1.139
(cd TEST10_remote;./test.sh)
if [ $? -ne 0 ];then exit 1;fi


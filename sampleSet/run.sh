#!/bin/sh
# 試験フォルダを巡り試験実行
for f in *; do
    if [ -d "$f" ]; then
        if expr "$f" : '[0-9].*' > /dev/null ; then
           (cd $f;./run.sh)
           if [ $? -ne 0 ];then exit 1;fi
        fi
    fi
done


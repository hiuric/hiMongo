#!/bin/bash
mongo --quiet << 'EOT'
use db01;
db.coll_01.                             // collection 'coll_01'選択
   find({type:'A'},{_id:0}).            // typeが'A'のレコード,_idは不要
   sort({_id:-1}).                      // _idで逆向きにソート
   limit(3).                            // 個数制限
   toArray().reverse().                 // 反転したリスト取得
   forEach(S=>print(JSON.stringify(S)));// レコード表示
EOT

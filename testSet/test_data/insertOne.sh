#!/bin/bash
mongo --quiet << 'EOT'
use db01
db.coll_01.drop();
db.coll_01
  .insertOne({type:'A',value:23,date:new Date(1597648050000)});
EOT

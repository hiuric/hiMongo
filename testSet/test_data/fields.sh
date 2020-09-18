#!/bin/bash
mongo --quiet << 'EOT'
use db02
db.coll_flds.drop()
db.coll_flds.insertMany([
    {A:10,B:20,C:30,D:40}
   ,{A:11,B:21,C:31,D:41}
   ])
print("find()")
db.coll_flds.find()
print("find({})")
db.coll_flds.find({})
print("find({},{})")
db.coll_flds.find({},{})
print("find({},{_id:0})")
db.coll_flds.find({},{_id:0})
print("find({},{_id:0,A:1})")
db.coll_flds.find({},{_id:0,A:1})
print("find({},{_id:0,A:0})")
db.coll_flds.find({},{_id:0,A:0})
print("find({},{_id:0,A:1,B:1})")
db.coll_flds.find({},{_id:0,A:1,C:1})
print("find({},{_id:0,A:0,B:0})")
db.coll_flds.find({},{_id:0,A:0,C:0})
EOT

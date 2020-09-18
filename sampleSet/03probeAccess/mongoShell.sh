#!/bin/bash
mongo --quiet << 'EOT'
use db01
var _last_date=db.coll_01.find({type:'A'},{_id:0,date:1}).
                  sort({_id:-1}).
                  limit(1).toArray()[0].date.getTime()
var _start_date=_last_date-30000
var _isodate=new Date(_start_date)
var _r=db.coll_01.aggregate([
   { $match:{$and:[{type:'A'},{date:{$gte:_isodate}}]}},
   { $group:{_id:"$type",min:{$min:"$value"},max:{$max:"$value"},avg:{$avg:"$value"}}}
   ]).toArray()[0]
print("last="+_last_date+" start="+_start_date)
print("min="+_r.min+" max="+_r.max+" avg="+_r.avg.toFixed(2));
EOT

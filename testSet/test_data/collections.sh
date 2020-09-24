#!/bin/bash
mongo --quiet << 'EOT'
use db01
db.dropDatabase()
db.coll_01.drop()
db.coll_01.insertMany([
   // ISODateはグリニッジ標準時であることに注意
   {type:'A',value:12.3,date:ISODate('2020-08-17T07:07:00.000Z')}
  ,{type:'A',value:4.56,date:ISODate('2020-08-17T07:07:10.000Z')}
  ,{type:'B',value:2001,date:ISODate('2020-08-17T07:07:20.000Z')}
  ,{type:'A',value:7.89,date:ISODate('2020-08-17T07:07:30.000Z')}
  ,{type:'A',value:0.12,date:ISODate('2020-08-17T07:07:40.000Z')}
   ])
db.coll_01.find()
db.coll_02.drop()
db.coll_02.insertMany([
   // ISODateはグリニッジ標準時であることに注意
  ,{type:'B',value:2001,date:ISODate('2020-08-17T07:07:20.000Z')}
  ,{type:'A',value:7.89,date:ISODate('2020-08-17T07:07:30.000Z')}
  ,{type:'A',value:0.12,date:ISODate('2020-08-17T07:07:40.000Z')}
   ])
use db02
db.dropDatabase()
db.coll_01.drop()
db.coll_01.insertMany([
   // ISODateはグリニッジ標準時であることに注意
   {type:'C',value:12.3,date:ISODate('2020-08-17T07:07:00.000Z')}
  ,{type:'D',value:4.56,date:ISODate('2020-08-17T07:07:10.000Z')}
  ,{type:'E',value:2001,date:ISODate('2020-08-17T07:07:20.000Z')}
   ])
db.coll_01.find()
db.coll_02.drop()
db.coll_02.insertMany([
   // ISODateはグリニッジ標準時であることに注意
  ,{type:'X',value:2001,date:ISODate('2020-08-17T07:07:20.000Z')}
  ,{type:'Y',value:7.89,date:ISODate('2020-08-17T07:07:30.000Z')}
  ,{type:'Z',value:0.12,date:ISODate('2020-08-17T07:07:40.000Z')}
   ])
//
show dbs
use db01
show collections
use db02
show collections
// exists
// count
EOT

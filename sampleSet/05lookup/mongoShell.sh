#!/bin/bash
mongo --quiet << 'EOT'
use sampleDB
db.店舗商品.
    aggregate([
       {$match:{$or:[
          {'店舗名':'東京'},
          {'店舗名':'福岡'}
          ]}},
      {$lookup:{
         from:'商品',
         localField:'商品id',
         foreignField:'商品id',
         as:'from商品'
         }},
      {$project:{
         '_id':0,
         '店舗名':1,
         'from商品.商品名':1,
         'from商品.販売単価':1,
         '数量':1}},
     {$unwind:'$from商品'}
    ])
EOT

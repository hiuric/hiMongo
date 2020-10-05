// use db01
db.coll_01.drop()
db.coll_01.insertMany([
   {type:'C',name:'X',value:5},
   {type:'C',name:'Y',value:10},
   {type:'C',name:'X',value:13}
   ])
db.coll_01.find()

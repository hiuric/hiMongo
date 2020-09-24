import hi.hiMongo;
import otsu.hiNote.*;
import java.util.*;
import java.io.*;
public class Test {
   static PrintStream ps=System.out;
   static class Arec {
      String _id;
      double min;
      double max;
      double avg;
      public String toString(){
         return String.format("min=%.2f max=%.2f avg=%.2f",min,max,avg);
         }
      }
   static class WithDate {
      Date date;
      }
   public static void main(String[] args_){
      if( "yes".equals(System.getenv("WITH_HSON")) ) hiMongo.with_hson(true);
      try{
         hiMongo.DB db=hiMongo.use("db01");
         long _start_date=
         db.get("coll_01")
            .find("{type:'A'}","{_id:0,date:1}")
            //.sort("{_id:-1}").limit(1).getProbeList().get(0)
            //.to("date").get(D->((Date)D).getTime()-30000)
            .sort("{_id:-1}").limit(1).getClassList(WithDate.class).get(0)
            .date.getTime()-30000
            ;
         db.get("coll_01")
            .aggregate("["+
                "{ $match:{$and:["+
                    "{type:'A'},"+
                    "{date:{$gte:{$date:"+_start_date+"}}}"+
                    "]}},"+
                "{ $group:{"+
                     "_id:'$type',"+
                     "min:{$min:'$value'},"+
                     "max:{$max:'$value'},"+
                     "avg:{$avg:'$value'}}}"+
                "]")
            .forEachNode(R->ps.println(hiMongo.json(R)))
            ;
         ps.println("#####################");
         db.get("coll_01")
            .aggregate()
            .match("{$and:["+
                       "{type:'A'},"+
                       "{date:{$gte:{$date:"+_start_date+"}}}"+
                    "]}")
            .group("{ _id:'$type',"+
                     "min:{$min:'$value'},"+
                     "max:{$max:'$value'},"+
                     "avg:{$avg:'$value'}}")
            .forEachClass(Arec.class,R->ps.println(R))
            ;
         //

         ps.println("============= WITH hiMongo.parse()");
         Object _a_node=hiMongo.parse(
              "["+
                "{ $match:{$and:["+
                    "{type:'A'},"+
                    "{date:{$gte:{$date:"+_start_date+"}}}"+
                    "]}},"+
                "{ $group:{"+
                     "_id:'$type',"+
                     "min:{$min:'$value'},"+
                     "max:{$max:'$value'},"+
                     "avg:{$avg:'$value'}}}"+
                "]"
            ).asNode();
        db.get("coll_01")
            .aggregate(_a_node)
            .forEach(R->ps.println(hiMongo.json(R)))
            ;

         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }

/*

4.1899999999999995
1 2345678901234567
use db01
var _last_date=db.coll_01.find({type:'A'},{_id:0,date:1}).
                    sort({_id:-1}).
                    limit(1).toArray()[0].date.getTime()
var _start_date=_last_date-30000
var _isodate=new Date(_start_date)
db.coll_01.
   find({date:{$gte:_isodate}})

use db01

db.coll_01.
   find({$and:[{type:'A'},{date:{$gte:_isodate}}]})

use db01
var _last_date=db.coll_01.find({type:'A'},{_id:0,date:1}).
                  sort({_id:-1}).
                  limit(1).toArray()[0].date.getTime()
var _start_date=_last_date-30000
var _isodate=new Date(_start_date)
var _result=db.coll_01.aggregate([
   { $match:{type:'A'}},
   { $match:{date:{$gte:_isodate}}},
   { $group:{_id:"$type",min:{$min:"$value"},max:{$max:"$value"},avg:{$avg:"$value"}}}
   ])

   //{ $match:{$and:[{type:'A'},{date:{$gte:_isodate}}]},

use db01
var _last_date=db.coll_01.find({type:'A'},{_id:0,date:1}).
                  sort({_id:-1}).
                  limit(1).toArray()[0].date.getTime()
var _start_date=_last_date-30000
var _isodate=new Date(_start_date)
db.coll_01.aggregate([
   { $match:{type:'A'}},
   { $group:{_id:"$type",min:{$min:"$value"},max:{$max:"$value"},avg:{$avg:"$value"}}}
   ])

db.coll_01.aggregate([
  // { $match:{$and:[{type:'A'},{date:{$gte:_isodate}}]},
   { $group:{_id:"$type",min:{$min:"$value"},max:{$max:"$value"},avg:{$avg:"$value"}}}
   ])

print(_last_date_B)
_last_date_B.date.getTime()

var _last=db.coll_01.find("{type:'A'}","{_id:0,date:1}")
                    .sort("{_id:-1}")
                    .limit(1)
db.coll_01.aggregate([
   { $match:{type:'A'} },
   { $group:{_id:"$type",min:{$min:"$value"},max:{$max:"$value"},avg:{$avg:"$value"}}}
   ])
*/

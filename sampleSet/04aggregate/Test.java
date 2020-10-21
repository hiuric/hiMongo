import hi.db.hiMongo;
import otsu.hiNote.*;
import java.util.*;
import java.io.*;
public class Test {
   static class Arec {
      String _id;
      double min;
      double max;
      double avg;
      }
   static class WithDate {
      Date date;
      }
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      long _start_date
      =db.in("coll_01")
         .find("{type:'A'}","{_id:0,date:1}")
         .sort("{_id:-1}").limit(1).getClassList(WithDate.class).get(0)
         .date.getTime()-30000;
      Arec _r
      = db.in("coll_01")
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
          .getClassList(Arec.class).get(0);
      System.out.println("start="+_start_date);
      System.out.println(String.format("min=%.2f max=%.2f avg=%.2f"
                                       ,_r.min,_r.max,_r.avg));
      }
   }

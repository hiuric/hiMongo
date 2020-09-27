import hi.hiMongo;
import otsu.hiNote.*;
import java.util.*;
public class Test {
   static class WithDate { // dateだけを得るクラス
      Date date;
      }
   static class Record {   // レコード内容
      String type;
      double value;
      Date   date;
      }
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      ArrayList<Record> _recs
      =db.get("coll_01")
         .find("{$and:["+
                     "{type:'A'},"+
                     "{date:{$gte:{$date:"+
              (
              db.get("coll_01")
                .find("{type:'A'}","{_id:0,date:1}")
                .sort("{_id:-1}").limit(1)
                .getClassList(WithDate.class)
                .get(0).date.getTime()-30000
              )
                                          +"}}}"+
                      "]}",
               "{_id:0}")
         .getClassList(Record.class);
      System.out.println("records="+hiU.str(_recs,hiU.WITH_INDENT));

      // 最大、最少、平均を求める
      double _min  = Double.MAX_VALUE;
      double _max  = Double.MIN_VALUE;
      double _total= 0;
      for(Record _rec:_recs){
         double _val=_rec.value;
         _min    = Math.min(_min,_val);
         _max    = Math.max(_max,_val);
         _total += _val;
         }
      double _avg= _total/_recs.size();
      System.out.printf("min=%.2f max=%.2f avg=%.2f\n",_min,_max,_avg);
      }
   }

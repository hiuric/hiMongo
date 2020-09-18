import hi.hiMongo;
import otsu.hiNote.*;
import java.util.*;
public class Test {
   public static void main(String[] args_){
      try(hiMongo.DB db=hiMongo.use("db01")){
         // 最後の'A'レコードの時刻(unix-epoch)を得る
         long _last_date
         =db.get("coll_01")
            .find("{type:'A'}","{_id:0,date:1}")
            .sort("{_id:-1}").limit(1)
            .getProbeList()
            .get(0).at("date").get(D->((Date)D).getTime());
         // 最後のレコードの30秒前からの'A'レコード取得
         long _start_date= _last_date-30000; // 30秒前
         System.out.println("last="+_last_date+" start="+_start_date);
         ArrayList<hiJSON.Probe> _recs
                 =db.get("coll_01")
                    .find("{$and:["+
                              "{type:'A'},"+
                              "{date:{$gte:{$date:"+_start_date+"}}}"+
                               "]}",
                           "{_id:0}")
                    .getProbeList();
         // 最大、最少、平均を求める
         double _min  = Double.MAX_VALUE;
         double _max  = Double.MIN_VALUE;
         double _total= 0;
         for(hiJSON.Probe _probe:_recs){
            double _val= _probe.at("value").getDouble();
            _min    = Math.min(_min,_val);
            _max    = Math.max(_max,_val);
            _total += _val;
            }
         double _avg= _total/_recs.size();
         System.out.printf("min=%.2f max=%.2f avg=%.2f\n",_min,_max,_avg);
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }

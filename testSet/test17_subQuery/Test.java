import hi.db.*;
import otsu.hiNote.*;
import java.util.*;
import java.io.*;
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
      //--------------------------------------------------------
      hiMongo.MoreMongo mongo;
      File _modeFile= new File("../test_workerMode.txt");
      if( _modeFile.exists() ) {
         String _host= hiFile.readTextAll(_modeFile).trim();
         if( _host.length()<5 ){
            mongo=new hiMongoCaller(new hiMongoWorker());
            hiU.out.println("// MODE: Caller/Worker");
            }
         else {
            mongo=new hiMongoCaller(new hiMonWorkerSample.COM(_host,8010,3));
            hiU.out.println("// MODE: call SERVER '"+_host+"'");
            }
         }
      else {
         mongo=new hiMongoDirect();
         hiU.out.println("// MODE: DIRECT");
         }
      //--------------------------------------------------------
      hiMongo.DB db=mongo.use("db01");
      long _start_time
      =db.in("coll_01")
         .find("{type:'A'}","{_id:0,date:1}")
         .sort("{_id:-1}").limit(1)
         .getClassList(WithDate.class)
         .get(0).date.getTime()-30000
         ;
      hiU.out.println("-- get start_time befor get target. start_tim="+_start_time);

      ArrayList<Record> _recs1
      =db.in("coll_01")
         .find("{$and:["+
                     "{type:'A'},"+
                     "{date:{$gte:{$date:"+_start_time+"}}}"+
                      "]}",
               "{_id:0}")
         .getClassList(Record.class);
      hiU.out.println("records="+hiU.str(_recs1,hiU.WITH_INDENT));
      // 最大、最少、平均を求める
      double _min  = Double.MAX_VALUE;
      double _max  = Double.MIN_VALUE;
      double _total= 0;
      for(Record _rec:_recs1){
         double _val=_rec.value;
         _min    = Math.min(_min,_val);
         _max    = Math.max(_max,_val);
         _total += _val;
         }
      double _avg= _total/_recs1.size();
      hiU.out.printf("min=%.2f max=%.2f avg=%.2f\n",_min,_max,_avg);



      hiU.out.println("-- insert db.in.find process to creating find filter string");
      db=mongo.use("db01");
      ArrayList<Record> _recs2
      =db.in("coll_01")
         .find("{$and:["+
                     "{type:'A'},"+
                     "{date:{$gte:{$date:"+
              (
              db.in("coll_01")
                .find("{type:'A'}","{_id:0,date:1}")
                .sort("{_id:-1}").limit(1)
                .getClassList(WithDate.class)
                .get(0).date.getTime()-30000
              )
                                          +"}}}"+
                      "]}",
               "{_id:0}")
         .getClassList(Record.class);
      hiU.out.println("records="+hiU.str(_recs2,hiU.WITH_INDENT));

      // 最大、最少、平均を求める
      _min  = Double.MAX_VALUE;
      _max  = Double.MIN_VALUE;
      _total= 0;
      for(Record _rec:_recs2){
         double _val=_rec.value;
         _min    = Math.min(_min,_val);
         _max    = Math.max(_max,_val);
         _total += _val;
         }
      _avg= _total/_recs2.size();
      hiU.out.printf("min=%.2f max=%.2f avg=%.2f\n",_min,_max,_avg);

     hiU.out.println("-- use readValue/eval");
     db=mongo.use("db01");
      ArrayList<Record> _recs3
      =db.in("coll_01")
         .find("{type:'A'}","{_id:0,date:1}")
         .sort("{_id:-1}")
         .readOne("{'#lastdate':'date'}")
         .find("{$and:["+
                      "{type:'A'},"+
                      //"{date:{$gte:{$sub:['#lastdate','30000']}}}"+
                      "{date:{$gte:{$calc:'#lastdate-30000'}}}"+
                      "]}",
               "{_id:0}")
         .getClassList(Record.class);
      hiU.out.println("records="+hiU.str(_recs3,hiU.WITH_INDENT));

      // 最大、最少、平均を求める
      _min  = Double.MAX_VALUE;
      _max  = Double.MIN_VALUE;
      _total= 0;
      for(Record _rec:_recs3){
         double _val=_rec.value;
         _min    = Math.min(_min,_val);
         _max    = Math.max(_max,_val);
         _total += _val;
         }
      _avg= _total/_recs3.size();
      hiU.out.printf("min=%.2f max=%.2f avg=%.2f\n",_min,_max,_avg);
      //--------------------------------------------------------
      }
   }

import hi.db.*;
import otsu.hiNote.*;
import java.util.*;
import java.io.*;
public class Test {
   static class Record {
      String type;
      double value;
      Date   date;
      }
   static class WithDate {
      Date date;
      }
   static class WithDate_X {
      Date date;
      int  NNN;
      }
   static class Record2 {
      String type;
      double value;
      Date   date;
      @hiU.AltName("$recordId")
      long   record_id;
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
      try{
         hiMongo.DB db=mongo.use("db01");
         // 最後の'A'レコードの時刻(unix-epoch)を得る
         long _last_date
         =db.in("coll_01")
            .find("{type:'A'}","{_id:0,date:1}")
            .sort("{_id:-1}")
                    //.limit(1).getProbeList().get(0)
                    //.at("date").get(D->((Date)D).getTime());
            .limit(1).getClassList(WithDate.class).get(0)
            .date.getTime();
         // 最後のレコードの30秒前からの'A'レコード取得
         long _start_date= _last_date-30000; // 30秒前
         hiU.out.println("last="+_last_date+" start="+_start_date);
         ArrayList<Record> _recs
         =db.in("coll_01")
            .find("{$and:["+
                        "{type:'A'},"+
                        "{date:{$gte:{$date:"+_start_date+"}}}"+
                        //"{'date.$date':{$gte:"+_start_date+"}}"+
                        "]}",
                   "{_id:0}")
             .getClassList(Record.class);
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
         hiU.out.printf("min=%.2f max=%.2f avg=%.2f\n",_min,_max,_avg);
         //
         final double[] _vals=new double[3];
         _vals[0]=Double.MAX_VALUE;// 0:min
         _vals[1]=Double.MIN_VALUE;// 1:max
         _vals[2]=0;               // 2:total
         db.in("coll_01")
           .find("{$and:["+
                       "{type:'A'},"+
                       "{date:{$gte:{$date:"+_start_date+"}}}"+
                        "]}",
                 "{_id:0}")
           .forEachClass(Record.class,
                    R->{
                       double _val=R.value;
                       _vals[0]=Math.min(_vals[0],_val);
                       _vals[1]=Math.max(_vals[1],_val);
                       _vals[2]+=_val;
                       }
                    );
         _avg= _vals[2]/_recs.size();
         hiU.out.printf("min=%.2f max=%.2f avg=%.2f\n",_vals[0],_vals[1],_avg);
         //========== 以下エラー系
         hiU.out.println("======== 異常系 =========");
         try{
           long _last_date_X
                 =db.in("coll_01")
                    .find("{type:'A'}","{_id:0,date:1}")
                    .sort("{_id:-1}")
                    .limit(1).getClassList(WithDate_X.class).get(0)
                    .date.getTime();
            hiU.out.println("_last_date_X "+_last_date_X);
            }
         catch(Exception _ex){
            hiU.out.println("EXCEPTION TEST "+_ex.getMessage());
            }
         hiU.out.println("======== 異常回避 =========");
         try{
           long _last_date_Y
           =db.in("coll_01")
              .find("{type:'A'}","{_id:0,date:1}")
              .sort("{_id:-1}")
              //.forThis(T->T.engine().without_option(hiU.CHECK_UNKNOWN_FIELD|hiU.CHECK_UNSET_FIELD))
              .without_option(hiU.CHECK_UNKNOWN_FIELD|hiU.CHECK_UNSET_FIELD)
              .limit(1).getClassList(WithDate_X.class).get(0)
              .date.getTime();
            hiU.out.println("_last_date_Y "+_last_date_Y);
            }
         catch(Exception _ex){
            hiU.out.println("EXCEPTION TEST "+_ex.getMessage());
            }
         hiU.out.println("======== 異常系 =========");
         try{
           long _last_date_X
           =db.in("coll_01")
              .find("{type:'A'}","{_id:1,date:1}")
              .sort("{_id:-1}")
              .limit(1).getClassList(WithDate_X.class).get(0)
              .date.getTime();
            hiU.out.println("_last_date_X "+_last_date_X);
            }
         catch(Exception _ex){
            hiU.out.println("EXCEPTION TEST "+_ex.getMessage());
            }
         hiU.out.println("======== 異常回避 =========");
         try{
           long _last_date_Y
           =db.in("coll_01")
              .find("{type:'A'}","{_id:1,date:1}")
              .sort("{_id:-1}")
              //.forThis(T->T.engine().without_option(hiU.CHECK_UNKNOWN_FIELD|hiU.CHECK_UNSET_FIELD))
              .without_option(hiU.CHECK_UNKNOWN_FIELD|hiU.CHECK_UNSET_FIELD)
              .limit(1).getClassList(WithDate_X.class).get(0)
              .date.getTime();
            hiU.out.println("_last_date_Y "+_last_date_Y);
            }
         catch(Exception _ex){
            hiU.out.println("EXCEPTION TEST "+_ex.getMessage());
            }
/*
TODO:別試験に移す
         hiU.out.println("======== 代替名 =========");
         db.in("coll_01")
            .find("{}","{_id:0}")
            .forThis(Fi->Fi.getIterable().showRecordId(true))
            .forEachDocument(Rd->hiU.out.println(Rd))
            .forEachClass(Record2.class,Rc->hiU.out.println(hiU.str(Rc)));
*/
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }
/*
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

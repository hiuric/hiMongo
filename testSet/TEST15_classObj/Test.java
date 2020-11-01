import hi.db.*;
import otsu.hiNote.*;
import java.util.*;
import java.io.*;
public class Test {
   static class NoElement{}
   static class Has_id{
      int _id;
      }
   static class Record {
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
      NoElement _noel=new NoElement();
      Has_id    _id  =new Has_id();

      try{
         hiMongo.DB         db = mongo.use("db01");
         hiMongo.Collection col= db.in("coll_01");
         ArrayList<Record> _recs
         =col.find(_noel,_id)
             .getClassList(Record.class);
         int _count=0;
         for(Record _rec:_recs){
            _rec.value=++_count;
            _rec.date =new Date(_rec.date.getTime()+2000);
            col.insertOne(_rec);
            }
         for(Record _rec:_recs){
            _rec.value+=100;
            _rec.date =new Date(_rec.date.getTime()+2000);
            }
         col.insertMany(_recs);
         //
         col.find()
            .forEachMson(Rm->hiU.out.println(Rm));
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }


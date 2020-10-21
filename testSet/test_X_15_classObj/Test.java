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
      hiMongo.MoreMongo mongo;
      if( new File("../test_workerMode.txt").exists() ) {
         mongo=new hiMongoCaller(new hiMongoWorker());
         hiU.out.println("// caller-worker mode");
         }
      else {
         mongo=new hiMongoDirect();
         hiU.out.println("// direct mode");
         }
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


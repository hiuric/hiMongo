import hi.db.*;
import otsu.hiNote.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bson.types.ObjectId;
public class Test {
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
      ArrayList<String> _dbs= mongo.show_dbs(true);
      hiU.out.println("--- show dbs ---");
      for(String _db:_dbs){
         if( !_db.startsWith("db0") ) hiU.out.print("//");
         hiU.out.println(_db);
         }

      hiU.out.println("--- use(db01) show collections ---");
      ArrayList<String> _cols01=mongo.use("db01").show_collections(true);
      for(String _col:_cols01){
         hiU.out.println(_col);
         }
      hiU.out.println("--- use(db02) show collections ---");
      ArrayList<String> _cols02=mongo.use("db02").show_collections(true);
      for(String _col:_cols02){
         hiU.out.println(_col);
         }

      hiU.out.println("--- use(db01) coll_02 exists ---");
      hiU.out.println(mongo.use("db01").exists("coll_02"));
      hiU.out.println("--- use(db01) coll_03 exists ---");
      hiU.out.println(mongo.use("db01").exists("coll_03"));

      hiU.out.println("--- use(db01) coll_01 count({}) ---");
      hiU.out.println(mongo.use("db01").in("coll_01").count("{}"));
      hiU.out.println("--- use(db01) coll_01 count({value:{$gt:5}}) ---");
      hiU.out.println(mongo.use("db01").in("coll_01").count("{value:{$gt:5}}"));

      hiU.out.println("--- use(db01).coll_01.drop().count({}) ---");
      hiU.out.println(mongo.use("db01").in("coll_01").drop().count("{}"));

      hiU.out.println("--- use(db01).show collections ---");
      hiU.out.println(mongo.use("db01").show_collections(true));

      hiU.out.println("--- use(db01).drop() show collections ---");
      hiU.out.println(mongo.use("db01").drop().show_collections(true));

      ArrayList<String> _dbs2= mongo.show_dbs(true);
      hiU.out.println("--- show dbs ---");
      for(String _db2:_dbs2){
         if( !_db2.startsWith("db0") ) hiU.out.print("//");
         hiU.out.println(_db2);
         }

      hiU.out.println("--- use(db03).drop() show collections ---");
      hiU.out.println(mongo.use("db03").drop().show_collections(true));

      hiU.out.println("--- use(db03).coll_03.insertOne.insertOne");
      hiU.out.println(mongo.use("db03").in("coll_03").insertOne("{a:'B'}").insertOne("{a:'X'}")
                           .find("{}","{_id:0}").getMsonList());
      hiU.out.println("--- show dbs ---");
      ArrayList<String> _dbs3= mongo.show_dbs(true);
      for(String _db3:_dbs3){
         if( !_db3.startsWith("db0") ) hiU.out.print("//");
         hiU.out.println(_db3);
         }
      }
   }

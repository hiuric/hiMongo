import hi.db.*;
import otsu.hiNote.*;
import java.util.*;
import java.io.*;
import org.bson.types.ObjectId;
public class Test {
   static PrintStream ps=hiU.out;
   static class Record {
      String           type;
      double           value;
      Map<String,Long> date;
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
         hiMongo.DB db  =mongo.use("db02");
         String _records=
              "[{type:'A',value:12.3,date:ISODate('2020-08-17T07:07:00.000Z')}"+
              ",{type:'A',value:4.56,date:ISODate('2020-08-17T07:07:10.000Z')}"+
              ",{type:'B',value:2001,date:ISODate('2020-08-17T07:07:20.000Z')}]";
         String _record2=
              "[{type:'A',value:7.89,date:ISODate('2020-08-17T07:07:30.000Z')}"+
              ",{type:'A',value:0.12,date:ISODate('2020-08-17T07:07:40.000Z')}]";

         hiMongo.Collection  _coll=db.in("coll_03").drop();
         _coll.insertMany(_records,_record2);
         _coll.insertOne("{type:'C',value:3.45,date:ISODate('2020-08-17T07:07:20.000Z')}"
                        ,"{type:'C',value:5.67,date:ISODate('2020-08-17T07:07:30.000Z')}");
         //_coll.insert("{type:'A',value:8.90,date:ISODate('2020-08-17T07:07:20.000Z')}");
         //_coll.insert("{type:'B',value:1.23,date:ISODate('2020-08-17T07:07:30.000Z')}"
         //            ,"{type:'D',value:4.56,date:ISODate('2020-08-17T07:07:40.000Z')}");
         //
         _coll.insertOne("{type:'A',value:8.90,date:ISODate('2020-08-17T07:07:20.000Z')}");
         _coll.insertOne("{type:'B',value:1.23,date:ISODate('2020-08-17T07:07:30.000Z')}"
                     ,"{type:'D',value:4.56,date:ISODate('2020-08-17T07:07:40.000Z')}");
         hiU.out.println("NORMAL-JSON");
         _coll.find("{}","{_id:0}").getJsonList()
              .forEach(Rj->hiU.out.println(Rj));
         hiU.out.println("MONGO-JSON");
         _coll.find("{}","{_id:0}").getMsonList()
              .forEach(Rm->hiU.out.println(Rm));
         hiU.out.println("");
         hiU.out.println("COUNT="+_coll.count());
         hiU.out.println("COUNT B OR C ="+_coll.count("{$or:[{type:'B'},{type:'C'}]}"));


         hiU.out.println("####################");

         String _records_json=hiFile.readTextAll("data.json");
         Object _recodes_node=hiMongo.parseText(_records_json)
                                     .asNodeList();
hiU.m("@@@@@@@@@@@@@@@@@@@@@@@@@@@@ .insertMany(_recodes_node);");
         db.in("composer").drop()
           .insertMany(_recodes_node);
         hiU.out.println("--- with hiMongo.parseText().asNode()");
         db.in("composer")
           .find("{}","{_id:0}")
           //.str_option(hiU.WITH_TYPE|hiU.WITH_INDENT)
           .forEachMson(Rm->hiU.out.println(Rm));
         hiU.out.println("--- 19世紀生まれ");
         db.in("composer")
           .find("{$and:[{'lifeTime.0':{$gte:1801}},{'lifeTime.0':{$lte:1900}}]},{_id:0}","{_id:0}")
           .sort("{'lifeTime.0':1}")
           .getMsonList()
           .forEach(Rm->hiU.out.println(Rm));

hiU.m("@@@@@@@@@@@@@@@@@@@@@@@@@@@@ .insertMany(_records_json);");
hiU.m("_records_json="+_records_json);
         db.in("composer2").drop()
           //.with_hson()
           .insertMany(_records_json);
         hiU.out.println("\n--- with_hson");
         db.in("composer2")
           .find("{}","{_id:0}")
           //.str_option(hiU.WITH_TYPE|hiU.WITH_INDENT)
           .forEachMson(Rm->hiU.out.println(Rm));
         hiU.out.println("--- 19世紀生まれ");
         db.in("composer2")
           .find("{$and:[{'lifeTime.0':{$gte:1801}},{'lifeTime.0':{$lte:1900}}]},{_id:0}","{_id:0}")
           .sort("{'lifeTime.0':1}")
           .getMsonList()
           .forEach(Rm->hiU.out.println(Rm));


         db.in("composer3").drop()
           //.with_hson()
           .insertMany(new File("data.json"));
         hiU.out.println("\n--- with_hson/File");
         db.in("composer3")
           .find("{}","{_id:0}")
           //.str_option(hiU.WITH_TYPE|hiU.WITH_INDENT)
           .forEachMson(Rm->hiU.out.println(Rm));
         hiU.out.println("--- 19世紀生まれ");
         db.in("composer3")
           .find("{$and:[{'lifeTime.0':{$gte:1801}},{'lifeTime.0':{$lte:1900}}]},{_id:0}","{_id:0}")
           .sort("{'lifeTime.0':1}")
           .getMsonList()
           .forEach(Rm->hiU.out.println(Rm));
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }

import hi.hiMongo;
import otsu.hiNote.*;
import java.util.*;
import java.io.*;
import org.bson.types.ObjectId;
public class Test {
   static PrintStream ps=System.out;
   static class Record {
      String           type;
      double           value;
      Map<String,Long> date;
      }
   public static void main(String[] args_){
      if( "yes".equals(System.getenv("WITH_HSON")) ) hiMongo.with_hson(true);
      try(hiMongo.DB          db   =hiMongo.use("db02")){
         String _records=
              "[{type:'A',value:12.3,date:ISODate('2020-08-17T07:07:00.000Z')}"+
              ",{type:'A',value:4.56,date:ISODate('2020-08-17T07:07:10.000Z')}"+
              ",{type:'B',value:2001,date:ISODate('2020-08-17T07:07:20.000Z')}]";
         String _record2=
              "[{type:'A',value:7.89,date:ISODate('2020-08-17T07:07:30.000Z')}"+
              ",{type:'A',value:0.12,date:ISODate('2020-08-17T07:07:40.000Z')}]";

         hiMongo.Collection  _coll=db.get("coll_03").drop();
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
         System.out.println("NORMAL-JSON");
         _coll.find("{}","{_id:0}").getJsonList()
              .forEach(S->System.out.println(S));
         System.out.println("MONGO-JSON");
         _coll.find("{}","{_id:0}").getMsonList()
              .forEach(S->System.out.println(S));
         System.out.println("");
         System.out.println("COUNT="+_coll.count());
         System.out.println("COUNT B OR C ="+_coll.count("{$or:[{type:'B'},{type:'C'}]}"));


         System.out.println("####################");

         String _records_json=hiFile.readTextAll("data.json");
         Object _recodes_node=hiMongo.parseText(_records_json)
                                     .asNodeList();
         db.get("composer").drop()
           .insertMany(_recodes_node);
         System.out.println("--- with hiMongo.parseText().asNode()");
         db.get("composer")
           .find("{}","{_id:0}")
           //.str_option(hiU.WITH_TYPE|hiU.WITH_INDENT)
           .forEachMson(M->System.out.println(M));
         System.out.println("--- 19世紀生まれ");
         db.get("composer")
           .find("{$and:[{'lifeTime.0':{$gte:1801}},{'lifeTime.0':{$lte:1900}}]},{_id:0}","{_id:0}")
           .sort("{'lifeTime.0':1}")
           .getMsonList()
           .forEach(M->System.out.println(M));


         db.get("composer2").drop()
           .with_hson()
           .insertMany(_records_json);
         System.out.println("\n--- with_hson");
         db.get("composer2")
           .find("{}","{_id:0}")
           //.str_option(hiU.WITH_TYPE|hiU.WITH_INDENT)
           .forEachMson(M->System.out.println(M));
         System.out.println("--- 19世紀生まれ");
         db.get("composer2")
           .find("{$and:[{'lifeTime.0':{$gte:1801}},{'lifeTime.0':{$lte:1900}}]},{_id:0}","{_id:0}")
           .sort("{'lifeTime.0':1}")
           .getMsonList()
           .forEach(M->System.out.println(M));


         db.get("composer3").drop()
           .with_hson()
           .insertMany(new File("data.json"));
         System.out.println("\n--- with_hson/File");
         db.get("composer3")
           .find("{}","{_id:0}")
           //.str_option(hiU.WITH_TYPE|hiU.WITH_INDENT)
           .forEachMson(M->System.out.println(M));
         System.out.println("--- 19世紀生まれ");
         db.get("composer3")
           .find("{$and:[{'lifeTime.0':{$gte:1801}},{'lifeTime.0':{$lte:1900}}]},{_id:0}","{_id:0}")
           .sort("{'lifeTime.0':1}")
           .getMsonList()
           .forEach(M->System.out.println(M));
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }

/*
db.composer.find({'lifeTime.0':{$gte:1800}},{_id:0})
db.composer.find({'lifeTime.0':{$lt:1900}},{_id:0})
db.composer.find({'lifeTime.0':{$and:[{$ge:1800},{$lt:1900}]}})
db.composer.find({$and:[{'lifeTime.0':{$gte:1800}},{'lifeTime.0':{$lt:1900}}]},{_id:0}).sort({'lifeTime.0':1})
*/

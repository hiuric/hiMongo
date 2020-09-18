import hi.hiMongo;
import otsu.hiNote.*;
import java.util.*;
public class Test {
   public static void main(String[] args_){
      try(hiMongo.DB db =hiMongo.use("db01")){
         hiMongo.Collection  _coll=db.get("coll_01").drop();
         System.out.println("--- insertOne/insertMany");
         _coll.insertOne(
              " {type:'A',value:12.3,date:"+hiMongo.date()+"}");
         _coll.insertMany("["+
              " {type:'A',value:4.56,date:"+hiMongo.date()+"}"+
              ",{type:'B',value:2001,date:"+hiMongo.date()+"}"+
              ",{type:'A',value:7.89,date:"+hiMongo.date()+"}"+
              ",{type:'A',value:0.12,date:"+hiMongo.date()+"}]");
         _coll.find("{}","{_id:0}").forEach(R->System.out.println(R));

         System.out.println("--- updateOne");
         _coll.updateOne("{$and:[{type:'B'},{value:{$gt:5}}]}",
                         "{$set:{value:4.32}}");
         _coll.find("{}","{_id:0,date:0}").forEach(R->System.out.println(R));

         System.out.println("--- updateMany");
         _coll.updateMany("{$and:[{type:'A'},{value:{$lt:5}}]}",
                         "{$set:{value:3.21}}");
         _coll.find("{}","{_id:0,date:0}").forEach(R->System.out.println(R));

         System.out.println("--- replaceOne");
         _coll.replaceOne("{$and:[{type:'A'},{value:{$lt:5}}]}",
                         "{type:'B',value:6543,date:"+hiMongo.date()+"}");
         _coll.find("{}","{_id:0,date:0}").forEach(R->System.out.println(R));

         System.out.println("--- deleteOne");
         _coll.deleteOne("{type:'B'}");
         _coll.find("{}","{_id:0,date:0}").forEach(R->System.out.println(R));

         System.out.println("--- deleteMany");
         _coll.deleteMany("{$and:[{type:'A'},{value:{$lt:8}}]}");
         _coll.find("{}","{_id:0,date:0}").forEach(R->System.out.println(R));
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }

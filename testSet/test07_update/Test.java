import hi.hiMongo;
import otsu.hiNote.*;
import java.io.*;
public class Test {
   static class A_Rec {
      String       店舗名;
      int          数量;
      from商品_Rec from商品;
      //
      static class from商品_Rec{
         String 商品名;
         int    販売単価;
         }
      }
   static PrintStream ps=System.out;
   public static void main(String[] args_){
      if( "yes".equals(System.getenv("WITH_HSON")) ) hiMongo.with_hson(true);
      try(hiMongo.DB db=hiMongo.use("db01")){
         db.get("coll_01")
           .find("{}","{_id:0}")
           .forThis(C->ps.println("---- befor"))
           .forEach(R->ps.println(R))
           .back()

           .updateOne("{$and:[{type:'A'},{value:4.56}]}",
                      "{$set:{value:0.55}}")
           .find("{}","{_id:0}")
           .forThis(C->ps.println("---- after 4.56->0.55"))
           .forEach(R->ps.println(R))
           .back()

           .updateMany("{$and:[{type:'A'},{value:{$lt:1.00}}]}}",
                      "{$set:{value:1.00}}")
           .find("{}","{_id:0}")
           .forThis(C->ps.println("---- after 0.xx -> 1.00 "))
           .forEach(R->ps.println(R))
           .back()

           .replaceOne("{$and:[{type:'A'},{value:7.89}]}",
                       "{type:'B',value:3000,date:ISODate('2020-08-17T07:07:50.000Z')}")
           .find("{}","{_id:0}")
           .forThis(C->ps.println("---- after replaceOne "))
           .forEach(R->ps.println(R))
           .back()

           .deleteOne("{type:'A'}")
           .find("{}","{_id:0}")
           .forThis(C->ps.println("---- after deleteOne type:'A' "))
           .forEach(R->ps.println(R))
           .back()

           .deleteMany("{value:1}")
           .find("{}","{_id:0}")
           .forThis(C->ps.println("---- after deleteMany value:1 "))
           .forEach(R->ps.println(R))
           .back()
           ;
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }

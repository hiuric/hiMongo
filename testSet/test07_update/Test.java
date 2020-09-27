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
      try{
         hiMongo.DB db=hiMongo.use("db01");
         db.get("coll_01")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- befor"))
           .forEach(Rd->ps.println(Rd))
           .back()

           .updateOne("{$and:[{type:'A'},{value:4.56}]}",
                      "{$set:{value:0.55}}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after 4.56->0.55"))
           .forEach(Rd->ps.println(Rd))
           .back()

           .updateMany("{$and:[{type:'A'},{value:{$lt:1.00}}]}}",
                      "{$set:{value:1.00}}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after 0.xx -> 1.00 "))
           .forEach(Rd->ps.println(Rd))
           .back()

           .replaceOne("{$and:[{type:'A'},{value:7.89}]}",
                       "{type:'B',value:3000,date:ISODate('2020-08-17T07:07:50.000Z')}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after replaceOne "))
           .forEach(Rd->ps.println(Rd))
           .back()

           .deleteOne("{type:'A'}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after deleteOne type:'A' "))
           .forEach(Rd->ps.println(Rd))
           .back()

           .deleteMany("{value:1}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after deleteMany value:1 "))
           .forEach(Rd->ps.println(Rd))
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

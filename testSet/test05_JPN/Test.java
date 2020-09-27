import hi.hiMongo;
import otsu.hiNote.*;
import java.util.*;
import java.io.*;
public class Test {
   static PrintStream ps=System.out;
   public static void main(String[] args_){
      if( "yes".equals(System.getenv("WITH_HSON")) ) hiMongo.with_hson(true);
      try{
         hiMongo.DB db=hiMongo.use("sampleDB");
         db.get("商品")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("--- Node ---"))
           .forEach(Rd->ps.println(Rd))
           .forThis(Fi->ps.println("--- Node.toJson (文字化けあり) ---"))
           .forEach(Rd->ps.println(Rd.toJson()))
           .forThis(Fi->ps.println("--- hiMongo.mson ---"))
           .forEach(Rd->ps.println(hiMongo.mson(Rd)))
           .forThis(Fi->ps.println("--- hiMongo.json ---"))
           .forEach(Rd->ps.println(hiMongo.json(Rd)))
           ;
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }

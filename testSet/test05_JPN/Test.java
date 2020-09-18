import hi.hiMongo;
import otsu.hiNote.*;
import java.util.*;
import java.io.*;
public class Test {
   static PrintStream ps=System.out;
   public static void main(String[] args_){
      if( "yes".equals(System.getenv("WITH_HSON")) ) hiMongo.with_hson(true);
      try(hiMongo.DB db=hiMongo.use("sampleDB")){
         db.get("商品").find("{}","{_id:0}")
            .forThis(T->ps.println("--- Node ---"))
            .forEach(R->ps.println(R))
            .forThis(T->ps.println("--- Node.toJson (文字化けあり) ---"))
            .forEach(R->ps.println(R.toJson()))
            .forThis(T->ps.println("--- hiMongo.mson ---"))
            .forEach(R->ps.println(hiMongo.mson(R)))
            .forThis(T->ps.println("--- hiMongo.json ---"))
            .forEach(R->ps.println(hiMongo.json(R)))
            ;
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }

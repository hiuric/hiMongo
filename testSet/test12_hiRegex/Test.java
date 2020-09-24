import hi.hiMongo;
import otsu.hiNote.*;
import java.util.*;
import java.io.*;
import java.math.*;
public class Test {
   static PrintStream ps=System.out;
   static class Record {
      String   type;
      BigDecimal value;
      Date     date;
      }
   public static void main(String[] args_){
      try{
         hiMongo.DB db=hiMongo.use("db02");
         db.get("coll_04").drop()
           .with_hson()  // コメントを許す
           .insertMany(
               hiRegex.with("<NOW>",hiMongo.date())
                      .read("data.json")
                      .result());
         db.get("coll_04")
           .find("{}","{_id:0}")
           .forEachClass(Record.class,R->ps.println(hiMongo.str(R)));
         db.get("coll_04")
           .find("{}","{_id:0}")
           //.forThis(T->ps.println("---- str ---"))
           //.forEach(Record.class,R->ps.println(hiMongo.str(R,hiU.WITH_TYPE|hiU.WITH_INDENT)))
           .forThis(T->ps.println("\n---- mson ---"))
           .forEachClass(Record.class,R->ps.println(hiMongo.mson(R,hiU.WITH_TYPE|hiU.WITH_INDENT)))
           .forThis(T->ps.println("\n---- json ---"))
           .forEachClass(Record.class,R->ps.println(hiMongo.json(R,hiU.WITH_TYPE|hiU.WITH_INDENT)));
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }



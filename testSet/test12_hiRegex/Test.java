import hi.db.*;
import otsu.hiNote.*;
import java.util.*;
import java.io.*;
import java.math.*;
public class Test {
   static PrintStream ps=hiU.out;
   static class Record {
      String   type;
      BigDecimal value;
      Date     date;
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
      try{
         hiMongo.DB db=mongo.use("db02");
         db.in("coll_04").drop()
           .with_hson()  // コメントを許す
           .insertMany(
               hiRegex.with("<NOW>",hiMongo.date())
                      .read("data.json")
                      .result());
         db.in("coll_04")
           .find("{}","{_id:0}")
           .forEachClass(Record.class,R->ps.println(hiMongo.str(R)));
         db.in("coll_04")
           .find("{}","{_id:0}")
           //.forThis(T->ps.println("---- str ---"))
           //.forEach(Record.class,R->ps.println(hiMongo.str(R,hiU.WITH_TYPE|hiU.WITH_INDENT)))
           .forThis(Fi->ps.println("\n---- mson ---"))
           .forEachClass(Record.class,Rc->ps.println(hiMongo.mson(Rc,hiU.WITH_TYPE|hiU.WITH_INDENT)))
           .forThis(Fi->ps.println("\n---- json ---"))
           .forEachClass(Record.class,Rc->ps.println(hiMongo.json(Rc,hiU.WITH_TYPE|hiU.WITH_INDENT)));
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }



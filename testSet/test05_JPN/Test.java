import hi.db.*;
import otsu.hiNote.*;
import java.util.*;
import java.io.*;
public class Test {
   static PrintStream ps=hiU.out;
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
         hiMongo.DB db=mongo.use("sampleDB");
         db.in("商品")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("--- Node ---"))
           .forEach(Rd->ps.println("//"+Rd))
           .forThis(Fi->ps.println("--- Node.toJson (文字化けあり) ---"))
           .forEach(Rd->ps.println("//"+Rd.toJson()))
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

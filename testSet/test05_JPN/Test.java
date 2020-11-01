import hi.db.*;
import otsu.hiNote.*;
import java.util.*;
import java.io.*;
public class Test {
   static PrintStream ps=hiU.out;
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
         hiMongo.DB db=mongo.use("sampleDB");
         db.in("商品")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("--- Node ---"))
           .forEachDocument(Rd->ps.println("//"+Rd))
           .forThis(Fi->ps.println("--- Node.toJson (文字化けあり) ---"))
           .forEachDocument(Rd->ps.println("//"+Rd.toJson()))
           .forThis(Fi->ps.println("--- hiMongo.mson ---"))
           .forEachDocument(Rd->ps.println(hiMongo.mson(Rd)))
           .forThis(Fi->ps.println("--- hiMongo.json ---"))
           .forEachDocument(Rd->ps.println(hiMongo.json(Rd)))
           ;
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }

import hi.db.*;
import otsu.hiNote.*;
import java.io.*;
import org.bson.Document;
import com.mongodb.client.model.IndexOptions;
public class Test {
   final static boolean USE_MSON=true;
   final static boolean DOT_ZERO=true;
   final static boolean TO_JSON= true;
   static String dot_zero(String value_){
      if( !DOT_ZERO ) return value_;
      return hiMongo.suprress_dot_0(value_);
      }
   static String str(Document doc_){
      if(!USE_MSON ) return dot_zero(doc_.toString());
      return hiMongo.mson(doc_);
      }
   static String json(Document doc_){
      if(!USE_MSON ) return dot_zero(doc_.toJson());
      return hiMongo.mson(doc_);
      }
   static String toJson(Document doc_){
      if(!TO_JSON ) return dot_zero(doc_.toString());
      return dot_zero(doc_.toJson());
      }
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
         hiU.out.println("--- befor creteIndex (do.toJson())");
         db.in("商品").getIndexList().forEach(Do->hiU.out.println(str(Do)));
         db.in("商品").createIndex("{商品id:1}","{unique:true,expireAfterDays:730}");
         hiU.out.println("--- after creteIndex (do.toJson())");
         db.in("商品").getIndexList().forEach(Do->hiU.out.println(str(Do)));

         db.in("店舗商品")
           .aggregate("["+
               "{$match:{$or:["+
                    "{'店舗名':'東京'},"+
                    "{'店舗名':'福岡'}"+
                    "]}},"+
               "{$lookup:{"+
                   "from:'商品',"+
                   "localField:'商品id',"+
                   "foreignField:'商品id',"+
                   "as:'from商品'"+
                   "}},"+
               "{$project:{"+
                   "'_id':0,"+
                   "'店舗名':1,"+
                   "'from商品.商品名':1,"+
                   "'from商品.販売単価':1,"+
                   "'数量':1}},"+
                "{$unwind:'$from商品'}"+
               "]")
           .forThis(Ag->ps.println("----- foreach mson(MNode) -----"))
           .forEach(Rd->ps.println(str(Rd)))
           .forThis(Ag->ps.println("----- toClass -----"))
           .forEach(A_Rec.class,
                    Rc->ps.println(hiU.str(Rc)))
           .forThis(Ag->ps.println("----- getMsonList -----"))
           .getMsonList().forEach(Rm->ps.println(Rm))
           ;
         db.in("商品").createIndex("{商品分類:1}","{unique:false,expireAfterDays:730}");
         hiU.out.println("--- after creteIndex (do.toJson())");
         db.in("商品").getIndexList().forEach(Do->hiU.out.println(str(Do)));
         db.in("商品").dropIndex("商品分類_1");
         hiU.out.println("--- after deleteIndex  商品分類_1(do.toJson())");
         db.in("商品").getIndexList().forEach(Do->hiU.out.println(str(Do)));
         db.in("商品").dropIndexes();
         hiU.out.println("--- after deleteIndexes (do.toJson())");
         db.in("商品").getIndexList().forEach(Do->hiU.out.println(str(Do)));
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }

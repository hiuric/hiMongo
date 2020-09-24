import hi.hiMongo;
import otsu.hiNote.*;
import java.io.*;
import org.bson.Document;
import com.mongodb.client.model.IndexOptions;
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
         hiMongo.DB db=hiMongo.use("sampleDB");
         System.out.println("--- befor creteIndex");
         db.get("商品").getIndexList().forEach(D->System.out.println(D));
         db.get("商品").createIndex("{商品id:1}","{unique:true,expireAfterDays:730}");
         System.out.println("--- after creteIndex");
         db.get("商品").getIndexList().forEach(D->System.out.println(D));

         db.get("店舗商品").aggregate("["+
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
            .forThis(A->ps.println("----- foreach MNode -----"))
            .forEachNode(R->ps.println(R))
            .forThis(A->ps.println("----- toClass -----"))
            .forEachClass(A_Rec.class,
                     R->ps.println(hiU.str(R)))
            .forThis(A->ps.println("----- getMsonList -----"))
            .getMsonList().forEach(R->ps.println(R))
            ;
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }

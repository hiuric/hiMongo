import hi.hiMongo;
import otsu.hiNote.*;
import java.util.*;
public class Test {
   static class A_Rec {
      String       店舗名;
      int          数量;
      from商品_Rec from商品;
      static class from商品_Rec{
         String 商品名;
         int    販売単価;
         }
      }
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("sampleDB");
      db.get("商品").getIndexList().forEach(D->System.out.println(D));
      db.get("商品").createIndex("{商品id:1}","{unique:true,expireAfterDays:730}");
      db.get("商品").getIndexList().forEach(D->System.out.println(D));
      ArrayList<A_Rec> _recs=
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
         .getClassList(A_Rec.class);
      for(A_Rec _rec:_recs){
         System.out.println(_rec.店舗名+" "+_rec.from商品.商品名+" 数量:"+_rec.数量);
         }
      }
   }

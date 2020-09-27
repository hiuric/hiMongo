import hi.hiMongo;
public class Test {
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      hiMongo.Collection col
      =db.createCappedCollection(
           "coll_cap",      // コレクション名
           "{size:10000,"+  // 最大容量（バイト)
           " records:5," +  // 最大レコード数
           " force:true}"   // 強制クリア
           );
      for(int _n=0;_n<20;++_n){
         col.insertOne("{type:'A',value:"+(_n+1)+"}");
         }
      col.find("{}","{_id:0}")
         .forEachMson(Rm->System.out.println(Rm));
      }
   }

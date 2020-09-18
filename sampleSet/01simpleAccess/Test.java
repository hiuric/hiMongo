import hi.hiMongo;
import otsu.hiNote.*;
public class Test {
   public static void main(String[] args_){
      try(hiMongo.DB db=hiMongo.use("db01")){  // database   'db01'選択
         db.get("coll_01")                     // collection 選択
            .find("{type:'A'}","{_id:0}")      // typeが'A'のレコード,
            .sort("{_id:-1}")                  // _idで逆向きにソート
            .limit(3)                          // 個数制限
            .getMsonList(hiU.REVERSE)          // 反転したリスト取得
            .forEach(S->System.out.println(S)) // レコード表
            ;
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }

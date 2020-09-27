import hi.hiMongo;
import otsu.hiNote.*;
public class Test {
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");  // database   'db01'選択
      db.get("coll_01")                     // collection 選択
         .find("{type:'A'}","{_id:0}")      // typeが'A'のレコード,
         .sort("{_id:-1}")                  // _idで逆向きにソート
         .limit(3)                          // 個数制限
         .getMsonList(hiU.REVERSE)          // 反転したリスト取得
         .forEach(Rm->System.out.println(Rm)) // レコード表示
         ;
      }
   }

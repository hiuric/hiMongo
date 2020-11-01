import hi.db.hiMongo;
import otsu.hiNote.*;
import com.mongodb.client.model.FindOneAndUpdateOptions;
public class Test {
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      db.in("coll_01")
        .forThis(Fc->System.out.println("---- before $set"))
        .find("{}","{_id:0}")
        .forEachMson(Rm->System.out.println(Rm))
        .back() // 表示に使用したFinderレベルからCollectionレベルに戻す
        .forThis(Rc->
           Rc.getMongoCollection()
             .findOneAndUpdate(
                   hiMongo.objToBson("{$and:[{type:'C'},{name:'Z'}]}"),
                   hiMongo.objToBson("{$set:{value:3}}"),
                   (new FindOneAndUpdateOptions()).upsert(true))
           )
        .forThis(Fc->System.out.println("---- after $set"))
        .find("{}","{_id:0}")
        .forEachMson(Rm->System.out.println(Rm));
      }
   }

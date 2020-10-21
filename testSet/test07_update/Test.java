import hi.db.*;
import otsu.hiNote.*;
import java.io.*;
import com.mongodb.client.*;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import org.bson.Document;
public class Test {
   final static boolean USE_MSON=false;
   final static boolean DOT_ZERO=true;
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
         final String _field_name="value";
         hiMongo.DB db=mongo.use("db01");
         db.in("coll_01")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- befor"))
           .forEach(Rd->ps.println(str(Rd)))
           .back()

           .updateOne("{$and:[{type:'A'},{value:4.56}]}",
                      "{$set:{value:0.55}}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after 4.56->0.55"))
           .forEach(Rd->ps.println(str(Rd)))
           .back()

           .updateMany("{$and:[{type:'A'},{value:{$lt:1.00}}]}",
                      "{$set:{value:1.00}}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after 0.xx -> 1.00 "))
           .forEach(Rd->ps.println(str(Rd)))
           .back()

           .replaceOne("{$and:[{type:'A'},{value:7.89}]}",
                       "{type:'B',value:3000,date:ISODate('2020-08-17T07:07:50.000Z')}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after replaceOne "))
           .forEach(Rd->ps.println(str(Rd)))
           .back()

           .deleteOne("{type:'A'}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after deleteOne type:'A' "))
           .forEach(Rd->ps.println(str(Rd)))
           .back()

           .deleteMany("{value:1}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after deleteMany value:1 "))
           .forEach(Rd->ps.println(str(Rd)))
           .back()

           // %set試験
           .drop()
           .insertMany("["+
                       "{type:'C',name:'X',value:5},"+
                       "{type:'C',name:'Y',value:10},"+
                       "{type:'C',name:'X',value:13}"+
                       "]")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- before $set"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
           .updateMany("{$and:[{type:'C'},{name:'X'}]}",
                       "{$set:{value:3}}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after $set"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
           // %inc試験
           .drop()
           .insertMany("["+
                       "{type:'C',name:'X',value:5},"+
                       "{type:'C',name:'Y',value:10},"+
                       "{type:'C',name:'X',value:13}"+
                       "]")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- before $inc"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
           .updateMany("{$and:[{type:'C'},{name:'X'}]}",
                       "{$inc:{value:3}}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after $inc"))
           .forEachMson(Rm->ps.println(Rm))
           .back()

           //----- 名称間違い試験(存在しないレコード)
           // %set試験
           .forThis(Fi->ps.println("===== 名称間違い試験(存在しないレコード)"))
           .drop()
           .insertMany("["+
                       "{type:'C',name:'X',value:5},"+
                       "{type:'C',name:'Y',value:10},"+
                       "{type:'C',name:'X',value:13}"+
                       "]")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- before $set"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
           .updateMany("{$and:[{type:'X'},{name:'X'}]}",
                       "{$set:{value:3}}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after $set"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
           // %inc試験
           .drop()
           .insertMany("["+
                       "{type:'C',name:'X',value:5},"+
                       "{type:'C',name:'Y',value:10},"+
                       "{type:'C',name:'X',value:13}"+
                       "]")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- before $inc"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
           .updateMany("{$and:[{type:'X'},{name:'X'}]}",
                       "{$inc:{value:3}}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after $inc"))
           .forEachMson(Rm->ps.println(Rm))
           .back()

           //----- 名称間違い試験(存在しないフィールド)
           // %set試験
           .forThis(Fi->ps.println("===== 名称間違い試験(存在しないフィールド)"))
           .drop()
           .insertMany("["+
                       "{type:'C',name:'X',value:5},"+
                       "{type:'C',name:'Y',value:10},"+
                       "{type:'C',name:'X',value:13}"+
                       "]")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- before $set"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
           .updateMany("{$and:[{type:'C'},{name:'X'}]}",
                       "{$set:{valueX:3}}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after $set"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
           // %inc試験
           .drop()
           .insertMany("["+
                       "{type:'C',name:'X',value:5},"+
                       "{type:'C',name:'Y',value:10},"+
                       "{type:'C',name:'X',value:13}"+
                       "]")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- before $inc"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
           .updateMany("{$and:[{type:'C'},{name:'X'}]}",
                       "{$inc:{valueX:3}}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after $inc"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
           //----- 名称間違い試験(予備:存在するフィールドexist使用)
           // %set試験
           .forThis(Fi->ps.println("===== 名称間違い試験(予備:存在するフィールドexist使用)"))
           .drop()
           .insertMany("["+
                       "{type:'C',name:'X',value:5},"+
                       "{type:'C',name:'Y',value:10},"+
                       "{type:'C',name:'X',value:13}"+
                       "]")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- before $set"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
           .updateMany("{$and:[{type:'C'},{name:'X'},{"+_field_name+":{$exists:true }}]}",
                       "{$set:{"+_field_name+":3}}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after $set"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
           // %inc試験
           .drop()
           .insertMany("["+
                       "{type:'C',name:'X',value:5},"+
                       "{type:'C',name:'Y',value:10},"+
                       "{type:'C',name:'X',value:13}"+
                       "]")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- before $inc"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
           .updateMany("{$and:[{type:'C'},{name:'X'},{value:{$exists:true }}]}",
                       "{$inc:{value:3}}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after $inc"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
           //----- 名称間違い試験(予備:存在するフィールドexist使用)
           // %set試験
           .forThis(Fi->ps.println("===== 名称間違い試験(予備:存在しないフィールドexist使用)"))
           .drop()
           .insertMany("["+
                       "{type:'C',name:'X',value:5},"+
                       "{type:'C',name:'Y',value:10},"+
                       "{type:'C',name:'X',value:13}"+
                       "]")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- before $set"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
           .updateMany("{$and:[{type:'C'},{name:'X'},{valueX:{$exists:true }}]}",
                       "{$set:{valueX:3}}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after $set"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
           // %inc試験
           .drop()
           .insertMany("["+
                       "{type:'C',name:'X',value:5},"+
                       "{type:'C',name:'Y',value:10},"+
                       "{type:'C',name:'X',value:13}"+
                       "]")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- before $inc"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
           .updateMany("{$and:[{type:'C'},{name:'X'},{valueX:{$exists:true }}]}",
                       "{$inc:{valueX:3}}")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after $inc"))
           .forEachMson(Rm->ps.println(Rm))
           .back()

           //---------------------------------------------
           // findOneAndUpdate upsert
           //---------------------------------------------
           // %set試験
           .forThis(Fi->ps.println("===== findOneAndUpdate"))
           .drop()
           .insertMany("["+
                       "{type:'C',name:'X',value:5},"+
                       "{type:'C',name:'Y',value:10},"+
                       "{type:'C',name:'X',value:13}"+
                       "]")
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- before $set"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
/* TODO:別建て
           .forThis(Rc->
              Rc.mongoCollection
                .findOneAndUpdate(
                      hiMongo.objToBson("{$and:[{type:'C'},{name:'Z'}]}",null),
                      hiMongo.objToBson("{$set:{valueX:3}}",null),
                      (new FindOneAndUpdateOptions()).upsert(true))
              )
           .find("{}","{_id:0}")
           .forThis(Fi->ps.println("---- after $set"))
           .forEachMson(Rm->ps.println(Rm))
           .back()
*/
           ;
         }
      catch(Exception _ex){
         _ex.printStackTrace(System.err);
         System.exit(1);
         }
      System.exit(0);
      }
   }

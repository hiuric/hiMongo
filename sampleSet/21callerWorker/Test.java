import hi.db.hiMongo;
import hi.db.hiStringCOM;
import hi.db.hiMongoCaller;
import hi.db.hiMongoWorker;
class Repeater implements hiStringCOM{
   hiStringCOM to;
   public Repeater(hiStringCOM to_){
      to= to_;
      }
   @Override
   public String call(String msg_){
      System.err.println("call    :"+msg_);
      String _resp= to.call(msg_);
      System.err.println("response:"+_resp);
      return _resp;
      }
   }
public class Test { 
   public static void main(String[] args_){
      Repeater          _repeater = new Repeater(new hiMongoWorker());
      hiMongo.MoreMongo mongo     = new hiMongoCaller(_repeater);
      hiMongo.DB db=mongo.use("db01");
      db.in("famiryTree")
        .find("{name:{$in:['P0027','P0028','P0029','P0030','P0031']}}")
        .forEachRecursive(
            "{#FILTER:{name:{$in:[#CUR.father,#CUR.mother]}},#FIELD:{_id:0}}"
           ,Fr->{ // 主処理（再帰呼び出しされる)
               if( "KING".equals(Fr.get("#CUR.status")) ) {
                  return Fr.disp(" is a descendant of KING #CUR.name");
                  }
               return null;
               }
           ,Ff->{ // 完了 #RESULT に主処理の戻り値
               System.out.println(Ff.disp("#TOP.name")+Ff.get("#RESULT")+".");
               }
           ,Fn->{ // 完了
               System.out.println(Fn.disp("#TOP.name is not."));
               }
            )
         ;
      System.out.println("log out = stderr;may be \"mon.log\" in this test.");
      }
   }

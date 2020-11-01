import hi.db.hiMongo;
public class Test {
   public static void main(String[] args_){
      hiMongo.DB db=hiMongo.use("db01");
      db.in("coll_01")
        .find()
        .sort("{_id:-1}")
        .readOne("{#last_date:'date'}")
        .find("{date:{$gte:{$calc:'#last_date-25000'}}}","{_id:0}")
        .forEachMson(Rm->System.out.println(Rm))
        ;
      }
   }
